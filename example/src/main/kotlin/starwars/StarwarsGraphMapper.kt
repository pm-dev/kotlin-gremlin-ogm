package starwars

import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.ogm.reflection.GraphDescription
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.springframework.stereotype.Component

@Component
internal open class StarwarsGraphMapper(
        override val g: GraphTraversalSource,
        override val graphDescription: GraphDescription
) : GraphMapper
