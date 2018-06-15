package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class VertexPropertyRequired(
        kClass: KClass<*>
) : AnnotationException(
        "Either @ToVertex @FromVertex cannot be found as a property of your graph element. " +
                "This library will first look for member properties " +
                "annotated with @ToVertex @FromVertex, otherwise it will look for a member property with " +
                "the same name as the parameter annotated with @ToVertex/@FromVertex. Class: $kClass"
)
