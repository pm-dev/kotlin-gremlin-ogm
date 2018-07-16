package starwars.graphql.character

import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.future.future
import kotlinx.coroutines.experimental.withContext
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.dataloader.DataLoader
import org.slf4j.LoggerFactory
import starwars.models.Character

internal class CharacterDataLoader(
        private val graphMapper: GraphMapper
) : DataLoader<Long, Character>(
        { keys ->
            future {
                withContext(DefaultDispatcher) {
                    logger.info("loading character ids: $keys")
                    val characters = graphMapper.V<Character>(keys.toSet()).fetch()
                    characters
                }
            }.thenApply { characters -> characters.syncWithKeys(keys) { it.id!! } }
        }
) {
    companion object {

        const val registryKey = "Character"

        private val logger = LoggerFactory.getLogger(CharacterDataLoader::class.java)

        private inline fun <K, V> Iterable<V>.syncWithKeys(keys: Iterable<K>, keySelector: (V) -> K): List<V?> {
            val objectsMap = associateBy(keySelector)
            return keys.map { key -> objectsMap[key] }
        }
    }
}
