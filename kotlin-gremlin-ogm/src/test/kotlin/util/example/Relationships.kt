package util.example

import org.apache.tinkerpop.gremlin.ogm.paths.steps.relationships.edgespec.*

internal val asymmetricManyToMany = ManyToManyAsymmetricEdgeSpec<VertexWithInt, VertexWithInt>(name = "asymmetric_many_to_many")
internal val asymmetricOptionalToMany = OptionalToManyEdgeSpec<VertexWithInt, VertexWithInt>(name = "asymmetric_optional_to_many")
internal val asymmetricOptionalToOptional = OptionalToOptionalAsymmetricEdgeSpec<VertexWithInt, VertexWithInt>(name = "asymmetric_optional_to_optional")
internal val asymmetricOptionalToSingle = OptionalToSingleEdgeSpec<VertexWithInt, VertexWithInt>(name = "asymmetric_optional_to_single")
internal val asymmetricSingleToMany = SingleToManyEdgeSpec<VertexWithInt, VertexWithInt>(name = "asymmetric_single_to_many")
internal val asymmetricSingleToOptional = SingleToOptionalEdgeSpec<VertexWithInt, VertexWithInt>(name = "asymmetric-Single_to_optional")
internal val asymmetricSingleToSingle = SingleToSingleAsymmetricEdgeSpec<VertexWithInt, VertexWithInt>(name = "asymmetric_single_to_single")
internal val symmetricManyToMany = ManyToManySymmetricEdgeSpec<VertexWithInt>(name = "symmetric_many_to_many")
internal val symmetricOptionalToOptional = OptionalToOptionalSymmetricEdgeSpec<VertexWithInt>(name = "symmetric_optional_to_optional")
internal val symmetricSingleToSingle = SingleToSingleSymmetricEdgeSpec<VertexWithInt>(name = "symmetric_single_to_single")
