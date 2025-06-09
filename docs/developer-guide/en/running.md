# Running

Kapua can be run in a few different ways. See the following chapters for more information
about how to start and run
Kapua. Keep in mind that if you want to deploy a released version, not building anything,
you can follow
the [Kapua quick start guide](https://github.com/eclipse/kapua/blob/develop/README.md).
This section is focused on running your custom build.

{% hint style='info' %} Most of the following descriptions focus on a developer centric
setup and not on a full blown
production setup. {% endhint %}

## Docker containers

Before running Kapua on Docker, Make sure that you have built Kapua docker images locally,
following the provided [building section](building.md).

### Requirements

Same as the ones showed in the [building section](building.md) plus

* Docker compose V2, starting from Kapua releases >= 1.6.12 (present in latest docker
  desktop versions)

If you previously set the IMAGE_VERSION environment variable to a released version, please
now unset it. In this way, latest built Kapua docker images will be deployed.

Now, you can start Kapua by using Docker Compose. To do so, run

    kapua/deployment/docker/docker-deploy.sh

After Kapua has been started, you can navigate your browser to http://localhost:8080 and
log in using the following
credentials:
`kapua-sys` : `kapua-password`

You can access the API using: http://localhost:8081

**Note**: If you are using Docker on Windows the hostname will most likely not be
`localhost` but the IP address of your
docker instance.

To stop Kapua, run

    kapua/deployment/docker/docker-undeploy.sh

#### Advanced options

It's possible to deploy kapua with some advanced options that are
exposed [in this document](deployment/docker/README.md)

If you are interested in having debugging/logging capabilities we recommend you to consult
it.

## OpenShift

OpenShift is a PaaS (Platform As A Service) platform based on Kubernetes. Kapua supports
deployments
into [OpenShift Origin](https://www.openshift.org), which is an open source community
project. Origin sources, can be
found [here](https://www.openshift.org/). We support Kapua on OpenShift Origin **1.4.1**.

Currently we support running OpenShift only on Linux OS. If you would like to run Kapua on
Mac OS or on Windows for
development purposes, please install Linux
(for example Fedora 25) into a virtual machine (for example VirtualBox) and install
OpenShift there.

### Starting OpenShift cluster

For running Kapua on an OpenShift you need to have OpenShift cluster installed and started
in the first place. You can
install it by yourself or rely on the script we provide:

    sudo kapua/deployment/openshift/openshift-start.sh

If you are running your OpenShift cluster for the first time, execute the following
initialized script as well:

    kapua/deployment/openshift/openshift-initialize.sh

Initialization script is responsible for logging you into a cluster and creating new
OpenShift project for Kapua.

If your Openshift cluster is not on the localhost, set the `OPENSHIFT_HOST` environment
variable. For example, something
like

    export OPENSHIFT_HOST=192.168.64.2:8443

If for some reasons, you cannot start your cluster, try to execute the startup script with
option `DOCKERIZED=FALSE`:

    sudo DOCKERIZED=FALSE kapua/deployment/openshift/openshift-start.sh

Option `DOCKERIZED=FALSE` tells startup script to use standard binary installation of
OpenShift Origin instead of
Docker-based `oc cluster up` command.

### Deploying Kapua into OpenShift cluster

Now when you have OpenShift cluster up, running and initialized, execute the following
script:

    cd kapua/deployment/openshift
    ./openshift-deploy.sh

Now open the following URL in your web browser - `http://localhost:8080`. And log-in into
Kapua UI using default
credentials:

<dl>
	<dt>username</dt><dd>kapua-sys</dd>
	<dt>password</dt><dd>kapua-password</dd>
</dl>

## Using Minishift

Minishift is a tool that helps you run OpenShift locally by running a single-node
OpenShift cluster inside a VM.
Follow [this guide](https://docs.openshift.org/latest/minishift/getting-started/index.html)
for installing and having
Minishift up and running.

Steps to run Kapua on Minishift are the following

1. Start Minishift (make sure you have enough memory and cpu resources for your cluster)

    ~~~bash
    minishift start --memory 8GB --cpus 4
    ~~~

2. Export Minishift docker and oc tools

    ~~~bash
    eval $(minishift docker-env)
    eval $(minishift oc-env)
    ~~~

3. Export address of the cluster

    ~~~bash
    export OPENSHIFT_HOST=$(minishift ip):8443
    ~~~

4. Initialize Kapua project

    ~~~bash
    kapua/deployment/openshift/openshift-initialize.sh
    ~~~

5. Deploy Kapua components

    ~~~bash
    cd kapua/deployment/openshift
    ./openshift-deploy.sh
    ~~~

6. Visit Minishift console

    ~~~bash
    minishift dashboard
    ~~~

## Advanced OpenShift configuration

### External Node port for MQTT

The default setup uses port 31883 to export the MQTT over TCP port of the broker. This
means that you can connect from
outside of the OpenShift cluster to Kapua over port 31883 (instead of port 1883) to Kapua.

However it is only possible for one service to make use of this port. If you are planning
to add a second Kapua
installation and still want to use the external node port, then you will need to choose a
different, yet unsed, port.

Also
see: https://docs.openshift.com/container-platform/latest/dev_guide/getting_traffic_into_cluster.html

### Adding metrics

If you have enabled metrics support in OpenShift (e.g. with `oc cluster up --metrics`)
then you can also install Grafana for Hawkular to visualize your data:

    oc new-app -f https://raw.githubusercontent.com/hawkular/hawkular-grafana-datasource/master/docker/openshift/openshift-template-ephemeral.yaml

### External access

In order to enable devices to access Kapua we need to allow external access to the
broker's MQTT connector. In the
default deployment there are two ways to achieve this.

First, the broker exposes MQTT over WebSocket transport. As WebSocket is based on HTTP we
can define a router inside the
Openshift to get those device connections to the broker. For example, if your Openshift
deployment is running at the
address `192.168.64.2`, you can connect the [Kura Simulator](../user-manual/simulator.md)
like this

    java -jar target/kapua-simulator-kura-*-SNAPSHOT-app.jar --broker ws://kapua-broker:kapua-password@broker-eclipse-kapua.192.168.64.2.nip.io:80

Not all MQTT clients have WebSocket support, so we need to enable direct MQTT over TCP
access to the broker as well. By
default, Kapua comes with the NodePort service that routes all traffic from port `31883`
to the broker. So you can
connect your MQTT clients directly to this service. For the simulator example similar to
the above, that would look
something like

    java -jar target/kapua-simulator-kura-2.1.0-FALSE_EXTENSIONS-SNAPSHOT-app.jar --broker tcp://kapua-broker:kapua-password@192.168.64.2:31883

This is suitable only for the local deployments. In the cloud or production environments,
you should deploy a proper
LoadBalancer Openshift service to enable external traffic flow to the broker.

### Ensuring enough entropy

It may happen that firing up docker containers and starting up application which use
Java's `SecureRandom` (which
happens in the next step a few times) run dry the Linux Kernel's entropy pool. The result
is that some application will
block during startup
(even longer than 30 seconds) which will trigger OpenShift to kill the pods since they are
considered unresponsive (
which they actually are).

You can check the amount of entropy the kernel has available with the following command:

     cat /proc/sys/kernel/random/entropy_avail

If this number drops to zero, then the kernel has run out of entropy and application will
block.

One solution (there are a few others) is to install `haveged` a user-space daemon which
provides entropy to the kernel.

On CentOS 7 it can be installed with the following commands (all as `root`):

     yum install epel-release # only if you 
     yum install haveged
     systemctl enable --now haveged

As the package comes from
the [EPEL repositories](https://fedoraproject.org/wiki/EPEL "Information about EPEL"). If
you
haven't yet enabled those repositories, then you need to do this before trying to install
`haveged`:

     yum install epel-release

For more information about `haveged` see http://www.issihosts.com/haveged/

For more information about the "EPEL repositories" see https://fedoraproject.org/wiki/EPEL