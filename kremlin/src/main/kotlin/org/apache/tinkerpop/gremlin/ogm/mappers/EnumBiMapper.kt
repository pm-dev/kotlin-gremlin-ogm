package org.apache.tinkerpop.gremlin.ogm.mappers

/**
 * The interface to use when creating the mapping between enums and their graph serialized string form.
 * A convenient place to implement this is on the enum's companion object.
 */
interface EnumBiMapper<T : Enum<T>> : PropertyBiMapper<T, String>
