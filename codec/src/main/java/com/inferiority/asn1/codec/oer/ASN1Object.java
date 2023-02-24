package com.inferiority.asn1.codec.oer;

import com.inferiority.asn1.codec.ASN1InputStream;
import com.inferiority.asn1.codec.ASN1OutputStream;
import com.inferiority.asn1.codec.Codeable;
import com.inferiority.asn1.codec.CodecException;

/**
 * @author cuijiufeng
 * @Class ASN1Object
 * @Date 2023/2/3 13:51
 */
public abstract class ASN1Object implements Codeable {

    /**
     * 解码
     * @param data
     */
    public void fromByteArray(byte[] data) throws CodecException {
        try {
            this.decode(new ASN1InputStream(data));
        } catch (Exception e) {
            throw new CodecException("decoding", this, e);
        }
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

    @Override
    public String toString() {
        switch (System.getProperty("asn1.print.format", "object")) {
            case "xml": return toXmlString();
            case "json": return toJsonString();
            case "object":
            default: return toObjectString();
        }
    }
}
