package org.apache.tinkerpop.gremlin.ogm.exceptions

internal class EmptyMapTokenIsReserved(
        emptyMapToken: String
) : ClientException(
        description = "Encountered a property value that is equal to a value that is reserved by the " +
                "library: $emptyMapToken. This should never happen since the empty map token is a UUID."
)
