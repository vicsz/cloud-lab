# Cloud Lab

Cloud Native Lab - Simple Workshop demostrating Cloud-Native development with Spring Boot and Pivotal Cloud Foundry.

## Prerequisites

### Modern Java JDK Installed (alteast 1.8)

### Cloud Foundry Command Line Interface (cf CLI) installed

[https://docs.cloudfoundry.org/cf-cli/install-go-cli.html](https://docs.cloudfoundry.org/cf-cli/install-go-cli.html)

### Java IDE of choice ..

Intellij recommended .. The Community addition is free.

### Access to a Pivotal Cloud Foundry instance

Access will be provided during the workshop, or you can sign up for a free access at : https://run.pivotal.io

## 0 - Initialization
### 0.1 - Generate a Spring Boot Template from https://start.spring.io
Stick to the default settings, however update:
- artifact name to cloud-lab
- for dependencies add Web
- select Gradle
Download it, and unzip it.
### 0.2 - Import the project into your IDE.
## 1 - WebApplication with SpringBoot
### 1.1 - Implement a HelloWorld endpoint
This can be done by creating a *HelloWorldController* Java class file with:


```java
@RestController
public class HelloWorldController {

    @RequestMapping("hello")
    public String helloWorld(){
        return "Hola Mundo!";
    }
}
### 1.2 - Run the application
```sh
./gradlew bootRun
```
### 1.3 - Test the /hello endpoint
The address will be: localhost:8080/hello

You can test via a browser or commandline:
```sh
curl localhost:8080/hello
```
### 1.4 - BONUS - Add Unit Tests for HelloWorld endpoint
This can be done by creating a *HelloWorldControllerTests* Java class file in the test/java directory with:

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelloWorldControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testHelloWorld(){
        String body = restTemplate.getForObject("/hello",String.class);

        assertThat(body).contains("Hola Mundo!");
    }
}
```

With Intellij, you can now run the Test by right clicking on it.

From the commandline you can run them with:

```sh
./gradlew test
```

## 2 - WebApplication on PCF
### 2.1 - Login into the PCF instance that you are using if required

```sh
cf login -a ENTER_API_URL_HERE
```

Enter your Username and Password.
### 2.2 - Deploy your application to PCF

```sh
cf push cloud-lab -p build/libs/cloud-lab-0.0.1-SNAPSHOT.jar
```

### 2.3 - Login into the PCF portal.

If you are using Pivotal Web Services, the portal is at:

https://run.pivotal.io

Click through to your app by selecting the default space and org.

Your route to the application (URL) will be presented besides your application.

### 2.4 - Test the /hello endpoint at this route <ROUTE>/hello

### 2.5 - Scale the App

Either provision more instances or more space.

This can be done via the command line or via the GUI.

To scale up to 2 instances:

```sh
cf scale cloud-lab -i 2
```

Via the GUI observe additional instances being spun up.

## 3 - Operations with Spring Boot

The Spring Actuator Dependency adds out-of-the-box endpoints for monitoring and interacting with your application.

### 3.1 - Add the Spring Boot Actuator dependency to your build script.

The full name of the dependency is : *org.springframework.boot:spring-boot-starter-actuator*

If using Gradle, your new dependency block should look like:

```groovy
dependencies {
    //...
    compile('org.springframework.boot:spring-boot-starter-actuator')
    //...
}
```

Re-run the application
```sh
./gradlew bootRun
```

### 3.2 - Check the localhost:8080/actuator/health endpoint

Verify that it works on your local running instance of the app:

```sh
curl localhost:8080/actuator/health
```

You can also use a browser

### 3.3 - Expose additional information on the Health Endpoint

To the application.properties file in resources add:

```properties
management.endpoint.health.show-details=ALWAYS
```

Later on in the Workshop this endpoint will also show database information.

### 3.4 - Check for new information on the health endpoint

This will require running rebuilding the application:

```sh
./gradlew bootRun
```

```sh
curl localhost:8080/actuator/health
```

### 3.5 - Enable ALL Actuator endpoints

Currently exposed actuator endpoints can be viewed at: http://localhost:8080/actuator

For security reasons, many of the these endpoints are turned off by default.

They can be ALL enabled by adding the following to your application.properties file:

```properties
management.endpoints.web.exposure.include=*
```

Rebuild, and check the http://localhost:8080/actuator endpoint for available ones.

### 3.6 - Add build information the /info endpoint

We want to easily view build information from deployed artifacts.

Add the following to your build.gradle:

 ```groovy
 springBoot {
    buildInfo()
 }
 ```

Rebuild, and check the http://localhost:8080/actuator/info endpoint.

### 3.7 - BONUS - Add GIT Information to the /info endpoint

Hint - you will need to init a GIT repot locally, and add the com.gorylenko.gradle-git-properties dependency in Gradle.

## 4 - Operations on PCF
### 4.1 - Add a /kill endpoint to your App and redeploy your App

Add a new KillController to allow simulating of a JVM crash.

```java
@RestController
public class KillController {

  @RequestMapping("/kill")
  public void kill(){
      System.exit(1);
  }
}
```

Rebuild your app, and redeploy to PCF.

```sh
 ./gradlew build && cf push cloud-lab -p build/libs/cloud-lab-0.0.1-SNAPSHOT.jar
```
### 4.2 - In separate terminal window TAIL the PCF app logs

```sh
cf logs cloud-lab
```

### 4.3 - Call the /kill endpoint

Note that PCF will automatically bring up a new instance.

This can be monitored from the PCF Dev Portal.

You can also view what happened in the logging window from the previous step.

### 4.4 - BONUS - Create a PCF manifest to simplify deployments

https://docs.cloudfoundry.org/devguide/deploy-apps/manifest.html

```sh
cf create-app-manifest cloud-lab
```

You can customize deployment settings, as well as default binary path.

### 4.5 - BONUS - Add Auto-Scaling to Your Application

https://docs.run.pivotal.io/appsman-services/autoscaler/using-autoscaler.html

From your Dev Space in the PCF Dev GUI , add an App-Scaler Server, bind it to your Application.

It is configurable via the Manage button.

## 5 - Configuration with Spring Boot

Spring Boot lets you externalize your configuration so that you can work with the same application code in different environments.

https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html

### 5.1 - Allow injection of the helloWorld message via external sources
```java
@RestController
public class HelloWorldController {

    @Value("${helloworld.message:'Hola Mundo - default!'}")
    private String helloMessage;

    @RequestMapping("hello")
    public String helloWorld(){
        return helloMessage;
    }
}
```

Note #1 : the usage of the default value in case nothing is found (optional).

Note #2 : You may have to fix broken tests!!

### 5.2 - Add a configurable value for helloworld.message in the application.properties file.

```properties
helloworld.message="Hello World - default config file"
```

### 5.3 - Rebuild / Restart your app, and verify the /hello endpoint.

Default should be "Hello World - default config file"

## 6 - Configuration on PCF
### 6.1 - Add a custom HelloWorld message for cloud deployments

PCF deploys will automatically load *cloud* profile settings.

In the resources folder, add a *application-cloud.properties* file.

Add:

```properties
helloworld.message="Hello World - cloud only"
```

Build and Redeploy to PCF.

Verify the updated message at the /hello endpoint.

## 7 - Caching with Spring Boot

### 7.1 - Add a slow / costly endpoint to the Application.

One example would be performing converting a String to UpperCase.

```java
@RestController
public class CacheExampleController {

  @RequestMapping("/uppercase")
  public String uppercase(String input ){
      try {Thread.sleep(5000); } catch (InterruptedException e) {}

      return input.toUpperCase();
  }
}
```

Rebuild, rerun the app.

From the browser you can call : *http://localhost:8080/uppercase?input=test*

Or using curl:

```sh
curl http://localhost:8080/uppercase?input=test
```

Note how the /uppercase endpoint is always slow.

### 7.2 - Add the Spring Boot Cache dependency to your build script.

The full name of the dependency is : *org.springframework.boot:spring-boot-starter-cache*

If using Gradle, your new dependency block should look like:

```groovy
dependencies {
//...
compile('org.springframework.boot:spring-boot-starter-cache')
//...
}
```

### 7.3 - Enable Caching on the endpoint by using the Cache Annotation.

Updated CacheExampleController should look like:

```java
@Cacheable("uppercase")
@RequestMapping("/uppercase")
public String uppercase(String input ){
    try {Thread.sleep(5000); } catch (InterruptedException e) {}

    return input.toUpperCase();
}
```

You will also need to turn on Caching by adding the EnableCaching annotation to the CloudLabApplication class:

```java
@SpringBootApplication
@EnableCaching
public class CloudLabApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudLabApplication.class, args);
	}
}
```

Restart your app, and verify that subsequent calls to the endpoint return much quicker.

### 7.4 - BONUS - Add an eviction endpoint to the controller

It will evict specified keys from the cache.

Hint: CacheEvict annotation

## 8 - Caching on PCF

Note, the default cache implementation uses local in-memory caching, but this can be easily change to use 3rd party caching solutions such as Redis.

### 8.1 - Add the Spring Boot Redis and commons-pool dependencies to your build script.

If using Gradle, your new dependency block should look like:

```groovy
dependencies {
//...
compile('org.springframework.boot:spring-boot-starter-data-redis')
compile('org.apache.commons:commons-pool2:2.4.2')
//...
}
```

Rebuild, and redeploy to PCF.

### 8.2 - Create a Redis Service Instance in PCF

You can view available self-self / on-demand provisioning services via the marketplace.

```sh
cf marketplace
```

To create a Redis Service run:

```sh
cf create-service p-redis shared-vm custom-redis
```

### 8.3 - Bind the Service to our application

```sh
cf bind-service cloud-lab custom-redis
```

Restage your app:
```sh
cf restage cloud-lab
```

Confirm connection to your Redis Server using the health endpoint: /actuator/health

Also confirm that cache is still working.

### 8.4 - BONUS - Default to Simple (non-Redis caching locally) and Redis Caching in the cloud

In application.properties add:

```properties
spring.cache.type=SIMPLE
```

In application-cloud.properties add:

```properties
spring.cache.type=REDIS
```

Verify that Caching works locally and in PCF.

## 9 - Data with Spring Boot

### 9.1 - Add the Spring Boot Data JPA , Rest and H2Database dependencies to your build script.

The full name of the dependencies are :
*org.springframework.boot:spring-boot-starter-data-jpa*
*org.springframework.boot:spring-boot-starter-data-rest*
*com.h2database:h2*

If using Gradle, your new dependency block should look like:

```groovy
dependencies {
	//...
	compile('org.springframework.boot:spring-boot-starter-data-rest')
	compile('org.springframework.boot:spring-boot-starter-data-jpa')
	compile('com.h2database:h2')
	//...
}
```

### 9.2 - Add a Simple Domain Object

Such as a Person class:

```java
@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String firstName;
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}

```

### 9.3 - Add a Repository Interface

Also create a PersonRepository interface:

```java
public interface PersonRepository extends JpaRepository<Person, Long> {}

```

### 9.4  - Add Default Sample Data

Create a import.sql file in the resources/ directory.

```sql
INSERT INTO PERSON(id, first_name, last_name) VALUES (1, 'Tony', 'Stark');
INSERT INTO PERSON(id, first_name, last_name) VALUES (2, 'Steve', 'Rogers');

```

### 9.5 - Test the new persons endpoints

Restart your app, and view the localhost:8080/persons.

The default is configured to use the embedded H2 Database.


## 9.6 - BONUS - Enable logging of all DML/DDL SQL statements

Add the following to the *applications.properties* file.

```properties
spring.jpa.show-sql=true
```

Restart your app, you should now see all SQL statement in the log output.


## 10 - Data on PCF

Note, the default cache implementation uses local in-memory caching, but this can be easily change to use 3rd party caching solutions such as Redis.

### 10.1 - Add the MySQL dependency to the build script.

The full name of the dependency is :
*mysql:mysql-connector-java*

If using Gradle, your new dependency block should look like:

```groovy
dependencies {
    //...
    compile("mysql:mysql-connector-java")
    //...
}
```

### 10.2 - Create a MySql Service Instance in PCF

You can view available self-self / on-demand provisioning services via the marketplace.

```sh
cf marketplace
```

To create a MySQL Service run:

```sh
cf create-service p-mysql 100mb custom-mysql
```

### 10.3 - Bind the Service to our application

```sh
cf bind-service cloud-lab custom-mysql
```

Restage your app:
```sh
cf restage cloud-lab
```

Confirm connection to your MySQL Server using the health endpoint: /actuator/health

Try to /persons endpoint.

It won't work as the database does not have the required Persons tables created.


## 11 - Database Migrations

Spring Boot supports two higher-level migration tools: Flyway and Liquibase.

We will use Flyway for performing MySQL migrations.

### 11.1 - Add the FlyAwayDB dependency to your build script:

The full name of the dependency is :
*org.flywaydb:flyway-core*

If using Gradle, your new dependency block should look like:

```groovy
dependencies {
	//...
	compile("org.flywaydb:flyway-core")
	//...
}
```

## 11.2 - Add a Base Database Init Script:

In the resources folder , create a db/migration sub-folder.

In it create a V1__init.sql file (resources/db/migration/V1__init.sql).

Add the following to it:

```sql
CREATE TABLE person (
	id int NOT NULL AUTO_INCREMENT,
	first_name varchar(255) not null,
	last_name varchar(255) not null,
	PRIMARY KEY (ID)
);

insert into person (first_name, last_name) values ('Peter', 'Parker');
```

## 11.3 - Rebuild and redeploy to PCF

You can look at http://localhost:8080/actuator/flyway to review the list of scripts.

FlyAway will only apply updates as needed, and keeps track of scripts run (in the flyway_schema_history table).

## 11.4 - BONUS - Add a middleName value to the Person Object , and create Database Migration scripts for this

```java
    private String middleName;

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
```

Create a V2__person_middle_name_addition.sql file (in db/migration).

Add the following to it:

```sql
ALTER TABLE person ADD middle_name varchar(255);
```