package com.inferiority.asn1.codec;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;

/**
 * @author cuijiufeng
 * @Class ASN1InputStream
 * @Date 2023/2/3 16:11
 */
public class ASN1InputStream extends ByteArrayInputStream {
    public ASN1InputStream(byte[] buf) {
        super(buf);
    }

    public byte readByte() throws IOException {
        int ch = super.read();
        if (ch < 0)
            throw new EOFException();
        return (byte)(ch);
    }

    public int length() {
        return this.buf.length;
    }
}
