package org.apache.tinkerpop.gremlin.ogm.exceptions

internal class UnregisteredLabel(
        label: String
) : ClientException(
        description = "Attempting to deserialize an element with label $label, but no " +
                "@Element class or Relationship has been registered with GraphMapper with the label $label."
)
