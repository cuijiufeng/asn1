package com.inferiority.codec.asn1;

import cn.com.easysec.v2x.asn1.coer.COERBoolean;
import cn.com.easysec.v2x.asn1.coer.COEREnumeration;
import cn.com.easysec.v2x.asn1.coer.COEREnumerationType;
import cn.com.easysec.v2x.asn1.coer.COERIA5String;
import cn.com.easysec.v2x.asn1.coer.COERInteger;
import cn.com.easysec.v2x.asn1.coer.COEROctetStream;
import cn.com.easysec.v2x.asn1.coer.COERSequence;
import cn.com.easysec.v2x.asn1.coer.COERSequenceOf;
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
import java.util.Arrays;

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
    public void testSequence() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        COERSequence esSequence = new COERSequence(false);
        esSequence.addField(0, new COERBoolean(false), false, new COERBoolean(true), new COERBoolean());
        esSequence.addField(1, new COERInteger(255, 0, 255), true, null, new COERInteger(0, 255));
        esSequence.encode(new DataOutputStream(baos));
        log.debug("es sequence: {}", Hex.encodeHexString(baos.toByteArray()));

        /*
         * Rocket ::= SEQUENCE { a BOOLEAN, b INTEGER (0..255) }
         * rec1value Rocket ::= { a TRUE, b 255 }
         * FF FF
         * */
        ASN1Sequence sequence0 = new ASN1Sequence(false);
        sequence0.setElement(0, false, false, new ASN1Boolean(true), null);
        sequence0.setElement(1, false, false, new ASN1Integer(255, 0, 255), null);
        log.debug("   sequence: {}", Hex.encodeHexString(sequence0.getEncoded()));
        Assert.assertEquals(Hex.encodeHexString(sequence0.getEncoded(), false), "FF FF".replaceAll(" ", ""));
        ASN1Sequence decodeSequence0 = new ASN1Sequence(false);
        decodeSequence0.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence0.setElement(1, false, false, new ASN1Integer(0, 255), null);
        Assert.assertEquals(sequence0, decodeSequence0.fromByteArray(sequence0.getEncoded()));

        /*
         * Rocket ::= SEQUENCE { a BOOLEAN, b INTEGER (0..255) OPTIONAL }
         * rec1value Rocket ::= { a TRUE, b 255 }
         * 80 FF FF
         * */
        ASN1Sequence sequence1 = new ASN1Sequence(false);
        sequence1.setElement(0, false, false, new ASN1Boolean(true), null);
        sequence1.setElement(1, false, true, new ASN1Integer(255, 0, 255), null);
        log.debug("   sequence: {}", Hex.encodeHexString(sequence1.getEncoded()));
        Assert.assertEquals(Hex.encodeHexString(sequence1.getEncoded(), false), "80 FF FF".replaceAll(" ", ""));
        ASN1Sequence decodeSequence1 = new ASN1Sequence(false);
        decodeSequence1.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence1.setElement(1, false, true, new ASN1Integer(0, 255), null);
        Assert.assertEquals(sequence1, decodeSequence1.fromByteArray(sequence1.getEncoded()));

        /*
         * Rocket ::= SEQUENCE { a BOOLEAN, b INTEGER (0..255) DEFAULT 64 }
         * rec1value Rocket ::= { a TRUE }
         * 00 FF
         * */
        ASN1Sequence sequence2 = new ASN1Sequence(false);
        sequence2.setElement(0, false, false, new ASN1Boolean(true), null);
        sequence2.setElement(1, false, false, null, new ASN1Integer(64, 0, 255));
        log.debug("   sequence: {}", Hex.encodeHexString(sequence2.getEncoded()));
        Assert.assertEquals(Hex.encodeHexString(sequence2.getEncoded(), false), "00 FF".replaceAll(" ", ""));
        ASN1Sequence decodeSequence2 = new ASN1Sequence(false);
        decodeSequence2.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence2.setElement(1, false, false, null, new ASN1Integer(64, 0, 255));
        Assert.assertEquals(sequence2, decodeSequence2.fromByteArray(sequence2.getEncoded()));

        /*
         * Rocket ::= SEQUENCE { a BOOLEAN, b INTEGER (0..255) DEFAULT 64 }
         * rec1value Rocket ::= { a TRUE, b 127 }
         * 80 FF 7F
         * */
        ASN1Sequence sequence3 = new ASN1Sequence(false);
        sequence3.setElement(0, false, false, new ASN1Boolean(true), null);
        sequence3.setElement(1, false, false, new ASN1Integer(127, 0, 255), new ASN1Integer(64, 0, 255));
        log.debug("   sequence: {}", Hex.encodeHexString(sequence3.getEncoded()));
        Assert.assertEquals(Hex.encodeHexString(sequence3.getEncoded(), false), "80 FF 7F".replaceAll(" ", ""));
        ASN1Sequence decodeSequence3 = new ASN1Sequence(false);
        decodeSequence3.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence3.setElement(1, false, false, new ASN1Integer(0, 255), new ASN1Integer(64, 0, 255));
        Assert.assertEquals(sequence3, decodeSequence3.fromByteArray(sequence3.getEncoded()));

        /*
         * Rocket ::= SEQUENCE { a BOOLEAN DEFAULT TRUE, b INTEGER (0..255) }
         * rec1value Rocket ::= { a TRUE, b 128 }
         * 80 FF 80
         * */
        ASN1Sequence sequence4 = new ASN1Sequence(false);
        sequence4.setElement(0, false, false, new ASN1Boolean(true), new ASN1Boolean(true));
        sequence4.setElement(1, false, false, new ASN1Integer(128, 0, 255), null);
        log.debug("   sequence: {}", Hex.encodeHexString(sequence4.getEncoded()));
        Assert.assertEquals(Hex.encodeHexString(sequence4.getEncoded(), false), "80 FF 80".replaceAll(" ", ""));
        ASN1Sequence decodeSequence4 = new ASN1Sequence(false);
        decodeSequence4.setElement(0, false, false, new ASN1Boolean(), new ASN1Boolean(true));
        decodeSequence4.setElement(1, false, false, new ASN1Integer(0, 255), null);
        Assert.assertEquals(sequence4, decodeSequence4.fromByteArray(sequence4.getEncoded()));

        /*
         * Rocket ::= SEQUENCE { a BOOLEAN, b INTEGER (0..255) OPTIONAL }
         * rec1value Rocket ::= { a TRUE }
         * 00 FF
         * */
        ASN1Sequence sequence5 = new ASN1Sequence(false);
        sequence5.setElement(0, false, false, new ASN1Boolean(true), null);
        sequence5.setElement(1, false, true, null, null);
        log.debug("   sequence: {}", Hex.encodeHexString(sequence5.getEncoded()));
        Assert.assertEquals(Hex.encodeHexString(sequence5.getEncoded(), false), "00 FF".replaceAll(" ", ""));
        ASN1Sequence decodeSequence5 = new ASN1Sequence(false);
        decodeSequence5.setElement(0, false, false, new ASN1Boolean(true), null);
        decodeSequence5.setElement(1, false, true, null, null);
        Assert.assertEquals(sequence5, decodeSequence5.fromByteArray(sequence5.getEncoded()));

        /*
         * Rocket ::= SEQUENCE { a BOOLEAN, b INTEGER (0..255) OPTIONAL, ... }
         * rec1value Rocket ::= { a TRUE, b 255 }
         * 40 FF FF
         * */
        ASN1Sequence sequence6 = new ASN1Sequence(true);
        sequence6.setElement(0, false, false, new ASN1Boolean(true), null);
        sequence6.setElement(1, false, true, new ASN1Integer(255, 0, 255), null);
        log.debug("   sequence: {}", Hex.encodeHexString(sequence6.getEncoded()));
        Assert.assertEquals(Hex.encodeHexString(sequence6.getEncoded(), false), "40 FF FF".replaceAll(" ", ""));
        ASN1Sequence decodeSequence6 = new ASN1Sequence(true);
        decodeSequence6.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence6.setElement(1, false, true, new ASN1Integer(0, 255), null);
        Assert.assertEquals(sequence6, decodeSequence6.fromByteArray(sequence6.getEncoded()));

        /*
         * Rocket ::= SEQUENCE { a BOOLEAN, b INTEGER (0..255), ... }
         * rec1value Rocket ::= { a FALSE, b 255 }
         * 00 00 FF
         * */
        ASN1Sequence sequence7 = new ASN1Sequence(true);
        sequence7.setElement(0, false, false, new ASN1Boolean(false), null);
        sequence7.setElement(1, false, false, new ASN1Integer(255, 0, 255), null);
        log.debug("   sequence: {}", Hex.encodeHexString(sequence7.getEncoded()));
        Assert.assertEquals(Hex.encodeHexString(sequence7.getEncoded(), false), "00 00 FF".replaceAll(" ", ""));
        ASN1Sequence decodeSequence7 = new ASN1Sequence(true);
        decodeSequence7.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence7.setElement(1, false, false, new ASN1Integer(0, 255), null);
        Assert.assertEquals(sequence7, decodeSequence7.fromByteArray(sequence7.getEncoded()));

        /*
         * Rocket ::= SEQUENCE { a BOOLEAN, b INTEGER (0..255), ..., c BOOLEAN, d BOOLEAN }
         * rec1value Rocket ::= { a FALSE, b 255 }
         * 00 00 FF
         * */
        ASN1Sequence sequence8 = new ASN1Sequence(true);
        sequence8.setElement(0, false, false, new ASN1Boolean(false), null);
        sequence8.setElement(1, false, false, new ASN1Integer(255, 0, 255), null);
        sequence8.setElement(0, true, false, null, null);
        sequence8.setElement(1, true, false, null, null);
        log.debug("   sequence: {}", Hex.encodeHexString(sequence8.getEncoded()));
        Assert.assertEquals(Hex.encodeHexString(sequence8.getEncoded(), false), "00 00 FF".replaceAll(" ", ""));
        ASN1Sequence decodeSequence8 = new ASN1Sequence(true);
        decodeSequence8.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence8.setElement(1, false, false, new ASN1Integer( 0, 255), null);
        decodeSequence8.setElement(0, true, false, null, null);
        decodeSequence8.setElement(1, true, false, null, null);
        Assert.assertEquals(sequence8, decodeSequence8.fromByteArray(sequence8.getEncoded()));

        /*
         * Rocket ::= SEQUENCE { a BOOLEAN, b INTEGER (0..255), ..., c BOOLEAN DEFAULT FALSE, d BOOLEAN }
         * rec1value Rocket ::= { a FALSE, b 255 }
         * 00 00 FF
         * */
        ASN1Sequence sequence9 = new ASN1Sequence(true);
        sequence9.setElement(0, false, false, new ASN1Boolean(false), null);
        sequence9.setElement(1, false, false, new ASN1Integer(255, 0, 255), null);
        sequence9.setElement(0, true, false, null, new ASN1Boolean(false));
        sequence9.setElement(1, true, false, null, null);
        log.debug("   sequence: {}", Hex.encodeHexString(sequence9.getEncoded()));
        Assert.assertEquals(Hex.encodeHexString(sequence9.getEncoded(), false), "00 00 FF".replaceAll(" ", ""));
        ASN1Sequence decodeSequence9 = new ASN1Sequence(true);
        decodeSequence9.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence9.setElement(1, false, false, new ASN1Integer(0, 255), null);
        decodeSequence9.setElement(0, true, false, null, new ASN1Boolean(false));
        decodeSequence9.setElement(1, true, false, null, null);
        Assert.assertEquals(sequence9, decodeSequence9.fromByteArray(sequence9.getEncoded()));

        /*
         * Rocket ::= SEQUENCE { a BOOLEAN, b INTEGER (0..255) OPTIONAL, ..., c BOOLEAN, d BOOLEAN }
         * rec1value Rocket ::= { a FALSE, b 255, c TRUE }
         * C0 00 FF 02 06 80 01 FF
         * */
        ASN1Sequence sequence10 = new ASN1Sequence(true);
        sequence10.setElement(0, false, false, new ASN1Boolean(false), null);
        sequence10.setElement(1, false, true, new ASN1Integer(255, 0, 255), null);
        sequence10.setElement(0, true, false, new ASN1Boolean(true), null);
        sequence10.setElement(1, true, false, null, null);
        log.debug("   sequence: {}", Hex.encodeHexString(sequence10.getEncoded()));
        Assert.assertEquals(Hex.encodeHexString(sequence10.getEncoded(), false), "C0 00 FF 02 06 80 01 FF".replaceAll(" ", ""));
        ASN1Sequence decodeSequence10 = new ASN1Sequence(true);
        decodeSequence10.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence10.setElement(1, false, true, new ASN1Integer(0, 255), null);
        decodeSequence10.setElement(0, true, false, new ASN1Boolean(), null);
        decodeSequence10.setElement(1, true, false, null, null);
        Assert.assertEquals(sequence10, decodeSequence10.fromByteArray(sequence10.getEncoded()));

        /*
         * Rocket ::= SEQUENCE { a BOOLEAN, b INTEGER (0..255) OPTIONAL, e INTEGER (0..255) OPTIONAL, ..., c BOOLEAN, d BOOLEAN }
         * rec1value Rocket ::= { a FALSE, b 255, e 127, c TRUE }
         * E0 00 FF 7F 02 06 80 01  FF
         * */
        ASN1Sequence sequence11 = new ASN1Sequence(true);
        sequence11.setElement(0, false, false, new ASN1Boolean(false), null);
        sequence11.setElement(1, false, true, new ASN1Integer(255, 0, 255), null);
        sequence11.setElement(2, false, true, new ASN1Integer(127, 0, 255), null);
        sequence11.setElement(0, true, false, new ASN1Boolean(true), null);
        sequence11.setElement(1, true, false, null, null);
        log.debug("   sequence: {}", Hex.encodeHexString(sequence11.getEncoded()));
        Assert.assertEquals(Hex.encodeHexString(sequence11.getEncoded(), false), "E0 00 FF 7F 02 06 80 01  FF".replaceAll(" ", ""));
        ASN1Sequence decodeSequence11 = new ASN1Sequence(true);
        decodeSequence11.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence11.setElement(1, false, true, new ASN1Integer(0, 255), null);
        decodeSequence11.setElement(2, false, true, new ASN1Integer(0, 255), null);
        decodeSequence11.setElement(0, true, false, new ASN1Boolean(), null);
        decodeSequence11.setElement(1, true, false, null, null);
        Assert.assertEquals(sequence11, decodeSequence11.fromByteArray(sequence11.getEncoded()));

        /*
         * Rocket ::= SEQUENCE { a BOOLEAN, b INTEGER (0..255), ..., c BOOLEAN, d BOOLEAN }
         * rec1value Rocket ::= { a FALSE, b 255, c TRUE, d FALSE }
         * 80 00 FF 02 06 C0 01 FF  01 00
         * */
        ASN1Sequence sequence12 = new ASN1Sequence(true);
        sequence12.setElement(0, false, false, new ASN1Boolean(false), null);
        sequence12.setElement(1, false, false, new ASN1Integer(255, 0, 255), null);
        sequence12.setElement(0, true, false, new ASN1Boolean(true), null);
        sequence12.setElement(1, true, false, new ASN1Boolean(false), null);
        log.debug("   sequence: {}", Hex.encodeHexString(sequence12.getEncoded()));
        Assert.assertEquals(Hex.encodeHexString(sequence12.getEncoded(), false), "80 00 FF 02 06 C0 01 FF  01 00".replaceAll(" ", ""));
        ASN1Sequence decodeSequence12 = new ASN1Sequence(true);
        decodeSequence12.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence12.setElement(1, false, false, new ASN1Integer(0, 255), null);
        decodeSequence12.setElement(0, true, false, new ASN1Boolean(), null);
        decodeSequence12.setElement(1, true, false, new ASN1Boolean(), null);
        Assert.assertEquals(sequence12, decodeSequence12.fromByteArray(sequence12.getEncoded()));

        /*
         * Rocket ::= SEQUENCE { a BOOLEAN, b INTEGER (0..255), ..., c BOOLEAN OPTIONAL, d BOOLEAN DEFAULT TRUE }
         * rec1value Rocket ::= { a FALSE, b 255 }
         * 80 00 FF 02 06 C0 01 FF  01 00
         * */
        ASN1Sequence sequence13 = new ASN1Sequence(true);
        sequence13.setElement(0, false, false, new ASN1Boolean(false), null);
        sequence13.setElement(1, false, false, new ASN1Integer(255, 0, 255), null);
        sequence13.setElement(0, true, true, new ASN1Boolean(true), null);
        sequence13.setElement(1, true, false, new ASN1Boolean(false), new ASN1Boolean(true));
        log.debug("   sequence: {}", Hex.encodeHexString(sequence13.getEncoded()));
        Assert.assertEquals(Hex.encodeHexString(sequence13.getEncoded(), false), "80 00 FF 02 06 C0 01 FF  01 00".replaceAll(" ", ""));
        ASN1Sequence decodeSequence13 = new ASN1Sequence(true);
        decodeSequence13.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence13.setElement(1, false, false, new ASN1Integer( 0, 255), null);
        decodeSequence13.setElement(0, true, true, new ASN1Boolean(), null);
        decodeSequence13.setElement(1, true, false, new ASN1Boolean(), new ASN1Boolean(true));
        Assert.assertEquals(sequence13, decodeSequence13.fromByteArray(sequence13.getEncoded()));

        /*
         * Rocket ::= SEQUENCE { a BOOLEAN, b INTEGER (0..255), ..., c BOOLEAN OPTIONAL, d BOOLEAN DEFAULT TRUE }
         * rec1value Rocket ::= { a FALSE, b 255, c TRUE, d FALSE }
         * 00 00 FF
         * */
        ASN1Sequence sequence14 = new ASN1Sequence(true);
        sequence14.setElement(0, false, false, new ASN1Boolean(false), null);
        sequence14.setElement(1, false, false, new ASN1Integer(255, 0, 255), null);
        sequence14.setElement(0, true, true, null, null);
        sequence14.setElement(1, true, false, null, new ASN1Boolean(true));
        log.debug("   sequence: {}", Hex.encodeHexString(sequence14.getEncoded()));
        Assert.assertEquals(Hex.encodeHexString(sequence14.getEncoded(), false), "00 00 FF".replaceAll(" ", ""));
        ASN1Sequence decodeSequence14 = new ASN1Sequence(true);
        decodeSequence14.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence14.setElement(1, false, false, new ASN1Integer(0, 255), null);
        decodeSequence14.setElement(0, true, true, null, null);
        decodeSequence14.setElement(1, true, false, null, new ASN1Boolean(true));
        Assert.assertEquals(sequence14, decodeSequence14.fromByteArray(sequence14.getEncoded()));
    }

    @Test
    public void testSequenceOf() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        COERSequenceOf esSequenceOf = new COERSequenceOf(Arrays.asList(new COERBoolean(true), new COERBoolean(false)));
        esSequenceOf.encode(new DataOutputStream(baos));
        log.debug("es sequence-of: {}", Hex.encodeHexString(baos.toByteArray()));

        ASN1SequenceOf<ASN1Boolean> sequenceOf = new ASN1SequenceOf<>(new ASN1Boolean[]{ new ASN1Boolean(true), new ASN1Boolean(false) });
        log.debug("   sequence-of: {}", Hex.encodeHexString(sequenceOf.getEncoded()));
        Assert.assertEquals(sequenceOf, new ASN1SequenceOf<>(ASN1Boolean::new).fromByteArray(sequenceOf.getEncoded()));
        Assert.assertArrayEquals(baos.toByteArray(), sequenceOf.getEncoded());
    }

    enum EnumeratedType implements COEREnumerationType {
        A,B
    }
}