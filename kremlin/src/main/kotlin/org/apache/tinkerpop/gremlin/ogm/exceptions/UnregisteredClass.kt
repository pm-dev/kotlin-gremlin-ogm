package org.apache.tinkerpop.gremlin.ogm.exceptions

internal class UnregisteredClass(obj: Any) :
        ClientException("Attempting to serialize $obj to the graph, however this object's class ${obj::class} " +
                "has not been registered with your GraphMapper instance.")
