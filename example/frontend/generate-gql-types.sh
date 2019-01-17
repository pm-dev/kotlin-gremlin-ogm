#!/usr/bin/env bash

# Exit on error; error on use of unassigned variable; propagate failed status codes through pipes
set -euo pipefail

find . -prune -type d -name '__generated__' -exec rm -rf {} +;
echo "  ✔ Deleted __generated__ directories"

# It would be nice to use the backend local
# schema file so that the backend wouldn't need to
# be running, however it seems apollo codegen doesn't
# handle types that inherit multiple interfaces
# --localSchemaFile=../src/main/Resources/starwars.graphqls \

$(npm bin)/apollo client:codegen \
  --endpoint="http://localhost:5000/graphql" \
  --queries=src/**/*.graphql \
  --target=typescript \
  --passthroughCustomScalars \
  --mergeInFieldsFromFragmentSpreads \
  --addTypename;

# Replace the first line of every file with '// @gen3rated' (misspelled intentionally here) which indicates to code
# review tools to skip these files in diffs not show the diff.
function prepend(){
	echo -e "// @""generated\n$(cat $1)" > "$1"
}

for f in __generated__/*; do
	prepend "$f";
done

for f in src/**/__generated__/*; do
	prepend "$f";
done

echo "  ✔ Added // @""generated to the top of generated files"
