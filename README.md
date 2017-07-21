# DMS crowdin Maven Plugin

This plugin allows Maven projects to be translated using crowdin. It is based on [glandais' crowdin-maven plugin](https://github.com/glandais/crowdin-maven), but has been adapted for use with the [Digital Media Server project](https://github.com/DigitalMediaServer/DigitalMediaServer).

## Configuration

To access crowdin this plugin needs a project identifier and an API key from crowdin. This is achieved by specifying a ```server``` in Maven configuration. The API key needs to be kept private, so the best place to put this is in the Maven user settings. This can be achieved by adding a server to your ```~/.m2/settings.xml``` like shown in the example below. If you don't have a ```~/.m2/settings.xml``` file, a template is provided later in this README which can be copy and pasted into an empty xml file.

```xml
<settings>
<!-- ... -->
  <servers>
    <!-- ... -->
    <server>
      <id>crowdin-dms</id>
      <username>DigitalMediaServer<username>
      <password>API key</password>
    </server>
    <!-- ... -->
  </servers>
  <!-- ... -->
</settings>
```

Further parameters need to be configured in your project's ```pom.xml``` so that they are available when requested by the plugin. This plugin doesn't bind to any ```phases``` and must be executed manually, so the changes to the ```pom.xml``` don't affect the build process itself. Below is a typical configuration.

```xml
<project>
<!-- ... -->
  <build>
    <!-- ... -->
    <pluginManagement>
      <!-- ... -->
      <plugins>
        <!-- ... -->
        <plugin>
          <groupId>org.digitalmediaserver</groupId>
          <artifactId>crowdin-maven-plugin</artifactId>
          <version>LATEST</version>   
          <configuration>
            <project>${project}</project>
            <languageFilesFolder>${project.basedir}/src/main/resources/i18n</languageFilesFolder>
            <downloadFolder>${project.basedir}/extras/crowdin</downloadFolder>
            <statusFile>${project.basedir}/src/main/resources/languages.properties</statusFile>
            <crowdinServerId>crowdin-dms</crowdinServerId>
            <pushFileName>messages.properties</pushFileName>
            <pushFileTitle>Digital Media Server</pushFileTitle>
            <projectName>Digital Media Server</projectName>
          </configuration>
        </plugin>
        <!-- ... -->
      </plugins>
      <!-- ... -->
    </pluginManagement>
    <!-- ... -->
  </build>
  <!-- ... -->
</project>
```

## Parameter description

* ```languageFilesFolder``` - The folder where the translation ```.properties``` files are located.
* ```downloadFolder``` - A temporary folder used to store the files downloaded from crowdin.
* ```statusFile``` - The full path to the ```.properties``` file where the translation status should be written.
* ```crowdinServerId``` - The ```id``` of the Maven configured ```server``` to be used by the plugin for crowdin authentication.
* ```pushFileName``` - The name of the file located in ```languageFilesFolder``` that is the _base language_ file that should be uploaded to crowdin during ```push```.
* ```pushFileTitle``` - The title to be associated with ```pushFileName``` on crowdin.
* ```projectName``` - The crowdin project name. To avoid pushing the wrong file to crowdin, this must match both the ```pom.xml``` project name and the crowdin project name for the given crowdin project identifier for ```push``` to be executed.
* ```rootBranch``` - The git branch that should be considered root on crowdin (that is, not exist in a branch folder). The default value is ```master```. This parameter can be specified in ```pom.xml```, from the command line with ```-DrootBranch=``` or left to it's default. Any git branch not matching this parameter will push to and fetch from a branch folder at crowdin.

## Using the plugin

Given that the parameters are configured correctly, you can execute a goal with:

```mvn dms-crowdin:<goal> [-D<property>=<value>]```

This plugin requires git to be installed to work, and will automatically look up the current git branch and use that as the crowdin branch unless the current git branch matches to ```rootBranch``` parameter. If no git branch is found (e.g. checked out to a tag) all goals but ```apply``` will fail. For further information about versions management and branches on crowdin, see the [crowdin documentation] (https://support.crowdin.com/articles/versions-management/).

The goals are explained below:

### Pushing strings for translation to crowdin

*Goal* | *Command* | *Description*
---- | ------- | -----------
**push** | ```mvn dms-crowdin:push -Dconfirm=true``` | Upload the _base language_ file to on crowdin. Any strings already on crowdin that's missing in the uploaded file is deleted from crowdin with all corresponding translations. Because of this, an extra argument ```confirm=true``` is required for ```push```.

### Getting translations from crowdin

*Goal* | *Command* | *Description*
---- | ------- | -----------
**build** | ```mvn dms-crowdin:build``` | Ask crowdin to build a downloadable zip file containing all the latest translations. Unpaid projects can only build once every 30 minutes via the API, but it's possible to build from the crowdin web interface at any time. The API replies with status ```skipped``` both if there are no changes since the last build and if the previous build was less than 30 minutes ago, so there's no way to tell the two apart.
**fetch** | `mvn dms-crowdin:fetch` | Download and extract the last built zip file from crowdin to ```downloadFolder```.
**apply** | `mvn dms-crowdin:apply` | Copy the downloaded files from ```downloadFolder``` and into their intended locations in accordance with ```languageFilesFolder``` and ```statusFile```.
**pull** | ```mvn dms-crowdin:pull``` | Perform ```build```, ```fetch``` and ```apply``` in sequence. This is a convenience goal combining the individual steps to get the latest translations from crowdin copied into your local project.

## ```settings.xml``` template

If you're missing the file ```~/.m2/settings.xml```, you can copy and paste the template below:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
	<pluginGroups>
	</pluginGroups>
	<proxies>
	</proxies>
	<servers>
		<server>
			<id>crowdin-dms</id>
			<username>DigitalMediaServer<username>
			<password>API key</password>
		</server>
	</servers>
	<mirrors>
	</mirrors>
	<profiles>
	</profiles>
</settings>
```
