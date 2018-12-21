import * as React from 'react';
import { BrowserRouter, Route } from 'react-router-dom';
import './App.css';
import Character from './Character';
import Home from './Home';


class App extends React.Component {
  public render() {
    return (
      <BrowserRouter>
        <div className="App">
          <Route exact={true} path="/" component={Home} />
          <Route path="/character/:name" component={Character} />
        </div>
      </BrowserRouter>
    );
  }
}

export default App;
