package com.inferiority.codec.asn1;

import com.inferiority.codec.ASN1InputStream;
import com.inferiority.codec.ASN1OutputStream;
import com.inferiority.codec.Codeable;
import com.inferiority.codec.CodecException;

/**
 * @author cuijiufeng
 * @Class ASN1Object
 * @Date 2023/2/3 13:51
 */
public abstract class ASN1Object implements Codeable {

    /**
     * 解码
     * @param data
     * @return T
    */
    public <T extends ASN1Object> T fromByteArray(byte[] data) throws CodecException {
        try {
            this.decode(new ASN1InputStream(data));
        } catch (Exception e) {
            throw new CodecException("decoding", this, e);
        }
        //noinspection unchecked
        return (T) this;
    }

    /**
     * 编码
     * @return byte[]
    */
    public byte[] getEncoded() throws CodecException {
        ASN1OutputStream os = new ASN1OutputStream();
        try {
            this.encode(os);
        } catch (Exception e) {
            throw new CodecException("encoding", this, e);
        }
        return os.toByteArray();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof Codeable) && asn1Equals((Codeable)obj);
    }
}
