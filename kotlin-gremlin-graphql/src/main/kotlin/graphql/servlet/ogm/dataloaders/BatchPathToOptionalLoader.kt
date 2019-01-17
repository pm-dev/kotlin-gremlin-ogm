package graphql.servlet.ogm.dataloaders

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.Path
import org.apache.tinkerpop.gremlin.ogm.paths.bound.from
import org.dataloader.BatchLoader
import org.slf4j.LoggerFactory


class BatchPathToOptionalLoader<FROM : Vertex, TO>(
        private val path: Path.ToOptional<FROM, TO>,
        private val graphMapper: GraphMapper
) : BatchLoader<FROM, TO?> {

    override fun load(froms: List<FROM>) = GlobalScope.future {
        logger.debug("Loading to-optional path $path from $froms")
        val result = graphMapper.traversal(path from froms).traverse()
        froms.map { from -> result[from] }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(BatchPathToOptionalLoader::class.java)
    }
}

