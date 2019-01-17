package org.apache.tinkerpop.gremlin.ogm.exceptions

internal class DuplicateRelationshipName(name: String) : AnnotationException(
        "Names of relationships must be unique. Duplicated name: $name"
)
