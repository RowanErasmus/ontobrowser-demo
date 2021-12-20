Building and Deploying OntoBrowser
---
## System Requirements
* [Mysql](https://dev.mysql.com/downloads/mysql/) server and client version 8 (or above)
* [Mysql connector-java-jar file](https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-8.0.14.zip) - [Download]
* [Java Development Kit (JDK)](http://www.oracle.com/technetwork/java/javase/overview/) version 8 (or above) - [Download](http://www.oracle.com/technetwork/java/javase/downloads/)
* [Apache Maven](http://maven.apache.org) version 3 (or above) - [Download](http://maven.apache.org/download.cgi)
* [Graphviz](http://www.graphviz.org) version 2.28 (or above) - [Download](http://www.graphviz.org/Download.php)
* [Wildfly](http://wildfly.org) version 14.0.1 (or above) - [Download](http://wildfly.org/downloads/) 

**Note:** OntoBrowser has been extensively tested and deployed on Linux in production environments. System testing should be performed before deploying OntoBrowser on a Windows production environment.

## Database Requirements
OntoBrowser requires access to a relational database supported by Hibernate (the ORM used by Wildfly) e.g. Oracle, MySQL, PostgreSQL etc... See [Supported Databases](https://developer.jboss.org/wiki/SupportedDatabases2) for more information.



## Database setup
### Create Schema
The SQL scripts to create the OntoBrowser database schema are located in the [mysql] directory of the project. Use the corresponding DDL script for the selected database eg. create_schema_mysql.sql for Mysql database.

The following is an example on how to create the OntoBrowser schema using Mysql command line tool:

```bash
CREATE SCHEMA `Ontobrowser Schema` ;

```

See the [database design](./database_design.pdf) documentation for the more information regarding the database schema. 

### Insert initial data
The SQL DML scripts to insert initial data into the OntoBrowser database schema are located in the [mysql](../db/mysql) directory of the project. Use the corresponding MYSQL script for the selected database e.g. [insert_initial_data_mysql.sql](../db/mysql/insert_initial_data_mysql.sql) for Mysql databases

The following is an example on how to insert initial data into the OntoBrowser schema using MYSQL command line tool:

```bash
$ INSERT INTO table name(
values 
)
```

The initial data consists of:

* Creating system user/curator
* Setting up version tracking
* Defining the OBO builtin relationship types i.e. is_a, union_of, disjoint_from, part_of etc...
* Inserting common datasources of nomenclature e.g. CDISC SEND, INHAND etc...

## Wildfly Setup
### Install JDBC Driver 
As recommended by the Wildfly [documentation](https://docs.jboss.org/author/display/WFLY8/DataSource+configuration),  install the database type 4 JDBC driver by copying the jar file to the `$JBOSS_HOME/standalone/deployments` directory e.g.

```bash
cp ojdbc7.jar $JBOSS_HOME/standalone/deployments
```

### Datasource Setup
See the [DataSource Configuration](https://docs.jboss.org/author/display/WFLY8/DataSource+configuration) Wildfly documentation. The JNDI name of the datasource must be specifed as `java:jboss/datasources/ontobrowser`.

Below is an example configuration for an Mysql database (from the `$JBOSS_HOME/standalone/configuration/standalone.xml` configuration file):

```xml
<datasource jndi-name="java:jboss/datasources/ontobrowser" pool-name="ontobrowser" enabled="true" use-java-context="true">
	<connection-url>jdbc:mysql://localhost:3306/ontobrowser</connection-url>
	<driver>com.mysql</driver>
	<pool>
	      <min-pool-size>1</min-pool-size>
	      <max-pool-size>20</max-pool-size>
	</pool>
	<security>
		<user-name>root</user-name>
		 <password>root</password>
	</security>
	</datasource>
<datasource jndi-name="java:/MySqlDS" pool-name="MySqlDS">
	                    <connection-url>jdbc:mysql://localhost:3306/mysqldb</connection-url>
	                    <driver-class>com.mysql.jdbc.Driver</driver-class>
	                    <driver>mysql-connector-java-8.0.13.jar</driver>
	                 <security>
	                        <user-name>root</user-name>
	                        <password>root</password>
		        </security>
	                    <validation>
	   <valid-connection-checker class name="org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLValidConnectionChecker"/>
		<background-validation>true</background-validation>
	        <exception-sorter class-name="org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLExceptionSorter"/>
		 </validation>
		</datasource>
```
### Module setup
For module setup, system require module.xml file under module folder in jboss with mysql-connector jar file.

```
<?xml version="1.0" encoding="UTF-8"?> 
  <module xmlns="urn:jboss:module:1.1" name="com.mysql"> 
     <resources> 
         <resource-root path="mysql-connector-java-5.1.35-bin.jar"/>
     </resources> 
     <dependencies> 
         <module name="javax.api"/> 
         <module name="javax.transaction.api"/> 
     </dependencies> 
 </module>
 ```
 

### OntoBrowser Configuration
Edit the `$JBOSS_HOME/standalone/configuration/standalone.xml` configuration file and add the following XML to the `<bindings>` element of the `urn:jboss:domain:naming:2.0` subsystem configuration. Change configuration values to suit the deployment environment:

```xml
<!-- external URL for OntoBrowser web app. Check VirtualHost section in apache conf -->
<simple name="java:global/ontobrowser/url" value="http://localhost/ontobrowser/" type="java.net.URL"/>
<!-- username of system/default curator. Check the CURATOR table in the database -->
<simple name="java:global/ontobrowser/curator/system" value="SYSTEM" type="java.lang.String"/>
<!-- local filesystem path for Lucene index -->
<simple name="java:global/ontobrowser/index/path" value="${jboss.server.data.dir}/ontobrowser/index" type="java.lang.String"/>
<!-- path to dot command line program. Check local installation of Graphviz. -->
<simple name="java:global/ontobrowser/dot/path" value="/usr/bin/dot" type="java.lang.String"/>
<!-- external URI for ontologies exported in OWL format -->
<simple name="java:global/ontobrowser/export/owl/uri" value="http://localhost/ontobrowser/ontologies" type="java.net.URL"/>
<!-- boolean flag indicating if OntoBrowser is using an Oracle database -->
<simple name="java:global/ontobrowser/database/oracle" value="false" type="boolean"/>
```

See the [Naming Subsystem Configuration](https://docs.jboss.org/author/display/WFLY8/Naming+Subsystem+Configuration) Wildfly documentation for more information.

### Additional Wildfly configuration (optional)
By default Stateless Session Bean (SLSB) pooling is not enabled in Wildfly. When deploying OntoBrowser in a production environment is it recommended to enable SLSB pooling to prevent a Graphviz dot process being spawned for each request.

Edit the `$JBOSS_HOME/standalone/configuration/standalone.xml` configuration file and add the following XML to the `<session-bean>` element of the `urn:jboss:domain:ejb3:2.0` subsystem:

```xml
<stateless>
    <bean-instance-pool-ref pool-name="slsb-strict-max-pool"/>
</stateless>
```

The resulting XML configuration should be:

```xml
<subsystem xmlns="urn:jboss:domain:ejb3:2.0">
	<session-bean>
		<stateless>
			<bean-instance-pool-ref pool-name="slsb-strict-max-pool"/>
		</stateless>
		...
	</session-bean>
	...
```

By default the Hibernate secondary cache expiration *max-idle* timeout is set to 100,000 milliseconds i.e. 1 minute 40 seconds. In a production environment it is recommended to increase the expiration *max-idle* and *lifespan* timeout to more appropriate values. Note: setting timeout values to -1 disables expiration and hence the cache entries will never expire (however they can still be evicted if *max-entries* is exceeded).

The example Hibernate secondary cache configuration below (from the `$JBOSS_HOME/standalone/configuration/standalone.xml` configuration file) increases the default eviction *max-entries* value from the default of 10,000 to 100,000 and disables  expiration for both the *entity* and *local-query* caches:

```xml
<cache-container name="hibernate" default-cache="local-query" module="org.hibernate">
	<local-cache name="entity">
		<transaction mode="NON_XA"/>
		<eviction strategy="LRU" max-entries="100000"/>
		<expiration lifespan="-1" max-idle="-1"/>
	</local-cache>
	<local-cache name="local-query">
		<transaction mode="NONE"/>
		<eviction strategy="LRU" max-entries="100000"/>
		<expiration lifespan="-1" max-idle="-1"/>
	</local-cache>
	<local-cache name="timestamps">
		<transaction mode="NONE"/>
		<eviction strategy="NONE"/>
	</local-cache>
</cache-container>
```

## Apache Basic Authentication and Proxy Setup
In a production environment it is recommended to perform the user authentication using a web server (e.g. Apache) located in a [DMZ](http://en.wikipedia.org/wiki/DMZ_(computing)).  Alternatively if installing OntoBrowser on a corporate intranet it is recommended to use a corporate single sign-on (SSO) system for user authentication.

The following example Apache configuration protects the `/ontobrowser` location using [Basic access ](http://en.wikipedia.org/wiki/Basic_access_authentication) and proxies requests (using AJP) to Wildfly running on the same machine:

```xml
<Location /ontobrowser/>
    AuthType Basic
    AuthName "OntoBrowser"
    AuthBasicProvider dbd
    AuthDBDUserPWQuery "SELECT password FROM CURATOR WHERE username = %s"
    Require valid-user
    ProxyPass ajp://localhost:8009/ontobrowser/
    ProxyPassReverse ajp://localhost:8009/ontobrowser/
</Location>
```

Note: the configuration above can alternatively be defined in a `<VirtualHost>` container.

The following is an example Apache DBD configuration for an Mysql database :

```
DBDriver mysql
DBDParams host=localhost,dbname=ontobrowser,port=3306,user=root,pass=root
DBDMin  2
DBDKeep 4
DBDMax  10
DBDExptime 300
```

For more details see the Apache [mod_authn_dbd](http://httpd.apache.org/docs/2.2/mod/mod_authn_dbd.html) and [mod_dbd](http://httpd.apache.org/docs/2.2/mod/mod_dbd.html) documentation.

## Building and Deploying OntoBrowser
1. Download the project from GitHub: https://github.com/Novartis/ontobrowser/archive/master.zip
2. Unzip the master.zip file
3. Build and package the project using Maven i.e. `mvn package`
4. Copy the `ontobrowser.war` file (located in the `target` directory) to Wildfly's `deployments` directory


The following bash commands provides and example on how to perform the steps above on a Unix based operating system:

```bash
$ curl -s -S -O -L https://github.com/Novartis/ontobrowser/archive/master.zip
$ unzip master.zip
$ cd ontobrowser-master
$ mvn package
$ cp target/ontobrowser.war $JBOSS_HOME/standalone/deployments
```

Note: Configuring Maven to use a proxy maybe required if behind a corporate firewall. See the Maven documentation on [configuring proxies](http://maven.apache.org/guides/mini/guide-proxies.html).

See the Wildfly [Getting Started Guide](https://docs.jboss.org/author/display/WFLY8/Getting+Started+Guide) on how to startup Wildfly. Below is an example for Unix based operating systems:

```bash
$ cd $JBOSS_HOME/bin
$ ./standalone.sh
```

## Loading an Ontology
Ontologies can be loaded into OntoBrowser using the `/ontobrowser/ontologies` [RESTful](http://en.wikipedia.org/wiki/Representational_state_transfer) web service. The web service only supports the PUT method for loading ontologies and only accepts [OBO formatted](http://oboformat.googlecode.com/svn/trunk/doc/GO.format.obo-1_2.html) data.

The following example downloads demo ontologies, and then loads them into OntoBrowser using the web service:

```bash
$ curl -O https://raw.githubusercontent.com/nikhitajatain/ontobrowser/master/ontologies/hpath.obo
$ curl -O https://raw.githubusercontent.com/nikhitajatain/ontobrowser/master/ontologies/in-life_observation.obo
$ curl -O https://raw.githubusercontent.com/nikhitajatain/ontobrowser/master/ontologies/ma.obo
$ curl -O https://raw.githubusercontent.com/nikhitajatain/ontobrowser/master/ontologies/moa.obo

$ curl -s -S -H "Content-Type: application/obo;charset=utf-8" -X PUT --data-binary "@hpath.obo" -u SYSTEM "http://localhost:8080/ontobrowser/ontologies/hpath"
$ curl -s -S -H "Content-Type: application/obo;charset=utf-8" -X PUT --data-binary "@in-life_observation.obo" -u SYSTEM "http://localhost:8080/ontobrowser/ontologies/inlife_observation"
$ curl -s -S -H "Content-Type: application/obo;charset=utf-8" -X PUT --data-binary "@ma.obo" -u SYSTEM "http://localhost:8080/ontobrowser/ontologies/Mouse%20adult%20gross%20anatomy"
$ curl -s -S -H "Content-Type: application/obo;charset=utf-8" -X PUT --data-binary "@moa.obo" -u SYSTEM "http://localhost:8080/ontobrowser/ontologies/moa"
```
Note: This setting works with the String username = "SYSTEM" in java file.

Note: Proxy parameters or environment variables maybe be require when downloading behind a corporate firewall.


## Loading Codelist
Codelist can be loaded into OntoBrowser using the `/ontobrowser/ontologies` [RESTful](http://en.wikipedia.org/wiki/Representational_state_transfer) web service. The web service only supports the PUT method for loading codelist and only accepts [OBO formatted](http://oboformat.googlecode.com/svn/trunk/doc/GO.format.obo-1_2.html) data.

The following example downloads demo codelists, and then loads them into OntoBrowser using the web service:

```bash

$ curl -s -S -H "Content-Type: application/obo;charset=utf-8" -X PUT --data-binary "@terms_C66729.obo" -u SYSTEM "http://localhost:8080/ontobrowser/ontologies/terms_C66729"
$ curl -s -S -H "Content-Type: application/obo;charset=utf-8" -X PUT --data-binary "@terms_C66732.obo" -u SYSTEM "http://localhost:8080/ontobrowser/ontologies/terms_C66732"
$ curl -s -S -H "Content-Type: application/obo;charset=utf-8" -X PUT --data-binary "@terms_C67154.obo" -u SYSTEM "http://localhost:8080/ontobrowser/ontologies/terms_C67154"
$ curl -s -S -H "Content-Type: application/obo;charset=utf-8" -X PUT --data-binary "@terms_C77530.obo" -u SYSTEM "http://localhost:8080/ontobrowser/ontologies/terms_C77530"
$ curl -s -S -H "Content-Type: application/obo;charset=utf-8" -X PUT --data-binary "@terms_C77808.obo" -u SYSTEM "http://localhost:8080/ontobrowser/ontologies/terms_C77808"
$ curl -s -S -H "Content-Type: application/obo;charset=utf-8" -X PUT --data-binary "@terms_C85493.obo" -u SYSTEM "http://localhost:8080/ontobrowser/ontologies/terms_C85493"
```

After uploading the codelist one have to set the values of IS_CODELIST in mysql ONTOLOGY table = 1 for the particular codelist.
UPDATE [ONTOLOGY] SET [IS_CODELIST] = '[1]' WHERE [IS_CODELIST] = [0];

## Setup a Controlled Vocabulary
An example SQL script to setup a *controlled vocabulary* is provided in the [mysql](../db/mysql) directory of the project: [insert_crtld_vocab_example.sql](../db/mysql/insert_crtld_vocab_example.sql). The example defines the [SEND Specimen](http://evs.nci.nih.gov/ftp1/CDISC/SEND/SEND%20Terminology.html#CL.C77529.SPEC) code list in the database as a *controlled vocabulary* so the terms from the code list can be subsequently loaded (and then mapped to the *Mouse adult gross anatomy* ontology loaded previously).



## Loading Controlled Vocabulary Terms

The *controlled vocabulary* terms must be loaded into the `CTRLD_VOCAB_TERM` table.

## Add a Curator
The SQL DML scripts to add a curator to the OntoBrowser database schema are located in the [mysql](../db/mysql) directory of the project. Use the corresponding DML script for the selected database e.g. [insert_curator_mysql.sql](../db/mysql/insert_curator_mysql.sql) for Mysql databases.



