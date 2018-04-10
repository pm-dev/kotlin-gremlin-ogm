package org.apache.tinkerpop.gremlin.ogm.mappers

/**
 * This typealias is used to represent any types that are accepted by the client's graph implementation
 * as a vertex property.
 *
 * In addition to the types accepted by the underlying graph implementation, a SerializedProperty
 * may also be one of these supported collection types:
 *
 * Map<String, SerializedProperty?>
 * List<SerializedProperty>
 */
typealias SerializedProperty = Any
