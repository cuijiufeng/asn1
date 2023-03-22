package com.inferiority.asn1.mapping.mapping;

import com.inferiority.asn1.analysis.model.Definition;
import com.inferiority.asn1.mapping.model.MappingContext;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewConstructor;
import javassist.NotFoundException;

import java.io.IOException;

/**
 * @author cuijiufeng
 * @Class IntegerMapping
 * @Date 2023/3/21 14:50
 */
public class IntegerMapping extends AbstractMapping {

    protected IntegerMapping() throws NotFoundException {
    }

    @Override
    public void mappingInternal(MappingContext context) throws CannotCompileException, IOException {
        Definition definition = context.getDefinition();
        CtClass JavassistTestClass = CLASS_POOL.makeClass(context.getPackageName() + "." + definition.getIdentifier());
        JavassistTestClass.setSuperclass(ASN1INTEGER_CLASS);
        JavassistTestClass.addField(CtField.make(
                "public static final java.math.BigInteger RANGE_MIN = new java.math.BigInteger(\"" + definition.getRangeMin() + "\");",
                JavassistTestClass));
        JavassistTestClass.addField(CtField.make(
                "public static final java.math.BigInteger RANGE_MAX = new java.math.BigInteger(\"" + definition.getRangeMax() + "\");",
                JavassistTestClass));
        JavassistTestClass.addConstructor(CtNewConstructor.make(null, null,
                "super(RANGE_MIN, RANGE_MAX);",
                JavassistTestClass));
        JavassistTestClass.addConstructor(CtNewConstructor.make(new CtClass[]{CtClass.intType}, null,
                "super(RANGE_MIN, RANGE_MAX);",
                JavassistTestClass));
        JavassistTestClass.writeFile(context.getOutputPath());
    }
}
