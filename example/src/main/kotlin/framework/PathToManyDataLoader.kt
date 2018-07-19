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

class PathToManyDataLoader<FROM : Vertex, TO, P : Path.ToMany<FROM, TO>>(
        private val path: P,
        private val graphMapper: GraphMapper
) : DataLoader<FROM, List<TO>>({ froms ->
    future {
        withContext(DefaultDispatcher) {
            logger.debug("Loading to-many path $path from $froms")
            val result = graphMapper.traverse(path from froms)
            froms.map { result[it] }
        }
    }
}) {
    companion object {
        private val logger = LoggerFactory.getLogger(PathToManyDataLoader::class.java)
    }
}
