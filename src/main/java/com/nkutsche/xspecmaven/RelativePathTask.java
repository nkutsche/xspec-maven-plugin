package com.nkutsche.xspecmaven;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.File;
import java.nio.file.Path;

public class RelativePathTask extends Task {

    private File from = null;
    private File to = null;
    private String property;

    /**
     * Sets the starting directory from which the relative path should be calculated.
     * @param from The starting directory.
     */
    public void setFrom(File from) {
        this.from = from;
    }


    /**
     * Sets the end directory to which the relative path should be calculated.
     * @param to The end directory.
     */
    public void setTo(File to) {
        this.to = to;
    }

    /**
     * Sets the name of the Ant property to which the relative path will be written.
     * @param property The property name.
     */
    public void setProperty(String property) {
        this.property = property;
    }

    @Override
    public void execute() throws BuildException {
        // 1. Validate inputs
        if (property == null || property.isEmpty()) {
            throw new BuildException("The 'property' attribute is required.");
        }

        File startDir = (from == null) ? getProject().getBaseDir() : from;

        if (to == null) {
            throw new BuildException("The 'to' attribute is required.");
        }

        log("Target path '" + to.getAbsolutePath() + "'");
        log("Base path '" + from.getAbsolutePath() + "'");

        Path relPath = from.toPath().relativize(to.toPath());

        log("Relative path: '" + relPath.toString() + "'");
        getProject().setProperty(property, relPath.toString());

    }
}
