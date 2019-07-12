package com.nkutsche.xspecmaven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.*;
import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.io.*;
import java.net.URL;
import java.util.Properties;

import static com.nkutsche.xspecmaven.AntProperties.*;

@Mojo(name = "run", defaultPhase = LifecyclePhase.TEST)
public class XSpecMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.directory}/xspecmaven", property = "com.nkutsche.xspecmaven.workingDir")
    private File workingDir;

    @Parameter(defaultValue = "${com.nkutsche.xspecmaven.workingDir}/xspec-temp-files")
    private File xspecTempDir;

    @Parameter(defaultValue = "${project.basedir}/src/test/xspec/schematron")
    private File testDir;

    @Parameter(defaultValue = "**/*.xspec")
    private String[] includes;

    @Parameter(alias = "excludes", defaultValue = "**/*.xspec-compiled.xspec")
    public String[] excludes;

    @Parameter(defaultValue = "${catalog.filename}")
    public File catalogFile;

    @Parameter(defaultValue = "${project.build.directory}/surefire-reports")
    public File surefireReportDir;

    @Parameter(defaultValue = "${project.build.directory}/xspec-reports")
    public File xspecReportDir;

    @Parameter(defaultValue = "${project.build.directory}", readonly = true)
    public File mvnBuildDir;

    @Parameter(defaultValue = "false")
    public Boolean generateSurefireReport;

    @Parameter(defaultValue = "${project.basedir}")
    private File projectBaseDir;

    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    private MavenProject project;

    @Parameter( defaultValue = "${plugin}", readonly = true )
    private PluginDescriptor pluginDescriptor;

    private enum TestType {
        SCHEMATRON, XSLT, XQUERY
    }
    @Parameter(defaultValue = "SCHEMATRON")
    private TestType testType;

    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info("Start XSpec for Maven and Schematron");




        try {
            Properties antProperties = extractResources(initProperties());


            Project p = new Project();
            p.addBuildListener(new BuildListener() {
                public void buildStarted(BuildEvent buildEvent) {

                }

                public void buildFinished(BuildEvent buildEvent) {

                }

                public void targetStarted(BuildEvent buildEvent) {
                    messageLogged(buildEvent);
                }

                public void targetFinished(BuildEvent buildEvent) {

                }

                public void taskStarted(BuildEvent buildEvent) {
                    messageLogged(buildEvent);
                }

                public void taskFinished(BuildEvent buildEvent) {

                }

                public void messageLogged(BuildEvent buildEvent) {
                    String msg = buildEvent.getMessage();
                    switch (buildEvent.getPriority()){
                        case Project.MSG_ERR:
                            getLog().error(msg);
                            break;
                        case Project.MSG_WARN:
                            getLog().warn(msg);
                            break;
                        case Project.MSG_INFO:
                            getLog().info(msg);
                            break;
                        case Project.MSG_VERBOSE:
                        case Project.MSG_DEBUG:
                        default:
                            getLog().debug(msg);
                            break;
                    }
                }
            });
            ProjectHelper.configureProject(
                    addPropertiesToAntProject(p, antProperties),
                    new File(antProperties.getProperty(ANT_FILE)));





            getLog().info("Start nested ANT process...");
            p.executeTarget(p.getDefaultTarget());
        } catch (IOException e) {
            e.printStackTrace();
            throw new MojoExecutionException("Execution failed because of a fatal error", e);
        }
    }

    private Project addPropertiesToAntProject(Project project, Properties properties){

        for (Object keyObj:
             properties.keySet()) {
            String key = keyObj.toString();
            project.setUserProperty(key, properties.getProperty(key));
        }

        return project;

    }

    private String verfiyDependencies() throws MojoExecutionException {
        String classPath = "";
        ClassRealm classRealm = pluginDescriptor.getClassRealm();
        try {
            Class<?> transform = classRealm.loadClass("net.sf.saxon.Transform");

            for(URL url: classRealm.getURLs()) {
                classPath += url.getFile() + ";";
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new MojoExecutionException("Could not find Saxon. Please add a Saxon version as dependency.");
        }

        getLog().debug(classPath);

        return classPath;
    }

    private Properties initProperties() throws MojoExecutionException {
        Properties properties = new Properties();


        properties.setProperty(PROJECT_DIR, projectBaseDir.getAbsolutePath());
        properties.setProperty(TEST_DIR, testDir.getAbsolutePath());
        if(catalogFile != null)
            properties.setProperty(CATALOG, catalogFile.getAbsolutePath());

        properties.setProperty(INCLUDES, filelistToString(this.includes));

        properties.setProperty(EXCLUDES, filelistToString(this.excludes));

        String testTypeValue;
        switch (testType){
            case XSLT:
                testTypeValue = "t";
                break;
            case XQUERY:
                testTypeValue = "q";
                break;
            case SCHEMATRON:
            default:
                testTypeValue = "s";
                break;
        }
        properties.setProperty(TEST_TYPE, testTypeValue);


        properties.setProperty(ANT_FILE, new File(workingDir, "build.xml").getAbsolutePath());
        properties.setProperty(XSPEC_PACKAGE, new File(workingDir, "xspec-1.3.0.zip").getAbsolutePath());

        properties.setProperty(XSPEC_FRAMEWORK, new File(workingDir, "xspec-framework/xspec-1.3.0").getAbsolutePath());

        properties.setProperty(BUILD_DIR, mvnBuildDir.getAbsolutePath());
        properties.setProperty(WORKING_DIR, workingDir.getAbsolutePath());
        properties.setProperty(TEMP_DIR, xspecTempDir.getAbsolutePath());
        properties.setProperty(CLASS_PATH, verfiyDependencies());

        properties.setProperty(REPORT_DIR, xspecReportDir.getAbsolutePath());

        if(generateSurefireReport){
            properties.setProperty(GENERATE_SUREFIRE_REPORTS, "true");
            properties.setProperty(SUREFIRE_REPORT_DIR, surefireReportDir.getAbsolutePath());
        }

        return properties;
    }


    private Properties extractResources(Properties properties) throws IOException, MojoExecutionException {

        getLog().debug("Tempfolder: " + workingDir.toString());

        workingDir.mkdirs();

        getLog().debug("Created temp folder successfully!");

        extractResources("/build.xml", new File(properties.getProperty(ANT_FILE)));
        extractResources("/xspec-1.3.0.zip", new File(properties.getProperty(XSPEC_PACKAGE)));

        return properties;

    }

    private String extractResources(String src, File out) throws IOException, MojoExecutionException {
        getLog().debug("Extract " + src + " to " + out);

        InputStream stream = XSpecMojo.class.getResourceAsStream(src);
        if(stream == null){
            throw new MojoExecutionException("Could not find source " + src);
        }

        FileOutputStream outputStream = new FileOutputStream(out);
        IOUtils.copy(stream, outputStream);

        getLog().debug("Extraction of " + src + " successufll!");

        return out.getAbsolutePath();
    }

    private String filelistToString(String[] files){
        String result = "";
        for (String incl:
                files) {
            result += incl + ",";
        }
        return result;
    }
}
