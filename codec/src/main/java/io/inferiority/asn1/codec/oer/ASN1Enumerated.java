package io.inferiority.asn1.codec.oer;

import io.inferiority.asn1.codec.ASN1InputStream;
import io.inferiority.asn1.codec.ASN1OutputStream;
import io.inferiority.asn1.codec.Codeable;

import java.io.EOFException;
import java.util.Objects;

/**
 * @author cuijiufeng
 * @Class ASN1Enumerated
 * @Date 2023/2/7 9:02
 */
public class ASN1Enumerated extends ASN1Object {
    private Enum<? extends Enum<?>> enumerated;
    private final Class<? extends Enum<?>> enumClass;

    public ASN1Enumerated(Class<? extends Enum<?>> enumClass) {
        Objects.requireNonNull(enumClass, "enum class cannot be null");
        this.enumClass = enumClass;
    }

    public ASN1Enumerated(Enum<? extends Enum<?>> enumerated) {
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
        for (Enum<? extends Enum<?>> constant : this.enumClass.getEnumConstants()) {
            if (ordinal == constant.ordinal()) {
                this.enumerated = constant;
                return;
            }
        }
    }

    public <T extends Enum<?>> T getEnumerated() {
        //noinspection unchecked
        return (T) enumerated;
    }

    @Override
    public boolean asn1Equals(Codeable obj) {
        if (!(obj instanceof ASN1Enumerated)) {
            return false;
        }
        return Objects.equals(enumerated, ((ASN1Enumerated) obj).enumerated);
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
