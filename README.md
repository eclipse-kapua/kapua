![Eclipse Kapua™ logo](docs/user-manual/en/images/kapua-logo.png)

# Eclipse Kapua&trade;

![GitHub Release](https://img.shields.io/github/v/release/eclipse/kapua?label=Release)
![GitHub](https://img.shields.io/github/license/eclipse/kapua?label=License)

![GitHub Issues](https://img.shields.io/github/issues-raw/eclipse/kapua?label=Open%20Issues)
![GitHub Pull Requests](https://img.shields.io/github/issues-pr/eclipse/kapua?label=Pull%20Requests&color=blue)
![GitHub Contributors](https://img.shields.io/github/contributors/eclipse/kapua?label=Contributors)
![GitHub Forks](https://img.shields.io/github/forks/eclipse/kapua?label=Forks)

![Gitter](https://img.shields.io/gitter/room/eclipse/kapua?label=Chat&logo=gitter)

![GitHub Actions CI](https://img.shields.io/github/actions/workflow/status/eclipse/kapua/kapua-ci.yaml) <br/>

[Eclipse Kapua&trade;](http://eclipse.org/kapua) is a modular platform providing the
services required to manage IoT gateways and smart edge devices. Kapua provides a core
integration framework and an
initial set of core IoT services including a device registry, device management services,
messaging services, data management, and application enablement.

## Quick Start Guide

Running an Eclipse Kapua&trade; demo instance on your local machine is a simple task.

Eclipse Kapua&trade; runs as distributed application that exposes three basic services:

* The Messaging Service
* The RESTful API
* The Web Administration Console

Two more backend services are required that implement the data tier:

* The Event Bus Service
* The SQL database
* The NoSQL datastore

Eclipse Kapua&trade; can be deployed in a variety of modes. A practical way for running a
local demo is through Docker containers.

### Requirements

Before starting, check that your environment has the following prerequisites:

* 64 bit architecture
* Docker Version 1.2+
    * Docker compose V2 for release >= 1.6.12 (present in latest docker desktop versions)
* Internet Access (needed to download the artifacts)

### Demo Setup

The team maintains some docker images in a Docker Hub repository
at [Kapua Repository](https://hub.docker.com/r/kapua/). Check the repo to view the list of
available images, if you haven't found one
fitting your needs you may create your own. Please refer to the
paragraph [More deployment info](#more-deployment-info) to find more about creating your
own images and/or alternative demo deployment
scenarios.

***
**Note:** the Docker Hub repository mentioned above is not the official project repository
from Eclipse Foundation.
***

Suppose the target is the released version V (for example, 1.6.7).

* Clone Eclipse Kapua&trade; into a local directory
* Open an OS shell and move to Kapua project root directory
* Start Docker runtime
* Perform a checkout to the related git tag for version V. For the proposed example, the
  command is:

  git checkout 1.6.7

* Set the `IMAGE_VERSION` environment variable with a value equal to the target version.
  For the proposed example:

  export IMAGE_VERSION=1.6.7

The repository mentioned above hosts only images of released versions. If your target is a
SNAPSHOT version, a local build is required
in order to create the docker images and proceed to the next
step. [Here you can found instructions for building](docs/developer-guide/en/building.md)

***
**A note regarding Docker Compose:** starting from Kapua 1.6.12, the project migrated to
Docker Compose V2. This means that,
depending on your terget version V, your Docker Desktop version should be aligned
accordingly in order to smoothly deploy the application. If you are trying to deploy a
version BEFORE 1.6.12, make sure to have a Docker Desktop version released before july

2023. Otherwise, the latest should be fine.**

***

* Start Kapua:

On Linux/MacOS:

```bash
    ./deployment/docker/unix/docker-deploy.sh
```

On Windows (PowerShell):

```bash
    ./deployment/docker/win/docker-deploy.ps1
```

The command starts all the Kapua containers using Docker Compose.

You can check if the containers are running by typing the following command:

```bash
    docker ps -as
```

Docker will list the containers currently running.

To stop Kapua, run

On Linux/MacOS:

```bash
    ./deployment/docker/unix/docker-undeploy.sh
```

On Windows (PowerShell):

```bash
    ./deployment/docker/win/docker-undeploy.ps1
```

### Access

Once the containers are running, the Kapua services can be accessed. Eclipse Kapua&trade;
is a multi tenant
system. The demo installation comes with one default tenant, called _kapua-sys_, which is
also the root tenant.
In Eclipse Kapua&trade; a _tenant_ is commonly referred to as an _account_.

#### The console

The administration console is available
at [http://localhost:8080/](http://localhost:8080/).
Copy paste the URL above to a Web browser, as the login screen appears, type the following
credentials:

* Username: `kapua-sys`
* Password: `kapua-password`

Press _Login_ button and start working with the console.

**Note**: If you are using Docker on Windows the hostname will most likely not be
`localhost` but
the IP address of your docker instance.

#### RESTful APIs

The documentation of RESTful API is available
at [http://localhost:8081/doc/](http://localhost:8081/doc/) while the mount points are
available at [http://localhost:8081/v1/](http://localhost:8081/v1/)
.

The documentation is available through Swagger UI which greatly helps testing and
exploring the exposed services.

In order to get access a REST resource through an API, an authentication token is needed.
Fortunately the token can be easily obtained by executing the authentication API. There
are several ways to
invoke the API, an easy one is by using the Swagger UI:

* Open the URL http://localhost:8081/doc/
* Select item _Authentication_
* Select item _/authentication/user_
* Using the test feature run 'POST /authentication/user' by specifying the following body:

```json
{
  "password": [
    "kapua-password"
  ],
  "username": "kapua-sys"
}
```

**Note:** as an alternative to the previous, if curl is available on your machine, execute
the following from the shell:

```bash
curl -X POST  'http://localhost:8081/v1/authentication/user' --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{
  "password": [
    "kapua-password"
  ],
  "username": "kapua-sys"
}'
```

The system will return a JSON object.

* Copy the value of the field _tokenId_
* Paste the value in the box labelled _api\_key_ at the top of the web page

Swagger will automatically add the authentication token to each subsequent request done
using the Swagger UI. You're ready to try executing the documented APIs.

**Note**: If you are using Docker on Windows the hostname will most likely not be
`localhost` but
the IP address of your docker instance.

#### The Broker

The broker container exposes an [Mqtt](http://mqtt.org/) end point at tcp://localhost:
1883/.
The broker can be accessed through
either [Eclipse Kura&trade;](http://www.eclipse.org/kura/) or a plain Mqtt client like,
for example, [Eclipse Paho&trade;](http://www.eclipse.org/paho/).

In order for a client to establish a Mqtt connection with the broker, a client must
provide a valid identity. The _kapua-sys_ account provides the user named _kapua-broker_
which has been pre-seeded
and profiled for the purpose.

The credentials for the user kapua-broker are the following:

* Username: `kapua-broker`
* Password: `kapua-password`

**Note:** do not use the user `kapua-sys` to establish Mqtt connections.

**Note**: If you are using Docker on Windows the hostname will most likely not be
`localhost` but
the IP address of your docker instance.

### Connecting Kura to Kapua

[We provide a quick start guide for the connection of Kura to Kapua](docs/kuraKapuaDocs.md)

#### Simulation

Kapua comes with a framework that you can use to simulate Kura gateways. It can be used to
test your Kapua deployments easily.
See [Simulator documentation](docs/user-manual/en/simulator.md) for more
info.

#### More deployment info

Installing and running a demo using Docker is easy, but it's not the only way. There are
other scenarios that the users may be interested in. We provide advanced setup scenarios
in the following
guides:

* [Running with Docker](deployment/docker/README.md)
* [Running with OpenShift](docs/developer-guide/en/running.md#openshift)
* [Running with Minishift](docs/developer-guide/en/running.md#minishift)

They will provide more advanced deployment scenarios. For example, if you are interested
in having debugging/logging capabilities we recommend you to consult them.

### User & Developer guides

* [User Manual](docs/user-manual/en/README.md)
* [Developer Guide](docs/developer-guide/en/README.md)

### Contributing

If you're interested to get involved in IoT and Eclipse Kapua&trade; project, join the
community and give your contribution to the project, please
read [how to contribute to Eclipse Kapua&trade;](https://github.com/eclipse/kapua/blob/develop/CONTRIBUTING.md).

### Community

- [Eclipse Kapua&trade; Gitter Room](https://gitter.im/eclipse/kapua)
- [Eclipse Kapua&trade; Forum](https://www.eclipse.org/forums/index.php?t=thread&frm_id=340)

### Acknowledgments

![](https://www.yourkit.com/images/yklogo.png)

Thanks to YourKit for providing us an open source license of YourKit Java Profiler!

YourKit supports open source projects with innovative and intelligent tools
for monitoring and profiling Java and .NET applications.
YourKit is the creator of [YourKit Java Profiler](https://www.yourkit.com/java/profiler/),
[YourKit .NET Profiler](https://www.yourkit.com/.net/profiler/),
and [YourKit YouMonitor](https://www.yourkit.com/youmonitor/).
