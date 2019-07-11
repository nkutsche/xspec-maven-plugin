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
import org.apache.tools.ant.launch.Launcher;
import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@Mojo(name = "run", defaultPhase = LifecyclePhase.TEST)
public class XSpecMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.directory}/xspecmaven")
    private File tempFolder;

    @Parameter(defaultValue = "${project.basedir}/src/test/xspec/schematron")
    private File xspecFolder;

    @Parameter(defaultValue = "**/*.xspec")
    private String[] includes;

    @Parameter(alias = "excludes")
    public List<String> excludes;

    @Parameter(defaultValue = "${catalog.filename}")
    public String catalogFile;

    @Parameter(defaultValue = "${project.build.directory}/surefire-reports")
    public File surefireReportDir;

    @Parameter(defaultValue = "false")
    public Boolean generateSurefireReport;

    @Parameter(defaultValue = "${project.basedir}")
    private File projectBaseDir;

    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    private MavenProject project;

    @Parameter( defaultValue = "${plugin}", readonly = true )
    private PluginDescriptor pluginDescriptor;

    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info("Start XSpec for Maven and Schematron");


        for (String incl:
             this.includes) {
            getLog().info(incl);
        }


        try {
            extractResources();

            Project p = new Project();
            p.addBuildListener(new BuildListener() {
                public void buildStarted(BuildEvent buildEvent) {
                }

                public void buildFinished(BuildEvent buildEvent) {

                }

                public void targetStarted(BuildEvent buildEvent) {

                }

                public void targetFinished(BuildEvent buildEvent) {

                }

                public void taskStarted(BuildEvent buildEvent) {

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
//            p.setCoreLoader(verfiyDependencies());
            ProjectHelper.configureProject(p, new File(tempFolder, "build.xml"));
            p.setUserProperty("xspec.dir", xspecFolder.getAbsolutePath());
            p.setUserProperty("project.dir", projectBaseDir.getAbsolutePath());
            p.setUserProperty("java.class.path", verfiyDependencies());
            getLog().info("Start ant...");
            p.executeTarget(p.getDefaultTarget());
        } catch (IOException e) {
            e.printStackTrace();
            throw new MojoExecutionException("Execution failed because of fatal error", e);
        }
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


    private void extractResources() throws IOException, MojoExecutionException {

        getLog().debug("Tempfolder: " + tempFolder.toString());

        tempFolder.mkdirs();

        getLog().debug("Created temp folder successfully!");

        extractResources("/build.xml", "build.xml");
        extractResources("/xspec-1.3.0.zip", "xspec-1.3.0.zip");

    }

    private void extractResources(String src, String out) throws IOException, MojoExecutionException {
        getLog().debug("Extract " + src + " to " + out);

        InputStream stream = XSpecMojo.class.getResourceAsStream(src);
        if(stream == null){
            throw new MojoExecutionException("Could not find source " + src);
        }
        FileOutputStream outputStream = new FileOutputStream(new File(tempFolder, out));
        IOUtils.copy(stream, outputStream);

        getLog().debug("Extraction of " + src + " successufll!");
    }
}
