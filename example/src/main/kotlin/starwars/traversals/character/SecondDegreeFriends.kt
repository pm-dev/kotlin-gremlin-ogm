package starwars.traversals.character

import org.apache.tinkerpop.gremlin.ogm.steps.Step
import org.apache.tinkerpop.gremlin.ogm.steps.StepTraverser
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
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
 * It it a 'ToSingle' step because it does not change the result count of the traversal.
 */
private val saveFirstDegreeFriends = object : Step.ToSingle<Character, Character> {

    override fun invoke(from: StepTraverser<Character>): GraphTraversal<*, Character> =
            from.traversal.aggregate(firstDegreeFriendsKey)
}

private val saveCharacter = object : Step.ToSingle<Character, Character> {

    override fun invoke(from: StepTraverser<Character>): GraphTraversal<*, Character> =
            from.traversal.aggregate(characterKey)
}

/**
 * This step removes any characters that are first degree friends. It is a 'ToOptional' step because it may reduce
 * the result count of the traversal.
 */
private val filterFirstDegreeFriends = object : Step.ToOptional<Character, Character> {

    override fun invoke(from: StepTraverser<Character>): GraphTraversal<*, Character> =
            from.traversal.`as`(secondDegreeFriendKey).select<Any>(firstDegreeFriendsKey, secondDegreeFriendKey).flatMap {
                val map = it.get()
                @Suppress("UNCHECKED_CAST")
                val first = map[firstDegreeFriendsKey] as Set<Character>
                val second = map[secondDegreeFriendKey] as Character
                if (first.contains(second)) emptyList<Character>().iterator() else listOf(second).iterator()
            }
}

private val filterCharacter = object : Step.ToOptional<Character, Character> {

    override fun invoke(from: StepTraverser<Character>): GraphTraversal<*, Character> =
            from.traversal.`as`(secondDegreeFriendKey).select<Any>(characterKey, secondDegreeFriendKey).flatMap {
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
