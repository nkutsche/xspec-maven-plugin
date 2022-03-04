# Maven Plugin for XSpec

This project provides an alternative Maven Plugin to execute XSpec scripts. This plugin is just a Maven Plugin wrapper to execute the native XSpec framework by an internal ANT process. **Note:** Use this plugin only if the [main plugin](https://github.com/xspec/xspec-maven-plugin-1) does not cover your needs.

## Release notes

### 2.0.0

* Update of used XSpec framework to v1.6.0 - and use it as fallback only.
* Added the possibility to use newer XSpec framework versions (2.0.7+).
* Detects XSpec test type (xslt, schematron, xquery) automatically per each XSpec file.
* Configuration extensions:
    * Added possibility to specify properties for the XSpec Ant call (`<xspecProperties>`).
    * Added project runtime classpath as `xspec.additional.classpath` (`<addDependenciesToClasspath>`)
    * Removed `<testType>` as it is detected now directly from the XSpec file.
* Handling of catalog fixed.

### 1.0.1

* Minor fixes of Readme handling
* Fixes Release procedure

### 1.0.0

* First public release


## Usage

As this is not published on Maven Central, you have do add this project as plugin repository to your `pom.xml`:

```xml
<pluginRepositories>
    <pluginRepository>
        <id>com.nkutsche</id>
        <url>https://raw.github.com/nkutsche/xspec-maven-plugin/master/bin/releases/</url>
    </pluginRepository>
</pluginRepositories>
```

Then you can add this as a minimal configuration:

```xml
<plugin>
    <groupId>com.nkutsche</groupId>
    <artifactId>xspec-maven-plugin</artifactId>
    <version>2.0.0</version>
    <executions>
        <execution>
            <goals>
                <goal>run-xspec</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### XSpec Version

By default this plugin uses XSpec 1.6.0. To use a newer version you can add it as a plugin dependency:

```xml
<dependency>
    <groupId>io.xspec</groupId>
    <artifactId>xspec</artifactId>
    <version>{xspec.version}</version>
    <classifier>enduser-files</classifier>
    <type>zip</type>
</dependency>
```


Please make sure you use the classifier `enduser-files` and as type `zip`! Supported are XSpec v2.0.7+. 


### Plugin configuration

This is the default configuration:

```xml
<configuration>
<!--  If true, it skips the test runs. Connect to Maven property ${skipTests} -->
    <skipTests>false</skipTests>
<!--  Working directory for the internal ant processes  -->
    <workingDir>${project.build.directory}/xspecmaven</workingDir>
<!--  Working directory to store the temporary files of the XSpec framework  -->
    <xspecTempDir>${project.build.directory}/xspecmaven/xspec-temp-files</xspecTempDir>
<!--  Directory to search for XSpec files  -->
    <testDir>${project.basedir}/src/test/xspec</testDir>
<!--  Whitelist patterns to search for XSpec files in {testDir}  -->
    <includes>
        <include>**/*.xspec</include>
    </includes>
<!--  Blacklist patterns to exclude files from search for XSpec files in {testDir}  -->
    <excludes>
        <exclude>**/*.xspec-compiled.xspec</exclude>
    </excludes>
<!--  Catalog file to resolve URLs -->
    <catalogFile>${catalog.filename}</catalogFile>
<!--  Directory for generating surefire reports (Only if {generateSurefireReport} == true!) -->
    <surefireReportDir>${project.build.directory}/surefire-reports</surefireReportDir>
<!--  Directory to generate the XSpec reports -->
    <xspecReportDir>${project.build.directory}/xspec-reports</xspecReportDir>
<!--  Boolean value to generate surefire reports -->
    <generateSurefireReport>false</generateSurefireReport>
    
<!--  A list of properties which is passed to the XSpec ant process, see  https://github.com/xspec/xspec/wiki/Running-with-Ant#ant-properties
    Note: use this carefully. In the most cases the properties should be set by the plugin.
-->
    <xspecProperties>
        <!-- <saxon.custom.options>...</saxon.custom.options> -->
    </xspecProperties>   
    
<!--  Adds the project and dependency artifacts to the XSpec framework (e.g. to enable extension functions) -->
    <addDependenciesToClasspath>true</addDependenciesToClasspath>    
</configuration>
```


