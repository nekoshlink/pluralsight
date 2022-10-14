# CLI Sample Interactions M5S1

# A set of NekoShlink CLI commands that try out different features of the server
# to make sure everything works correctly and data is properly stored in the DB.

# This version has CLI security with OIDC and OAUTH2
# PluralSight Module 5 (Step1)

# assemble command line first with `gradle assemble`!

# Use Postman or any other means to obtain an access token from your Auth Server, store
# the token in the oauth2.token file (or wherever you prefer) and set the path to your
# file on line 16
# Then run the tests in this file

OAUTH2_TOKEN_FILE=oauth2.token

set -v
cd ..

rm -f data/nekoshlink*

./nkshlink --access-token-file "$OAUTH2_TOKEN_FILE" domains list

./nkshlink --access-token-file "$OAUTH2_TOKEN_FILE" domains create https://nksf.link
./nkshlink --access-token-file "$OAUTH2_TOKEN_FILE" domains delete https://nksf.link
./nkshlink --access-token-file "$OAUTH2_TOKEN_FILE" domains create --scheme https nksf.link
./nkshlink --access-token-file "$OAUTH2_TOKEN_FILE" domains create --scheme https ednk.link
./nkshlink --access-token-file "$OAUTH2_TOKEN_FILE" domains list
./nkshlink --access-token-file "$OAUTH2_TOKEN_FILE" domains edit --scheme http ednk.link
./nkshlink --access-token-file "$OAUTH2_TOKEN_FILE" domains default ednk.link
./nkshlink --access-token-file "$OAUTH2_TOKEN_FILE" domains list
./nkshlink --access-token-file "$OAUTH2_TOKEN_FILE" domains default localhost:8443

./nkshlink --access-token-file "$OAUTH2_TOKEN_FILE" short-urls create https://google.com
./nkshlink --access-token-file "$OAUTH2_TOKEN_FILE" short-urls create https://news.google.com -s gnews
./nkshlink short-urls resolve gnews
./nkshlink short-urls qr-resolve gnews
./nkshlink --access-token-file "$OAUTH2_TOKEN_FILE" short-urls edit -s gnews -l https://news.google.com -t google -t news -t ps
./nkshlink --access-token-file "$OAUTH2_TOKEN_FILE" short-urls create https://google.com -s gsearch -t google
./nkshlink --api-key federicomestrone short-urls list
./nkshlink --api-key federicomestrone short-urls create https://news.google.com -s ednknews -d ednk.link
./nkshlink --access-token-file "$OAUTH2_TOKEN_FILE" short-urls create https://news.google.com -s nksfnews -d nksf.link
./nkshlink --access-token-file "$OAUTH2_TOKEN_FILE" short-urls create https://news.google.com -s nks -d nksf.link
./nkshlink --access-token-file "$OAUTH2_TOKEN_FILE" short-urls delete --id 1
./nkshlink --api-key federicomestrone short-urls list

./nkshlink --access-token-file "$OAUTH2_TOKEN_FILE" tags list
./nkshlink --access-token-file "$OAUTH2_TOKEN_FILE" tags rename ps pluralsight
./nkshlink --access-token-file "$OAUTH2_TOKEN_FILE" tags describe pluralsight "Links used in PluralSight courses"
./nkshlink --access-token-file "$OAUTH2_TOKEN_FILE" tags create misc --desc "A generic tag that I don't need"
./nkshlink --api-key federicomestrone tags list
./nkshlink --access-token-file "$OAUTH2_TOKEN_FILE" tags delete misc
./nkshlink --api-key federicomestrone tags list

./nkshlink --api-key federicomestrone short-urls resolve gsearch
./nkshlink --api-key federicomestrone short-urls qr-resolve gsearch
./nkshlink --access-token-file "$OAUTH2_TOKEN_FILE" tags list --stats
./nkshlink --access-token-file "$OAUTH2_TOKEN_FILE" visits list
