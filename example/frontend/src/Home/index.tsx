import * as React from "react";
import { Link } from "react-router-dom";

export default class HomeComponent extends React.Component<{}, {}> {
  public render() {
    return (
      <div>
        <h3>{'Starwars Character Directory'}</h3>
        <div>
          <h6><Link to={`/character/Luke Skywalker`}>Luke Skywalker</Link></h6>
          <h6><Link to={`/character/Han Solo`}>Han Solo</Link></h6>
          <h6><Link to={`/character/Leia Organa`}>Leia Organa</Link></h6>
          <h6><Link to={`/character/C-3PO`}>C-3PO</Link></h6>
          <h6><Link to={`/character/R2-D2`}>R2-D2</Link></h6>
        </div>
      </div>
    );
  }
}
