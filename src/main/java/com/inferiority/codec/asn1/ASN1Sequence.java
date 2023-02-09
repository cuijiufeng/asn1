package com.inferiority.codec.asn1;

import com.inferiority.codec.ASN1InputStream;
import com.inferiority.codec.ASN1OutputStream;
import com.inferiority.codec.Codeable;

import java.io.IOException;

/**
 * @author cuijiufeng
 * @Class ASN1Sequence
 * @Date 2023/2/9 9:30
 */
public class ASN1Sequence extends ASN1Object{
    private boolean extensible;
    private Codeable[] elements;

    @Override
    public void encode(ASN1OutputStream os) {

    }

    @Override
    public void decode(ASN1InputStream is) throws IOException {

    }

    @Override
    public boolean asn1Equals(Codeable obj) {
        return false;
    }
}
