package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class DuplicateVertexProperty(
        kClass: KClass<*>
) : AnnotationException(
        "At most one member property of $kClass may be annotated with @FromVertex or @ToVertex"
)
