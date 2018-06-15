package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class IDParameterRequired(
        kClass: KClass<*>
) : AnnotationException(
        "Graph elements must have exactly one parameter annotated with @ID. Class: $kClass"
)
