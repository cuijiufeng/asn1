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

    public <T extends ASN1Object> T fromByteArray(byte[] data) throws IOException {
        this.decode(new ASN1InputStream(data));
        //noinspection unchecked
        return (T) this;
    }

    public byte[] getEncoded() {
        ASN1OutputStream os = new ASN1OutputStream();
        this.encode(os);
        return os.toByteArray();
    }

    protected abstract void encode(ASN1OutputStream os);

    protected abstract void decode(ASN1InputStream is) throws IOException;
}
