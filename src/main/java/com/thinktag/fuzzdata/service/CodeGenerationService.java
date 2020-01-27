package com.thinktag.fuzzdata.service;

import com.thinktag.fuzzdata.util.CodeGenerator;
import com.thinktag.fuzzdata.util.ObjectGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CodeGenerationService {

    @Value("${code.location}")
    String codeLocation;

    @Value("classpath:strings.txt")
    Resource fuzzyStrings;

    List<String> base64Strings = new ArrayList<>();

    public List<String> getBase64Strings()throws IOException {
        synchronized (base64Strings) {
            if (base64Strings.isEmpty()) {
                List<String> lines = Files.readAllLines(fuzzyStrings.getFile().toPath());
                lines.stream().filter(l -> !l.startsWith("#")).forEach(o -> base64Strings.add(o));
            }
            return base64Strings;
        }
    }


    public void generateCode(String schema, String rootEntity) throws IOException {
        CodeGenerator cg = new CodeGenerator(codeLocation);
        List<String> generatedClassNames = cg.generateCodeGetClasses(schema, rootEntity);
        cg.compileCode();
    }

    public String generateObject(String rootEntity) throws Exception {
        ObjectGenerator og = new ObjectGenerator(codeLocation, getBase64Strings());
        Object o = og.generateObject(rootEntity, getClassNames());
        return og.writeJson(o);
    }

    private Set<String> getClassNames() {
        Set<String> set = new HashSet<>();
        String files[] = new File(codeLocation).list();
        for (String file : files) {
            set.add(file.replaceAll(".java", "").replaceAll(".class", ""));
        }
        return set;
    }
}
