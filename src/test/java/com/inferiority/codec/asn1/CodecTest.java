package com.inferiority.codec.asn1;

import cn.com.easysec.v2x.asn1.coer.COERBoolean;
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
        COERInteger esInt = new COERInteger(new BigInteger("9223372036854775807"), new BigInteger("9223372036854775807"), new BigInteger("9223372036854775808"));
        esInt.encode(new DataOutputStream(baos));
        log.debug("es integer: {}", Hex.encodeHexString(baos.toByteArray()));

        ASN1Integer integer = new ASN1Integer(new BigInteger("9223372036854775807"), new BigInteger("9223372036854775807"), new BigInteger("9223372036854775808"));
        log.debug("   integer: {}", Hex.encodeHexString(integer.getEncoded()));
        log.debug("   integer: {}", integer.longValue());
        Assert.assertEquals(integer, new ASN1Integer(new BigInteger("9223372036854775807"), new BigInteger("9223372036854775808")).fromByteArray(integer.getEncoded()));
        Assert.assertArrayEquals(baos.toByteArray(), integer.getEncoded());
    }
}