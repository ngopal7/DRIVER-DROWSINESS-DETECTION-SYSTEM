package com.example.drowsiness.pipeline

import kotlin.math.abs
import kotlin.math.min

/**
 * Strategy-based channel fusion mirroring `signal_processing/channel_fusion.py`.
 *
 * Three strategies: `priority_only`, `compare_and_flag`, `full_fusion`.
 * Never silently treats channel agreement as gold-standard truth.
 */
object ChannelFusion {

    /**
     * Select a named fusion strategy. Mirrors `fuse()` in Python.
     */
    fun fuse(
        channelA: ChannelSeries,
        channelB: ChannelSeries,
        strategy: String?,
        config: Map<String, Any>,
    ): RRSeries {
        require(channelA.fs == channelB.fs) { "channel series must share a sampling rate" }

        val resolved = strategy
            ?: configStringOrNull(config, "fusion_strategy")
            ?: configStringOrNull(configSectionOrNull(config, "fusion"), "strategy")
            ?: "priority_only"

        return when (resolved) {
            "priority_only" -> priority(channelA, channelB)
            "compare_and_flag" -> {
                val base = priority(channelA, channelB)
                val (flagged, magnitude, meta) = disagreement(channelA, channelB, config)
                base.disagreement.let { /* overwrite via copy-into mutable fields */ }
                RRSeries(
                    base.peakIndices, base.rrMs, base.beatSources, base.valid,
                    disagreement = flagged,
                    disagreementMs = magnitude,
                    handoffBoundaries = base.handoffBoundaries,
                    metadata = mutableMapOf<String, Any?>().apply {
                        putAll(base.metadata)
                        putAll(meta)
                        put("strategy", "compare_and_flag")
                    }
                )
            }
            "full_fusion" -> fullFusion(channelA, channelB, config)
            else -> throw IllegalArgumentException(
                "fusion strategy must be priority_only, compare_and_flag, or full_fusion"
            )
        }
    }

    /**
     * Compatibility adapter matching `fuse_channels()` in Python.
     * Ground cannot enter the ChannelSeries interface.
     */
    fun fuseChannels(
        steeringSig: DoubleArray,
        seatbeltSig: DoubleArray,
        ground: DoubleArray,
        steeringQuality: QualityResult,
        seatbeltQuality: QualityResult,
        steeringPeaks: IntArray,
        seatbeltPeaks: IntArray,
        fs: Double,
        cfg: Map<String, Any>,
        steeringResult: PeakResult? = null,
        seatbeltResult: PeakResult? = null,
        motionArtifactEstimate: DoubleArray? = null,
    ): FusionResult {
        require(ground.size == steeringSig.size && steeringSig.size == seatbeltSig.size) {
            "All synchronized streams must have equal sample length"
        }
        val a = ChannelSeries(
            EcgChannel.STEERING_WHEEL,
            steeringResult ?: PeakResult(steeringPeaks, "legacy"),
            steeringQuality, fs, motionArtifactEstimate
        )
        val b = ChannelSeries(
            EcgChannel.SEATBELT,
            seatbeltResult ?: PeakResult(seatbeltPeaks, "legacy"),
            seatbeltQuality, fs, motionArtifactEstimate
        )
        val rr = fuse(a, b, configStringOrNull(cfg, "fusion_strategy"), cfg)
        val selected = when {
            rr.beatSources.isNotEmpty() && rr.beatSources[0] == EcgChannel.STEERING_WHEEL.value ->
                EcgChannel.STEERING_WHEEL
            rr.beatSources.isNotEmpty() && rr.beatSources[0] == EcgChannel.SEATBELT.value ->
                EcgChannel.SEATBELT
            else -> null
        }
        return FusionResult(
            selected, rr.peakIndices, rr.valid, rr.disagreement,
            "fusion strategy applied",
            buildMap {
                putAll(rr.metadata)
                put("rr_series", rr)
                put("handoff_boundaries", rr.handoffBoundaries)
                put("disagreement_ms", rr.disagreementMs)
            }
        )
    }

    // --- Strategy implementations ---

    private fun isValid(series: ChannelSeries): Boolean =
        series.quality.passed && series.peaks.indices.size > 1

    private fun confidence(result: PeakResult): DoubleArray {
        val c = result.confidence
        return if (c != null && c.size == result.indices.size) c
        else DoubleArray(result.indices.size) { 1.0 }
    }

