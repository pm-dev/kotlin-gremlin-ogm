package graphql.servlet.ogm.batchloaders

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.steps.Step
import org.dataloader.BatchLoader
import org.slf4j.LoggerFactory


data class StepToSingleBatchLoader<FROM, TO>(
        private val step: Step.ToSingle<FROM, TO>,
        private val graphMapper: GraphMapper
) : BatchLoader<FROM, TO> {

    override fun load(froms: List<FROM>) = GlobalScope.future {
        logger.debug("Loading to-many path $step from $froms")
        val result = graphMapper.traverse(step from froms)
        froms.map { from -> result[from] }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(StepToSingleBatchLoader::class.java)
    }
}
