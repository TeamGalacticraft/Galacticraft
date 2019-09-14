#!/bin/bash

# modified from https://github.com/DiscordHooks/travis-ci-discord-webhook

if [ -z "$2" ]; then
  echo -e "WARNING!!\nYou need to pass the WEBHOOK_URL environment variable as the second argument to this script." && exit
fi

echo -e "[Webhook]: Sending webhook to Discord...\\n";

case $1 in
  "success" )
    EMBED_COLOR=3066993
    STATUS_MESSAGE="Passed"
    ;;

  "failure" )
    EMBED_COLOR=15158332
    STATUS_MESSAGE="Failed"
    ;;

  * )
    EMBED_COLOR=0
    STATUS_MESSAGE="Status Unknown"
    ;;
esac

AUTHOR_NAME="$(git log -1 "$GITHUB_SHA" --pretty="%aN")"
COMMITTER_NAME="$(git log -1 "$GITHUB_SHA" --pretty="%cN")"
COMMIT_SUBJECT="$(git log -1 "$GITHUB_SHA" --pretty="%s")"
COMMIT_MESSAGE="$(git log -1 "$GITHUB_SHA" --pretty="%b")" | sed -E ':a;N;$!ba;s/\r{0,1}\n/\\n/g'
CREDITS="$GITHUB_ACTOR ran action"
URL="https://github.com/$GITHUB_REPOSITORY/commit/$GITHUB_SHA"
TIMESTAMP=$(date --utc +%FT%TZ)
WEBHOOK_DATA='{
  "username": "",
  "avatar_url": "https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png",
  "embeds": [ {
    "color": '$EMBED_COLOR',
    "author": {
      "name": "Build '"$STATUS_MESSAGE"' - '"$GITHUB_REPOSITORY"'",
      "url": "'"$URL"'",
      "icon_url": "https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png"
    },
    "title": "'"$COMMIT_SUBJECT"'",
    "url": "'"$URL"'",
    "description": "'"${COMMIT_MESSAGE//$'\n'/ }"\\n\\n"$CREDITS"'",
    "fields": [
      {
        "name": "Commit",
        "value": "'"[\`${TRAVIS_COMMIT:0:7}\`](https://github.com/$$GITHUB_REPOSITORY/commit/$GITHUB_SHA)"'",
        "inline": true
      }
    ],
    "timestamp": "'"$TIMESTAMP"'"
  } ]
}'

(curl --fail --progress-bar -A "Github-Actions-Webhook" -H Content-Type:application/json -H X-Author:JoeZwet#6252 -d "${WEBHOOK_DATA//	/ }" "$2" \
  && echo -e "\\n[Webhook]: Successfully sent the webhook.") || echo -e "\\n[Webhook]: Unable to send webhook."