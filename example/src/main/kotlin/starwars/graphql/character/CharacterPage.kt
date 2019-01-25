package starwars.graphql.character

import graphql.schema.pagination.Page
import starwars.models.Character

internal data class CharacterPage(
        override val previous: CharacterPageInfo?,
        override val next: CharacterPageInfo?,
        override val results: List<Character>
) : Page<Character>
