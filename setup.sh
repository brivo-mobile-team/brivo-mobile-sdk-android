#!/bin/sh
for remote in \
  "git@bitbucket.org:brivoinc/mobile-sdk-android-samples.git" \
  "https://github.com/brivo-mobile-team/brivo-mobile-sdk-android.git"
do
  git remote set-url --delete --push origin $remote 2> /dev/null
  git remote set-url --add --push origin $remote
done

git remote show origin