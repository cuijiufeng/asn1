package com.inferiority.asn1.codec.oer;

import com.inferiority.asn1.codec.ASN1InputStream;
import com.inferiority.asn1.codec.ASN1Object;
import com.inferiority.asn1.codec.ASN1OutputStream;

import java.io.IOException;

/**
 * @author cuijiufeng
 * @Class ASN1Integer
 * @Date 2023/2/3 13:45
 */
public class ASN1Integer extends ASN1Object {
    public ASN1Integer(byte[] data) throws IOException {
        super(data);
    }

    @Override
    protected void encode(ASN1OutputStream os) {

    }

    @Override
    protected void decode(ASN1InputStream is) throws IOException {

    }
}
