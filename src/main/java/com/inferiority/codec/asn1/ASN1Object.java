package com.inferiority.codec.asn1;

import com.inferiority.codec.ASN1InputStream;
import com.inferiority.codec.ASN1OutputStream;
import com.inferiority.codec.Codeable;

import java.io.IOException;

/**
 * @author cuijiufeng
 * @Class ASN1Object
 * @Date 2023/2/3 13:51
 */
public abstract class ASN1Object implements Codeable {

    protected ASN1Object() {
    }

    public ASN1Object(byte[] data) throws IOException {
        this.decode(new ASN1InputStream(data));
    }

    public byte[] getEncoded() throws IOException {
        ASN1OutputStream os = new ASN1OutputStream();
        this.encode(os);
        return os.toByteArray();
    }

    protected abstract void encode(ASN1OutputStream os);

    protected abstract void decode(ASN1InputStream is) throws IOException;
}
