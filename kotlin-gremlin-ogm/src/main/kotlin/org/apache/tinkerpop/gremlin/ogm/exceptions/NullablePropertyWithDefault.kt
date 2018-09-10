package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KParameter

internal class NullablePropertyWithDefault(
        parameter: KParameter
) : AnnotationException(
        "Parameter with @DefaultVaule annotation marked nullable. It's pretty odd to have an @Property that allows null " +
                "when the Element is created and serialized to the graph, but is given a default value when there's " +
                "no value for the @Property when deserializing from the graph. Most likely this parameter should be " +
                "non-nullable." +
                "Parameter: ${parameter.name}."
)
