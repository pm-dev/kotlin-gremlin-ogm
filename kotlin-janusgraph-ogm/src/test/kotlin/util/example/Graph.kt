package util.example

import org.janusgraph.core.JanusGraph
import org.janusgraph.core.JanusGraphFactory

internal fun exampleGraph(): JanusGraph =
        JanusGraphFactory.build()
                .set("storage.backend", "inmemory")
                .set("index.search.backend", "lucene")
                .set("index.search.directory", "/tmp")
                .open()
