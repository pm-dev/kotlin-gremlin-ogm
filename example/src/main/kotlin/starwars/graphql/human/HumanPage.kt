package starwars.graphql.human

import graphql.schema.pagination.Page
import starwars.models.Human

internal data class HumanPage(
        override val previous: HumanPageInfo?,
        override val next: HumanPageInfo?,
        override val results: List<Human>
) : Page<Human>
