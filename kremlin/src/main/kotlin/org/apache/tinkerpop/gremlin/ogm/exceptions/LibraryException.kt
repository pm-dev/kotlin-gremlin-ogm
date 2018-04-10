package org.apache.tinkerpop.gremlin.ogm.exceptions

internal open class LibraryException(description: String) :
        RuntimeException("LibraryException - This should never happen, there's likely a bug with the library:\n$description")
