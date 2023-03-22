package com.inferiority.asn1.mapping.mapping;

import com.inferiority.asn1.analysis.common.Reserved;
import com.inferiority.asn1.analysis.util.RegexUtil;
import com.inferiority.asn1.codec.oer.ASN1Integer;
import com.inferiority.asn1.mapping.MappingException;
import com.inferiority.asn1.mapping.model.MappingContext;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.IOException;

/**
 * @author cuijiufeng
 * @Class AbstractMapping
 * @Date 2023/3/21 14:30
 */
public abstract class AbstractMapping {
    protected final ClassPool CLASS_POOL = ClassPool.getDefault();
    protected final CtClass ASN1INTEGER_CLASS = CLASS_POOL.getCtClass(ASN1Integer.class.getName());

    protected AbstractMapping() throws NotFoundException {
    }

    public static AbstractMapping getInstance(MappingContext context) throws NotFoundException {
        if (Reserved.NULL.equals(context.getDefinition().getPrimitiveType())) {
            return null;
        } else if (Reserved.BOOLEAN.equals(context.getDefinition().getPrimitiveType())) {
            return null;
        } else if (Reserved.INTEGER.equals(context.getDefinition().getPrimitiveType())) {
            return new IntegerMapping();
        } else if (Reserved.ENUMERATED.equals(context.getDefinition().getPrimitiveType())) {
            return null;
        } else if (Reserved.IA5String.equals(context.getDefinition().getPrimitiveType())) {
            return null;
        } else if (Reserved.UTF8String.equals(context.getDefinition().getPrimitiveType())) {
            return null;
        } else if (Reserved.SEQUENCE.equals(context.getDefinition().getPrimitiveType())) {
            return null;
        } else if (Reserved.CHOICE.equals(context.getDefinition().getPrimitiveType())) {
            return null;
        } else if (context.getDefinition().getPrimitiveType().equals(Reserved.BIT + " " + Reserved.STRING)) {
            return null;
        } else if (context.getDefinition().getPrimitiveType().equals(Reserved.OCTET + " " + Reserved.STRING)) {
            return null;
        } else if (RegexUtil.matches(Reserved.SEQUENCE + "\\s*" + Reserved.OF, context.getDefinition().getPrimitiveType())) {
            return null;
        }
        return null;
    }

    public void mapping(MappingContext context) {
        try {
            mappingInternal(context);
        } catch (Exception e) {
            throw new MappingException(context.getDefinition() + "mapping to class error", e);
        }
    }

    protected abstract void mappingInternal(MappingContext context) throws CannotCompileException, IOException;
}
