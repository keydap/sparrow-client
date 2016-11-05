## Sparrow Client
This is a client for any [SCIM v2](http://www.simplecloud.info) compliant server implementation.
This was primarily implemented to support the Sparrow Cloud Identity Server which is based on SCIM v2 protocol.

## Features
1. Automatic generation of model classes for resources from the resourcetypes and schema
2. Support for custom(when auto generation is not required) resource classes with __@Resource__ and
   __@Extension__ annotations
3. No need to construct JSON payloads while sending requests (except in the case of PATCH 
   operation, which requires individual operation data to be  hand written in JSON)
4. Flexible authenitcation mechanism using Authenticator interface
 
 
## Building
```
git clone https://github.com/keydap/sparrow-client.git
cd sparrow-client
mvn clean install
```

## Usage

#### Adding the client library
If you are using Maven then add the below dependency in your pom.xml
```xml
<dependency>
    <groupId>com.keydap.sparrow</groupId>
    <artifactId>sparrow-client</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
#### Generating resource models using json2java maven plugin
The json2java plugin can automatically generate the POJOs representing the resourcetypes configured on the
SCIM service provider. The sample configuration of this plugin is shown below:

```
<plugin>
    <groupId>com.keydap.sparrow</groupId>
    <artifactId>sparrow-json2java</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <generatePackage>com.keydap.sparrow.scim</generatePackage>
        <baseUrl>http://localhost:7090/v2</baseUrl>
    </configuration>
</plugin>
```  
This will place the generated POJOs under the package `com.keydap.sparrow.scim`

If you want to work with custom classes instead of the generated POJOs then they must be annotated with `com.keydap.sparrow.Resource`
Here is a sample part of the generated POJO representing User resource.
```java
package com.keydap.sparrow.scim;

import com.keydap.sparrow.Extension;
import com.keydap.sparrow.Resource;
import java.util.List;
import java.util.Date;

/*
 * Generated on Tue Nov 01 13:03:51 IST 2016 using Keydap Sparrow's json2java plugin 
 */

@Resource(schemaId="urn:ietf:params:scim:schemas:core:2.0:User", endpoint="/Users", desc="User Account")
public  class User
{
      private String id;
      private String externalId;
      private Meta meta;
      private String userName;
      ...
```
#### Instantiate the client
The client can be instantiated with or without an Authenticator. An authenticator allows us to supply necessary credentials
required to authenticate first and later to authorize each request. The Authenticator implementation depends on the target
server the client is connecting to.
```java
// the below example shows an authenticator used for connecting to Sparrow server
Authenticator authenticator = new SparrowAuthenticator("admin", "example.COM", "secret");
client = new ScimClient("http://localhost:7090/v2", authenticator);
client.authenticate();
```

#### Register the resource classes
Before executing any requests the client must be made aware of the resource classes so that it can handle the serialization and deserialization of SCIM v2 request and response payloads in JSON format.
Suppose we have three resource classes, User, Group and Device then they can be registered using `register()` method
```java
client.register(User.class, Group.class, Device.class);
```

#### SCIM v2 API
1. Add a new resource
    ```java
    User u = new User();
    u.setUserName("bjensen");
    Name name = new Name();
    name.setFamilyName("Jensen");
    name.setFormatted("Ben Jensen");
    name.setGivenName("Ben");
    name.setHonorificPrefix("Mr.");
    u.setName(name);

    Email e = new Email();
    e.setValue("bjensen@example.com");
    e.setPrimary(true);
    e.setType("home");
    u.setEmails(Collections.singletonList(e));

    u.setPassword("secret001");

    Response<User> resp = client.addResource(u);
    ```
2. Getting a resource

    ```java
    Response<User> resp = client.getResource("value-of-the-id-attribute-of-the-resource", User.class)
    ```
3. Deleting a resource

    ```java
    // deleting a User resource 
    Response<Boolean> resp = client.deleteResource("value-of-the-id-attribute-of-the-resource", User.class);
    ```
4. Replace a resource

    ```java
    // replacing a User resource
    Response<User> resp = client.replaceResource("value-of-the-id-attribute-of-the-resource", User.class)
    ```
5. Patching a resource

    ```java
    PatchRequest pr = new PatchRequest(thermostat.getId(), Device.class);
    // feed the attribute and values in JSON format 
    pr.add("location", "{\"latitude\": \"1.1\", \"longitude\": \"2.2\", \"desc\": \"device location\"}");
    // call setAttributes if a sub-set of attributes are needed, the parameter can contain a comma separated attribute names
    pr.setAttributes("location");

    Response<Device> resp = client.patchResource(pr);
    ```
6. Searching for resources (uses GET method)

    ```java
    SearchResponse<User> resp = client.searchResource("username eq \"elecharny\"", User.class);
    ```
7. Searching for resources using SearchRequest (uses POST method)

    ```java
    SearchRequest req = new SearchRequest();
    req.setFilter("emails.type eq \"work\"");
    req.setAttributes("username");

    SearchResponse<User> resp = client.searchResource(req, User.class);
    // now access the resources
    List<User> received = resp.getResources();
    ```
8. Searching for all resources (uses POST method)

    ```java
    SearchRequest req = new SearchRequest();
    req.setFilter("id pr");

    SearchResponse<Object> resp = client.searchAll(req);
    // now access the resources
    List<User> received = resp.getResources();
    ```
    
## License
Sparrow-client is released under [ASL v2](https://github.com/keydap/sparrow-client/blob/master/LICENSE)
```
   Copyright 2016 Keydap Software

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
