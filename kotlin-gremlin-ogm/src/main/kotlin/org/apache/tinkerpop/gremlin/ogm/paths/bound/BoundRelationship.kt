package org.apache.tinkerpop.gremlin.ogm.paths.bound

import org.apache.tinkerpop.gremlin.ogm.elements.Vertex
import org.apache.tinkerpop.gremlin.ogm.paths.relationships.Relationship

/**
 * A [BoundRelationship] encapsulates a Relationship, as well as the object(s) the relationship starts from
 */
interface BoundRelationship<FROM : Vertex, TO : Vertex> : BoundPath<FROM, TO> {

    override val path: Relationship<FROM, TO>

    /**
     * A [BoundRelationship] where the relationship is a [Relationship.ToOne]
     */
    interface ToOne<FROM : Vertex, TO : Vertex> : BoundRelationship<FROM, TO>, BoundPath.ToOne<FROM, TO> {

        override val path: Relationship.ToOne<FROM, TO>
    }

    /**
     * A [BoundRelationship] where the relationship is a [Relationship.ToMany]
     */
    interface ToMany<FROM : Vertex, TO : Vertex> : BoundRelationship<FROM, TO>, BoundPath.ToMany<FROM, TO> {

        override val path: Relationship.ToMany<FROM, TO>
    }

    /**
     * A [BoundRelationship] where the relationship is a [Relationship.ToSingle]
     */
    interface ToSingle<FROM : Vertex, TO : Vertex> : ToOne<FROM, TO>, BoundPath.ToSingle<FROM, TO> {

        override val path: Relationship.ToSingle<FROM, TO>
    }

    /**
     * A [BoundRelationship] where the relationship is a [Relationship.ToOptional]
     */
    interface ToOptional<FROM : Vertex, TO : Vertex> : ToOne<FROM, TO>, BoundPath.ToOptional<FROM, TO> {

        override val path: Relationship.ToOptional<FROM, TO>
    }
}
