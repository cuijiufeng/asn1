package com.inferiority.codec.asn1;

import com.inferiority.codec.ASN1InputStream;
import com.inferiority.codec.ASN1OutputStream;

import java.io.IOException;
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
        this.enumClass = enumClass;
    }

    public ASN1Enumerated(Enum<? extends Enum<?>> enumerated) {
        this.enumerated = enumerated;
        this.enumClass = enumerated.getDeclaringClass();
    }

    @Override
    protected void encode(ASN1OutputStream os) {
        os.writeEnumeratedValue(this.enumerated.ordinal());
    }

    @Override
    protected void decode(ASN1InputStream is) throws IOException {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ASN1Enumerated that = (ASN1Enumerated) o;
        return Objects.equals(enumerated, that.enumerated);
    }

    @Override
    public int hashCode() {
        return enumerated != null ? enumerated.hashCode() : 0;
    }
}
