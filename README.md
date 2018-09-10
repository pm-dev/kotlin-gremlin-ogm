# The Object Graph Mapping Library for Kotlin and Gremlin

[![Build Status](https://travis-ci.org/pm-dev/kotlin-gremlin-ogm.svg?branch=master)](https://travis-ci.org/pm-dev/kotlin-gremlin-ogm)
[![Latest Release](https://img.shields.io/maven-metadata/v/http/central.maven.org/maven2/com/github/pm-dev/kotlin-gremlin-ogm/maven-metadata.xml.svg)](http://central.maven.org/maven2/com/github/pm-dev/kotlin-gremlin-ogm/)
[![Kotlin Version](https://img.shields.io/badge/kotlin-1.2.60-blue.svg)](http://kotlinlang.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

An interactive example can be run using the [starwars example project](https://github.com/pm-dev/kotlin-gremlin-ogm/tree/master/example).
From the repo root directory, run:

    ./gradlew :example:run

Then load GraphiQL at `http://localhost:5000/graphiql.html` to explore the data mapped with this library.



Gremlin is the graph traversal language for the Apache TinkerPop graph framework and is
supported by most graph database implementations. 
Check out [kotlin-janusgraph-ogm](https://github.com/pm-dev/kotlin-gremlin-ogm/tree/master/kotlin-janusgraph-ogm)
for additional JanusGraph specific features and [kotlin-gremlin-graphql](https://github.com/pm-dev/kotlin-gremlin-ogm/tree/master/kotlin-gremlin-graphql)


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

        graphMapper.traverse(friends from michael).fetch() // returns list: [ dwight ]
        graphMapper.traverse(friends from dwight).fetch() // returns list: [ michael ]        


#### Installation:

- Gradle
        
        compile 'com.github.pm-dev:kotlin-gremlin-ogm:0.18.0'

- Maven

        <dependency>
            <groupId>com.github.pm-dev</groupId>
            <artifactId>kotlin-gremlin-ogm</artifactId>
            <version>0.18.0</version>
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
property mapper for it with `GraphMapper` using the `scalarMappers` param
or declare a [`@Mapper`](https://github.com/pm-dev/kotlin-gremlin-ogm/blob/master/kotlin-gremlin-ogm/src/main/kotlin/org/apache/tinkerpop/gremlin/ogm/annotations/Mapper.kt) for that property.


#### Built-in property mappers:

- [`Instant` -> `String`](https://github.com/pm-dev/kotlin-gremlin-ogm/blob/master/kotlin-gremlin-ogm/src/main/kotlin/org/apache/tinkerpop/gremlin/ogm/mappers/scalar/InstantPropertyMapper.kt)
- [`UUID` -> `String`](https://github.com/pm-dev/kotlin-gremlin-ogm/blob/master/kotlin-gremlin-ogm/src/main/kotlin/org/apache/tinkerpop/gremlin/ogm/mappers/scalar/UUIDPropertyMapper.kt)

To use other property types, register your custom property mapper with `GraphMapper` using the `scalarMappers` param or declare
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
