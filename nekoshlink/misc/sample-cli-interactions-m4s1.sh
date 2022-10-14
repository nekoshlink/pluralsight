# CLI Sample Interactions M4S1

# A set of NekoShlink CLI commands that try out different features of the server
# to make sure everything works correctly and data is properly stored in the DB.

# This version has CLI security with username and password from a JPA custom user repository
# PluralSight Module 4 (Step1)

# assemble command line first with `gradle assemble`!

# Run once by uncommenting lines 20 and 24, and get the password for the admin user on line 15
# Then run the tests by commenting lines 20 and 24 again

# remember to escape any $ signs if present!
PASSWORD=0R\$8z6ntn

set -v
cd ..

#rm -f data/nekoshlink*

./nkshlink --usr nekoadm --pwd "$PASSWORD" domains list

#exit

./nkshlink --usr nekoadm --pwd "$PASSWORD" domains create https://nksf.link
./nkshlink --usr nekoadm --pwd "$PASSWORD" domains delete https://nksf.link
./nkshlink --usr nekoadm --pwd "$PASSWORD" domains create --scheme https nksf.link
./nkshlink --usr nekoadm --pwd "$PASSWORD" domains create --scheme https ednk.link
./nkshlink --usr nekoadm --pwd "$PASSWORD" domains list
./nkshlink --usr nekoadm --pwd "$PASSWORD" domains edit --scheme http ednk.link
./nkshlink --usr nekoadm --pwd "$PASSWORD" domains default ednk.link
./nkshlink --usr nekoadm --pwd "$PASSWORD" domains list
./nkshlink --usr nekoadm --pwd "$PASSWORD" domains default localhost:8443

./nkshlink --usr nekoadm --pwd "$PASSWORD" short-urls create https://google.com
./nkshlink --usr nekoadm --pwd "$PASSWORD" short-urls create https://news.google.com -s gnews
./nkshlink short-urls resolve gnews
./nkshlink short-urls qr-resolve gnews
./nkshlink --usr nekoadm --pwd "$PASSWORD" short-urls edit -s gnews -l https://news.google.com -t google -t news -t ps
./nkshlink --usr nekoadm --pwd "$PASSWORD" short-urls create https://google.com -s gsearch -t google
./nkshlink --api-key federicomestrone short-urls list
./nkshlink --usr nekoadm --pwd "$PASSWORD" short-urls delete --id 1
./nkshlink --api-key federicomestrone short-urls create https://news.google.com -s ednknews -d ednk.link
./nkshlink --usr nekoadm --pwd "$PASSWORD" short-urls create https://news.google.com -s nksfnews -d nksf.link
./nkshlink --api-key federicomestrone short-urls create https://news.google.com -s nks -d nksf.link
./nkshlink --api-key federicomestrone short-urls list

./nkshlink --usr nekoadm --pwd "$PASSWORD" tags list
./nkshlink --usr nekoadm --pwd "$PASSWORD" tags rename ps pluralsight
./nkshlink --usr nekoadm --pwd "$PASSWORD" tags describe pluralsight "Links used in PluralSight courses"
./nkshlink --usr nekoadm --pwd "$PASSWORD" tags create misc --desc "A generic tag that I don't need"
./nkshlink --api-key federicomestrone tags list
./nkshlink --usr nekoadm --pwd "$PASSWORD" tags delete misc
./nkshlink --api-key federicomestrone tags list

./nkshlink --api-key federicomestrone short-urls resolve gsearch
./nkshlink --api-key federicomestrone short-urls qr-resolve gsearch
./nkshlink --usr nekoadm --pwd "$PASSWORD" tags list --stats
./nkshlink --usr nekoadm --pwd "$PASSWORD" visits list
