package com.inferiority.codec.asn1;

import cn.com.easysec.v2x.asn1.coer.COERBoolean;
import cn.com.easysec.v2x.asn1.coer.COEREnumeration;
import cn.com.easysec.v2x.asn1.coer.COEREnumerationType;
import cn.com.easysec.v2x.asn1.coer.COERInteger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

@Slf4j
public class CodecTest {

    @Test
    public void TestBoolean() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        COERBoolean esTrue = new COERBoolean(false);
        esTrue.encode(new DataOutputStream(baos));
        log.debug("es boolean: {}", Hex.encodeHexString(baos.toByteArray()));

        ASN1Boolean bTrue = new ASN1Boolean(false);
        log.debug("   boolean: {}", Hex.encodeHexString(bTrue.getEncoded()));
        Assert.assertEquals(bTrue, new ASN1Boolean().fromByteArray(bTrue.getEncoded()));
        Assert.assertArrayEquals(baos.toByteArray(), bTrue.getEncoded());
    }

    @Test
    public void testInteger() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        COERInteger esInt = new COERInteger(new BigInteger(
                    "7FFFFFFFFFFFFFFF7FFFFFFFFFFFFFFF" +
                        "7FFFFFFFFFFFFFFF7FFFFFFFFFFFFFFF" +
                        "7FFFFFFFFFFFFFFF7FFFFFFFFFFFFFFF" +
                        "7FFFFFFFFFFFFFFF7FFFFFFFFFFFFFFF" +
                        "7FFFFFFFFFFFFFFF7FFFFFFFFFFFFFFF" +
                        "7FFFFFFFFFFFFFFF7FFFFFFFFFFFFFFF" +
                        "7FFFFFFFFFFFFFFF7FFFFFFFFFFFFFFF" +
                        "7FFFFFFFFFFFFFFF7FFFFFFFFFFFFFFF", 16), null, null);
        esInt.encode(new DataOutputStream(baos));
        log.debug("es integer: {}", Hex.encodeHexString(baos.toByteArray()));
        COERInteger esIntThat = new COERInteger(null, null);
        esIntThat.decode(new DataInputStream(new ByteArrayInputStream(baos.toByteArray())));
        log.debug("es integer: {}", esIntThat);

        ASN1Integer integer = new ASN1Integer(new BigInteger(
                    "7FFFFFFFFFFFFFFF7FFFFFFFFFFFFFFF" +
                        "7FFFFFFFFFFFFFFF7FFFFFFFFFFFFFFF" +
                        "7FFFFFFFFFFFFFFF7FFFFFFFFFFFFFFF" +
                        "7FFFFFFFFFFFFFFF7FFFFFFFFFFFFFFF" +
                        "7FFFFFFFFFFFFFFF7FFFFFFFFFFFFFFF" +
                        "7FFFFFFFFFFFFFFF7FFFFFFFFFFFFFFF" +
                        "7FFFFFFFFFFFFFFF7FFFFFFFFFFFFFFF" +
                        "7FFFFFFFFFFFFFFF7FFFFFFFFFFFFFFF", 16), null, null);
        log.debug("   integer: {}", Hex.encodeHexString(integer.getEncoded()));
        log.debug("   integer: {}", integer.getValue());
        Assert.assertEquals(integer, new ASN1Integer(null, null).fromByteArray(integer.getEncoded()));
        Assert.assertArrayEquals(baos.toByteArray(), integer.getEncoded());
    }

    @Test
    public void testEnumerated() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        COEREnumeration esEnumerated = new COEREnumeration(EnumeratedType.B);
        esEnumerated.encode(new DataOutputStream(baos));
        log.debug("es enumerated: {}", Hex.encodeHexString(baos.toByteArray()));

        ASN1Enumerated enumerated = new ASN1Enumerated(EnumeratedType.B);
        log.debug("   enumerated: {}", Hex.encodeHexString(enumerated.getEncoded()));
        log.debug("   enumerated: {}", enumerated.getEnumerated());
        Assert.assertEquals(enumerated, new ASN1Enumerated(EnumeratedType.class).fromByteArray(enumerated.getEncoded()));
        Assert.assertArrayEquals(baos.toByteArray(), enumerated.getEncoded());
    }

    enum EnumeratedType implements COEREnumerationType {
        A,B
    }
}