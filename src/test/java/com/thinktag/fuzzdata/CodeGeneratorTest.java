package com.thinktag.fuzzdata;

import com.thinktag.fuzzdata.util.CodeGenerator;
import com.thinktag.fuzzdata.util.ObjectGenerator;
import org.junit.Test;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CodeGeneratorTest {

    @Test
    public void test() throws Exception {

        CodeGenerator cg = new CodeGenerator("/tmp/test");
        List<String> generatedClassNames = cg.generateCodeGetClasses(getSchema("/domain.json"), "Customer");
        cg.compileCode();
        ObjectGenerator og = new ObjectGenerator("/tmp/test", new ArrayList<>());
        Object o = og.generateObject( "Customer",new HashSet<>(generatedClassNames));
        System.out.println(og.writeJson(o));

    }

    private String getSchema(String schemaName) throws IOException {
        return StreamUtils.copyToString(getClass().getResourceAsStream(schemaName), StandardCharsets.UTF_8);

    }


}
