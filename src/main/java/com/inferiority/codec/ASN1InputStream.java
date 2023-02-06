package com.inferiority.codec;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;

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

    public int readLengthDetermine() throws IOException {
        int firstOctet = read();
        if (firstOctet < 128) {
            return firstOctet;
        } else {
            byte[] lengthValue = new byte[firstOctet & 127];
            //noinspection ResultOfMethodCallIgnored
            read(lengthValue);
            return new BigInteger(1, lengthValue).intValue();
        }
    }
}
