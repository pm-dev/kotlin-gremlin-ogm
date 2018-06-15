package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class DuplicateToVertex(
        kClass: KClass<*>
) : AnnotationException(
        "Only one param may be annotated with @ToVertex. Class: $kClass."
)
