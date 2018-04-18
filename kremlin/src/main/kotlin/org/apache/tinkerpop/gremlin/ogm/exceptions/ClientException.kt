package org.apache.tinkerpop.gremlin.ogm.exceptions

internal open class ClientException(
        description: String
) : RuntimeException("ClientException: $description")
