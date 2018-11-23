package graphql.servlet.ogm.dataloaders

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.Path
import org.apache.tinkerpop.gremlin.ogm.paths.bound.from
import org.dataloader.DataLoader
import org.slf4j.LoggerFactory

class PathToOptionalDataLoader<FROM : Vertex, TO, P : Path.ToOptional<FROM, TO>>(
        private val path: P,
        private val graphMapper: GraphMapper
) : DataLoader<FROM, TO?>({ froms ->
    GlobalScope.future {
        graphql.servlet.ogm.dataloaders.PathToOptionalDataLoader.logger.debug("Loading to-optional path $path from $froms")
        val result = graphMapper.traverse(path from froms)
        froms.map { result[it] }
    }
}) {
    companion object {
        private val logger = LoggerFactory.getLogger(PathToOptionalDataLoader::class.java)
    }
}
