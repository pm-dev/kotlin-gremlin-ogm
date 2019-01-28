# The Object Graph Mapping Library for Kotlin and JanusGraph

[![Build Status](https://travis-ci.org/pm-dev/kotlin-gremlin-ogm.svg?branch=master)](https://travis-ci.org/pm-dev/kotlin-gremlin-ogm)
[![Latest Release](https://img.shields.io/maven-metadata/v/http/central.maven.org/maven2/com/github/pm-dev/kotlin-janusgraph-ogm/maven-metadata.xml.svg)](http://central.maven.org/maven2/com/github/pm-dev/kotlin-janusgraph-ogm/)
[![Kotlin Version](https://img.shields.io/badge/kotlin-1.3.10-blue.svg)](http://kotlinlang.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

This library is an extension of [kotlin-gremlin-ogm](https://github.com/pm-dev/kotlin-gremlin-ogm) that provides the
additional features:

- Indexing via @Indexed annotation


#### Basic Usage:

Define a Vertex

    @Element("Person")
    data class Person(
    
            @ID
            val id: Long? = null,
               
            @Indexed(unique = false) @Property("name")
            val name: String)
                
Save a Vertex

        val mighael = graphMapper.saveV(Person(name = "Michael Scott"))
        
Lookup Vertex by a property other than its ID

        val michael = graphMapper.allV<Person> { has("name", "Michael Scott") }.single()    

More complex examples can be seen in the [starwars example project](https://github.com/pm-dev/kotlin-gremlin-ogm/tree/master/example/src/main/kotlin/starwars), 
which exposes a graph database through a GraphQL endpoint.


#### Installation:

- Gradle
        
        compile 'com.github.pm-dev:kotlin-janusgraph-ogm:0.21.0'

- Maven

        <dependency>
            <groupId>com.github.pm-dev</groupId>
            <artifactId>kotlin-janusgraph-ogm</artifactId>
            <version>0.21.0</version>
        </dependency>
