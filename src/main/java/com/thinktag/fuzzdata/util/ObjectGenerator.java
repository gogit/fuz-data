package com.thinktag.fuzzdata.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ObjectGenerator {

    private String location;
    List<String> base64Strings=new ArrayList<>();

    public ObjectGenerator(String location, List<String> base64Strings){
        this.location = location;
        this.base64Strings.addAll(base64Strings);
    }

    public Object generateObject(String domainRoot,Set<String> generatedClassNames)throws Exception{

        Object o = loadClass(location, domainRoot).newInstance();

        ObjectGraph.visitor(new ObjectGraph.Visitor() {
            @Override
            public boolean visit(Object object, Class<?> clazz)throws Exception {

                Field fields[] = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if ("additionalProperties".equals(field.getName())) {
                        continue;
                    }
                    System.out.print(field.getName());
                    System.out.println(" "+field.getType().getName());
                    DummyValues.assign(object, field, base64Strings);
                }
                return false;
            }
        }).includeClasses(generatedClassNames).traverse(o);
        return o;
    }


    public Class<?> loadClass(String location, String domainRoot)throws Exception{
        File classesDir = new File(location);
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { classesDir.toURI().toURL() });
        List<Class<?>> c = new ArrayList<>();
        return Class.forName(domainRoot, true, classLoader);
    }

    public String writeJson(Object ob) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        return om.writeValueAsString(ob);
    }
}
