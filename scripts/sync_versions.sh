#!/usr/bin/env sh
TAG=$1

if [[ "$TAG" -eq "" ]]
then
    echo "Enter a valid version to publish!"
    echo ""
    echo "Usage:"
    echo "$0 [DAIKON_VERSION]"
    exit 1
fi

DAIKON_ROOT="$(dirname $0)/.."
SCRIPT_NAME="publish.sh"

exclude_main_daikon() {
    cat - | grep -v "./daikon/"
}

extensions_scripts_folders() {
    find ${DAIKON_ROOT}/.. -name "${SCRIPT_NAME}" | exclude_main_daikon | sed "s/${SCRIPT_NAME}$//"
}

for folder in $(extensions_scripts_folders)
do
    (
        cd ${folder}
        echo "deploy: $(pwd)"
        git stash
        git pull --rebase
        git pull --tags
        ./${SCRIPT_NAME} ${TAG}
        git stash pop || true
    )
done