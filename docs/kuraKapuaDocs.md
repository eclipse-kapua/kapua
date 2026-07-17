# Kura - Kapua connection

This is a short introduction to locally connect Kura and Kapua. Although some of the info below can already be found on Kapua github project and Kura guides, this document contains everything needed for the connection. If you have any questions post them on [Eclipse Community Forums](https://www.eclipse.org/forums/index.php/f/340/), [Gitter chat room](https://gitter.im/eclipse/kapua) or join our [mailing list](https://dev.eclipse.org/mailman/listinfo/kapua-dev). In case you find any issues, [report them](https://github.com/eclipse/kapua/issues). Also if you want to contribute, [this is the place to start!](https://github.com/eclipse/kapua).

## Useful links

 - [Kapua quick start guide](https://github.com/eclipse/kapua/blob/develop/README.md)
 - [Kapua User Manual](http://download.eclipse.org/kapua/docs/develop/user-manual/en/)
 - [Kapua Developer guide](http://download.eclipse.org/kapua/docs/develop/developer-guide/en/)
 - [Eclipse Kura Documentation](http://eclipse-kura.github.io/kura/)


## Hardware Requirements

 - Raspberry Pi
   - HDMI cable and monitor*
   - Keyboard*
   - Ethernet cable
 - PC (Windows/MAC/Linux)
 * External monitor and keyboard not mandatory, if you can determine Raspberry's IP without them and then establish ssh connection from local PC. 

 ## Software Requirements (for PC)
  - 64 bit architecture 
  - Java VM Version 8 
  - Docker Version 1.2+
  - Internet Access


## Downloading and configuring Kapua

This part of the tutorial consists of several pieces. First you need to download Java VM, Docker and Kapua. You can get Java [here](https://java.com/en/download/) and Docker [here](https://docs.docker.com/engine/installation/#supported-platforms). After that follow steps bellow for downloading and building Kapua.

1. Open OS Shell (Terminal) and go to home directory.
2. Download Kapua project from [Github repository](https://github.com/eclipse/kapua.git) with command `git clone https://github.com/eclipse/kapua.git`
3. Go to Kapua folder and run command `mvn clean install -DskipTests -Pconsole,docker` which will build the project with the Web Admin console.
4. After build finishes run Docker deploy script in `deployment/docker`:
 
```
./docker-deploy.sh
```

The docker images needed will be downloaded from Docker Hub and all the containers will be started.

You can check if every container is running properly by typing the following command:

  docker ps -as

The output should be similar to this: 

```
CONTAINER ID        IMAGE                                   COMMAND                  CREATED             STATUS                     PORTS                                                                  NAMES                 SIZE
505d32a0bb9e        eclipsekapua/kapua-api                  "/home/kapua/run-j..."   4 hours ago         Created                                                                                           kapua-api             0B (virtual 180MB)
dd9852845105        eclipsekapua/kapua-console              "/home/kapua/run-c..."   4 hours ago         Up 4 hours                 0.0.0.0:8080->8080/tcp, 8778/tcp                                       kapua-console         83kB (virtual 206MB)
65c8e15ab7e9        eclipsekapua/kapua-broker               "/maven/bin/active..."   4 hours ago         Up 4 hours                 8778/tcp, 0.0.0.0:1883->1883/tcp, 0.0.0.0:61614->61614/tcp, 8883/tcp   kapua-broker          94.4kB (virtual 212MB)
b3309e145e84        elasticsearch:7.8.1                     "/docker-entrypoin..."   4 hours ago         Up 4 hours                                                                         kapua-elasticsearch   32.8kB (virtual 352MB)
e097f77f1758        eclipsekapua/kapua-sql                  "/home/kapua/run-h2"     4 hours ago         Up 4 hours                 0.0.0.0:3306->3306/tcp, 0.0.0.0:8181->8181/tcp, 8778/tcp               kapua-sql             32.8kB (virtual 86MB)
785efe9976cf        eclipsekapua/kapua-events-broker:latest "/run-artemis"           About an hour ago   Up About an hour    0.0.0.0:5672->5672/tcp                                                 compose_events-broker_1   410kB (virtual 130MB)
```

6. Kapua can be stopped by executing the script: 

```
./docker-undeploy.sh
```

7. Open web browser (Firefox or Chrome) and open the admin console at [http://localhost:8080/.](http://localhost:8080/)

If everything is OK, you should now see the login prompt. Enter username and password. 
<dl>
<dt>
<code>Username:</code>
<dd> 
kapua-sys
</dd>
</dt>
<dt>
<code>Password:</code>
<dd> 
kapua-password
</dd>
</dt>
</dl>

### Adding Account and User

Now you can login to Kapua, but we still need to create an Account and a User. This section has four steps. First one is creating a child account. 

While logged in as kapua-sys click **Child Accounts** and **Add** a user named User123.

<dl>
<dt> 
<code>Account Name: </code>
<dd>
Account123
</dd>
</dt>
<dt>
<code>Organization Name: </code>
<dd>
Account123org
</dd>
</dt>
<dt>
<code>Email: </code>
<dd>
Account123@user123.com
</dd>
</dt>
</dl>

Save the credentials with click on button **Submit**. 

Before we create a user under this account, we have to change some settings under **Settings** tab below. 
Basically every boolean setting has to be set on **true** except `TagService`. Every other setting should not be changed. 

Now click on **Users** tab and create new User with credentials: 

<dl>
<dt> 
<code>UserName: </code>
<dd>
User123
</dd>
</dt>
<dt>
<code>Password: </code>
<dd>
Kapu@12345678
</dd>
</dt>
<dt>
<code>Confirm Password: </code>
<dd>
Kapu@12345678
</dd>
</dt>
</dl>

User should be created successfully and we have to give this user some permissions. In upper right corner click **Switch Account** and select Account123. 
Go to **Users** and select User123. In lower part you have **Roles** and **Permissions** tabs. Click **Roles** and **Add**, select **admin** and submit changes. 

After that go to **Permissions** tab and click **Add**, select **ALL** in every checkbox and submit changes. 

Now we have everything we need to connect Kura to Kapua.  

## Downloading and configuring Kura

Second part of this guide describes how to configure Raspberry Pi, but you can also use BBB (BeagleBone Black) or Intel Edison [as described on Kura download page](http://www.eclipse.org/kura/downloads.php?).
Because there already exists a guide for [installing Kura on Raspberry Pi](https://eclipse-kura.github.io/kura/docs-release-5.5/getting-started/install-kura/) we will not repeat the procedure here. When you finish, come back and follow the procedure below. In this example, we will assume that Raspberry Pi has IP: 192.168.1.11 and our PC 192.168.1.10.

1. Connect Raspberry Pi and local PC to the same network
2. On PC open browser and enter Raspberry's IP (which can be obtained with **ifconifg** command in Terminal of Raspberry Pi)
3. If IP is correct, login window should appear. Enter **admin** as username and **admin** as password. 

First we will configure the Cloud service, so we will be able to connect to our Kapua; We need to fill in the data from account and user we created earlier in Kapua.

1. Go to **Cloud Service** and under **MqttDataTransport** fill the following fields: 
   - **Broker URL: mqqt:**//[IP address of local PC]:1883/, e.g. mqtt://192.168.1.10:1883/
   - **Topic.context.account-name:** Account123
   - **Username:** User123
   - **Password:** Kapu@12345678
   - **Client-id:** test1 (it has to be same as **device custom name** in **CloudService**)
2. Go to **DataService** and under **connect.auto-on-startup** select **true**. 
3. All other fields should remain unchanged. 
4. Click **connect** and Kura should connect to Kapua (go to Kapua, under **devices** and **connections** there should be listed your Raspberry Pi with name **test1**.

Kura is now fully configured and is ready to send some data. 

## Sending data to Kapua

As you can see, our **data** sections Kapua is empty. That is because we did not yet send any data to it. So in this example we will user **Example publisher** deployment package, which can be obtained from [Eclipse marketplace](https://marketplace.eclipse.org/). 

This bundle sends some data to the provided Cloud (in our case Kapua). We will use it to verify our connection. [Click here](https://marketplace.eclipse.org/content/example-publisher-eclipse-kura) to visit Example Publisher page. 

Click on download button and copy the link address (it has **.dp** extension) and paste it into **Kura -> Packages -> Install/Upgrade -> URL**. 

After clicking **Submit** package should be installed but not visible under **Services** on the left side (If it is listed, skip this step). 

Click **+** right from **search** box. Under **Factory** select **org.eclipse.kura.example.publisher.ExamplePublisher**, set a **Name** of your choice and click **Apply**. 

Example publisher should automatically start sending data to [Kapua](http://localhost:8080/), which can be verified in kura.log file or in Kapua itself (kura.log file is in on Raspberry Pi in /var/log folder).

If everything is done correctly, data should be transmitted to Kapua and accumulated in **data/metrics**. 







