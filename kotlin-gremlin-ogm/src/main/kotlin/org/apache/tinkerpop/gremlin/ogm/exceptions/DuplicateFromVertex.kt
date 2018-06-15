package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class DuplicateFromVertex(
        kClass: KClass<*>
) : AnnotationException(
        "Only one param may be annotated with @FromVertex. Class: $kClass."
)
