package org.apache.tinkerpop.gremlin.ogm.mappers

/**
 * A type that maps an object to/from its graph representation.
 */
interface PropertyBiMapper<DESERIALIZED : Any, SERIALIZED : SerializedProperty> : BiMapper<DESERIALIZED, SERIALIZED>
