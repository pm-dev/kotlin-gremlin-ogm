package starwars.traversals.character

import org.apache.tinkerpop.gremlin.ogm.paths.steps.Step
import org.apache.tinkerpop.gremlin.ogm.paths.steps.StepToOptional
import org.apache.tinkerpop.gremlin.ogm.paths.steps.StepToSingle
import org.apache.tinkerpop.gremlin.ogm.paths.steps.dedup
import starwars.models.Character

/**
 * Traverse to the Character's second degree friends. The results of this g does not include this character
 * nor any first degree friends.
 */

internal val Character.Companion.secondDegreeFriends
    get(): Step.ToMany<Character, Character> =
        saveCharacter
                .to(friends)
                .to(saveFirstDegreeFriends)
                .to(friends)
                .dedup()
                .to(filterFirstDegreeFriends)
                .to(filterCharacter)

/**
 * This step saves the results of the first friends step so that the first degree friends can be filtered out later.
 * It it a 'ToSingle' step because it does not change the result count of the g.
 */
private val saveFirstDegreeFriends = StepToSingle<Character, Character> {
    it.traversal.aggregate(firstDegreeFriendsKey)
}

private val saveCharacter = StepToSingle<Character, Character> {
    it.traversal.aggregate(characterKey)
}

/**
 * This step removes any characters that are first degree friends. It is a 'ToOptional' step because it may reduce
 * the result count of the g.
 */
private val filterFirstDegreeFriends = StepToOptional<Character, Character> { it ->
    it.traversal.`as`(secondDegreeFriendKey).select<Any>(firstDegreeFriendsKey, secondDegreeFriendKey).flatMap {
        val map = it.get()
        @Suppress("UNCHECKED_CAST")
        val first = map[firstDegreeFriendsKey] as Set<Character>
        val second = map[secondDegreeFriendKey] as Character
        if (first.contains(second)) emptyList<Character>().iterator() else listOf(second).iterator()
    }
}

private val filterCharacter = StepToOptional<Character, Character> { traverser ->
    traverser.traversal.`as`(secondDegreeFriendKey).select<Any>(characterKey, secondDegreeFriendKey).flatMap {
        val map = it.get()
        @Suppress("UNCHECKED_CAST")
        val first = map[characterKey] as Set<Character>
        val second = map[secondDegreeFriendKey] as Character
        if (first.contains(second)) emptyList<Character>().iterator() else listOf(second).iterator()
    }
}

private const val characterKey = "characterKey"
private const val firstDegreeFriendsKey = "firstDegreeFriends"
private const val secondDegreeFriendKey = "secondDegreeFriend"
