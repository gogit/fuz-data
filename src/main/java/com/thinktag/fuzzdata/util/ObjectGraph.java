package com.thinktag.fuzzdata.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public  class ObjectGraph {

    final Map<Object, Class<?>> visited = new IdentityHashMap<>();
    final Queue<Object> toVisit = new ArrayDeque<>();
    private final Set<String> includedClasses = new HashSet<>();
    final Visitor visitor;

    public interface Visitor {
        /**
         * Called for each Object visited.
         *
         * @param object The object being visited
         * @param clazz The type of field this object was originally found in. This may differ from
         *     object.getClass() as an field defined as an Object, by hold any kind of class.
         * @return return true if you wish the graph transversal to stop, otherwise it will continue.
         */
        boolean visit(Object object, Class<?> clazz)throws Exception;
    }

    ObjectGraph(Visitor visitor) {
        this.visitor = visitor;
    }

    public static ObjectGraph visitor(Visitor visitor) {
        return new ObjectGraph(visitor);
    }

    /**
     * Conducts a breath first search of the object graph.
     *
     * @param root the object to start at.
     */
    public void traverse(Object root)throws Exception {
        // Reset the state
        visited.clear();
        toVisit.clear();

        if (root == null) return;

        addIfNotVisited(root, root.getClass());
        start();
    }

    /**
     * Is this class a type we can descend deeper into. For example, primitives do not contains
     * fields, so we can not descend into them.
     *
     * @param clazz
     * @return if this class is descendable.
     */
    private boolean canDescend(Class<?> clazz) {
        // We can't descend into Primitives (they are not objects)
        return !clazz.isPrimitive();
    }

    /**
     * Add this object to be visited if it has not already been visited, or scheduled to be.
     *
     * @param object The object
     * @param clazz The type of the field
     */
    private void addIfNotVisited(Object object, Class<?> clazz)throws Exception {
        if (object != null && !visited.containsKey(object) && includedClasses.contains(clazz.getName())) {
            toVisit.add(object);
            visited.put(object, clazz);
        }
    }

    public ObjectGraph includeClasses(Set<String> classes) {
        for (String c : classes) {
            if (c == null) {
                throw new NullPointerException("Null class not allowed");
            }
            includedClasses.add(c);
        }
        return this;
    }

    /**
     * Return all declared and inherited fields for this class.
     *
     * @param fields
     * @param clazz
     * @return
     */
    private List<Field> getAllFields(List<Field> fields, Class<?> clazz) {

        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        if (clazz.getSuperclass() != null) {
            getAllFields(fields, clazz.getSuperclass());
        }

        return Collections.unmodifiableList(fields);
    }

    private void start() throws Exception{

        while (!toVisit.isEmpty()) {

            Object obj = toVisit.remove();
            Class<?> clazz = visited.get(obj);

            boolean terminate = visitor.visit(obj, clazz);
            if (terminate) return;

            if (!canDescend(clazz)) continue;

            if (clazz.isArray()) {
                // If an Array, add each element to follow up
                Class<?> arrayType = clazz.getComponentType();
                obj = new ArrayList<>(1);
                ((List)obj).add(arrayType.newInstance());
                final int len = Array.getLength(obj);
                for (int i = 0; i < len; i++) {
                    addIfNotVisited(Array.get(obj, i), arrayType);
                }

            } else {
                // If a normal class, add each field
                List<Field> fields = getAllFields(new ArrayList<>(), obj.getClass());
                for (Field field : fields) {
                    int modifiers = field.getModifiers();

                    Class<?> fieldType = field.getType();
                    try {
                        field.setAccessible(true);
                        Object value = field.get(obj);
                        if(value==null){
                            value = fieldType.newInstance();
                        }
                        addIfNotVisited(value, fieldType);

                    } catch (IllegalAccessException e) {
                        // Ignore the exception
                    }
                }
            }
        }
    }
}
