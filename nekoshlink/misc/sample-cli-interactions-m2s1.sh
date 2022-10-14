# CLI Sample Interactions M2S1

# A set of NekoShlink CLI commands that try out different features of the server
# to make sure everything works correctly and data is properly stored in the DB.

# This version has CLI security with username and password from an in-memory hard-coded userbase
# PluralSight Module 2 (Step1)

# assemble command line first with `gradle assemble`!

set -v
cd ..

rm -f data/nekoshlink*

./nkshlink --usr admin --pwd password1 domains list
./nkshlink --usr admin --pwd password1 domains create https://nksf.link
./nkshlink --usr admin --pwd password1 domains delete https://nksf.link
./nkshlink --usr admin --pwd password1 domains create --scheme https nksf.link
./nkshlink --usr admin --pwd password1 domains create --scheme https ednk.link
./nkshlink --usr admin --pwd password1 domains list
./nkshlink --usr admin --pwd password1 domains edit --scheme http ednk.link
./nkshlink --usr admin --pwd password1 domains default ednk.link
./nkshlink --usr admin --pwd password1 domains list
./nkshlink --usr admin --pwd password1 domains default localhost:8080

./nkshlink --api-key federicomestrone short-urls create https://google.com
./nkshlink --api-key federicomestrone short-urls create https://news.google.com -s gnews
./nkshlink short-urls resolve gnews
./nkshlink short-urls qr-resolve gnews
./nkshlink --api-key federicomestrone short-urls edit -s gnews -l https://news.google.com -t google -t news -t ps
./nkshlink --api-key federicomestrone short-urls create https://google.com -s gsearch -t google
./nkshlink --api-key federicomestrone short-urls list
./nkshlink --api-key federicomestrone short-urls delete --id 1
./nkshlink --api-key federicomestrone short-urls create https://news.google.com -s ednknews -d ednk.link
./nkshlink --usr admin --pwd password1 short-urls create https://news.google.com -s nksfnews -d nksf.link
./nkshlink --api-key federicomestrone short-urls create https://news.google.com -s nks -d nksf.link
./nkshlink --api-key federicomestrone short-urls list

./nkshlink --api-key federicomestrone tags list
./nkshlink --api-key federicomestrone tags rename ps pluralsight
./nkshlink --api-key federicomestrone tags describe pluralsight "Links used in PluralSight courses"
./nkshlink --api-key federicomestrone tags create misc --desc "A generic tag that I don't need"
./nkshlink --api-key federicomestrone tags list
./nkshlink --api-key federicomestrone tags delete misc
./nkshlink --api-key federicomestrone tags list

./nkshlink --api-key federicomestrone short-urls resolve gsearch
./nkshlink --api-key federicomestrone short-urls qr-resolve gsearch
./nkshlink --api-key federicomestrone tags list --stats
./nkshlink --usr admin --pwd password1 visits list
