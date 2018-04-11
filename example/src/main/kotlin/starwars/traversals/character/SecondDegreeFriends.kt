package starwars.traversals.character

import org.apache.tinkerpop.gremlin.ogm.relationships.bound.from
import org.apache.tinkerpop.gremlin.ogm.relationships.steps.*
import starwars.models.Character
import starwars.models.Character.Companion.friends

/**
 * Traverse to the Character's second degree friends. The results of this traversal does not include this character
 * nor any first degree friends.
 */
internal fun Character.toSecondDegreeFriends(range: LongRange? = null) =
    friends
            .to(saveFirstDegreeFriends)
            .to(friends)
            .dedup()
            .to(filterFirstDegreeFriends)
            .filter { it != this }
            .let { if (range == null) it else it.slice(range) }
            .from(this)

/**
 * This step saves the results of the first friends step so that the first degree friends can be filtered out later.
 * It it a 'ToSingle' step because it does not change the result count of the traversal.
 */
private val saveFirstDegreeFriends = Step.ToSingle<Character, Character>({
    it.traversal.aggregate(firstDegreeFriendsKey)
})

/**
 * This step removes any characters that are first degree friends. It is a 'ToOptional' step because it may reduce
 * the result count of the traversal.
 */
private val filterFirstDegreeFriends = Step.ToOptional<Character, Character>({
    it.traversal.`as`(secondDegreeFriendKey).select<Any>(firstDegreeFriendsKey, secondDegreeFriendKey).flatMap {
        val map = it.get()
        @Suppress("UNCHECKED_CAST")
        val first = map[firstDegreeFriendsKey] as Set<Character>
        val second = map[secondDegreeFriendKey] as Character
        if (first.contains(second)) emptyList<Character>().iterator() else listOf(second).iterator()
    }
})

private const val firstDegreeFriendsKey = "firstDegreeFriends"
private const val secondDegreeFriendKey = "secondDegreeFriend"
