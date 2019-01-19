package graphql.servlet.ogm.dataloaders

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.bound.bind
import org.apache.tinkerpop.gremlin.ogm.paths.steps.Step
import org.dataloader.BatchLoader
import org.slf4j.LoggerFactory


class PathToSingleBatchLoader<FROM : Vertex, TO>(
        private val step: Step.ToSingle<FROM, TO>,
        private val graphMapper: GraphMapper
) : BatchLoader<FROM, TO> {

    override fun load(froms: List<FROM>) = GlobalScope.future {
        logger.debug("Loading to-many path $step from $froms")
        val result = graphMapper bind froms traverse step
        froms.map { from -> result[from] }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PathToSingleBatchLoader::class.java)
    }
}
