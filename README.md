## Sparrow Client
This is a client for any [SCIM v2](http://www.simplecloud.info) compliant server implementation.
This was primarily implemented to support the Sparrow Cloud Identity Server which is based on SCIM v2 protocol.

## Building

```
git clone https://github.com/keydap/sparrow-client.git
cd sparrow-client
mvn clean install

```

## Usage

#### Adding the client 
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
The client can be instantiated with or without an Authenticator. An authenticator allows in supplying the necessary credentials
to authorize each request.
```java
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
