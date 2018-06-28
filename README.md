# The Object Graph Mapping Library for Kotlin and Gremlin

[![Build Status](https://travis-ci.org/pm-dev/kotlin-gremlin-ogm.svg?branch=master)](https://travis-ci.org/pm-dev/kotlin-gremlin-ogm)
[![Latest Release](https://maven-badges.herokuapp.com/maven-central/com.github.pm-dev/kotlin-gremlin-ogm/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.pm-dev/kotlin-gremlin-ogm/)

Gremlin is the graph traversal language for the Apache TinkerPop graph framework and is
supported by most graph database implementations. 
Check out [kotlin-janusgraph-ogm](https://github.com/pm-dev/kotlin-gremlin-ogm/tree/master/kotlin-janusgraph-ogm)
for additional JanusGraph specific features.


#### Basic Usage:

Define a Vertex

    @Element("Person")
    class Person(
    
            @ID
            val id: Long? = null,
               
            @Property("name")
            val name: String)
    
Define a Relationship

       val friends = Relationship.symmetricManyToMany<Person>("friends")

Save a Vertex

        val mighael = graphMapper.saveV(Person(name = "Michael Scott"))
        val dwight = graphMapper.saveV(Person(name = "Dwight Schrute"))
        
Save an Edge

        graphMapper.saveE(friends from michael to dwight)
        
Traverse an Edge

        graphMapper.traverse(friends from michael) // returns list: [ dwight ]
        graphMapper.traverse(friends from dwight) // returns list: [ michael ]        

More complex examples can be seen in the [starwars example project](https://github.com/pm-dev/kotlin-gremlin-ogm/tree/master/example/src/main/kotlin/starwars), 
which exposes a graph database through a GraphQL endpoint.


#### Installation:

- Gradle
        
        compile 'com.github.pm-dev:kotlin-gremlin-ogm:0.11.0'

- Maven

        <dependency>
            <groupId>com.github.pm-dev</groupId>
            <artifactId>kotlin-gremlin-ogm</artifactId>
            <version>0.11.0</version>
        </dependency>
        
        
#### Features:

- Take full advantage of Kotlin's type-safety. Traversals return either a list, non-optional, or optional based on
whether a relationship is defined as to-many, to-single, or to-optional, respectively.
- Map and filter traversals using your Kotlin objects.
- No runtime code generation. Other ogms use third-party libraries that generate new classes at runtime.
- External dependencies are limited to: Kotlin's standard library, the gremlin-driver, and slf4j. 
- Annotation-based so you can bring your current POJO domain objects.
- Kotlin compiler plugins 'all-open' and 'no-arg' are not required.


#### Why use a graph database and ogm?

- Graph databases are powerful for modeling data that is highly connected.
- This OGM enables for strong typing of domain objects in the application layer while removing the need for a schema enforced by the db.
    - This allows for data to be backed by a NoSQL datastore. NoSQL datastores are horizontally scalable and can be partition tolerant.
    - This makes migrations much easier.

#### Limitations:

- This library will not work if you're trying to connect to a Gremlin Server remotely. This library creates traversals
that call back into the library, thus, your graph implementation must be running within the same JVM.
    - For this reason, connecting to Amazon Neptune is not currently supported, as Amazon Neptune does not support
    calling arbitrary java from within a traversal.


#### Design Principles:

- Common use-cases should be easy. Uncommon use-cases should be possible.
- Performance is important.
- Fail fast with helpful exceptions.


#### Native property types are stored directly in the graph as property values:

- `Boolean`
- `Byte`
- `Double`
- `Float`
- `Int`
- `Long`
- `String`

If your Gremlin implementation does not support one of these native types, make sure to register a 
property mapper for it with `GraphMapper` using the [`scalarMappers` param](https://github.com/pm-dev/kotlin-gremlin-ogm/blob/master/kotlin-gremlin-ogm/src/main/kotlin/org/apache/tinkerpop/gremlin/ogm/GraphMapper.kt#L39) 
or declare a [`@Mapper`](https://github.com/pm-dev/kotlin-gremlin-ogm/blob/master/kotlin-gremlin-ogm/src/main/kotlin/org/apache/tinkerpop/gremlin/ogm/annotations/Mapper.kt) for that property.


#### Built-in property mappers:

- [`Instant` -> `String`](https://github.com/pm-dev/kotlin-gremlin-ogm/blob/master/kotlin-gremlin-ogm/src/main/kotlin/org/apache/tinkerpop/gremlin/ogm/mappers/scalar/InstantPropertyMapper.kt)
- [`UUID` -> `String`](https://github.com/pm-dev/kotlin-gremlin-ogm/blob/master/kotlin-gremlin-ogm/src/main/kotlin/org/apache/tinkerpop/gremlin/ogm/mappers/scalar/UUIDPropertyMapper.kt)

To use other property types, register your custom property mapper with `GraphMapper` using the [`scalarMappers` param](https://github.com/pm-dev/kotlin-gremlin-ogm/blob/master/kotlin-gremlin-ogm/src/main/kotlin/org/apache/tinkerpop/gremlin/ogm/GraphMapper.kt#L39) or declare
a [`@Mapper`](https://github.com/pm-dev/kotlin-gremlin-ogm/blob/master/kotlin-gremlin-ogm/src/main/kotlin/org/apache/tinkerpop/gremlin/ogm/annotations/Mapper.kt) for that property.


#### Built-in traversal steps:

- [`Dedup`](https://github.com/pm-dev/kotlin-gremlin-ogm/blob/master/kotlin-gremlin-ogm/src/main/kotlin/org/apache/tinkerpop/gremlin/ogm/relationships/steps/Dedup.kt)
- [`Filter`](https://github.com/pm-dev/kotlin-gremlin-ogm/blob/master/kotlin-gremlin-ogm/src/main/kotlin/org/apache/tinkerpop/gremlin/ogm/relationships/steps/Filter.kt)
- [`FilterMap`](https://github.com/pm-dev/kotlin-gremlin-ogm/blob/master/kotlin-gremlin-ogm/src/main/kotlin/org/apache/tinkerpop/gremlin/ogm/relationships/steps/FilterMap.kt)
- [`FlatMap`](https://github.com/pm-dev/kotlin-gremlin-ogm/blob/master/kotlin-gremlin-ogm/src/main/kotlin/org/apache/tinkerpop/gremlin/ogm/relationships/steps/FlatMap.kt)
- [`Map`](https://github.com/pm-dev/kotlin-gremlin-ogm/blob/master/kotlin-gremlin-ogm/src/main/kotlin/org/apache/tinkerpop/gremlin/ogm/relationships/steps/Map.kt)
- [`Slice`](https://github.com/pm-dev/kotlin-gremlin-ogm/blob/master/kotlin-gremlin-ogm/src/main/kotlin/org/apache/tinkerpop/gremlin/ogm/relationships/steps/Slice.kt)
- [`Sort`](https://github.com/pm-dev/kotlin-gremlin-ogm/blob/master/kotlin-gremlin-ogm/src/main/kotlin/org/apache/tinkerpop/gremlin/ogm/relationships/steps/Sort.kt)

Or build your own custom traversal-step. 

#### How the mapping works:

- A description of your graph, based annotations, is processed and cached when your `GraphMapper` is instantiated.
- Using this description of the graph, we can create 'vertex mappers' that knows how to serialize/deserialize objects marked with `@Element` to/from
the graph.
- Nested objects are not natively supported by Gremlin. When mapping a nested object to properties of a vertex, 
the property key uses periods ('.') to denote a nested object. For example:

Given:

        class Name(val first: String, val last: String)
        class Person(val name: Name)

...is serialized in the graph using vertex properties:

        "name.first" -> "Lionel"
        "name.last" -> "Messi"

- `List` and `Set` types are supported. These collections are stored as follows:

Given:

        class Name(val first: String, val last: String)
        class Person(val names: Set<Name>)
        
...is serialized in the graph using vertex properties:

        "names.0.first" -> "Cassius"
        "names.0.last" -> "Clay"
        "names.1.first" -> "Muhammad"
        "names.1.last" -> "Ali"

To preserve the difference between a null and empty collection or map, we use 
a special `UUID` token. For example if the names `Set` was empty:

        "names" -> "474A56F1-6309-41B5-A632-AD53F57DBDAE"                

...or in the case of an empty map, the special `UUID` is: `9B94DCB9-D405-47C1-B56D-72F83C4E81D3`.


#### Legal:

Licensed under the Apache Software License 2.0. 
This code is in no way affiliated with, authorized, maintained, sponsored or endorsed by the Apache Software Foundation.


#### Future improvements to consider:

- Deleting edges & vertices:
Gremlin supports removing edges and vertices, however, I haven't built this yet because removing data
from the graph is not typically a good idea. It's a better pattern to introduce properties that can be used
in filtering. This could still be useful however, when doing a migration.

- Default property values:
If you wanted to add a non-nullable property to a vertex, you would first have to add the property as nullable,
migrate all vertices in the graph to have a value for that property, then change the property to non-nullable.
However, if migrating all vertices is too costly in terms of time or cpu, this could potentially be avoided by
specifying a value (or function producing a value) to use when null is loaded from the graph for a non-nullable property.

- [Coroutine](https://kotlinlang.org/docs/reference/coroutines.html) support:
For `GraphMapper` functions that execute a traversal, it might be useful to make them a `suspend` function.
Coroutines are still experimental in Kotlin so I'll likely hold off on this for a bit.
