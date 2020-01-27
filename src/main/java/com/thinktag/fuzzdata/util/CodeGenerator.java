package com.thinktag.fuzzdata.util;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson2Annotator;
import org.jsonschema2pojo.SchemaGenerator;
import org.jsonschema2pojo.SchemaMapper;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.rules.RuleFactory;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CodeGenerator {

    private String location;

    public CodeGenerator(String location){
        this.location = location;
    }

    public void compileCode(){

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        File files = new File(location);

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        Iterable<? extends JavaFileObject> compilationUnits1 =
                fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files.listFiles()));
        compiler.getTask(null, fileManager, null, null, null, compilationUnits1).call();
    }

    public List<String> generateCodeGetClasses(String schemaJson, String className) throws IOException {
        File files = new File(location);
        if(files.listFiles()!=null) {
            Arrays.stream(files.listFiles()).forEach(f -> {
                f.delete();
            });
        }
        JCodeModel codeModel = new JCodeModel();

        GenerationConfig config = new DefaultGenerationConfig() {
            @Override
            public boolean isGenerateBuilders() { // set config option by overriding method
                return true;
            }
        };

        SchemaMapper mapper = new SchemaMapper(new RuleFactory(config, new Jackson2Annotator(config), new SchemaStore()), new SchemaGenerator());
        mapper.generate(codeModel, className, "", schemaJson);
        codeModel.build(files);
        //FOR debugging
        //debug(codeModel);
        List<String> classNames = new ArrayList<>();
        String []sourceFiles =files.list();
        for(String sourceFile: sourceFiles){
            classNames.add(sourceFile.replaceAll(".java", ""));
        }
        return classNames;
    }


    private void debug(JCodeModel codeModel)throws IOException{
        codeModel.build(new CodeWriter(){
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            @Override
            public OutputStream openBinary(JPackage jPackage, String s) throws IOException {
                return bos;
            }

            @Override
            public void close() throws IOException {
                System.out.println(new String(bos.toByteArray()));
                bos.close();
                bos = new ByteArrayOutputStream();
            }
        });

        //System.out.println(new String(bos.toByteArray()));
    }
}
