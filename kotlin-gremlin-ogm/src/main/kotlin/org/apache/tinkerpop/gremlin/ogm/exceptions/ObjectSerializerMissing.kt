package org.apache.tinkerpop.gremlin.ogm.exceptions

internal class ObjectSerializerMissing(
        obj: Any
) : ClientException(
        description = "You need to tell the library how to serialize an object to the graph: $obj. " +
                "Where you create your GraphMapper instance, please register ${obj::class} as a nested " +
                "object (if it is annotated) or register a scalarMapper for ${obj::class}."
)
