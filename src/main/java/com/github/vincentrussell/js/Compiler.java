package com.github.vincentrussell.js;

import org.apache.commons.io.IOUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Compiler class used to compile the jsx
 */
public class Compiler {

    private String babelLocation = "classpath:babel.6.15.0.js";

    private final ScriptEngineManager manager = new ScriptEngineManager();
    private final ScriptEngine service = manager.getEngineByName("nashorn");
    private final SimpleBindings bindings = new SimpleBindings();

    private Compiler(){}

    private void setup() {
        try {
            service.eval( "load('"+ babelLocation +"');",bindings);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    private void setBabelLocation(String babelLocation) {
        this.babelLocation = babelLocation;
    }

    /**
     * compiple the incoming jsx inputstream
     * @param inputStream the jsx stream
     * @param outputStream the destination stream
     * @throws ScriptException
     * @throws IOException
     */
    public void compileReact(InputStream inputStream, OutputStream outputStream) throws ScriptException, IOException {
        notNull(inputStream,"inputStream can not be null");
        notNull(outputStream,"outputStream can not be null");
        String input = IOUtils.toString(inputStream,"UTF-8");
        bindings.put("input", input);
        Object output = service.eval("Babel.transform(input, { presets: ['react'] }).code", bindings);
        IOUtils.write(output.toString(),outputStream);
    }

    /**
     * Builder for Compiler
     */
    public static class Builder {
        private String babelLocation;

        /**
         * set location for babel, can be on on the classpath with classpath:&lt;location&gt;
         * or a file location
         * @param babelLocation the location
         * @return
         */
        public Builder setBabelLocation(String babelLocation) {
            this.babelLocation = babelLocation;
            return this;
        }


        /**
         * build the Compiler
         * @return built compiler
         */
        public Compiler build() {
            Compiler compiler = new Compiler();
            if (babelLocation !=null) {
                compiler.setBabelLocation(babelLocation);
            }
            compiler.setup();
            return compiler;
        }
    }

}
