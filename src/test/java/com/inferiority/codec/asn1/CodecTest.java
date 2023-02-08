package com.inferiority.codec.asn1;

import cn.com.easysec.v2x.asn1.coer.COERBitString;
import cn.com.easysec.v2x.asn1.coer.COERBoolean;
import cn.com.easysec.v2x.asn1.coer.COEREnumeration;
import cn.com.easysec.v2x.asn1.coer.COEREnumerationType;
import cn.com.easysec.v2x.asn1.coer.COERIA5String;
import cn.com.easysec.v2x.asn1.coer.COERInteger;
import cn.com.easysec.v2x.asn1.coer.COEROctetStream;
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

    @Test
    public void testIA5String() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        COERIA5String esIa5String = new COERIA5String("hello world", 5, null);
        esIa5String.encode(new DataOutputStream(baos));
        log.debug("es IA5String: {}", Hex.encodeHexString(baos.toByteArray()));

        ASN1IA5String ia5String = new ASN1IA5String("hello world", 5, null);
        log.debug("   IA5String: {}", Hex.encodeHexString(ia5String.getEncoded()));
        log.debug("   IA5String: {}", ia5String.getString());
        Assert.assertEquals(ia5String, new ASN1IA5String(5, null).fromByteArray(ia5String.getEncoded()));
        Assert.assertArrayEquals(baos.toByteArray(), ia5String.getEncoded());
    }

    @Test
    public void testVisibleString() throws IOException {
        ASN1VisibleString visibleString = new ASN1VisibleString("hello world\u0080", 5, null);
        log.debug("   IA5String: {}", Hex.encodeHexString(visibleString.getEncoded()));
        log.debug("   IA5String: {}", visibleString.getString());
        Assert.assertEquals(visibleString, new ASN1VisibleString(5, null).fromByteArray(visibleString.getEncoded()));
    }

    @Test
    public void testPrintableString() throws IOException {
        ASN1PrintableString visibleString = new ASN1PrintableString("hello world", 5, null);
        log.debug("   visibleString: {}", Hex.encodeHexString(visibleString.getEncoded()));
        log.debug("   visibleString: {}", visibleString.getString());
        Assert.assertEquals(visibleString, new ASN1PrintableString(5, null).fromByteArray(visibleString.getEncoded()));
    }

    @Test
    public void testOctetString() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        COEROctetStream esOctetStream = new COEROctetStream("hello world".getBytes(), 5, null);
        esOctetStream.encode(new DataOutputStream(baos));
        log.debug("es octetString: {}", Hex.encodeHexString(baos.toByteArray()));

        ASN1OctetString octetString = new ASN1OctetString("hello world".getBytes(), 5, null);
        log.debug("   octetString: {}", Hex.encodeHexString(octetString.getEncoded()));
        Assert.assertEquals(octetString, new ASN1OctetString(5, null).fromByteArray(octetString.getEncoded()));
        Assert.assertArrayEquals(baos.toByteArray(), octetString.getEncoded());
    }

    @Test
    public void testBitString() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        COERBitString esBitString = new COERBitString(0x0030, 16, false);
        esBitString.encode(new DataOutputStream(baos));
        log.debug("es bitString: {}", Hex.encodeHexString(baos.toByteArray()));

        //                                                      en      de
        //固定大小
        //00000011 00000000 0000    =03 00 00                   Y

        //非固定大小
        //00000000 00000000         =01 00                      N
        //00000000 00000000 00000000=01 00

        //00000000 00110000         =03 04 00 30        1
        //00000000 00000011         =03 00 00 03        1

        //00000000                  =02 00 00                   Y
        //00000000 00000000 00000000=04 00 00 00 00             Y
        //10000000 00000000 00000000=04 00 80 00 00             Y
        //10000000 00000000 00010000=04 00 80 00 10             Y
        //10000000 00000000 00010000=04 04 80 00 10     19      Y
        //10000000 00000000 01000000=04 06 80 00 40     19      Y
        //10000000 00000000 00000010=04 01 80 00 02     19      Y
        //10000000 00000000 00000000=02 07 80           19
        ASN1BitString bitString = new ASN1BitString(new byte[] {(byte) 0x80, 0x00, 0x00}, 19, false);
        log.debug("   bitString: {}", Hex.encodeHexString(bitString.getEncoded()));
        Assert.assertEquals(bitString, new ASN1BitString(null, 19).fromByteArray(bitString.getEncoded()));
        //Assert.assertArrayEquals(baos.toByteArray(), bitString.getEncoded());
    }

    @Test
    public void t() {
        short i = Short.parseShort("11111111", 2);
        System.out.println(Long.toBinaryString(255));
        System.out.println(Hex.encodeHexString(new BigInteger("0000001100000000", 2).toByteArray()));
    }

    enum EnumeratedType implements COEREnumerationType {
        A,B
    }
}