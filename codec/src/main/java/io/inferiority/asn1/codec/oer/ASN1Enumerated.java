package io.inferiority.asn1.codec.oer;

import io.inferiority.asn1.codec.ASN1InputStream;
import io.inferiority.asn1.codec.ASN1OutputStream;
import io.inferiority.asn1.codec.Codeable;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.EOFException;
import java.util.Objects;

/**
 * @author cuijiufeng
 * @Class ASN1Enumerated
 * @Date 2023/2/7 9:02
 */
public class ASN1Enumerated<T extends Enum<T>> extends ASN1Object {
    private T enumerated;
    private final Class<T> enumClass;

    @SuppressWarnings("unchecked")
    public ASN1Enumerated() {
        try {
            this.enumClass = (Class<T>) ((ParameterizedTypeImpl) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            Objects.requireNonNull(enumClass, "enum class cannot be null");
        } catch (Exception e) {
            throw new UnsupportedOperationException("please call ASN1Enumerated(Class<T> enumClass) method");
        }
    }

    public ASN1Enumerated(Class<T> enumClass) {
        Objects.requireNonNull(enumClass, "enum class cannot be null");
        this.enumClass = enumClass;
    }

    public ASN1Enumerated(T enumerated) {
        Objects.requireNonNull(enumerated, "enumerated cannot be null");
        this.enumerated = enumerated;
        this.enumClass = enumerated.getDeclaringClass();
    }

    @Override
    public void encode(ASN1OutputStream os) {
        os.writeEnumeratedValue(this.enumerated.ordinal());
    }

    @Override
    public void decode(ASN1InputStream is) throws EOFException {
        int ordinal = is.readEnumeratedValue();
        for (T constant : this.enumClass.getEnumConstants()) {
            if (ordinal == constant.ordinal()) {
                this.enumerated = constant;
                return;
            }
        }
    }

    public T getEnumerated() {
        return enumerated;
    }

    @Override
    public boolean asn1Equals(Codeable obj) {
        if (!(obj instanceof ASN1Enumerated)) {
            return false;
        }
        return Objects.equals(enumerated, ((ASN1Enumerated<?>) obj).enumerated);
    }

    @Override
    public String toObjectString() {
        return enumerated.name();
    }

    @Override
    public String toJsonString() {
        return "\"" + enumerated.name() + "\"";
    }

    @Override
    public int hashCode() {
        return enumerated != null ? enumerated.hashCode() : 0;
    }
}
