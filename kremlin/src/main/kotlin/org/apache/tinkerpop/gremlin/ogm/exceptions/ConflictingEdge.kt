package org.apache.tinkerpop.gremlin.ogm.exceptions

internal class ConflictingEdge(
        from: Any,
        to: Any,
        relationshipName: String
) : ClientException("Unable to create edge $relationshipName from $from to $to. " +
        "This means at least one of the following conditions was true:\n" +
        "\t1) The relationship is a 'FromOne' and the 'to' object already has an edge with this name." +
        "\t2) The relationship is a 'ToOne' and the 'from' object already has an edge with this name.")
