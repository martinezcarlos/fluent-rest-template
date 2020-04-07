# Fluent Rest Template
[![CircleCI](https://circleci.com/gh/martinezcarlos/fluent-rest-template.svg?style=shield&circle-token=04fcca82fadd123d595b35c61bd1bcd93d112975)](https://circleci.com/gh/martinezcarlos/fluent-rest-template) 
[![codecov](https://codecov.io/gh/martinezcarlos/fluent-rest-template/branch/develop/graph/badge.svg?token=tlXxBkJNhD)](https://codecov.io/gh/martinezcarlos/fluent-rest-template) 
[![Hex.pm](https://img.shields.io/hexpm/l/plug)](http://www.apache.org/licenses/LICENSE-2.0)

## Overview
**FluentRestTemplate** is a handy wrapper to build and execute REST request calls with a single chain expression, relieving you from all that common boilerplate code around RestTemplate.

### The swamp of good intentions
Everyone has a way to build and execute REST calls, some are great and some are... just good intentions.

This is an example of a not so cool way to build and execute a REST call:

```java
String url = "https://" + I_BUILD + myUrls + "this/{way}";

Map<String, String> uriVars = new HashMap<>();
uriVars.put("way", "getOverIt");

List<MediaType> accepts = new ArrayList<>();
accepts.add(MediaType.APPLICATION_JSON);

HttpHeaders headers = new HttpHeaders();
headers.set("foo", "bar");
headers.setContentType(MediaType.APPLICATION_JSON);
headers.setAccept(accepts);

HttpEntity<String> entity = new HttpEntity<>(someJsonBody, headers);

ResponseEntity<CoolResponse> really =
 restTemplate.exchange(url, HttpMethod.POST, entity, 
  CoolResponse.class, uriVars);
```
Yeah, you  are right, there are plenty of better (and worse) ways to do this, but there resides the problem of the swamp of good intentions: not everybody knows that!

### FluentRestTemplate way

As simple as this:

```java
final ResponseEntity<CoolResponse> cool = 
  fluentRestTemplate
      .put(someJsonBody)
      .into(url)
      .uriVariable("way", "getOverIt")
      .executor()
      .header("foo", "bar")
      .contenType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON)
      .execute(CoolResponse.class);
```

And it can be as extensive as you want or as short as this:

```java
final URI uri = URI.create("https://postman-echo.com/get");
final String postmanGet = 
  fluentRestTemplate
      .get()
      .from(uri)
      .executor()
      .executeForObject(String.class);
```

## Requirements

| Artifact       | Minimal version  |
| :------------- | :--------------- |
| spring-web     | 4.1.0.RELEASE    |

* Spring Web version 4.1.0.RELEASE or higher.
## Installation

Add this dependency to your dependency management file:

Maven
```maven
<dependency>
  <groupId>mart.karl</groupId>
  <artifactId>fluent-rest-template</artifactId>
  <version>1.0.0</version>
</dependency>
```
Gradle
```gradle
compile 'mart.karl:fluent-rest-template:1.0.0'
```
And add the following repository section:

Maven
```maven
<repositories>
  <repository>
    <id>karl-mart-release</id>
    <url>https://packagecloud.io/karl-mart/release/maven2</url>
    <releases>
      <enabled>true</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>
```
Gradle
```gradle
repositories {
    maven {
        url "https://packagecloud.io/karl-mart/release/maven2"
    }
}
```

**WARNING**: this repository section is temporal and will be modified soon

## Usage

There are several ways on how you can initialize a FluentRestTemplate; please read the [wiki](#wiki) for a detailed explanation. This is a simple, yet recommended one:

```java
@Configuration
public class BeanConfig {

  // Build a basic RestTemplate bean.
  @Bean
  public RestTemplate initRestTemplate(final RestTemplateBuilder builder) {
    return builder.build();
  }

  // Use RestTemplate to build a FluentRestTemplate.
  @Bean
  public FluentRestTemplate initFluentRestTemplate(final RestTemplate restTemplate) {
    return new FluentRestTemplate(restTemplate);
  }
 }
```

Ready to be injected in your Spring components.

The overview section already showed a couple of simple ways to  use FluentRestTemplate (by using URI and URI String). There is, however, a more powerful mechanism to this: FluentService.

**FluentService** is a handy abstraction of a service's API collection of resources, identified by Uniform Resource Identifiers (URI) plus optional API version, a Map of endpoints paths exposed by that service, common service query params and common fragment. These optional parameters can be used to request the creation of complete URI at runtime.

Let's suppose we have a FluentService that conforms to this simple YAML snippet:

```yml
services:
   my-cool-service:
     scheme: https
     host: cool-service.com
     endpoints:
       updateCoolStuff: update/stuff/{stuffId}
       postReminder: reminder/set
```

and supposee we want to invoke a PUT method in **"https://cool-service.com/update/stuff/123"** to update cool stuff, providing a request body, accepting a JSON media type, with a locale de_DE and expect an updated CoolSuff object as response:

```java
FluentService myCoolService = ...
CoolStuff coolStuff = ...
CoolStuff updatedCoolStuff =
  fluentRestTemplate
   .put(coolStuff)
   .into(myCoolService)
   .withEndpoint("updateCoolStuff")
   .uriVariable("stuffId", "123")
   .executor()
   .accept(MediaType.APPLICATION_JSON)
   .header("locale", "de_DE")
   .executeForObject(CoolStuff.class);
```

The **usages** shown  here are just basic. For a better understanding on **FluentRestTemplate** and **FluentService** please read the [wiki](#wiki).

## Other
[![Open Source](https://img.shields.io/badge/LinkedIn-carlosmartinezm-blue)](https://www.linkedin.com/in/carlosmartinezm/)
[![Open Source](https://badges.frapsoft.com/os/v1/open-source.svg?v=103)](https://opensource.org/)
