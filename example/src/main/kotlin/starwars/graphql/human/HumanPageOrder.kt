package starwars.graphql.human

import starwars.models.Human

internal enum class HumanPageOrder(
        private val comparator: Comparator<Human>
) : Comparator<Human> {

    CREATED_AT_ASCENDING(Comparator.comparing(Human::createdAt)),

    CREATED_AT_DESCENDING(CREATED_AT_ASCENDING.comparator.reversed()),

    FULL_NAME_ASCENDING(Comparator.comparing<Human, String> { it.name.full }),

    FULL_NAME_DESCENDING(FULL_NAME_ASCENDING.comparator.reversed()),

    HOME_PLANET_ASCENDING(Comparator.comparing<Human, String>(Human::homePlanet)),

    HOME_PLANET_DESCENDING(HOME_PLANET_ASCENDING.comparator.reversed())
    ;

    override fun compare(o1: Human?, o2: Human?): Int = comparator.compare(o1, o2)
}
