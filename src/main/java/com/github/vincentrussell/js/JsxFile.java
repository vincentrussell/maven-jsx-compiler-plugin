package com.github.vincentrussell.js;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

import static org.apache.commons.lang3.Validate.notNull;

public class JsxFile {
    @Parameter(property = "sourceFile", required = true)
    private File sourceFile;

    @Parameter(property = "destinationFile", required = true)
    private File destinationFile;

    public File getSourceFile() {
        notNull(sourceFile,"sourceFile is null");
        return sourceFile;
    }

    public File getDestinationFile() {
        notNull(destinationFile,"destinationFile is null");
        return destinationFile;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("sourceFile", sourceFile).
                append("destinationFile", destinationFile).
                toString();
    }

}
