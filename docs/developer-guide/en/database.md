# Working with Kapua database

In current implementation of Kapua we hold device data in SQL database (H2 SQL Server). 
Kapua is also compatible with other DBMS that can be setup by providing the correct JDBC Driver and correct JDBC URL String.

In order to keep SQL schema updated, we decided to use [Liquibase](https://www.liquibase.org/).

## Liquibase in Kapua

Kapua REST APIs, Admin Console and Broker can use, upon their startup, the embedded Liquibase client which ensures that all database update scripts have been collected and
applied to your database. The coordinates of database URL connection are obtained from regular Kapua settings i.e. the following
system properties:

- `commons.db.username`
- `commons.db.password`
- `commons.db.schema`

Please note that the scripts execution is disabled by default. To enable it, set the system property `commons.db.schema.update` to true when running any of the applications.

Liquibase clients looks up for xml scripts located in the classpath matching `liquibase/(*-master.pre.xml|*-master.xml|*-master.post.xml)` pattern. 

Such files will be collected all together from the plugin, then sorted in three distinct sets: all the `*-master.pre.xml` files first, then the `*-master.xml` files and finally `*-master.post.xml` files. 
Every set will be sorted by path name, then the three sets will then be executed in the same same order (the `master.pre` first, then the `master` set and finally the `master.post` set.)

Every master file must contain at least one reference (via the `<include>` Liquibase XML tag) to a version-specific changelog file, usually contained in a folder named after the service version. 
Such changelog will, in turn, contain references to the actual Liquibase XML files that gets executed by the Liquibase client.

### Adding new XML script to the project

If you would like a new Liquibase script to your service, just add new `*.xml` file to `src/main/resources/liquibase/_service-version_` directory of your Maven project. 
Just keep in mind that the name of your file should be unique, so the best way is to at least reference the name of your service module (for example `device-new-creation-script.xml` for device management). 
Also, after you create the XML, be sure to add a reference to it in the main changelog XML for the version. 
If such file doesn't exist yet, because it's releated to a new version of the service, create it and add a reference to it in an appropriate master XML file.

More details regarding Liquibase XML file syntax, can be found on [Liquibase web page](http://www.liquibase.org/documentation/xml_format.html).

## Adding support for different DBMS inside the code-base

Currently, using Docker, Kapua supports H2 by default but also MariaDB and MySQL, providing the correct deployment options (see readme file under the deployment section).
These are the steps you need to follow to be able to assembly your db image and deploy it with Kapua:

1) Under the "kapua-assembly-sql" module, provide a new descriptor, dockerfile and entrypoint script for the DMBS and configure a maven profile to build the corresponding docker image
2) Provide the dependency for the correct JDBC class, both in root pom file and the module of containers that connect to the db (let the position of the dependency of mariadb-java-client guide you)
3) Some liquibase scripts could be not fully compatible with the new DBMS, or could be the case that new scripts have to be inserted to adapt to nuances of this new DBMS. See for example the liquibase changeSet with id="device_alter-value-clob-to-longtext" placed under _service/device/registry/internal/src/main/resources/liquibase/2.1.0/device-extended-properties-lob.xml_
4) Under the "kapua-docker" deployment module, modify the "docker-deploy.sh" script to insert a new deployment option for the new dbms, along with the compose file for it (that sets env variables needed for the dbms docker container). See how this has been done for mariadb/mysql to guide you in this operation.

### Running QA with the new DBMS

The QA code-base adapts to different types of DBMS and this is accomplished separating the details of it under the abstraction _org.eclipse.kapua.qa.common.dbms.DbmsSpecifics_.
There is one implementation of this interface for each supported DBMS, that specifies the details of it. The needed implementation is then properly "injected" in the qa code with the _DbmsSpecificsFactory_
Having supported a new DBMS, running QA with it gives more assurance about correct operations in a production context. These are the steps to take:

1) Create a new implementation of _DbmsSpecifics_
2) Modify _DbmsSpecificsFactory_ to provide it
3) Set the property _org.eclipse.kapua.qa.dbms_ accordingly and run the test or set of tests you need, they will automatically switch from H2 to the needed DBMS



