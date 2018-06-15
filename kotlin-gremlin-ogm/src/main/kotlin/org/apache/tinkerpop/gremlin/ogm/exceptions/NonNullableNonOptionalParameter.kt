package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KParameter

internal class NonNullableNonOptionalParameter(
        parameter: KParameter
) : AnnotationException(
        "Non-nullable, non-optional, primary constructor parameter must be annotated " +
                "with @Property or @ID (for elements) or @ToVertex (for edges) or @FromVertex (for edges). " +
                "Parameter: ${parameter.name}."
)
