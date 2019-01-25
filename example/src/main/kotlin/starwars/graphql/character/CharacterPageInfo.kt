package starwars.graphql.character

import graphql.schema.pagination.Page
import starwars.models.Character

internal data class CharacterPageInfo(
        override val order: CharacterPageOrder,
        override val fromID: Long? = null,
        override val direction: Page.Info.Direction? = null,
        @Transient override val limit: Int? = null
) : Page.Info<Character> {

    fun paginate(results: Collection<Character>): CharacterPage {
        val parts = parts(results = results)
        val previousResult = parts.previous
        val nextResult = parts.next
        return CharacterPage(
                previous = when (previousResult) {
                    null -> null
                    else -> CharacterPageInfo(
                            order = order,
                            fromID = previousResult.id,
                            direction = Page.Info.Direction.BACKWARD)
                },
                next = when (nextResult) {
                    null -> null
                    else -> CharacterPageInfo(
                            order = order,
                            fromID = nextResult.id,
                            direction =
                            Page.Info.Direction.FORWARD)
                },
                results = parts.page)
    }
}
