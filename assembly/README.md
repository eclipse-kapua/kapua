## Docker

The section describes how Eclipse Kapua docker images can be used.

### Build

To learn how to build Kapua Docker images, please consult [developer manual](https://github.com/eclipse/kapua/blob/develop/docs/developer-guide/en/running.md#docker-containers).

### Run

To learn how to run Kapua in Docker, please consult [developer manual](https://github.com/eclipse/kapua/blob/c5b2617594d261cec7da50352ad25aafd0faf164/docs/developer-guide/en/building.md#docker-images).

### Access

Navigate your browser to http://localhost:8080 and log in using the following credentials:
`kapua-sys` : `kapua-password`

You can access the API using: http://localhost:8081

**Note**: If you are using Docker on Windows the hostname will most likely not be `localhost` but
the IP address of your docker instance.

### SSO (OpenID Connect) testing

**Note:** This is only a setup for testing SSO support.

The following paragraphs describe how to set up an SSO OpenID Connect Provider in Kapua via environment variables.
For further information, please see the [SSO Developer Guide](docs/developer-guide/en/sso.md).

#### Keycloak Provider

It is possible to test the sso with a Keycloak image by simply launching the `deploy` scripts located in the `deployment/docker/unix/sso` directory.
The provided Keycloak instance is already configured with a dedicated realm and client. 
However, if you prefer to manually run and configure Keycloak, please follow the instruction below.

You can also start a Keycloak instance in addition:

    docker run -td --name sso -p 8082:8080 -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=secret jboss/keycloak

Starting the `kapua-console` image with the following command line instead:

    docker run -td --name kapua-console --link sso --link kapua-sql:db --link kapua-broker:broker --link kapua-elasticsearch:es -p 8080:8080 -e KEYCLOAK_URL=http://$(docker inspect --format '{{ .NetworkSettings.IPAddress }}' sso):8080 -e KAPUA_CONSOLE_URL=http://localhost:8080 kapua/kapua-console

You will also need to create a new realm named `kapua` in the Keycloak web UI and create a new client called `console`, 
assigning `http://localhost:8080/*` as a valid redirect URI.

To use the Keycloak provider with the Kapua Console, the following environment variables must be provided:

- `KAPUA_CONSOLE_URL` : the `kapua-console` URL;
- `KEYCLOAK_URL` : the URL of the Keycloak instance;
- `KEYCLOAK_REALM` : the keycloak realm (the default value is `kapua`);
- `KEYCLOAK_CLIENT_ID` : the client id in the keycloak realm (the default value is `console`);
- `KAPUA_OPENID_CLIENT_SECRET` : the client secret (optional).

#### Generic Provider

It is also possible to use a generic OpenID Connect provider, by providing to the console the following environment 
variables:

- `KAPUA_CONSOLE_URL` : the `kapua-console` URL;
- `KAPUA_OPENID_JWT_ISSUER` : the base URL to the OpenID Connect server provider;
- `KAPUA_OPENID_JWT_AUDIENCE` : the JWT audience (the default value is `console`);
- `KAPUA_OPENID_CLIENT_ID` : the client id (the default value is `console`);
- `KAPUA_OPENID_CLIENT_SECRET` : the client secret (optional);
- `KAPUA_OPENID_AUTH_ENDPOINT` : the endpoint URL to the authentication API (optional, already retrieved via well-known document);
- `KAPUA_OPENID_TOKEN_ENDPOINT` : the endpoint URL to the token API (optional, already retrieved via well-known document);
- `KAPUA_OPENID_LOGOUT_ENDPOINT` : the URL to the logout endpoint (optional, already retrieved via well-known document).

Note that `OPENID_CLIENT_ID` and `JWT_AUDIENCE` are usually mapped with the same value,
(see the [SSO Developer Guide](docs/developer-guide/en/sso.md) for further information).

### Tomcat images

It is also possible to use Tomcat as a web container. For this use the following run commands instead:

    docker run -td --name kapua-console --link kapua-sql:db --link kapua-broker:broker --link kapua-elasticsearch:es -p 8080:8080 eclipsekapua/kapua-console
    docker run -td --name kapua-api --link kapua-sql:db --link kapua-broker:broker --link kapua-elasticsearch:es -p 8081:8080 eclipsekapua/kapua-api

Please note that in this case you also have to append `/admin` and `/api` to the URL.

