# Maven Plugin for XSpec

This project provides an alternative Maven Plugin to execute XSpec scripts. This plugin is just a Maven Plugin wrapper to execute the native XSpec framework by an internal ANT process. **Note:** Use this plugin only if the [main plugin](https://github.com/xspec/xspec-maven-plugin-1) does not cover your needs.

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
    <version>${project.version}</version>
    <executions>
        <execution>
            <goals>
                <goal>run-xspec</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Plugin configuration

This is the default configuration:

```xml
<configuration>
<!--  If true, it skips the test runs. Connect to Maven property \${skipTests} -->
    <skipTests>false</skipTests>
<!--  Working directory for the internal ant processes  -->
    <workingDir>\${project.build.directory}/xspecmaven</workingDir>
<!--  Working directory to store the temporary files of the XSpec framework  -->
    <xspecTempDir>\${project.build.directory}/xspecmaven/xspec-temp-files</xspecTempDir>
<!--  Directory to search for XSpec files  -->
    <testDir>\${project.basedir}/src/test/xspec/schematron</testDir>
<!--  Whitelist patterns to search for XSpec files in {testDir}  -->
    <includes>
        <include>*.xspec</include>
    </includes>
<!--  Blacklist patterns to exclude files from search for XSpec files in {testDir}  -->
    <excludes>
        <exclude>**/*.xspec-compiled.xspec</exclude>
    </excludes>
<!--  Catalog file to resolve URLs -->
    <catalogFile>\${catalog.filename}</catalogFile>
<!--  Directory for generating surefire reports (Only if {generateSurefireReport} == true!) -->
    <surefireReportDir>\${project.build.directory}/surefire-reports</surefireReportDir>
<!--  Directory to generate the XSpec reports -->
    <xspecReportDir>\${project.build.directory}/xspec-reports</xspecReportDir>
<!--  Boolean value to generate surefire reports -->
    <generateSurefireReport>false</generateSurefireReport>
<!--  Type of XSPec tests - posible values are: SCHEMATRON, XSLT, XQUERY -->
    <testType>SCHEMATRON</testType>
</configuration>
```

