#!/usr/bin/env sh
TAG=$1

sed -ie "s|'com.github.DaikonWeb:daikon:.*'|'com.github.DaikonWeb:daikon:${TAG}'|g" README.md
sed -ie "s|<version>.*</version>|<version>${TAG}</version>|g" README.md

git commit -am "Release ${TAG}"
git tag $TAG
git push --tags
