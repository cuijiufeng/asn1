package com.inferiority.codec.asn1;

import com.inferiority.codec.ASN1InputStream;
import com.inferiority.codec.ASN1OutputStream;
import com.inferiority.codec.Codeable;

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
        this.enumClass = enumClass;
    }

    public ASN1Enumerated(Enum<? extends Enum<?>> enumerated) {
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
    public int hashCode() {
        return enumerated != null ? enumerated.hashCode() : 0;
    }
}
