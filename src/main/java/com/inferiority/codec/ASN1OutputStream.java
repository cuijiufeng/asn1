package com.inferiority.codec;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

/**
 * @author cuijiufeng
 * @Class ASN1OutputStream
 * @Date 2023/2/3 16:11
 */
public class ASN1OutputStream extends ByteArrayOutputStream {
    public void writeLengthPrefix(int length) {
        if (length < 128) {
            write(length);
        } else {
            BigInteger bigInteger = BigInteger.valueOf(length);
            byte[] lengthBytes = bigInteger.toByteArray();
            int signedOctets = lengthBytes[0] == 0 && lengthBytes.length > 1 ? 1 : 0;

            write((lengthBytes.length - signedOctets) | 0x80);
            write(lengthBytes, signedOctets, lengthBytes.length - signedOctets);
        }
    }

    public void writeLengthDetermine(int length) {
        writeLengthPrefix(length);
    }

    public void writeEnumeratedValue(int value) {
        writeLengthPrefix(value);
    }
}
