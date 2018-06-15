package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass
import kotlin.reflect.KParameter

internal class NullableVertexParam(
        kClass: KClass<*>,
        param: KParameter
) : AnnotationException(
        "Param '${param.name}' annotated with @ToVertex or @FromVertex must be non-nullable. Class $kClass"
)
