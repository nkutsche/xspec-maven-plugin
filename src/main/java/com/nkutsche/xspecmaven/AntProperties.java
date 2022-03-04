package com.nkutsche.xspecmaven;

public interface AntProperties {

    //    SOURCE
    String PROJECT_DIR = "project.dir";
    String TEST_DIR = "test.dir";
    String INCLUDES = "includes";
    String EXCLUDES = "excludes";

    //    Configuration
    String TEST_TYPE = "test.type";
    String XSPEC_PROPERTIES = "xspec.properties";

    //    PROCESS FILES
    String ANT_FILE = "ant.file";
    String XSPEC_PACKAGE = "xspec.package";
    String SCHXSLT_PACKAGE = "schxslt.package";
    String SCHXSLT_ROOT = "schxslt.root";
    String XSPEC_FRAMEWORK = "xspec.framework";
    String BUILD_DIR = "build.dir";
    String WORKING_DIR = "working.dir";
    String TEMP_DIR = "temp.dir";
    String CLASS_PATH = "java.class.path";



    //    OUTPUT
    String REPORT_DIR = "report.dir";
    String GENERATE_SUREFIRE_REPORTS = "generateSurefireReport";
    String SUREFIRE_REPORT_DIR = "surefireReportDir";
}
