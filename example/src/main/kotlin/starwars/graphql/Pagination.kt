package starwars.graphql

import starwars.graphql.character.CharacterPage
import starwars.graphql.character.CharacterPageInfo
import starwars.graphql.human.HumanPage
import starwars.graphql.human.HumanPageInfo
import starwars.models.Character
import starwars.models.Human

internal fun List<Character>.paginate(info: CharacterPageInfo): CharacterPage = info.paginate(results = this)
internal fun List<Human>.paginate(info: HumanPageInfo): HumanPage = info.paginate(results = this)
