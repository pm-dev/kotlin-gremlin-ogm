package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KParameter

internal class MapperUnsupported(
        param: KParameter
) : ClientException(
        description = "The @Mapper annotation is not supported on the field annotated with @ID, @InVertex or @OutVertex" +
                " For @ID, the type of the field must match the id type of your Gremlin implementation. " +
                "Parameter name: ${param.name}"
)
