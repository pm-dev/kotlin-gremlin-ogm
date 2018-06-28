package starwars

import org.apache.tinkerpop.gremlin.ogm.reflection.GraphDescription
import org.janusgraph.ogm.JanusGraphIndicesBuilder
import org.springframework.stereotype.Component

@Component
internal class StarwarsGraphIndicesBuilder(override val graphDescription: GraphDescription) : JanusGraphIndicesBuilder
