package com.inferiority.codec.asn1;

import com.inferiority.codec.ASN1InputStream;
import com.inferiority.codec.ASN1OutputStream;
import com.inferiority.codec.Codeable;

import java.io.IOException;

/**
 * @author cuijiufeng
 * @Class ASN1Null
 * @Date 2023/2/9 12:00
 */
public class ASN1Null extends ASN1Object{
    public ASN1Null() {
    }

    @Override
    public void encode(ASN1OutputStream os) {
    }

    @Override
    public void decode(ASN1InputStream is) throws IOException {
    }

    @Override
    public boolean asn1Equals(Codeable obj) {
        return obj instanceof ASN1Boolean;
    }

    @Override
    public int hashCode()
    {
        return -1;
    }
}
