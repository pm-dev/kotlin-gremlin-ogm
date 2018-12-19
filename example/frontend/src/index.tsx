import ApolloClient from 'apollo-boost';
import gql from 'graphql-tag';
import * as React from 'react';
import * as ReactDOM from 'react-dom';
import App from './App';
import './index.css';
import registerServiceWorker from './registerServiceWorker';

const client = new ApolloClient({
  uri: 'http://localhost:5000/graphql',
});

client.query({
  query: gql`{
      hero {
          name {
              full
          }
      }
  }`
}).then(result => {
  console.log(result);
});

ReactDOM.render(
  <App />,
  document.getElementById('root') as HTMLElement
);
registerServiceWorker();
