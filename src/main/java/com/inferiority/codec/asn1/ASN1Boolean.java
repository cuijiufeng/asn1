package com.inferiority.codec.asn1;

import com.inferiority.codec.ASN1InputStream;
import com.inferiority.codec.ASN1OutputStream;
import com.inferiority.codec.Codeable;

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

    public ASN1Boolean() {
    }

    public ASN1Boolean(boolean data) {
        this.value = data;
    }

    @Override
    public void encode(ASN1OutputStream os) {
        if (this.value) {
            os.write(TRUE_VALUE);
        } else {
            os.write(FALSE_VALUE);
        }
    }

    @Override
    public void decode(ASN1InputStream is) throws IOException {
        this.value = FALSE_VALUE != is.readByte();
    }

    public boolean isTrue() {
        return this.value;
    }

    @Override
    public boolean asn1Equals(Codeable obj) {
        if (!(obj instanceof ASN1Boolean)) {
            return false;
        }
        return value == ((ASN1Boolean) obj).value;
    }

    @Override
    public String toObjectString() {
        return this.value ? "TRUE" : "FALSE";
    }

    @Override
    public String toJsonString() {
        return this.value ? "true" : "false";
    }

    @Override
    public int hashCode() {
        return (value ? 1 : 0);
    }
}
