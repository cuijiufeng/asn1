package com.inferiority.asn1.codec.oer;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

public class ASN1BooleanTest {

    @Test
    public void codec() {
        ASN1Boolean flag = new ASN1Boolean(true);
        System.out.println(flag);
        byte[] encoded = flag.getEncoded();
        System.out.println(Hex.encodeHex(encoded));
    }
}