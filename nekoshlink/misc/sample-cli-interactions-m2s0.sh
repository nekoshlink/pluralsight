# CLI Sample Interactions M2S0

# A set of NekoShlink CLI commands that try out different features of the server
# to make sure everything works correctly and data is properly stored in the DB.

# This version has no security
# PluralSight Module 2 (Start)

# assemble command line first with `gradle assemble`!

set -v
cd ..

rm -f data/nekoshlink*

./nkshlink domains list
./nkshlink domains create https://nksf.link
./nkshlink domains delete https://nksf.link
./nkshlink domains create --scheme https nksf.link
./nkshlink domains create --scheme https ednk.link
./nkshlink domains list
./nkshlink domains edit --scheme http ednk.link
./nkshlink domains default ednk.link
./nkshlink domains list
./nkshlink domains default localhost:8080

./nkshlink short-urls create https://google.com
./nkshlink short-urls create https://news.google.com -s gnews
./nkshlink short-urls resolve gnews
./nkshlink short-urls qr-resolve gnews
./nkshlink short-urls edit -s gnews -l https://news.google.com -t google -t news -t ps
./nkshlink short-urls create https://google.com -s gsearch -t google
./nkshlink short-urls list
./nkshlink short-urls delete --id 1
./nkshlink short-urls create https://news.google.com -s ednknews -d ednk.link
./nkshlink short-urls create https://news.google.com -s nksfnews -d nksf.link
./nkshlink short-urls create https://news.google.com -s nks -d nksf.link
./nkshlink short-urls list

./nkshlink tags list
./nkshlink tags rename ps pluralsight
./nkshlink tags describe pluralsight "Links used in PluralSight courses"
./nkshlink tags create misc --desc "A generic tag that I don't need"
./nkshlink tags list
./nkshlink tags delete misc
./nkshlink tags list

./nkshlink short-urls resolve gsearch
./nkshlink short-urls qr-resolve gsearch
./nkshlink tags list --stats
./nkshlink visits list
