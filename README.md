# Maven Plugin for XSpec

This project provides an alternative Maven Plugin to execute XSpec scripts. This plugin is just a Maven Plugin wrapper to execute the native XSpec framework by an internal ANT process. **Note:** Use this plugin only if the [main plugin](https://github.com/xspec/xspec-maven-plugin-1) does not cover your needs.

Add this as minimal configuration to your `pom.xml`:

```xml
<plugin>
    <groupId>com.nkutsche</groupId>
    <artifactId>xspec-maven-plugin</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <executions>
        <execution>
            <goals>
                <goal>run</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Plugin configuration

This is the default configuration:

```xml
<configuration>
<!--  Working directory for the internal ant processes  -->
    <workingDir>${project.build.directory}/xspecmaven</workingDir>
<!--  Working directory to store the temporary files of the XSpec framework  -->
    <xspecTempDir>${project.build.directory}/xspecmaven/xspec-temp-files</xspecTempDir>
<!--  Directory to search for XSpec files  -->
    <testDir>${project.basedir}/src/test/xspec/schematron</testDir>
<!--  Whitelist patterns to search for XSpec files in {testDir}  -->
    <includes>
        <include>*.xspec</include>
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
<!--  Type of XSPec tests - posible values are: SCHEMATRON, XSLT, XQUERY -->
    <testType>SCHEMATRON</testType>
</configuration>
```

