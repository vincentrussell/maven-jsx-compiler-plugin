package com.github.vincentrussell.js;

import com.github.approval.Approvals;
import com.github.approval.reporters.Reporters;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import javax.script.ScriptException;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CompilerTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        Approvals.setReporter(Reporters.console());
    }


    private Path getApprovalPath(String testName) {
        final String basePath = Paths.get("src", "test", "resources", "approvals", CompilerTest.class.getSimpleName()).toString();
        return Paths.get(basePath, testName);
    }

    @Test
    public void nullInputStream() throws ScriptException, IOException {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("inputStream can not be null");
        new Compiler.Builder().build().compileReact(null,new ByteArrayOutputStream());
    }

    @Test
    public void nullOutputStream() throws ScriptException, IOException {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("outputStream can not be null");
        new Compiler.Builder().build().compileReact(new ByteArrayInputStream("input".getBytes()),null);
    }

    @Test
    public void simpleJsxTest() throws ScriptException, IOException {
        Compiler compiler = new Compiler.Builder().build();
        try (InputStream inputStream = this.getClass().getResourceAsStream("/jsx/test1.jsx");
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            compiler.compileReact(inputStream,outputStream);
            Approvals.verify(outputStream.toString("UTF-8"), getApprovalPath("simpleJsxTest"));
        }
    }

    @Test
    public void simpleJsxTest2() throws ScriptException, IOException {
        Compiler compiler = new Compiler.Builder().build();
        try (InputStream inputStream = this.getClass().getResourceAsStream("/jsx/test2.jsx");
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            compiler.compileReact(inputStream,outputStream);
            Approvals.verify(outputStream.toString("UTF-8"), getApprovalPath("simpleJsxTest2"));
        }
    }

    @Test
    public void setBabelLocation() throws IOException, ScriptException {
        File babelLocation = temporaryFolder.newFile("babel.js");
        try (InputStream babelInputStream = this.getClass().getResourceAsStream("/test/babel.some_other_version.js");
        FileOutputStream babelOutputStream = new FileOutputStream(babelLocation)) {
            IOUtils.copy(babelInputStream,babelOutputStream);
        }
        try (InputStream inputStream = this.getClass().getResourceAsStream("/jsx/test2.jsx");
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Compiler compiler = new Compiler.Builder()
                    .setBabelLocation(babelLocation.getAbsolutePath())
                    .build();
            compiler.compileReact(inputStream,outputStream);
            Approvals.verify(outputStream.toString("UTF-8"), getApprovalPath("simpleJsxTest2"));
        }
    }


    @Test(expected = ScriptException.class)
    public void invalidJavascript() throws ScriptException, IOException {
        Compiler compiler = new Compiler.Builder().build();
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            compiler.compileReact(new ByteArrayInputStream("some invalid javascript".getBytes()),outputStream);
        }
    }



}
