package starwars.graphql.character

import starwars.models.Character


internal enum class CharacterPageOrder(
        private val comparator: Comparator<Character>
) : Comparator<Character> {

    CREATED_AT_ASCENDING(Comparator.comparing(Character::createdAt)),

    CREATED_AT_DESCENDING(CREATED_AT_ASCENDING.comparator.reversed()),

    FULL_NAME_ASCENDING(Comparator.comparing<Character, String> { it.name.full }),

    FULL_NAME_DESCENDING(FULL_NAME_ASCENDING.comparator.reversed()),
    ;

    override fun compare(o1: Character?, o2: Character?): Int = comparator.compare(o1, o2)
}
