package org.apache.tinkerpop.gremlin.ogm.exceptions


internal class ObjectNotSaved(obj: Any) :
        ClientException(
                "Object $obj must be saved before creating edges with it. This library doesn't implicitly save vertices " +
                "when saving edges. This is done to prevent saving objects multiple times and creating duplicates.")
