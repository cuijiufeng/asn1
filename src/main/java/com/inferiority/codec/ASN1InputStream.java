package com.inferiority.codec;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
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

    public byte readByte() throws EOFException {
        int ch = super.read();
        if (ch < 0)
            throw new EOFException();
        return (byte)(ch);
    }

    public int readLengthPrefix() throws EOFException {
        int firstOctet = read();
        if (firstOctet < 128) {
            return firstOctet;
        } else {
            byte[] lengthValue = new byte[firstOctet & 127];
            if (lengthValue.length != read(lengthValue, 0, lengthValue.length)) {
                throw new EOFException(String.format("expected to read %s bytes", lengthValue.length));
            }
            return new BigInteger(1, lengthValue).intValue();
        }
    }

    public int readLengthDetermine() throws EOFException {
        return readLengthPrefix();
    }

    public int readEnumeratedValue() throws EOFException {
        return readLengthPrefix();
    }
}
