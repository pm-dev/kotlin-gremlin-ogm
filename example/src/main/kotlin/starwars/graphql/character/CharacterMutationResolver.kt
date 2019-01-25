@file:Suppress("unused")

package starwars.graphql.character

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import graphql.schema.DataFetchingEnvironment
import graphql.servlet.ogm.mutate
import org.springframework.stereotype.Component
import starwars.models.Character
import starwars.models.Droid
import starwars.models.Human
import starwars.models.Name

@Component
internal class CharacterMutationResolver : GraphQLMutationResolver {

    fun updateCharacterName(
            characterID: Long,
            name: String,
            env: DataFetchingEnvironment
    ): Character = env.mutate {
        val character = V<Character>(id = characterID)!!
        val newName = Name.parse(raw = name)
        val updated = when (character) {
            is Droid -> character.copy(name = newName)
            is Human -> character.copy(name = newName)
        }
        saveV(vertex = updated)
    }
}
