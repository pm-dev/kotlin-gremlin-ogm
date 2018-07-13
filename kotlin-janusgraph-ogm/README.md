# The Object Graph Mapping Library for Kotlin and JanusGraph

[![Build Status](https://travis-ci.org/pm-dev/kotlin-gremlin-ogm.svg?branch=master)](https://travis-ci.org/pm-dev/kotlin-gremlin-ogm)
[![Latest Release](https://maven-badges.herokuapp.com/maven-central/com.github.pm-dev/kotlin-janusgraph-ogm/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.pm-dev/kotlin-janusgraph-ogm/)
[![Kotlin Version](https://img.shields.io/badge/kotlin-1.2.51-blue.svg)](http://kotlinlang.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Code Coverage](https://codecov.io/gh/pm-dev/kotlin-gremlin-ogm/branch/code-cov/graph/badge.svg)](https://codecov.io/gh/pm-dev/kotlin-gremlin-ogm/branch/code-cov)

This library is an extension of [kotlin-gremlin-ogm](https://github.com/pm-dev/kotlin-gremlin-ogm) that provides the
additional features:

- Indexing via @Indexed annotation


#### Basic Usage:

Define a Vertex

    @Element("Person")
    class Person(
    
            @ID
            val id: Long? = null,
               
            @Indexed(unique = false) @Property("name")
            val name: String)
    
Define a Relationship

       val friends = Relationship.symmetricManyToMany<Person>("friends")

Save a Vertex

        val mighael = graphMapper.saveV(Person(name = "Michael Scott"))
        val dwight = graphMapper.saveV(Person(name = "Dwight Schrute"))
        
Save an Edge

        graphMapper.saveE(friends from michael to dwight)
        
Lookup Vertex and Traverse an Edge

        val michael = graphMapper.allV<Person> { has("name", "Michael Scott") }.toSingle().fetch()      
        val dwight = graphMapper.traverse(friends from michael).toSingle().fetch()

More complex examples can be seen in the [starwars example project](https://github.com/pm-dev/kotlin-gremlin-ogm/tree/master/example/src/main/kotlin/starwars), 
which exposes a graph database through a GraphQL endpoint.


#### Installation:

- Gradle
        
        compile 'com.github.pm-dev:kotlin-janusgraph-ogm:0.13.0'

- Maven

        <dependency>
            <groupId>com.github.pm-dev</groupId>
            <artifactId>kotlin-janusgraph-ogm</artifactId>
            <version>0.13.0</version>
        </dependency>
