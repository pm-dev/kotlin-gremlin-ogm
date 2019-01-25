package graphql.schema.pagination

import kotlin.math.max
import kotlin.math.min


interface Page<T : Identifiable> {

    val previous: Info<T>?

    val next: Info<T>?

    val results: List<T>

    data class Parts<T>(val previous: T?, val page: List<T>, val next: T?)

    interface Info<T : Identifiable> {

        val order: Comparator<T>

        val fromID: ID?

        val direction: Direction?

        val limit: Int?

        enum class Direction {
            FORWARD,
            BACKWARD
        }

        fun parts(results: Iterable<T>): Page.Parts<T> {
            val sorted = results.sortedWith(order)
            val fromIndex = when (fromID) {
                null -> 0
                else -> {
                    val fromIndex = sorted.indexOfFirst { it.id == fromID }
                    when (fromIndex) {
                        -1 -> 0
                        else -> fromIndex
                    }
                }
            }
            val limit = limit
            val (startIndexInclusive, endIndexExclusive) = when (direction) {
                Direction.BACKWARD -> when (limit) {
                    null -> 0 to fromIndex + 1
                    else -> max(fromIndex + 1 - limit, 0) to fromIndex + 1
                }
                else -> when (limit) {
                    null -> fromIndex to sorted.size
                    else -> fromIndex to min(fromIndex + limit, sorted.size)
                }
            }
            val previousResult = when (startIndexInclusive) {
                0 -> null
                else -> sorted[startIndexInclusive - 1]
            }
            val nextResult = when (endIndexExclusive) {
                sorted.size -> null
                else -> sorted[endIndexExclusive]
            }
            return Page.Parts(
                    previousResult,
                    sorted.subList(startIndexInclusive, endIndexExclusive),
                    nextResult)
        }
    }
}
