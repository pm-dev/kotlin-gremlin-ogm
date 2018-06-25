package starwars

import org.apache.tinkerpop.gremlin.ogm.GraphMapper
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.janusgraph.core.JanusGraph
import org.janusgraph.core.JanusGraphFactory
import org.springframework.stereotype.Component
import starwars.models.*
import starwars.models.Character.Companion.friends
import starwars.models.Sibling.Companion.siblings

@Component
internal class StarwarsGraphMapper : GraphMapper(
        g = graph.setupIndex().traversal(),
        vertices = setOf(
                Human::class,
                Droid::class),
        relationships = mapOf(
                friends to null,
                siblings to Sibling::class
        ),
        nestedObjects = setOf(
                Name::class
        ),
        scalarMappers = mapOf(
                Episode::class to Episode
        ))

private val graph = JanusGraphFactory.build()
        .set("storage.backend", "inmemory")
        .set("index.search.backend", "lucene")
        .set("index.search.directory", "/private/var/tmp")
        .open()

private fun JanusGraph.setupIndex(): JanusGraph {
    val mgmt = openManagement()
    val firstName = mgmt.makePropertyKey("name.first").dataType(String::class.java).make()
    mgmt.buildIndex("character-first-name-index", Vertex::class.java).addKey(firstName).buildCompositeIndex()
    val lastName = mgmt.makePropertyKey("name.last").dataType(String::class.java).make()
    mgmt.buildIndex("character-last-name-index", Vertex::class.java).addKey(lastName).buildCompositeIndex()
    mgmt.commit()
    return this
}
