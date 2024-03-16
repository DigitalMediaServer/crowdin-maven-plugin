# Crowdin Maven Plugin

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.digitalmediaserver/crowdin-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.digitalmediaserver/crowdin-maven-plugin)

This Maven plugin synchronizes translation files between the local project and Crowdin using the Crowdin API v2. It was originally based on [glandais' crowdin-maven plugin](https://github.com/glandais/crowdin-maven), but has since been completely rewritten. The Maven project must be in a Git repository since Git branches are translated to Crowdin branches.

## Table of Contents
- [1. Configuration](#1-configuration)
  - [1.1 Credentials](#11-credentials)
    - [1.1.1 Example credentials configuration](#111-example-credentials-configuration)
    - [1.1.2 Settings template](#112-settings-template)
  - [1.2 Project configuration](#12-project-configuration)
    - [1.2.1 Skeleton project configuration](#121-skeleton-project-configuration)
    - [1.2.2 Parameter description](#122-parameter-description)
      - [1.2.2.1 `statusFile` parameter description](#1221-statusfile-parameter-description)
      - [1.2.2.2 `translationFileSet` parameter description](#1222-translationfileset-parameter-description)
      - [1.2.2.3 `conversion` parameter description](#1223-conversion-parameter-description)
      - [1.2.2.4 `escapeQuotes` options](#1224-escapequotes-options)
      - [1.2.2.5 `escapeSpecialCharacters` options](#1225-escapespecialcharacters-options)
      - [1.2.2.6 `exportQuotes` options](#1226-exportquotes-options)
      - [1.2.2.7 `updateOption` options](#1227-updateoption-options)
      - [1.2.2.8 Crowdin file types](#1228-crowdin-file-types)
      - [1.2.2.9 Crowdin placeholders](#1229-crowdin-placeholders)
      - [1.2.2.10 Additional `targetFileName` placeholders](#12210-additional-targetfilename-placeholders)
    - [1.2.3 Example project configuration](#123-example-project-configuration)
- [2. Using the plugin](#2-using-the-plugin)
  - [2.1 Pushing strings for translation to Crowdin](#21-pushing-strings-for-translation-to-crowdin)
  - [2.2 Getting translations from Crowdin](#22-getting-translations-from-crowdin)
  - [2.3 Cleaning the intermediate folder](#23-cleaning-the-intermediate-folder)

## 1. Configuration

This plugin needs configuration to know what files to handle and how. It is meant to be executed manually using Maven, but it would require too many command-line arguments to be practical if the configuration wasn't stored somewhere. The project's `pom.xml` is therefore used to store most of the configuration, while the Crowdin credentials is stored in Maven's settings, preferably in `~/.m2/settings.xml`. This way the configuration is checked in to the Git repository while the credentials are kept in the local file system for those that are authorized to synchronize translations with Crowdin.

The `pom.xml` configuration for this plugin doesn't affect the Maven build process by default, as the only `phase` it binds to is the `clean` phase. It is possible to bind one or more `goal` from this plugin to specific build phases, but it's hard to see how that could be useful. Running the plugin without having configured the `pom.xml` and the credentials will result in an error.

### 1.1 Credentials

To access Crowdin your API token is needed. This is configured by specifying a ```server``` in Maven's configuration, which is then referenced in the plugin configuration with the `crowdinServerId` parameter. You can create an API token under [Account settings -> API](https://crowdin.com/settings#api-key) at Crowdin.

The API token needs to be kept private, so the best place to put it is usually in Maven's user settings. This can be achieved by adding a server to your ```~/.m2/settings.xml``` as shown in the example below. If you don't have a ```~/.m2/settings.xml``` file, a template that can be pasted into an empty file [is provided](#112-settings-template).

#### 1.1.1 Example credentials configuration
```xml
<settings>
<!-- ... -->
  <servers>
    <!-- ... -->
    <server>
      <id>crowdin</id>
      <password>API token</password>
    </server>
    <!-- ... -->
  </servers>
  <!-- ... -->
</settings>
```

#### 1.1.2 Settings template

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
      <id>crowdin</id>
      <password>Your API token</password>
    </server>
  </servers>
  <mirrors>
  </mirrors>
  <profiles>
  </profiles>
</settings>
```

### 1.2 Project configuration

The project configuration is stored in the `pom.xml` of the project. A detailed description is given below.

#### 1.2.1 Skeleton project configuration

Here is a skeleton project configuration showing the location of all configuration options:

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
          <version>...</version>
          <configuration>
            <projectId></projectId>
            <comment></comment>
            <confirm></confirm>
            <crowdinServerId></crowdinServerId>
            <httpTimeout></httpTimeout>
            <downloadFolder></downloadFolder>
            <lineSeparator></lineSeparator>
            <projectName></projectName>
            <rootBranch></rootBranch>
            <skipUntranslatedStrings></skipUntranslatedStrings>
            <skipUntranslatedFiles></skipUntranslatedFiles>
            <exportApprovedOnly></exportApprovedOnly>
            <updateOption></updateOption>
            <replaceModifiedContext></replaceModifiedContext>
            <statusFiles>
              <statusFile>
                <addComment></addComment>
                <comment></comment>
                <encoding></encoding>
                <escapeUnicode></escapeUnicode>
                <lineSeparator></lineSeparator>
                <sortLines></sortLines>
                <targetFile></targetFile>
                <type></type>
                <conversions>
                  <conversion><from></from><to></to></conversion>
                  <!-- ... -->
                </conversions>
              </statusFile>
              <statusFile>
                <!-- ... -->
              </statusFile>
              <!-- ... -->
            </statusFiles>
            <translationFileSets>
              <translationFileSet>
                <addComment></addComment>
                <baseFileName></baseFileName>
                <comment></comment>
                <commentTag></commentTag>
                <crowdinPath></crowdinPath>
                <encoding></encoding>
                <escapeQuotes></escapeQuotes>
                <escapeSpecialCharacters></escapeSpecialCharacters>
                <exportQuotes></exportQuotes>
                <escapeUnicode></escapeUnicode>
                <exportPattern></exportPattern>
                <languageFilesFolder></languageFilesFolder>
                <lineSeparator></lineSeparator>
                <sortLines></sortLines>
                <targetFileName></targetFileName>
                <title></title>
                <type></type>
                <updateOption></updateOption>
                <replaceModifiedContext></replaceModifiedContext>
                <writeBOM></writeBOM>
                <conversions>
                  <conversion><from></from><to></to></conversion>
                  <!-- ... -->
                </conversions>
                <excludes>
                  <exclude></exclude>
                  <!-- ... -->
                </excludes>
                <includes>
                  <include></include>
                  <!-- ... -->
                </includes>
              </translationFileSet>
              <translationFileSet>
                <!-- ... -->
              </translationFileSet>
              <!-- ... -->
            </translationFileSets>
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

#### 1.2.2 Parameter description

|<sub>Name</sub>|<sub>Type</sub>|<sub>Req.</sub>|<sub>Default</sub>|<sub>Description</sub>|
|--|:--:|:--:|:--:|--|
|<sub>`projectId`</sub>|<sub>Integer</sub>|<sub>Yes</sub>| |<sub>The Crowdin project Id. It can be found at Crowdin under `Tools -> API` </sub>|
|<sub>`comment`</sub>|<sub>String</sub>|<sub>No</sub>| * |<sub>The global comment to add to the top of downloaded translation files. If defined, this parameter acts as the default for all `translationFileSets` and `statusFiles`.</sub>|
|<sub>`confirm`</sub>|<sub>String</sub>|<sub>`push`</sub>| |<sub>This is required to be `true` to use the `push` goal. This parameter can be overridden on the command line with `-Dconfirm`. Any strings that exist on Crowdin but don't exist in the uploaded files will have all their translations deleted on Crowdin when pushed. As such, it's important to make sure that a push is intended. Although this parameter can be set to `true` in `pom.xml`, it is recommended not to. That way, adding `-Dconfirm` to the command line is required to be able to push.</sub>|
|<sub>`crowdinServerId`</sub>|<sub>String</sub>|<sub>Yes</sub>| |<sub>The `id` of the Maven configured `server` to be used for Crowdin authentication.</sub>|
|<sub>`httpTimeout`</sub>|<sub>Integer</sub>|<sub>No<sub>| |<sub>Timeout in seconds for HTTP operations when communicating with the Crowdin API. The default is no timeout.</sub>
|<sub>`downloadFolder`</sub>|<sub>String</sub>|<sub>Yes</sub>| |<sub>The intermediate folder used to store the downloaded files.</sub>|
|<sub>`lineSeparator`</sub>|<sub>String</sub>|<sub>No</sub>| |<sub>The global alternative line separator to apply to the downloaded files, for example `\n` or `\r\n`. If defined, this parameter acts as the default for all `translationFileSets` and `statusFiles`.</sub>|
|<sub>`projectName`</sub>|<sub>String</sub>|<sub>`push`</sub>| |<sub>This is required to use the `push` goal. The value must match the project name defined in `pom.xml`. It is a safety check to make sure you don't push to the wrong project if the configuration has been copied from another project.</sub>|
|<sub>`rootBranch`</sub>|<sub>String</sub>|<sub>No</sub>|<sub>`master`</sub>|<sub>The Git branch that should be considered the root on Crowdin (that is; not exist in a branch folder). This parameter can be overridden on the command line with `-DrootBranch=`. Any local Git branch not matching this parameter will push to and fetch from a branch folder at Crowdin.</sub>|
|<sub>`statusFiles`</sub>|<sub>List</sub>|<sub>No</sub>| |<sub>A list of one or more `statusFile` elements. A `statusFile` element represents a local status file. This is a file a file in either `properties` or `xml` format, whose content is the output of the `status` [Crowdin API method](https://support.crowdin.com/api/status/). The file contains basic information about the state of the translations per language for all files in total. Crowdin doesn't allow getting the status per file, so having more than one status file for a project would serve little purpose. See [separate definition](#1221-statusfile-parameter-description).</sub>|
|<sub>`translationFileSets`</sub>|<sub>List</sub>|<sub>Yes</sub>| |<sub>A list of one or more `translationFileSet` elements. A `translationsFileSet` element represents a local *base language file* and its set of corresponding translations in other languages. It also represents a single file on Crowdin. Only the *base language file* will be uploaded to Crowdin, and only the corresponding translated language files will be downloaded. See [separate definition](#1222-translationfileset-parameter-description).</sub>|
|<sub>`skipUntranslatedStrings`</sub>|<sub>Boolean</sub>|<sub>No</sub>|<sub>`true`</sub>|<sub>**Note:** This parameter cannot be `true` if `skipUntranslatedFiles` is `true`. Only translated strings will be included in the exported translation files. This option is not applied to text documents: `*.docx`, `*.pptx`, `*.xlsx`, etc., since missing texts may cause the resulting files to be unreadable. This parameter is not used by this plugin, it is merely passed on to Crowdin when triggering a new build.</sub>|
|<sub>`skipUntranslatedFiles`</sub>|<sub>Boolean</sub>|<sub>No</sub>|<sub>`false`</sub>|<sub>**Note:** This parameter cannot be `true` if `skipUntranslatedStrings` is `true`. Only translated files will be included in the exported translation files. This parameter is not used by this plugin, it is merely passed on to Crowdin when triggering a new build.</sub>|
|<sub>`exportApprovedOnly`</sub>|<sub>Boolean</sub>|<sub>No</sub>|<sub>`false`</sub>|<sub>Only texts that are both translated and approved will be included in the exported translation files. This will require additional efforts from your proofreaders to approve all suggestions. This parameter is not used by this plugin, it is merely passed on to Crowdin when triggering a new build.</sub>|
|<sub>`updateOption`</sub>|<sub>Enum</sub>|<sub>No</sub>|<sub>Clear</sub>|<sub>The global `Update or Restore File` [Crowdin API parameter](https://developer.crowdin.com/api/v2/#operation/api.projects.files.put). See [separate definition](#1227-updateoption-options). This parameter is not used by this plugin, it is merely passed on to Crowdin when uploading source files. If defined, this parameter acts as the default for all `translationFileSets`.</sub>|
|<sub>`replaceModifiedContext`</sub>|<sub>Boolean</sub>|<sub>No</sub>|<sub>`false`</sub>|<sub>The global option of whether to overwrite context when updating source files, even if the context has been modified on Crowdin. This parameter is not used by this plugin, it is merely passed on to Crowdin when uploading source files. If defined, this parameter acts as the default for all `translationFileSets`.</sub>|

<sub>`*` The default comment is `This file has been generated automatically, modifications will be overwritten. If you'd like to change the content, please do so at Crowdin.`</sub>

##### 1.2.2.1 `statusFile` parameter description

|<sub>Name</sub>|<sub>Type</sub>|<sub>Req.</sub>|<sub>Default</sub>|<sub>Description</sub>|
|--|:--:|:--:|:--:|--|
|<sub>`addComment`</sub>|<sub>Boolean</sub>|<sub>No</sub>|<sub>`true`</sub>|<sub>Sets whether or not a comment should be added at the top of the status file when deploying.</sub>|
|<sub>`comment`</sub>|<sub>String</sub>|<sub>No</sub>| |<sub>The comment to add at the top of the status file. If defined, this parameter overrides the corresponding global parameter.</sub>|
|<sub>`encoding`</sub>|<sub>String</sub>|<sub>No</sub>|<sub>UTF-8</sub>|<sub>The encoding to use for the status file. The default is `ISO 8859-1` if the file type is `properties`.</sub>|
|<sub>`escapeUnicode`</sub>|<sub>Boolean</sub>|<sub>No</sub>|<sub>`true`</sub>|<sub>This only applies if the type is `properties`. If `true`, Unicode characters will be encoded in the form `\u<xxxx>` where `<xxxx>` is the hexadecimal Unicode code point.</sub>|
|<sub>`lineSeparator`</sub>|<sub>String</sub>|<sub>No</sub>| |<sub>The alternative line separator to apply to the status file, for example `\n` or `\r\n`. If defined, this parameter overrides the corresponding global parameter.</sub>|
|<sub>`sortLines`</sub>|<sub>Boolean</sub>|<sub>No</sub>|<sub>`true`</sub>|<sub>This only applies if the type is `properties`. If `true`, this will sort the lines of the status file by key when deploying. The keys are "grouped" by `.`. Integer groups are sorted numerically, all other groups are sorted lexicographically. When comparing integer groups with non-integer groups, integer groups are sorted last.</sub>|
|<sub>`targetFile`</sub>|<sub>String</sub>|<sub>Yes</sub>| |<sub>The full path to this status file.</sub>|
|<sub>`type`</sub>|<sub>Enum</sub>|<sub>No</sub>|<sub>`properties`</sub>|<sub>The file type to use when deploying the status file. Valid values are `properties` and `xml`.</sub>|
|<sub>`conversions`</sub>|<sub>List</sub>|<sub>No</sub>| |<sub>A list of one or more `conversion` elements. A `conversion` element represents a "find and replace operation" that will be executed during deployment. It applies to the language codes only. See [separate definition](#1223-conversion-parameter-description).</sub>|

##### 1.2.2.2 `translationFileSet` parameter description

|<sub>Name</sub>|<sub>Type</sub>|<sub>Req.</sub>|<sub>Default</sub>|<sub>Description</sub>|
|--|:--:|:--:|:--:|--|
|<sub>`addComment`</sub>|<sub>Boolean</sub>|<sub>No</sub>|<sub>`true`</sub>|<sub>Whether or not a comment should be added at the top of translation files when deploying.</sub>|
|<sub>`baseFileName`</sub>|<sub>String</sub>|<sub>Yes</sub>| |<sub>The name or path to the "base language file" that should be uploaded to Crowdin for translation, relative to `languageFilesFolder`.</sub>|
|<sub>`comment`</sub>|<sub>String</sub>|<sub>No</sub>| |<sub>The comment to add at the top of translation files. If defined, this parameter overrides the corresponding global parameter.</sub>|
|<sub>`commentTag`</sub>|<sub>String</sub>|<sub>No</sub>|<sub>`#`</sub>|<sub>The character (sequence) to use if a comment is added during the `deploy` goal. This is not used for `Properties`, `HTML` or `XML` files.</sub>|
|<sub>`crowdinPath`</sub>|<sub>String</sub>|<sub>No</sub>| |<sub>The path from the root or branch root folder to the location of this set of files on Crowdin.</sub>|
|<sub>`encoding`</sub>|<sub>String</sub>|<sub>No</sub>|<sub>`UTF-8`</sub>|<sub>The encoding to use for the deployed translation files. The default is `ISO 8859-1` if the file type is `properties`.</sub>|
|<sub>`escapeQuotes`</sub>|<sub>Integer</sub>|<sub>No</sub>|<sub>`0`</sub>|<sub>**Only valid** if the type is `properties`. The `Escape single quote` [Crowdin parameter](https://support.crowdin.com/parsers-configuration/#parser-configuration-parameters). See [separate definition](#1224-escapequotes-options). This is not used by this plugin, and is merely passed on to Crowdin when creating new files.</sub>|
|<sub>`escapeSpecialCharacters`</sub>|<sub>Integer</sub>|<sub>No</sub>|<sub>`0`</sub>|<sub>**Only valid** if the type is `properties`. The `Escape special characters` [Crowdin parameter](https://support.crowdin.com/parsers-configuration/#parser-configuration-parameters). Defines whether any special characters (`=`, `:`, `!` and `#`) should be escaped by backslash. See [separate definition](#1225-escapespecialcharacters-options). This is not used by this plugin, and is merely passed on to Crowdin when creating new files.</sub>|
|<sub>`exportQuotes`</sub>|<sub>String</sub>|<sub>No</sub>|<sub>`single`</sub>|<sub>**Only valid** if the type is `js`. Defines what type of quotes to use when exporting JavaScript translations. See [separate definition](#1226-exportquotes-options). This is not used by this plugin, and is merely passed on to Crowdin when creating new files.</sub>|
|<sub>`escapeUnicode`</sub>|<sub>Boolean</sub>|<sub>No</sub>|<sub>`true`</sub>|<sub>**Only valid** if the type is `properties`. If `true`, Unicode characters will be encoded in the form `\u<xxxx>` where `<xxxx>` is the hexadecimal Unicode code point.</sub>|
|<sub>`exportPattern`</sub>|<sub>String</sub>|<sub>Yes</sub>| |<sub>This is used by Crowdin to generate filenames during export, and is the string specified under "Resulting file name when exported" in the file settings at Crowdin. It is also used by this plugin to parse the filenames exported from Crowdin to recognize placeholders. The placeholder codes are defined by Crowdin and are also listed in [this table](#1229-crowdin-placeholders).</sub>|
|<sub>`languageFilesFolder`</sub>|<sub>String</sub>|<sub>Yes</sub>| |<sub>The folder where this set of files is located.</sub>|
|<sub>`lineSeparator`</sub>|<sub>String</sub>|<sub>No</sub>| |<sub>An alternative line separator to apply to the translation files, for example `\n` or `\r\n`. If defined, this parameter overrides the corresponding global parameter.</sub>|
|<sub>`sortLines`</sub>|<sub>Boolean</sub>|<sub>No</sub>|<sub>`true`</sub>|<sub>This only applies if the type is `properties`. If `true`, this will sort the lines of the translation files by key when deploying. The keys are "grouped" by `.`. Integer groups are sorted numerically, all other groups are sorted lexicographically. When comparing integer groups with non-integer groups, integer groups are sorted last.</sub>|
|<sub>`targetFileName`</sub>|<sub>String</sub>|<sub>No</sub>| |<sub>The file path relative to `languageFilesFolder` to use when deploying the translation files. If left blank, the path exported by Crowdin (`exportPattern`) is used. Any placeholders used in `exportPattern` can be used. In addition, [these placeholders](#12210-additional-targetfilename-placeholders) can be used independently of what is used in  `exportPattern`.</sub>|
|<sub>`title`</sub>|<sub>String</sub>|<sub>No</sub>| |<sub>The title of this file as it should appear to translators at Crowdin.</sub>|
|<sub>`type`</sub>|<sub>Enum</sub>|<sub>No</sub>|<sub>Auto</sub>|<sub>The file type to use both when uploading to Crowdin and when processing files during the `deploy` goal. See [separate definition](#1228-crowdin-file-types). If not specified, auto-detection will be attempted based on the file extension.</sub>|
|<sub>`updateOption`</sub>|<sub>Enum</sub>|<sub>No</sub>|<sub>Clear</sub>|<sub>The `Update or Restore File` [Crowdin API parameter](https://developer.crowdin.com/api/v2/#operation/api.projects.files.put). See [separate definition](#1227-updateoption-options). This is not used by this plugin, and is merely passed on to Crowdin. If defined, this parameter overrides the corresponding global parameter.</sub>|
|<sub>`replaceModifiedContext`</sub>|<sub>Boolean</sub>|<sub>No</sub>|<sub>`false`</sub>|<sub>Whether or not to overwrite context when updating the source file, even if the context has been modified on Crowdin.</sub>|
|<sub>`writeBOM`</sub>|<sub>Boolean</sub>|<sub>No</sub>|<sub>`false`</sub>|<sub>Whether or not to write a [BOM](https://en.wikipedia.org/wiki/Byte_order_mark) (Byte Order Mark) at the beginning of the file when deploying translations files. This is only applicable to Unicode encodings, and generally isn't recommended for `UTF-8`. Despite this, some systems, like NSIS, requires a `UTF-8` BOM to be present to interpret the file as UTF-8. In such cases, set this parameter to `true`.</sub>|
|<sub>`conversions`</sub>|<sub>List</sub>|<sub>No</sub>| |<sub>A list of one or more `conversion` elements. A `conversion` element represents a "find and replace operation" that will be executed during deployment. It applies to placeholders only and must match the complete placeholder. The content of any placeholders that match will be replaced. See [separate definition](#1223-conversion-parameter-description).</sub>|
|<sub>`excludes`</sub>|<sub>List</sub>|<sub>No</sub>| |<sub>A list of one or more translation files to exclude. This is a basic filter that works on the file names exported from Crowdin, before any conversions are performed. It works like most file system searches, where the only wildcard characters are `*` and `?`.</sub>|
|<sub>`includes`</sub>|<sub>List</sub>|<sub>No</sub>| |<sub>A list of one or more translation files to include. This is a basic filter that works on the file names exported from Crowdin, before any conversions are performed. It works like most file system searches, where the only wildcard characters are `*` and `?`. If one or more `include` elements are defined, any paths that aren't included are excluded (it becomes a white-list).</sub>|

##### 1.2.2.3 `conversion` parameter description

|Name|Type|Req.|Description|
|--|:--:|:--:|--|
|`from`|String|Yes|The value to match against any placeholder|
|`to`|String|Yes|The value to replace the placeholder content with|

##### 1.2.2.4 `escapeQuotes` options

| Code | Description |
|--|--|
|0|Do not escape single quote|
|1|Escape single quote by another single quote|
|2|Escape single quote by backslash|
|3|Escape single quote by another single quote only in strings containing variables (`{0}`)|

##### 1.2.2.5 `escapeSpecialCharacters` options

| Code | Description |
|--|--|
|0|Do not escape special characters|
|1|Escape special characters by a backslash|

##### 1.2.2.6 `exportQuotes` options

| Code | Description |
|--|--|
|`single`|Output will be enclosed in single quotes|
|`double`|Output will be enclosed in double quotes|

##### 1.2.2.7 `updateOption` options
**Please note**: These options have changed names in Crowdin's API v2.

| Code | Description |
|--|--|
|`clear_translations_and_approvals`|Delete translations of changed strings (Previously `delete_translations`)|
|`keep_translations`|Preserve translations of changed strings but remove approvals of those translations if they exist (Previously `update_as_unapproved`)|
|`keep_translations_and_approvals`|Preserve translations and approvals of changed strings (Previously `update_without_changes`)|

##### 1.2.2.8 Crowdin file types

| Code | Description |
|--|--|
|`auto`|Try to detect file type by extension or MIME type|
|`android`|Android|
|`macosx`|Mac OS X / iOS|
|`resx`|.NET, Windows Phone|
|`properties`|Java|
|`gettext`|GNU GetText|
|`yaml`|Ruby On Rails|
|`php`|Hypertext Preprocessor|
|`json`|Generic JSON|
|`xml`|Generic XML|
|`ini`|Generic INI|
|`rc`|Windows Resources|
|`resw`|Windows 8 Metro|
|`resjson`|Windows 8 Metro|
|`qtts`|Nokia Qt|
|`joomla`|Joomla localizable resources|
|`chrome`|Google Chrome Extension|
|`dtd`|Mozilla DTD|
|`dklang`|Delphi DKLang|
|`flex`|Flex|
|`nsh`|NSIS Installer Resources|
|`wxl`|WiX Installer|
|`xliff`|XLIFF|
|`xliff_two`|XLIFF 2.0|
|`html`|HTML|
|`haml`|Haml|
|`txt`|Plain Text|
|`csv`|Comma Separated Values|
|`md`|Markdown|
|`mdx_v1`|MDX (v1)|
|`mdx_v2`|MDX (v2)|
|`flsnp`|MadCap Flare|
|`fm_html`|Jekyll HTML|
|`fm_md`|Jekyll Markdown|
|`mediawiki`|MediaWiki|
|`docx`|Microsoft Office, OpenOffice.org Documents, Adobe InDesign Adobe FrameMaker|
|`xlsx`|Microsoft Excel|
|`sbv`|Youtube `.sbv`|
|`properties_play`|Play Framework|
|`properties_xml`|Java Application|
|`maxthon`|Maxthon Browser|
|`go_json`|Go|
|`dita`|DITA Document|
|`mif`|Adobe FrameMaker|
|`idml`|Adobe InDesign|
|`stringsdict`|iOS|
|`plist`|Mac OS property list|
|`vtt`|Video Subtitling and WebVTT|
|`vdf`|Steamworks Localization Valve Data File|
|`srt`|SubRip `.srt`|
|`stf`|Salesforce|
|`toml`|Toml|
|`contentful_rt`|Contentful|
|`svg`|SVG|
|`js`|JavaScript|
|`coffee`|CoffeeScript|
|`ts`|TypeScript|
|`i18next_json`|i18next|
|`xaml`|XAML|
|`arb`|Application Resource Bundle|
|`adoc`|Asc|
|`fbt`|Facebook FBT|
|`ftl`|Mozilla Project Fluent|
|`webxml`|Web XML|
|`nestjs_i18n`|NestJS i18n|

##### 1.2.2.9 Crowdin placeholders
| Code | Description |
|--|--|
|`%language%`|Language name (e.g. Ukrainian).|
|`%two_letters_code%`|Language code `ISO 639-1` (i.e. `uk`).|
|`%three_letters_code%`|Language code `ISO 639-2/T` (i.e. `ukr`).|
|`%locale%`|Locale (i.e. `uk-UA`).|
|`%locale_with_underscore%`|Locale (i.e. `uk_UA`).|
|`%android_code%`|Android Locale identifier used to name `values-` folders.|
|`%osx_code%`|macOS Locale identifier used to name `.lproj` folders.|
|`%osx_locale%`|macOS Locale used to name translated resources (i.e. `uk`, `zh_Hans`).|
|`%original_file_name%`|Original file name.|
|`%file_name%`|File name without extension.|
|`%file_extension%`|Original file extension.|
|`%original_path%`|Use parent folders' names in your project to build the file path in the resulting archive.|

##### 1.2.2.10 Additional `targetFileName` placeholders
| Code | Description |
|--|--|
|`%crowdin_code%`|Crowdin language code (i.e `en-GB` or `da`).|
|`%crowdin_code_with_underscore%`|Crowdin language code with underscore (i.e `en_GB` or `da`).|
|`%shortest_iso639_code%`|The shortest `ISO 639` language code (i.e `en` or `ceb`).|

#### 1.2.3 Example project configuration
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
          <version>...</version>   
          <configuration>
            <projectId>xxxxxx</projectID>
            <projectName>Digital Media Server</projectName>
            <crowdinServerId>crowdin</crowdinServerId>
            <downloadFolder>${project.basedir}/extras/crowdin</downloadFolder>
            <translationFileSets>
              <translationFileSet>
                <title>Digital Media Server</title>
                <languageFilesFolder>${project.basedir}/src/main/resources/i18n</languageFilesFolder>
                <baseFileName>messages.properties</baseFileName>
                <exportPattern>messages_%locale_with_underscore%.properties</exportPattern>
                <targetFileName>messages_%crowdin_code_with_underscore%.properties</targetFileName>
                <updateOption>keep_translations</updateOption>
                <conversions>
                  <conversion>
                    <from>es-ES</from><to>es</to>
                  </conversion>
                  <!-- ... -->
                </conversions>
                <excludes>
                  <!-- ... -->
                </excludes>
              </translationFileSet>
            </translationFileSets>
            <statusFiles>
              <statusFile>
                <targetFile>${project.basedir}/src/main/resources/languages.properties</targetFile>
                <conversions>
                  <conversion>
                    <from>es-ES</from><to>es</to>
                  </conversion>
                  <!-- ... -->
                </conversions>
              </statusFile>
            </statusFiles>
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

## 2. Using the plugin

This plugin requires Git, and will automatically look up the current Git branch. Unless the current Git branch matches the `rootBranch` parameter, all operation will be performed against a branch with the same name on Crowdin. If no Git branch is found (e.g. checked out to a tag or detached) all goals except `deploy` will fail. This allows translation of a branch while it's being worked on and merging of the translations at Crowdin when the branch is merged. For further information about versions management and branches on Crowdin, see the [Crowdin documentation](https://support.crowdin.com/articles/versions-management/).

If the configuration is correct, you can execute a goal with:

`mvn crowdin-maven-plugin:<goal> [-D<property>=<value>]`

The prefix or alias `crowdin` will be resolved to this plugin, unless another plugin configured in the same `pom.xml` use the same alias and registers it first. That means that it is very likely that you can instead execute a goal with:

`mvn crowdin:<goal> [-D<property>=<value>]`

This short form will be used in the descriptions below.



### 2.1 Pushing strings for translation to Crowdin

|*Goal*|*Command*|*Description*|
|--|--|--|
|**push** | `mvn crowdin:push -Dconfirm=true` | Uploads the _base language_ file to Crowdin. Any strings already present on Crowdin that's missing in the uploaded file will be deleted from Crowdin together with all corresponding translations. To avoid accidental pushes, an extra argument `confirm` is required for `push`.|

### 2.2 Getting translations from Crowdin

|*Goal* | *Command* | *Description*|
|--|--|--|
|**fetch** | `mvn crowdin:fetch` | Builds a downloadable zip file containing the latest translations, then downloads and extracts the zip file from Crowdin to `downloadFolder`.|
|**deploy** | `mvn crowdin:deploy` | Applies any transformations and deploys the files from `downloadFolder` into their intended locations as defined by the [translationsFileSets](#1222-translationfileset-parameter-description) and the [statusFiles](#1221-statusfile-parameter-description).|
|**pull** | `mvn crowdin:pull` | Executes `fetch` and `deploy` in sequence. This is a convenience goal combining the individual steps needed to get the latest translations from Crowdin built and deployed into your local project.|

### 2.3 Cleaning the intermediate folder

|*Goal* | *Command* | *Description*|
|--|--|--|
|**clean** | `mvn crowdin:clean` | Deletes all content in `downloadFolder`.|

The `clean` goal also binds to the `clean` phase, which means that it's automatically executed when `mvn clean` is run.
