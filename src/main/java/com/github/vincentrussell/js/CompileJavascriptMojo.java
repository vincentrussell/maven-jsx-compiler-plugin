package com.github.vincentrussell.js;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import javax.script.ScriptException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.apache.commons.lang3.Validate.isTrue;

@Mojo( name = "compile", defaultPhase = LifecyclePhase.COMPILE, threadSafe = false, requiresDependencyResolution = ResolutionScope.RUNTIME )
public class CompileJavascriptMojo extends AbstractMojo {

    //config items
    @Parameter(property = "babelLocation", required = false)
    protected String babelLocation = null;
    @Parameter(property = "jsxFiles", required = true)
    protected JsxFile[] jsxFiles = new JsxFile[0];
    // ^^^^^ config items ^^^^^^

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            compileJsxFiles();
        } catch (IOException | ScriptException e) {
            throw new MojoExecutionException(e.getMessage(),e);
        }

    }

    private void compileJsxFiles() throws IOException, ScriptException {
        if (jsxFiles!=null) {
            Compiler.Builder builder = new Compiler.Builder();
            if (babelLocation!=null) {
                builder.setBabelLocation(babelLocation);
            }
            Compiler compiler = builder.build();
            for (JsxFile jsxFile : jsxFiles) {
                isTrue(jsxFile.getSourceFile()!=null && jsxFile.getSourceFile().exists(),
                        "jsx source file doesn't exist: " + jsxFile.toString());
                isTrue(jsxFile.getDestinationFile()!=null,
                        "jsx destination file doesn't exist: " + jsxFile.toString());

                jsxFile.getDestinationFile().getParentFile().mkdirs();

                try(FileInputStream fileInputStream = new FileInputStream(jsxFile.getSourceFile());
                    FileOutputStream fileOutputStream = new FileOutputStream(jsxFile.getDestinationFile())) {
                    compiler.compileReact(fileInputStream,fileOutputStream);
                }
            }
        }
    }
}
