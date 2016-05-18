package com.skenny.jardependencyscanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.DescendingVisitor;
import org.apache.bcel.classfile.JavaClass;

/**
 *
 * @author skenny
 */
public class JarDependencyScanner {
    
    public ClassModel createModel(File classfile) {
        return new ClassModel(null);
    }
    
    public ClassModel createModel(JarFile jarFile, String className) {
        return new ClassModel(null);
    }

    public static void main(String[] args) throws IOException {
        String filePath = args[0];
        JarFile jar = new JarFile(new File(filePath));
        List<String> jarClassList = new ArrayList<>();
        Enumeration<JarEntry> zipEntries = jar.entries();
        for (JarEntry entry : Collections.list(zipEntries)) {
            if (entry.getName().contains(".class")) {
                System.out.println(entry.getName());
                jarClassList.add(entry.getName());
            }
        }

        Map<String, ClassModel> classMap = new HashMap<>();
        for (String classPath : jarClassList) {
            try {
                ClassParser cp = new ClassParser(filePath, classPath);
                JavaClass cls = cp.parse();
                ClassModel model = new ClassModel(cls);
                DescendingVisitor classWalker = new DescendingVisitor(cls, model);
                classWalker.visit();
                classMap.put(cls.getClassName(), model);
            } catch (ClassFormatException e) {
                e.printStackTrace();
            }
        }

        Map<String, List<String>> dependencyMap = new HashMap();

        for (String clazz : classMap.keySet()) {
            ClassModel model = classMap.get(clazz);
            Set<ConstantClass> constantClasses = model.getDependencyClassSet();
            List<String> dependencyList = new ArrayList();

            for (ConstantClass cls : constantClasses) {
                dependencyList.add(cls.getBytes(model.getConstantPool()));
            }
            Collections.sort(dependencyList);
            dependencyMap.put(clazz, dependencyList);
        }     
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(dependencyMap));
    }
}
