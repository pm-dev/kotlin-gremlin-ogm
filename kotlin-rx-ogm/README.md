# The Object Graph Mapping Library for Kotlin and Gremlin using ReactiveX

[![Build Status](https://travis-ci.org/pm-dev/kotlin-gremlin-ogm.svg?branch=master)](https://travis-ci.org/pm-dev/kotlin-gremlin-ogm)
[![Latest Release](https://img.shields.io/maven-metadata/v/http/central.maven.org/maven2/com/github/pm-dev/kotlin-janusgraph-ogm/maven-metadata.xml.svg)](http://central.maven.org/maven2/com/github/pm-dev/kotlin-janusgraph-ogm/)
[![Kotlin Version](https://img.shields.io/badge/kotlin-1.3.10-blue.svg)](http://kotlinlang.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

This library is an extension of [kotlin-gremlin-ogm](https://github.com/pm-dev/kotlin-gremlin-ogm) that
provides traversal functions returning Observables instead of Collections


#### Basic Usage:

Define a Vertex

    @Element("Person")
    data class Person(
    
            @ID
            val id: Long? = null,
               
            @Property("name")
            val name: String)
    
Define a Relationship

       val friends = ManyToManySymmetricEdgeSpec<Person>("friends")
       val subordinates = SingleToManyEdgeSpec<Person, Person>("boss_to_subordinates")
       val boss = subordinates.inverse
       
Save a Vertex

        val mighael = graphMapper.saveV(Person(name = "Michael Scott"))
        val dwight = graphMapper.saveV(Person(name = "Dwight Schrute"))
        
Save an Edge

        graphMapper.saveE(friends from michael to dwight)
        graphMapper.saveE(boss from michael to dwight)
        
Traverse an Edge

        graphMapper.traverse(friends from michael) // returns Observable<Person> emitting dwight
        graphMapper.traverse(boss from dwight) // returns Single<Person> emitting micheal
        
        
#### Installation:

- Gradle
        
        compile 'com.github.pm-dev:kotlin-rx-ogm:0.21.0'

- Maven

        <dependency>
            <groupId>com.github.pm-dev</groupId>
            <artifactId>kotlin-rx-ogm</artifactId>
            <version>0.21.0</version>
        </dependency>
