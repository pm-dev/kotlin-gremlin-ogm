package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass
import kotlin.reflect.KParameter

internal class NonNullableNonOptionalParameter(
        kClass: KClass<*>,
        parameter: KParameter
) : AnnotationException("Non-nullable, non-optional, primary constructor parameter must be annotated " +
        "with @Property or @ID. Parameter: ${parameter.name}. Class: $kClass")
