package com.thinktag.fuzzdata.util;

import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import org.apache.commons.lang.math.IntRange;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DummyValues {

    static Faker faker = new Faker();

    public static void assign(Object o, Field f, List<String> fuzzy)throws Exception{
        f.setAccessible(true);
        Class<?> c =f.getType();
        switch(c.getName()){
            case "java.lang.String":
                f.set(o, fuzzy.get(new Random().nextInt(fuzzy.size()-1)));
                break;
            case "java.lang.Integer":
                f.set(o, new Integer(1));
                break;
                default:
                    f.set(o, c.newInstance());
                    break;
        }
    }

    private static String getRandomString(){

        return faker.letterify("????test");
    }
}
