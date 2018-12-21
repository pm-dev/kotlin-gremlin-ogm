const fetch = require('node-fetch');
const fs = require('fs');

fetch(`http://localhost:5000/graphql`, {
  body: JSON.stringify({
    headers: {'Content-Type': 'application/json'},
    method: 'POST',
    query: `
      {
        __schema {
          types {
            kind
            name
            possibleTypes {
              name
            }
          }
        }
      }
    `,
    variables: {},
  }),
}).then(result => result.json()).then(result => {
  // here we're filtering out any type information unrelated to unions or interfaces
  result.data.__schema.types = result.data.__schema.types.filter(
    type => type.possibleTypes !== null,
  );
  fs.writeFile(
    './src/fragmentTypes.json',
    JSON.stringify(result.data),
    err => {
      if (err) {
        // tslint:disable-next-line:no-console
        console.error('Error writing fragmentTypes file', err);
      } else {
        // tslint:disable-next-line:no-console
        console.log('  ✔ Fragment types successfully extracted');
      }
    });
});
