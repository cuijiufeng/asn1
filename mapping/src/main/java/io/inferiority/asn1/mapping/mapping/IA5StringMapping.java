package io.inferiority.asn1.mapping.mapping;

import com.squareup.javapoet.TypeName;
import io.inferiority.asn1.mapping.model.MappingContext;
import io.inferiority.asn1.mapping.utils.JavaPoetUtil;

/**
 * @author cuijiufeng
 * @date 2023/3/25 14:26
 */
public class IA5StringMapping extends AbstractStringMapping {
    public static final IA5StringMapping MAPPING = new IA5StringMapping();

    @Override
    public TypeName getSuperclass(MappingContext context) {
        return JavaPoetUtil.primitiveTypeName(context);
    }
}
