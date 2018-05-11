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
- for dependencies add Web, Actuator, Config Client
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

The Spring Actuator Dependency we added early includes out-of-the-box endpoint for monitoring and interacting with your application.

### 3.1 - Check the localhost:8080/actuator/health endpoint

Verify that it works on your local running instance of the app:

```sh
curl localhost:8080/actuator/health
```

You can also use a browser

### 3.2 - Expose additional information on the Health Endpoint

To the application.properties file in resources add:

```properties
management.endpoint.health.show-details=ALWAYS
```

Later on in the Workshop this endpoint will also show database information.

### 3.3 - Check for new information on the health endpoint

This will require running rebuilding the application:

```sh
./gradlew bootRun
```

```sh
curl localhost:8080/actuator/health
```


### 3.4 - Enable ALL Actuator endpoints

Currently exposed actuator endpoints can be viewed at: http://localhost:8080/actuator

For security reasons, many of the these endpoints are turned off by default.

They can be ALL enabled by adding the following to your application.properties file:

```properties
management.endpoints.web.exposure.include=*
```

Rebuild, and check the http://localhost:8080/actuator endpoint for available ones.

### 3.5 - Add build information the /info endpoint

We want to easily view build information from deployed artifacts.

Add the following to your build.gradle:

 ```groovy
 springBoot {
    buildInfo()
 }
 ```

Rebuild, and check the http://localhost:8080/actuator/info endpoint.

### 3.6 - BONUS - Add GIT Information to the /info endpoint

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
## 7 - Caching with Spring Boot
## 8 - Caching on PCF
## 9 - Data with Spring Boot
## 10 - Data on PCF