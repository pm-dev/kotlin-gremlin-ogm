package org.apache.tinkerpop.gremlin.ogm.exceptions

import org.apache.tinkerpop.gremlin.ogm.reflection.PropertyDescription

internal class IncompatibleIterable(
        propertyDescription: PropertyDescription<*>
) : ClientException(
        description = "Unsupported iterable of type ${propertyDescription.property}. " +
                "This library knows how to serialize/deserialize the iterable based on the iterable's type " +
                "parameter's first upper bound constraint, which must be a class. For example when " +
                "deserializing List<T>, T's first upper bound constraint must be a class. That class determines how to" +
                "map the iterable's elements to/from the graph. Alternatively, you can define a custom vertexMapper for this iterable property."
)
