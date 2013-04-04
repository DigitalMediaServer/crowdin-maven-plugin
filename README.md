# crowdin-maven

This plugin allows Maven projects to be translated using crowdin.

# Quick start

## Configuration

Add a server to your ~/.m2/settings.xml (keeping your API key private)

```xml
<settings>
<!-- ... -->
 <servers>
  <!-- ... -->
  <server>
   <id>crowdin-myproject</id>
   <username>myproject<username>
   <password>API key</password>
  <server>
  <!-- ... -->
 </servers>
<!-- ... -->
 <pluginGroups>
  <!-- ... -->
  <pluginGroup>com.googlecode.crowdin-maven</pluginGroup>
  <!-- ... -->
 </pluginGroups>
<!-- ... -->
</settings>
```

Configure your build for crowdin usage in your project's pom.xml :

```xml
<project>
<!-- ... -->
 <build>
 <!-- ... -->
  <plugins>
   <!-- ... -->
   <plugin>
    <groupId>com.googlecode.crowdin-maven</groupId>
    <artifactId>crowdin-plugin</artifactId>
    <version>1.4</version>   
     <executions>
      <execution>
       <goals>
        <goal>aggregate</goal>
       </goals>
      </execution>
     </executions>
     <configuration>
      <crowdinServerId>crowdin-myproject</crowdinServerId>
     </configuration>
   </plugin>
   <!-- ... -->
  </plugin>
  <!-- ... -->
 </build>
 <!-- ... -->
</project>
```

## Pushing translations to crowdin

Put your messages files in properties format in src/main/messages.

*Goal* | *Description*
--- | ---
`mvn crowdin:push` | Push the messages files on crowdin.<br>It is a Maven first, files or keys not in Maven will be erased on crowdin.

## Getting translations from crowdin

*Goal* | *Description*
--- | ---
`mvn crowdin:export` | Ask crowdin to update the translations on their side.<br>There is a limit of 30 minutes between two exports.
`mvn crowdin:pull` | Retrieve messages from crowdin in `src/main/crowdin`.<br>`src/main/crowdin` must be considered as a derived resource. Do not edit those files.
`mvn crowdin:aggregate` | This goal should be executed when the project is built.<br>It aggregates the properties from `src/main/crowdin` in regular Java properties files.<br>Those files are attached to the build, included in the packaging next to the classes.<br>Using the configuration above in project's pom.xml, this goal is executed on Maven `generate-resources`.
