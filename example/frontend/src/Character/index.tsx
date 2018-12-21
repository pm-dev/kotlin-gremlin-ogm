import * as React from 'react';
import { Query } from "react-apollo";
import { Link, RouteComponentProps } from "react-router-dom";
import {
  Character,
  Character_character_Droid,
  Character_character_Human,
  CharacterVariables
} from "./__generated__/Character";
import query from "./query.graphql";


interface RouteParams {
  name: string
}

export type Props = {} & RouteComponentProps<RouteParams>;

interface State {

}

const CharacterQuery = class extends Query<Character, CharacterVariables> {
};

export default class CharacterComponent extends React.Component<Props, State> {
  public render() {
    return (
      <CharacterQuery query={query} variables={{ name: this.props.match.params.name }}>
        {({ loading, data, error }) => {
          if (loading) {
            return <div>Loading</div>;
          }
          if (error) {
            return <h1>ERROR</h1>;
          }
          if (!data) {
            return <div>no data</div>;
          }
          const { character } = data;
          if (!character) {
            return (
              <div>
                {this.props.match.params.name} is not a character in Starwars.
              </div>
            );
          }
          const droid = character.__typename === 'Droid' && character as Character_character_Droid;
          const human = character.__typename === 'Human' && character as Character_character_Human;
          return (
            <div>
              <h3>{character.name.full}</h3>
              <h6>{character.__typename}: {character.id}</h6>
              <h6>{character.appearsIn.map(episode => episode.replace('_', ' ')).join(', ')}</h6>
              <h6>{droid && droid.primaryFunction}</h6>
              <h6>{human && human.homePlanet}</h6>
              <br/>
              <h4>{'Friends'}</h4>
              <hr/>
              {character.friends.map(friend =>
                <h6 key={friend.id}>
                  <Link to={`/character/${friend.name.full}`}>{friend.name.full}</Link>
                </h6>)}
              <br/>
              <h4>{'Second Degree Friends'}</h4>
              <hr/>
              {character.secondDegreeFriends.map(friend =>
                <h6 key={friend.id}>
                  <Link to={`/character/${friend.name.full}`}>{friend.name.full}</Link>
                </h6>)}
            </div>
          );
        }}
      </CharacterQuery>
    );
  }
}
