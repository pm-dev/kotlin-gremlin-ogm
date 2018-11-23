package starwars.traversals.character

import org.apache.tinkerpop.gremlin.ogm.paths.Path
import org.apache.tinkerpop.gremlin.ogm.paths.steps.Step
import org.apache.tinkerpop.gremlin.ogm.paths.steps.dedup
import org.apache.tinkerpop.gremlin.ogm.paths.steps.to
import starwars.models.Character

/**
 * Traverse to the Character's second degree friends. The results of this traversal does not include this character
 * nor any first degree friends.
 */

internal val Character.Companion.secondDegreeFriends
    get(): Path.ToMany<Character, Character> =
        saveCharacter
                .to(friends)
                .to(saveFirstDegreeFriends)
                .to(friends)
                .dedup()
                .to(filterFirstDegreeFriends)
                .to(filterCharacter)

/**
 * This step saves the results of the first friends step so that the first degree friends can be filtered out later.
 * It it a 'ToSingle' step because it does not change the result count of the traversal.
 */
private val saveFirstDegreeFriends = Step.ToSingle<Character, Character> {
    it.traversal.aggregate(firstDegreeFriendsKey)
}

private val saveCharacter = Step.ToSingle<Character, Character> {
    it.traversal.aggregate(characterKey)
}

/**
 * This step removes any characters that are first degree friends. It is a 'ToOptional' step because it may reduce
 * the result count of the traversal.
 */
private val filterFirstDegreeFriends = Step.ToOptional<Character, Character> { it ->
    it.traversal.`as`(secondDegreeFriendKey).select<Any>(firstDegreeFriendsKey, secondDegreeFriendKey).flatMap {
        val map = it.get()
        @Suppress("UNCHECKED_CAST")
        val first = map[firstDegreeFriendsKey] as Set<Character>
        val second = map[secondDegreeFriendKey] as Character
        if (first.contains(second)) emptyList<Character>().iterator() else listOf(second).iterator()
    }
}

private val filterCharacter = Step.ToOptional<Character, Character> { traverser ->
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
