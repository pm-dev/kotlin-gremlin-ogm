package org.apache.tinkerpop.gremlin.ogm.exceptions

import org.apache.tinkerpop.gremlin.ogm.reflection.PropertyDescription

internal class IncompatibleMap(
        propertyDescription: PropertyDescription<*>
) : ClientException("Unsupported map of type ${propertyDescription.kClass}. " +
        "This library knows how to serialize/deserialize the map based on the map's type " +
        "parameters' first upper bound constraint, which must be a class. For example when " +
        "deserializing Map<K, V>, K and V must both have their first upper bound constraint be a class." +
        "That class determines how to map the keys/values to and from the graph." +
        "Alternatively, you can define a custom mapper for this map property.")
