package com.skenny.jardependencyscanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;

/**
 *
 * @author skenny
 */
public class ClassModel extends EmptyVisitor {

    private final JavaClass javaClass;
    private final ConstantPool constantPool;
    private final Set<ConstantClass> dependencyClassSet;
    private final List<Method> methodList;
    private final Map<Method, InstructionList> instructionMap;

    private final ClassGen classGen;
    private final ConstantPoolGen poolGen;

    public ClassModel(JavaClass javaClass) {
        this.javaClass = javaClass;
        this.constantPool = javaClass.getConstantPool();
        this.dependencyClassSet = new HashSet<>();
        this.methodList = new ArrayList<>();
        this.instructionMap = new HashMap<>();

        this.classGen = new ClassGen(javaClass);
        this.poolGen = this.classGen.getConstantPool();
    }

    @Override
    public void visitConstantClass(ConstantClass obj) {
        this.getDependencyClassSet().add(obj);
    }

    @Override
    public void visitMethod(Method obj) {
        MethodGen methodGen = new MethodGen(obj, this.getJavaClass().getClassName(), this.getPoolGen());
        InstructionList instructionList = methodGen.getInstructionList();
        this.getInstructionMap().put(obj, instructionList);
        this.methodList.add(obj);
    }

    public JavaClass getJavaClass() {
        return javaClass;
    }

    public ConstantPool getConstantPool() {
        return constantPool;
    }

    public Set<ConstantClass> getDependencyClassSet() {
        return dependencyClassSet;
    }

    public List<Method> getMethodList() {
        return methodList;
    }

    public Map<Method, InstructionList> getInstructionMap() {
        return instructionMap;
    }

    public ClassGen getClassGen() {
        return classGen;
    }

    public ConstantPoolGen getPoolGen() {
        return poolGen;
    }
}
