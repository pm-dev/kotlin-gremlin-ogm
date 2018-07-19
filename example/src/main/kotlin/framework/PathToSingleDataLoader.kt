package framework

import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.future.future
import kotlinx.coroutines.experimental.withContext
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.Path
import org.apache.tinkerpop.gremlin.ogm.paths.bound.from
import org.dataloader.DataLoader
import org.slf4j.LoggerFactory

abstract class PathToSingleDataLoader<FROM : Vertex, TO, P : Path.ToSingle<FROM, TO>>(
        private val path: P,
        private val graphMapper: GraphMapper
) : DataLoader<FROM, TO>({ froms ->
    future {
        withContext(DefaultDispatcher) {
            logger.debug("Loading to-single path $path from $froms")
            val result = graphMapper.traverse(path from froms)
            froms.map { result[it] }
        }
    }
}) {
    companion object {
        private val logger = LoggerFactory.getLogger(PathToSingleDataLoader::class.java)
    }
}
