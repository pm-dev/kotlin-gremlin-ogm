package graphql.servlet.ogm.dataloaders

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.Path
import org.apache.tinkerpop.gremlin.ogm.paths.bound.from
import org.dataloader.BatchLoader
import org.slf4j.LoggerFactory


class BatchPathToManyLoader<FROM : Vertex, TO>(
        private val path: Path.ToMany<FROM, TO>,
        private val graphMapper: GraphMapper
) : BatchLoader<FROM, List<TO>> {

    override fun load(froms: List<FROM>) = GlobalScope.future {
        logger.debug("Loading to-many path $path from $froms")
        val result = graphMapper.traversal(path from froms).traverse()
        froms.map { from -> result[from] }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(BatchPathToSingleLoader::class.java)
    }
}
