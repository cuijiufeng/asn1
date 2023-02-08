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
        //固定大小
        //00000011 00000000 00000000=03 00 00                   Y       Y
        ASN1BitString bitString0 = new ASN1BitString(new byte[] {0x03,0x00,0x00}, null, true);
        log.debug("   bitString: {}", Hex.encodeHexString(bitString0.getEncoded()));
        Assert.assertEquals(bitString0, new ASN1BitString(3, null).fromByteArray(bitString0.getEncoded()));

        //非固定大小
        //00000000                  =01 00              2       N
        ASN1BitString bitString1 = new ASN1BitString(new byte[] {0x00}, 2, false);
        log.debug("   bitString: {}", Hex.encodeHexString(bitString1.getEncoded()));
        Assert.assertEquals(bitString1, new ASN1BitString(null, 2).fromByteArray(bitString1.getEncoded()));

        //00000000 00110000         =03 04 00 30        2       Y       Y
        ASN1BitString bitString4 = new ASN1BitString(new byte[] {0x00,0x30}, 2, false);
        log.debug("   bitString: {}", Hex.encodeHexString(bitString4.getEncoded()));
        Assert.assertEquals(bitString4, new ASN1BitString(null, 2).fromByteArray(bitString4.getEncoded()));

        //00000000 00000011         =03 00 00 03        2       Y       Y
        ASN1BitString bitString5 = new ASN1BitString(new byte[] {0x00,0x03}, 2, false);
        log.debug("   bitString: {}", Hex.encodeHexString(bitString5.getEncoded()));
        Assert.assertEquals(bitString5, new ASN1BitString(null, 2).fromByteArray(bitString5.getEncoded()));

        //00000000                  =02 00 00                   Y       Y
        ASN1BitString bitString6 = new ASN1BitString(new byte[] {0x00}, null, false);
        log.debug("   bitString: {}", Hex.encodeHexString(bitString6.getEncoded()));
        Assert.assertEquals(bitString6, new ASN1BitString(null, null).fromByteArray(bitString6.getEncoded()));

        //00000000 00000000 00000000=04 00 00 00 00             Y       Y
        ASN1BitString bitString7 = new ASN1BitString(new byte[] {(byte) 0x00,0x00,0x00}, null, false);
        log.debug("   bitString: {}", Hex.encodeHexString(bitString7.getEncoded()));
        Assert.assertEquals(bitString7, new ASN1BitString(null, null).fromByteArray(bitString7.getEncoded()));

        //10000000 00000000 00000000=04 00 80 00 00             Y       Y
        ASN1BitString bitString8 = new ASN1BitString(new byte[] {(byte) 0x80,0x00,0x00}, null, false);
        log.debug("   bitString: {}", Hex.encodeHexString(bitString8.getEncoded()));
        Assert.assertEquals(bitString8, new ASN1BitString(null, null).fromByteArray(bitString8.getEncoded()));

        //10000000 00000000 00010000=04 00 80 00 10             Y       Y
        ASN1BitString bitString9 = new ASN1BitString(new byte[] {(byte) 0x80,0x00,0x10}, null, false);
        log.debug("   bitString: {}", Hex.encodeHexString(bitString9.getEncoded()));
        Assert.assertEquals(bitString9, new ASN1BitString(null, null).fromByteArray(bitString9.getEncoded()));

        //10000000 00000000 00010000=04 04 80 00 10     19      Y       Y
        ASN1BitString bitString10 = new ASN1BitString(new byte[] {(byte) 0x80,0x00,0x10}, 19, false);
        log.debug("   bitString: {}", Hex.encodeHexString(bitString10.getEncoded()));
        Assert.assertEquals(bitString10, new ASN1BitString(null, 19).fromByteArray(bitString10.getEncoded()));

        //10000000 00000000 01000000=04 06 80 00 40     19      Y       Y
        ASN1BitString bitString11 = new ASN1BitString(new byte[] {(byte) 0x80,0x00,0x40}, 19, false);
        log.debug("   bitString: {}", Hex.encodeHexString(bitString11.getEncoded()));
        Assert.assertEquals(bitString11, new ASN1BitString(null, 19).fromByteArray(bitString11.getEncoded()));

        //10000000 00000000 00000010=04 01 80 00 02     19      Y       Y
        ASN1BitString bitString12 = new ASN1BitString(new byte[] {(byte) 0x80,0x00,0x02}, 19, false);
        log.debug("   bitString: {}", Hex.encodeHexString(bitString12.getEncoded()));
        Assert.assertEquals(bitString12, new ASN1BitString(null, 19).fromByteArray(bitString12.getEncoded()));

        //10000000 00000000 00000000=02 07 80           19      Y
        ASN1BitString bitString13 = new ASN1BitString(new byte[] {(byte) 0x80,0x00,0x00}, 19, false);
        log.debug("   bitString: {}", Hex.encodeHexString(bitString13.getEncoded()));
        Assert.assertEquals(bitString13, new ASN1BitString(null, 19).fromByteArray(bitString13.getEncoded()));
        log.debug("   bitString pos 0 : {}", bitString13.getBit(0));
        log.debug("   bitString pos 15: {}", bitString13.getBit(15));
        bitString13.setBit(15, true);
        log.debug("   bitString pos 15: {}", bitString13.getBit(16));
        bitString13.setBit(16, true);
        log.debug("   bitString pos 16: {}", bitString13.getBit(16));
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