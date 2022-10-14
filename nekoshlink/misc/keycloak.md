# Installing KeyCloak

Check out the official documentation for KeyCloak to install the product on your own servers

https://www.keycloak.org/guides#getting-started

# Configuring KeyCloak for NekoShlink OAuth2 support

## KeyCloak v19

These steps have been tested on KeyCloak v19

- Create a new Realm called `nekoshlink`
- In the new `nekoshlink` realm, create a new Client
    - Make sure OpenID Connect is selected as a type and give an ID of `nekoshlink`
    - In the second page, enable client authentication and make sure "Standard flow" is selected
    - Nothing else is needed here, you can save the new Client
- Once saved, in the new Client configuration pages configure it as follows:
    - In the "General Settings", add a name and description of your choice (they will appear in the consent page)
    - In the "General Settings", add valid redirect URIs
        - `https://oidcdebugger.com/debug`
        - `http://localhost:3000/*`
    - And post logout valid redirect URIs
        - `+` (means: use the same as the valid redirect URIs)
    - And also add web origins
        - `http://localhost:3000` (NB no trailing slash)
    - In the "Login Settings", enable consent ("consent required") but not client consent ("display client on screen")
    - Save the configuration again, just in case
- Switch to the "Credentials" tab
    - Check that Client ID and Secret is selected
    - The Cliend ID in the previous tab and the secret you find here are the information you need when setting up clients
- Switch to the Keys tab
    - Generate new keys
        - Select the PKCS12 format
        - Type a Key Alias which is the same as the Client ID `nekoshlink`
        - Use `Passw0rd` for both passwords
- Switch to the Roles tab and create the following roles
    - Admin	Can perform any operation on NekoShlink
    - User	Can perform basic operations on NekoShlink, but has no access to admin functionality, such as managing domains
    - Anyone	A role assigned to unauthenticated parties - it represents the anonymous user and can only access open endpoints
- Click on the Client Scopes menu item on the left
    - Create the new `nekoshlink:manage` Client Scope
        - You can give it a description if you want
        - Leave the type as "None"
        - Make sure the protocol is "OpenID Connect"
        - Save the new scope to create it
    - in the new Client Scope configuration, switch to the "Mappers" tab and configure a new mapper
        - Choose the User Client Role type
        - Name the mapper `NekoShlink Roles`
        - Select the `nekoshlink` Client ID
        - Leave Multivalue set to On
        - For the token claim name, type `nkshlink-roles`
        - Leave the other settings to On and save
    - Back in the `nekoshlink` Client configuration
        - In the Client Scopes tab
            - Add the new client scope `nekoshlink:manage` to the Client as Optional
            - Remove the `roles` client scope from the list

## KeyCloak v18

These steps have been tested on KeyCloak v18

- Create a new Realm called `nekoshlink`
- In the new `nekoshlink` realm, create a new Client
    - Make sure OpenID Connect is selected as a type and give an ID of `nekoshlink`
- Once saved, in the new Client configuration pages configure it as follows:
    - add a name and description of your choice (they will appear in the consent page)
    - enable consent ("consent required") but not client consent ("display client on consent screen")
    - change access type to confidential
    - add valid redirect URIs
        - `https://oidcdebugger.com/debug`
        - `http://localhost:3000/*`
    - add web origins
        - `http://localhost:3000` (NB no trailing slash)
    - save the configuration, which will add the Credentials tab
- Switch to the "Credentials" tab
    - check that Client Id and Secret is selected
    - the cliend Id in the previous tab and the secret you find here are the information you need when setting up clients
- Switch to the "Keys" tab
    - generate new keys and certificate
        - PKCS12 format
        - alias same as client id
        - `Passw0rd` for both passwords
- Switch to the "Roles" tab and create the following roles
    - Admin	Can perform any operation on NekoShlink
    - Anyone	A role assigned to unauthenticated parties - it represents the anonymous user and can only access open endpoints
    - User	Can perform basic operations on NekoShlink, but has no access to admin functionality, such as managing domains
- In the Client Scopes section
    - create the `nekoshlink:manage` scope
    - in the new scope, create a new mapper, called `nekoshlink roles`
        - for the openid protocol, of type User Client Roles, for the nekoshlink-server client ID, with a token name claim of `nkshlink-roles`
    - then add the new scope to the Optional Client Scopes in the Client Scopes tab of the NekoShlink Server client
        - also remove the roles client scope from the default client scopes
