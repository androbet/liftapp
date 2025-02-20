package com.patrykandpatryk.liftapp.core.search

import javax.inject.Inject

class SearchAlgorithmImpl @Inject constructor() : SearchAlgorithm {

    override operator fun <T> invoke(
        query: String,
        entities: List<T>,
        selector: (T) -> String,
    ) = entities.mapNotNull { entity ->

        val (indexOfMatch, fuzzy) = entity
            .getIndexOfMatch(query = query, selector = selector)
            ?: return@mapNotNull null

        SearchInfo(
            entity = entity,
            indexOfMatch = indexOfMatch,
            fuzzy = fuzzy,
            naturalMatchPosition = entity.naturalMatchPosition(
                indexOfMatch = indexOfMatch,
                selector = selector,
            ),
        )
    }
        .sortedDescending()
        .map { searchInfo -> searchInfo.entity }

    private inline fun <T> T.getIndexOfMatch(
        query: String,
        selector: (T) -> String,
    ): Pair<Int, Boolean>? {

        val candidate = selector(this)

        candidate
            .indexOf(string = query, ignoreCase = true)
            .also { if (it != -1) return it to false }

        return getIndexOfFuzzyMatch(
            query = query,
            candidate = candidate,
        )
    }

    private fun getIndexOfFuzzyMatch(
        query: String,
        candidate: String,
    ): Pair<Int, Boolean>? {

        candidate
            .takeIf { query.length >= FUZZY_SEARCH_MIN_LENGTH }
            ?.indices
            ?.take(n = (candidate.length - query.length + 1).coerceAtLeast(minimumValue = 0))
            ?.forEach { potentialIndex ->

                var mismatchCount = 0

                val substring = candidate
                    .substring(range = potentialIndex until potentialIndex + query.length)
                    .lowercase()

                for (i in substring.indices) {

                    val mismatch = query[i].lowercaseChar() != substring[i]
                    val noSwap = i == 0 ||
                        (
                            query[i].lowercaseChar() != substring[i - 1] ||
                                query[i - 1].lowercaseChar() != substring[i]
                            )

                    if (mismatch && noSwap) mismatchCount++
                }

                if (mismatchCount <= getMaxMismatchCount(queryLength = query.length)) {
                    return potentialIndex to true
                }
            }

        return null
    }

    private inline fun <T> T.naturalMatchPosition(
        indexOfMatch: Int,
        selector: (T) -> String,
    ) = indexOfMatch == 0 || beforeNaturalMatchPosition.contains(selector(this)[indexOfMatch - 1])

    private fun getMaxMismatchCount(queryLength: Int) =
        (queryLength - FUZZY_SEARCH_MIN_LENGTH) / MAX_MISMATCH_COUNT_STEP_LENGTH + 1

    private data class SearchInfo<R>(
        val entity: R,
        val indexOfMatch: Int,
        val fuzzy: Boolean,
        val naturalMatchPosition: Boolean,
    ) : Comparable<SearchInfo<*>> {

        override fun compareTo(other: SearchInfo<*>): Int = when {
            other.fuzzy && fuzzy.not() -> 1
            fuzzy && other.fuzzy.not() -> -1
            other.naturalMatchPosition && naturalMatchPosition.not() -> -1
            naturalMatchPosition && other.naturalMatchPosition.not() -> 1
            other.indexOfMatch < indexOfMatch -> -1
            indexOfMatch < other.indexOfMatch -> 1
            else -> 0
        }
    }

    private companion object {

        const val FUZZY_SEARCH_MIN_LENGTH = 3
        const val MAX_MISMATCH_COUNT_STEP_LENGTH = 4
        val beforeNaturalMatchPosition = listOf(' ', '-')
    }
}
