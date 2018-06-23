package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KParameter

internal class PropertyUnsupported(
        parameter: KParameter
) : ClientException(
        description = "Element parameter '${parameter.name}' must be a KClass type"
)
