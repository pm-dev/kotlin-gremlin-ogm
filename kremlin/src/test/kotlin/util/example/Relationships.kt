package util.example

import org.apache.tinkerpop.gremlin.ogm.relationships.Relationship

val asymmetricManyToMany = Relationship.asymmetricManyToMany<VertexWithInt, VertexWithInt>(name = "asymmetric_many_to_many")
val asymmetricOptionalToMany = Relationship.asymmetricOptionalToMany<VertexWithInt, VertexWithInt>(name = "asymmetric_optional_to_many")
val asymmetricOptionalToOptional = Relationship.asymmetricOptionalToOptional<VertexWithInt, VertexWithInt>(name = "asymmetric_optional_to_optional")
val asymmetricOptionalToSingle = Relationship.asymmetricOptionalToSingle<VertexWithInt, VertexWithInt>(name = "asymmetric_optional_to_single")
val asymmetricSingleToMany = Relationship.asymmetricSingleToMany<VertexWithInt, VertexWithInt>(name = "asymmetric_single_to_many")
val asymmetricSingleToOptional = Relationship.asymmetricSingleToOptional<VertexWithInt, VertexWithInt>(name = "asymmetric-Single_to_optional")
val asymmetricSingleToSingle = Relationship.asymmetricSingleToSingle<VertexWithInt, VertexWithInt>(name = "asymmetric_single_to_single")
val symmetricManyToMany = Relationship.symmetricManyToMany<VertexWithInt>(name = "asymmetric_many_to_many")
val symmetricOptionalToOptional = Relationship.symmetricOptionalToOptional<VertexWithInt>(name = "asymmetric_optional_to_optional")
val symmetricSingleToSingle = Relationship.symmetricSingleToSingle<VertexWithInt>(name = "symmetric_single_to_single")
