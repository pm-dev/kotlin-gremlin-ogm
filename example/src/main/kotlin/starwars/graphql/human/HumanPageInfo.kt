package starwars.graphql.human

import graphql.schema.pagination.Page
import starwars.models.Human

internal data class HumanPageInfo(
        override val order: HumanPageOrder,
        override val fromID: Long? = null,
        override val direction: Page.Info.Direction? = null,
        @Transient override val limit: Int? = null
) : Page.Info<Human> {

    fun paginate(results: List<Human>): HumanPage {
        val parts = parts(results = results)
        val previousResult = parts.previous
        val nextResult = parts.next
        return HumanPage(
                previous = when (previousResult) {
                    null -> null
                    else -> HumanPageInfo(
                            order = order,
                            fromID = previousResult.id,
                            direction = Page.Info.Direction.BACKWARD)
                },
                next = when (nextResult) {
                    null -> null
                    else -> HumanPageInfo(
                            order = order,
                            fromID = nextResult.id,
                            direction =
                            Page.Info.Direction.FORWARD)
                },
                results = parts.page)
    }
}