    private fun asRR(
        peaks: IntArray,
        sources: Array<String>,
        fs: Double,
        extraMeta: Map<String, Any?> = emptyMap(),
    ): RRSeries {
        if (peaks.size < 2) {
            return RRSeries(peaks, doubleArrayOf(), sources, false,
                metadata = extraMeta.toMutableMap())
        }
        // same[i] = sources[i] == sources[i+1]
        val rr = DoubleArray(peaks.size - 1) { i ->
            (peaks[i + 1] - peaks[i]) * 1000.0 / fs
        }
        val same = BooleanArray(peaks.size - 1) { i -> sources[i + 1] == sources[i] }
        val filteredRR = rr.filterIndexed { i, _ -> same[i] }.toDoubleArray()
        val handoffs = same.indices.filter { !same[it] }.map { it + 1 }.toIntArray()
        return RRSeries(peaks, filteredRR, sources, peaks.size > 1,
            handoffBoundaries = handoffs,
            metadata = extraMeta.toMutableMap())
    }

    private fun priority(a: ChannelSeries, b: ChannelSeries): RRSeries {
        val (primary, secondary) = if (a.channel == EcgChannel.STEERING_WHEEL) a to b else b to a

        if (isValid(primary)) {
            if (!isValid(secondary)) {
                return asRR(
                    primary.peaks.indices,
                    Array(primary.peaks.indices.size) { primary.channel.value },
                    primary.fs,
                    mapOf("strategy" to "priority_only")
                )
            }
            // Seatbelt continuity beats admitted only outside primary coverage or into long gaps
            val pp = primary.peaks.indices
            val sp = secondary.peaks.indices
            val expected = if (pp.size > 1) {
                val diffs = IntArray(pp.size - 1) { pp[it + 1] - pp[it] }
                diffs.sorted()[diffs.size / 2].toDouble()
            } else Double.MAX_VALUE

            val fill = mutableListOf<Int>()
            for (p in sp) {
                val pos = pp.binarySearchInsertionPoint(p)
                if (pos == 0 || pos == pp.size) {
                    fill.add(p)
                } else if (pp[pos] - pp[pos - 1] > 1.5 * expected) {
                    fill.add(p)
                }
            }

            val allPeaks = IntArray(pp.size + fill.size)
            val allSources = Array<String>(pp.size + fill.size) { "" }
            pp.copyInto(allPeaks)
            for (i in pp.indices) allSources[i] = primary.channel.value
            for (i in fill.indices) {
                allPeaks[pp.size + i] = fill[i]
                allSources[pp.size + i] = secondary.channel.value
            }
            val order = allPeaks.indices.sortedBy { allPeaks[it] }
            return asRR(
                order.map { allPeaks[it] }.toIntArray(),
                order.map { allSources[it] }.toTypedArray(),
                primary.fs,
                mapOf("strategy" to "priority_only", "seatbelt_continuity_beats" to fill.size)
            )
        }

        if (isValid(secondary)) {
            return asRR(
                secondary.peaks.indices,
                Array(secondary.peaks.indices.size) { secondary.channel.value },
                secondary.fs,
                mapOf("strategy" to "priority_only", "primary_failed" to true)
            )
        }

        return RRSeries(
            intArrayOf(), doubleArrayOf(), emptyArray(), false,
            metadata = mutableMapOf("strategy" to "priority_only")
        )
    }

