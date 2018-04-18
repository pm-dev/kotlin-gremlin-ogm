package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KParameter

internal class IDMapperUnsupported(
        param: KParameter
) : ClientException(
        description = "The @Mapper annotation is not supported on the field annotated with @ID " +
                " The type of the @ID field must match the id type of your Gremlin implementation. " +
                "Parameter name: ${param.name}"
)
