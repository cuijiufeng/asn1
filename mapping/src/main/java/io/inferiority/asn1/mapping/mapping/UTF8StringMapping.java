package io.inferiority.asn1.mapping.mapping;

import com.squareup.javapoet.TypeName;
import io.inferiority.asn1.mapping.model.MappingContext;
import io.inferiority.asn1.mapping.utils.JavaPoetUtil;

/**
 * @author cuijiufeng
 * @date 2023/3/25 14:27
 */
public class UTF8StringMapping extends AbstractStringMapping {
    public static final UTF8StringMapping MAPPING = new UTF8StringMapping();

    @Override
    public TypeName getSuperclass(MappingContext context) {
        return JavaPoetUtil.primitiveTypeName(context);
    }
}
