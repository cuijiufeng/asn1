package com.inferiority.asn1.codec.oer;

import com.inferiority.asn1.codec.ASN1InputStream;
import com.inferiority.asn1.codec.ASN1Object;
import com.inferiority.asn1.codec.ASN1OutputStream;

import java.io.IOException;

/**
 * @author cuijiufeng
 * @Class ASN1Boolean
 * @Date 2023/2/3 15:53
 */
public class ASN1Boolean extends ASN1Object {
    private static final byte FALSE_VALUE = 0x00;
    private static final byte TRUE_VALUE = (byte) 0xFF;

    private boolean value;

    private ASN1Boolean(byte[] data) throws IOException {
        super(data);
    }

    public ASN1Boolean(boolean data) {
        this.value = data;
    }

    @Override
    protected void encode(ASN1OutputStream os) {
        if (this.value) {
            os.write(TRUE_VALUE);
        } else {
            os.write(FALSE_VALUE);
        }
    }

    @Override
    protected void decode(ASN1InputStream is) throws IOException {
        if (is.length() != 1) {
            throw new IllegalArgumentException("BOOLEAN value should have 1 byte in it");
        }
        this.value = FALSE_VALUE != is.readByte();
    }

    public boolean isTrue()
    {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return value == ((ASN1Boolean) o).value;
    }

    @Override
    public int hashCode() {
        return (value ? 1 : 0);
    }

    public String toString()
    {
        return isTrue() ? "TRUE" : "FALSE";
    }
}