    private fun disagreement(
        a: ChannelSeries,
        b: ChannelSeries,
        cfg: Map<String, Any>,
    ): Triple<Boolean, Double, Map<String, Any?>> {
        if (!(isValid(a) && isValid(b))) {
            return Triple(false, Double.NaN, mapOf("comparison_coverage" to "single_channel"))
        }

        val fCfg = configSectionOrNull(cfg, "fusion") ?: cfg

        // Nearest-peak timing disagreement
        val nearestMs = DoubleArray(a.peaks.indices.size) { i ->
            val ap = a.peaks.indices[i]
            var minDist = Double.MAX_VALUE
            for (bp in b.peaks.indices) {
                val d = abs(ap - bp) * 1000.0 / a.fs
                if (d < minDist) minDist = d
            }
            minDist
        }
        val timing = median(nearestMs)

        // RR disagreement
        val n = min(a.peaks.indices.size, b.peaks.indices.size)
        val rrA = IntArray(maxOf(0, n - 1)) { a.peaks.indices[it + 1] - a.peaks.indices[it] }
        val rrB = IntArray(maxOf(0, n - 1)) { b.peaks.indices[it + 1] - b.peaks.indices[it] }
        val rrDelta = DoubleArray(rrA.size) { abs(rrA[it] - rrB[it]) * 1000.0 / a.fs }
        val rrMedian = if (rrDelta.isNotEmpty()) median(rrDelta) else Double.NaN

        val maxPeakMs = configDouble(fCfg, "max_peak_disagreement_ms")
        val maxRrMs = configDouble(fCfg, "max_rr_disagreement_ms")
        val flagged = timing > maxPeakMs || (rrMedian.isFinite() && rrMedian > maxRrMs)

        // Shared motion assessment
        var sharedMotion = false
        val ma = a.motionArtifactEstimate
        val mb = b.motionArtifactEstimate
        if (ma != null && mb != null && ma.isNotEmpty() && mb.isNotEmpty()) {
            val corr = pearsonCorrelation(ma, mb)
            val p95 = percentile(ma.map { abs(it) }.toDoubleArray(), 0.95)
            sharedMotion = corr > 0.8 && p95 > 0
        }

        return Triple(
            flagged || sharedMotion,
            maxOf(timing, if (rrMedian.isFinite()) rrMedian else 0.0),
            mapOf(
                "timing_disagreement_ms" to timing,
                "rr_disagreement_ms" to rrMedian,
                "shared_motion_artifact_risk" to sharedMotion,
            )
        )
    }

    private fun fullFusion(
        a: ChannelSeries,
        b: ChannelSeries,
        cfg: Map<String, Any>,
    ): RRSeries {
        if (!isValid(a) || !isValid(b)) return priority(a, b)

        val fCfg = configSectionOrNull(cfg, "fusion") ?: cfg
        val tolerance = configDouble(fCfg, "max_peak_disagreement_ms") * a.fs / 1000

        val pa = a.peaks.indices
        val pb = b.peaks.indices
        val ca = confidence(a.peaks)
        val cb = confidence(b.peaks)

        val usedB = mutableSetOf<Int>()
        val peaks = mutableListOf<Int>()
        val sources = mutableListOf<String>()

        for (i in pa.indices) {
            val p = pa[i]
            val candidates = pb.indices.filter { j ->
                j !in usedB && abs(pb[j] - p) <= tolerance
            }
            if (candidates.isNotEmpty()) {
                val j = candidates.minByOrNull { abs(pb[it] - p) }!!
                usedB.add(j)
                val total = ca[i] + cb[j]
                peaks.add(((ca[i] * p + cb[j] * pb[j]) / total).toInt())
                sources.add("confidence_fused")
            } else {
                peaks.add(p)
                sources.add(a.channel.value)
            }
        }
        for (j in pb.indices) {
            if (j !in usedB) {
                peaks.add(pb[j])
                sources.add(b.channel.value)
            }
        }

        val order = peaks.indices.sortedBy { peaks[it] }
        return asRR(
            order.map { peaks[it] }.toIntArray(),
            order.map { sources[it] }.toTypedArray(),
            a.fs,
            mapOf("strategy" to "full_fusion")
        )
    }

    // --- Utilities ---

    private fun IntArray.binarySearchInsertionPoint(value: Int): Int {
        var lo = 0; var hi = size
        while (lo < hi) {
            val mid = (lo + hi) / 2
            if (this[mid] < value) lo = mid + 1 else hi = mid
        }
        return lo
    }

    private fun pearsonCorrelation(a: DoubleArray, b: DoubleArray): Double {
        val n = min(a.size, b.size)
        if (n < 2) return 0.0
        val meanA = a.take(n).average()
        val meanB = b.take(n).average()
        var cov = 0.0; var varA = 0.0; var varB = 0.0
        for (i in 0 until n) {
            val da = a[i] - meanA; val db = b[i] - meanB
            cov += da * db; varA += da * da; varB += db * db
        }
        val denom = kotlin.math.sqrt(varA * varB)
        return if (denom > 0) cov / denom else 0.0
    }

    private fun percentile(data: DoubleArray, p: Double): Double {
        if (data.isEmpty()) return 0.0
        val sorted = data.sorted()
        val index = p * (sorted.size - 1)
        val lo = index.toInt()
        val hi = min(lo + 1, sorted.size - 1)
        val frac = index - lo
        return sorted[lo] * (1 - frac) + sorted[hi] * frac
    }
}
