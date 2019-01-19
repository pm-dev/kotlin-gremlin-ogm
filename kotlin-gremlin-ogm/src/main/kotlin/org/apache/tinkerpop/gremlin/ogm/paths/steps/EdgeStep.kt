package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.ogm.elements.Edge
import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.steps.paths.Path
import org.apache.tinkerpop.gremlin.ogm.paths.bound.*
import org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.edgespec.EdgeSpec

interface EdgeStep<FROM : Vertex, out TO : Vertex, E : Edge<FROM, TO>> : Step<FROM, E> {

    class ToSingle<FROM : Vertex, out TO : Vertex, E : Edge<FROM, TO>>(
            edgeSpec: EdgeSpec.ToSingle<FROM, TO>
    ) : StepToSingle<FROM, E>({ traverser ->

        traverser.traversal.map {
            traverser.graphMapper.serialize(it.get())
        }.outE(edgeSpec.name).map {
            traverser.graphMapper.deserialize<FROM, TO, E>(it.get())
        }
    })

    class ToOptional<FROM : Vertex, out TO : Vertex, E : Edge<FROM, TO>>(
            edgeSpec: EdgeSpec.ToOptional<FROM, TO>
    ) : StepToOptional<FROM, E>({ traverser ->

        traverser.traversal.map {
            traverser.graphMapper.serialize(it.get())
        }.outE(edgeSpec.name).map {
            traverser.graphMapper.deserialize<FROM, TO, E>(it.get())
        }
    })

    class ToMany<FROM : Vertex, out TO : Vertex, E : Edge<FROM, TO>>(
            edgeSpec: EdgeSpec.ToMany<FROM, TO>
    ) : StepToMany<FROM, E>({ traverser ->

        traverser.traversal.map {
            traverser.graphMapper.serialize(it.get())
        }.outE(edgeSpec.name).map {
            traverser.graphMapper.deserialize<FROM, TO, E>(it.get())
        }
    })
}
