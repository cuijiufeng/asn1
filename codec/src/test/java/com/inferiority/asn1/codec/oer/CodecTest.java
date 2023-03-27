package com.inferiority.asn1.codec.oer;

import com.inferiority.asn1.codec.CodecException;
import com.inferiority.asn1.codec.utils.HexEncoder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;

@Slf4j
public class CodecTest {

    @Test
    public void TestBoolean() throws CodecException, IOException {
        ASN1Boolean bTrue = new ASN1Boolean(false);
        log.debug("   boolean: {}", HexEncoder.encodeString(bTrue.getEncoded()));
        ASN1Boolean decodeAsn1Boolean = new ASN1Boolean();
        decodeAsn1Boolean.fromByteArray(bTrue.getEncoded());
        Assert.assertEquals(bTrue, decodeAsn1Boolean);
        log.debug(decodeAsn1Boolean.toObjectString());
        log.debug(decodeAsn1Boolean.toJsonString());
    }

    @Test
    public void testInteger() throws IOException, CodecException {

        ASN1Integer integer = new ASN1Integer(new BigInteger(
                    "7FFFFFFFFFFFFFFF7FFFFFFFFFFFFFFF" +
                        "7FFFFFFFFFFFFFFF7FFFFFFFFFFFFFFF" +
                        "7FFFFFFFFFFFFFFF7FFFFFFFFFFFFFFF" +
                        "7FFFFFFFFFFFFFFF7FFFFFFFFFFFFFFF" +
                        "7FFFFFFFFFFFFFFF7FFFFFFFFFFFFFFF" +
                        "7FFFFFFFFFFFFFFF7FFFFFFFFFFFFFFF" +
                        "7FFFFFFFFFFFFFFF7FFFFFFFFFFFFFFF" +
                        "7FFFFFFFFFFFFFFF7FFFFFFFFFFFFFFF", 16), null, null);
        log.debug("   integer: {}", HexEncoder.encodeString(integer.getEncoded()));
        log.debug("   integer: {}", integer.getValue());
        ASN1Integer decodeInteger = new ASN1Integer(null, null);
        decodeInteger.fromByteArray(integer.getEncoded());
        Assert.assertEquals(integer, decodeInteger);
        log.debug(decodeInteger.toObjectString());
        log.debug(decodeInteger.toJsonString());
    }

    @Test
    public void testEnumerated() throws IOException, CodecException {
        ASN1Enumerated enumerated = new ASN1Enumerated(EnumeratedType.B);
        log.debug("   enumerated: {}", HexEncoder.encodeString(enumerated.getEncoded()));
        log.debug("   enumerated: {}", enumerated.getEnumerated());
        ASN1Enumerated decodeEnumerated = new ASN1Enumerated(EnumeratedType.class);
        decodeEnumerated.fromByteArray(enumerated.getEncoded());
        Assert.assertEquals(enumerated, decodeEnumerated);
        log.debug(decodeEnumerated.toObjectString());
        log.debug(decodeEnumerated.toJsonString());
    }

    @Test
    public void testIA5String() throws IOException, CodecException {
        ASN1IA5String ia5String = new ASN1IA5String("hello world", 5, null);
        log.debug("   IA5String: {}", HexEncoder.encodeString(ia5String.getEncoded()));
        log.debug("   IA5String: {}", ia5String.getString());
        ASN1IA5String decodeIa5String = new ASN1IA5String(5, null);
        decodeIa5String.fromByteArray(ia5String.getEncoded());
        Assert.assertEquals(ia5String, decodeIa5String);
        log.debug(decodeIa5String.toObjectString());
        log.debug(decodeIa5String.toJsonString());
    }

    @Test
    public void testVisibleString() throws CodecException {
        ASN1VisibleString visibleString = new ASN1VisibleString("hello world\u0080", 5, null);
        log.debug("   IA5String: {}", HexEncoder.encodeString(visibleString.getEncoded()));
        log.debug("   IA5String: {}", visibleString.getString());
        ASN1VisibleString decodecVisibleString = new ASN1VisibleString(5, null);
        decodecVisibleString.fromByteArray(visibleString.getEncoded());
        Assert.assertEquals(visibleString, decodecVisibleString);
    }

    @Test
    public void testPrintableString() throws CodecException {
        ASN1PrintableString visibleString = new ASN1PrintableString("hello world", 5, null);
        log.debug("   visibleString: {}", HexEncoder.encodeString(visibleString.getEncoded()));
        log.debug("   visibleString: {}", visibleString.getString());
        ASN1PrintableString asn1PrintableString = new ASN1PrintableString(5, null);
        asn1PrintableString.fromByteArray(visibleString.getEncoded());
        Assert.assertEquals(visibleString, asn1PrintableString);
    }

    @Test
    public void testOctetString() throws IOException, CodecException {
        ASN1OctetString octetString = new ASN1OctetString("hello world".getBytes(), 5, null);
        log.debug("   octetString: {}", HexEncoder.encodeString(octetString.getEncoded()));
        ASN1OctetString decodeOctetString = new ASN1OctetString(5, null);
        decodeOctetString.fromByteArray(octetString.getEncoded());
        Assert.assertEquals(octetString, decodeOctetString);
        log.debug(decodeOctetString.toObjectString());
        log.debug(decodeOctetString.toJsonString());
    }

    @Test
    public void testBitString() throws CodecException {
        //固定大小
        //00000011 00000000 00000000=03 00 00                   Y       Y
        ASN1BitString bitString0 = new ASN1BitString(new byte[] {0x03,0x00,0x00}, true, null);
        log.debug("   bitString: {}", HexEncoder.encodeString(bitString0.getEncoded()));
        ASN1BitString decodeBitString0 = new ASN1BitString(3, null);
        decodeBitString0.fromByteArray(bitString0.getEncoded());
        Assert.assertEquals(bitString0, decodeBitString0);
        log.debug(decodeBitString0.toObjectString());
        log.debug(decodeBitString0.toJsonString());

        //非固定大小
        //00000000                  =01 00              2       N
        ASN1BitString bitString1 = new ASN1BitString(new byte[] {0x00}, false, new String[2]);
        log.debug("   bitString: {}", HexEncoder.encodeString(bitString1.getEncoded()));
        ASN1BitString decodeBitString1 = new ASN1BitString(null, new String[2]);
        decodeBitString1.fromByteArray(bitString1.getEncoded());
        Assert.assertEquals(bitString1, decodeBitString1);
        log.debug(decodeBitString1.toObjectString());
        log.debug(decodeBitString1.toJsonString());

        //00000000 00110000         =03 04 00 30        2       Y       Y
        ASN1BitString bitString4 = new ASN1BitString(new byte[] {0x00,0x30}, false, new String[2]);
        log.debug("   bitString: {}", HexEncoder.encodeString(bitString4.getEncoded()));
        ASN1BitString decodeBitString4 = new ASN1BitString(null, new String[2]);
        decodeBitString4.fromByteArray(bitString4.getEncoded());
        Assert.assertEquals(bitString4, decodeBitString4);
        log.debug(decodeBitString4.toObjectString());
        log.debug(decodeBitString4.toJsonString());

        //00000000 00000011         =03 00 00 03        2       Y       Y
        ASN1BitString bitString5 = new ASN1BitString(new byte[] {0x00,0x03}, false, new String[2]);
        log.debug("   bitString: {}", HexEncoder.encodeString(bitString5.getEncoded()));
        ASN1BitString decodeBitString5 = new ASN1BitString(null, new String[2]);
        decodeBitString5.fromByteArray(bitString5.getEncoded());
        Assert.assertEquals(bitString5, decodeBitString5);
        log.debug(decodeBitString5.toObjectString());
        log.debug(decodeBitString5.toJsonString());

        //00000000                  =02 00 00                   Y       Y
        ASN1BitString bitString6 = new ASN1BitString(new byte[] {0x00}, false, null);
        log.debug("   bitString: {}", HexEncoder.encodeString(bitString6.getEncoded()));
        ASN1BitString decodeBitString6 = new ASN1BitString(null, null);
        decodeBitString6.fromByteArray(bitString6.getEncoded());
        Assert.assertEquals(bitString6, decodeBitString6);
        log.debug(decodeBitString6.toObjectString());
        log.debug(decodeBitString6.toJsonString());

        //00000000 00000000 00000000=04 00 00 00 00             Y       Y
        ASN1BitString bitString7 = new ASN1BitString(new byte[] {(byte) 0x00,0x00,0x00}, false, null);
        log.debug("   bitString: {}", HexEncoder.encodeString(bitString7.getEncoded()));
        ASN1BitString decodeBitString7 = new ASN1BitString(null, null);
        decodeBitString7.fromByteArray(bitString7.getEncoded());
        Assert.assertEquals(bitString7, decodeBitString7);
        log.debug(decodeBitString7.toObjectString());
        log.debug(decodeBitString7.toJsonString());

        //10000000 00000000 00000000=04 00 80 00 00             Y       Y
        ASN1BitString bitString8 = new ASN1BitString(new byte[] {(byte) 0x80,0x00,0x00}, false, null);
        log.debug("   bitString: {}", HexEncoder.encodeString(bitString8.getEncoded()));
        ASN1BitString decodeBitString8 = new ASN1BitString(null, null);
        decodeBitString8.fromByteArray(bitString8.getEncoded());
        Assert.assertEquals(bitString8, decodeBitString8);
        log.debug(decodeBitString8.toObjectString());
        log.debug(decodeBitString8.toJsonString());

        //10000000 00000000 00010000=04 00 80 00 10             Y       Y
        ASN1BitString bitString9 = new ASN1BitString(new byte[] {(byte) 0x80,0x00,0x10}, false, null);
        log.debug("   bitString: {}", HexEncoder.encodeString(bitString9.getEncoded()));
        ASN1BitString decodeBitString9 = new ASN1BitString(null, null);
        decodeBitString9.fromByteArray(bitString9.getEncoded());
        Assert.assertEquals(bitString9, decodeBitString9);
        log.debug(decodeBitString9.toObjectString());
        log.debug(decodeBitString9.toJsonString());

        //10000000 00000000 00010000=04 04 80 00 10     19      Y       Y
        ASN1BitString bitString10 = new ASN1BitString(new byte[] {(byte) 0x80,0x00,0x10}, false, new String[19]);
        log.debug("   bitString: {}", HexEncoder.encodeString(bitString10.getEncoded()));
        ASN1BitString decodeBitString10 = new ASN1BitString(null, new String[19]);
        decodeBitString10.fromByteArray(bitString10.getEncoded());
        Assert.assertEquals(bitString10, decodeBitString10);
        log.debug(decodeBitString10.toObjectString());
        log.debug(decodeBitString10.toJsonString());

        //10000000 00000000 01000000=04 06 80 00 40     19      Y       Y
        ASN1BitString bitString11 = new ASN1BitString(new byte[] {(byte) 0x80,0x00,0x40}, false, new String[19]);
        log.debug("   bitString: {}", HexEncoder.encodeString(bitString11.getEncoded()));
        ASN1BitString decodeBitString11 = new ASN1BitString(null, new String[19]);
        decodeBitString11.fromByteArray(bitString11.getEncoded());
        Assert.assertEquals(bitString11, decodeBitString11);
        log.debug(decodeBitString11.toObjectString());
        log.debug(decodeBitString11.toJsonString());

        //10000000 00000000 00000010=04 01 80 00 02     19      Y       Y
        ASN1BitString bitString12 = new ASN1BitString(new byte[] {(byte) 0x80,0x00,0x02}, false, new String[19]);
        log.debug("   bitString: {}", HexEncoder.encodeString(bitString12.getEncoded()));
        ASN1BitString decodeBitString12 = new ASN1BitString(null, new String[19]);
        decodeBitString12.fromByteArray(bitString12.getEncoded());
        Assert.assertEquals(bitString12, decodeBitString12);
        log.debug(decodeBitString12.toObjectString());
        log.debug(decodeBitString12.toJsonString());

        //10000000 00000000 00000000=02 07 80           19      Y
        ASN1BitString bitString13 = new ASN1BitString(new byte[] {(byte) 0x80,0x00,0x00}, false, new String[19]);
        log.debug("   bitString: {}", HexEncoder.encodeString(bitString13.getEncoded()));
        ASN1BitString decodeBitString13 = new ASN1BitString(null, new String[19]);
        decodeBitString13.fromByteArray(bitString13.getEncoded());
        Assert.assertEquals(bitString13, decodeBitString13);
        log.debug(decodeBitString13.toObjectString());
        log.debug(decodeBitString13.toJsonString());
        log.debug("   bitString pos 0 : {}", bitString13.getBit(0));
        log.debug("   bitString pos 15: {}", bitString13.getBit(15));
        bitString13.setBit(15, true);
        log.debug("   bitString pos 15: {}", bitString13.getBit(16));
        bitString13.setBit(16, true);
        log.debug("   bitString pos 16: {}", bitString13.getBit(16));
    }

    @Test
    public void testSequence() throws IOException, CodecException {
        /*
         * Rocket ::= SEQUENCE { a BOOLEAN, b INTEGER (0..255) }
         * rec1value Rocket ::= { a TRUE, b 255 }
         * FF FF
         * */
        ASN1Sequence sequence0 = new ASN1Sequence(false);
        sequence0.setElement(0, false, false, new ASN1Boolean(true), null);
        sequence0.setElement(1, false, false, new ASN1Integer(255, 0, 255), null);
        log.debug("   sequence: {}", HexEncoder.encodeString(sequence0.getEncoded()));
        Assert.assertEquals(HexEncoder.encodeString(sequence0.getEncoded(), false), "FF FF".replaceAll(" ", ""));
        ASN1Sequence decodeSequence0 = new ASN1Sequence(false);
        decodeSequence0.setElement("a", 0, false, false, new ASN1Boolean(), null);
        decodeSequence0.setElement("b", 1, false, false, new ASN1Integer(0, 255), null);
        decodeSequence0.fromByteArray(sequence0.getEncoded());
        Assert.assertEquals(sequence0, decodeSequence0);
        log.debug(decodeSequence0.toObjectString());
        log.debug(decodeSequence0.toJsonString());

        /*
         * Rocket ::= SEQUENCE { a BOOLEAN, b INTEGER (0..255) OPTIONAL }
         * rec1value Rocket ::= { a TRUE, b 255 }
         * 80 FF FF
         * */
        ASN1Sequence sequence1 = new ASN1Sequence(false);
        sequence1.setElement(0, false, false, new ASN1Boolean(true), null);
        sequence1.setElement(1, false, true, new ASN1Integer(255, 0, 255), null);
        log.debug("   sequence: {}", HexEncoder.encodeString(sequence1.getEncoded()));
        Assert.assertEquals(HexEncoder.encodeString(sequence1.getEncoded(), false), "80 FF FF".replaceAll(" ", ""));
        ASN1Sequence decodeSequence1 = new ASN1Sequence(false);
        decodeSequence1.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence1.setElement(1, false, true, new ASN1Integer(0, 255), null);
        decodeSequence1.fromByteArray(sequence1.getEncoded());
        Assert.assertEquals(sequence1, decodeSequence1);

        /*
         * Rocket ::= SEQUENCE { a BOOLEAN, b INTEGER (0..255) DEFAULT 64 }
         * rec1value Rocket ::= { a TRUE }
         * 00 FF
         * */
        ASN1Sequence sequence2 = new ASN1Sequence(false);
        sequence2.setElement(0, false, false, new ASN1Boolean(true), null);
        sequence2.setElement(1, false, false, null, new ASN1Integer(64, 0, 255));
        log.debug("   sequence: {}", HexEncoder.encodeString(sequence2.getEncoded()));
        Assert.assertEquals(HexEncoder.encodeString(sequence2.getEncoded(), false), "00 FF".replaceAll(" ", ""));
        ASN1Sequence decodeSequence2 = new ASN1Sequence(false);
        decodeSequence2.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence2.setElement(1, false, false, null, new ASN1Integer(64, 0, 255));
        decodeSequence2.fromByteArray(sequence2.getEncoded());
        Assert.assertEquals(sequence2, decodeSequence2);

        /*
         * Rocket ::= SEQUENCE { a BOOLEAN, b INTEGER (0..255) DEFAULT 64 }
         * rec1value Rocket ::= { a TRUE, b 127 }
         * 80 FF 7F
         * */
        ASN1Sequence sequence3 = new ASN1Sequence(false);
        sequence3.setElement(0, false, false, new ASN1Boolean(true), null);
        sequence3.setElement(1, false, false, new ASN1Integer(127, 0, 255), new ASN1Integer(64, 0, 255));
        log.debug("   sequence: {}", HexEncoder.encodeString(sequence3.getEncoded()));
        Assert.assertEquals(HexEncoder.encodeString(sequence3.getEncoded(), false), "80 FF 7F".replaceAll(" ", ""));
        ASN1Sequence decodeSequence3 = new ASN1Sequence(false);
        decodeSequence3.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence3.setElement(1, false, false, new ASN1Integer(0, 255), new ASN1Integer(64, 0, 255));
        decodeSequence3.fromByteArray(sequence3.getEncoded());
        Assert.assertEquals(sequence3, decodeSequence3);

        /*
         * Rocket ::= SEQUENCE { a BOOLEAN DEFAULT TRUE, b INTEGER (0..255) }
         * rec1value Rocket ::= { a TRUE, b 128 }
         * 80 FF 80
         * */
        ASN1Sequence sequence4 = new ASN1Sequence(false);
        sequence4.setElement(0, false, false, new ASN1Boolean(true), new ASN1Boolean(true));
        sequence4.setElement(1, false, false, new ASN1Integer(128, 0, 255), null);
        log.debug("   sequence: {}", HexEncoder.encodeString(sequence4.getEncoded()));
        Assert.assertEquals(HexEncoder.encodeString(sequence4.getEncoded(), false), "80 FF 80".replaceAll(" ", ""));
        ASN1Sequence decodeSequence4 = new ASN1Sequence(false);
        decodeSequence4.setElement(0, false, false, new ASN1Boolean(), new ASN1Boolean(true));
        decodeSequence4.setElement(1, false, false, new ASN1Integer(0, 255), null);
        decodeSequence4.fromByteArray(sequence4.getEncoded());
        Assert.assertEquals(sequence4, decodeSequence4);

        /*
         * Rocket ::= SEQUENCE { a BOOLEAN, b INTEGER (0..255) OPTIONAL }
         * rec1value Rocket ::= { a TRUE }
         * 00 FF
         * */
        ASN1Sequence sequence5 = new ASN1Sequence(false);
        sequence5.setElement(0, false, false, new ASN1Boolean(true), null);
        sequence5.setElement(1, false, true, null, null);
        log.debug("   sequence: {}", HexEncoder.encodeString(sequence5.getEncoded()));
        Assert.assertEquals(HexEncoder.encodeString(sequence5.getEncoded(), false), "00 FF".replaceAll(" ", ""));
        ASN1Sequence decodeSequence5 = new ASN1Sequence(false);
        decodeSequence5.setElement(0, false, false, new ASN1Boolean(true), null);
        decodeSequence5.setElement(1, false, true, null, null);
        decodeSequence5.fromByteArray(sequence5.getEncoded());
        Assert.assertEquals(sequence5, decodeSequence5);

        /*
         * Rocket ::= SEQUENCE { a BOOLEAN, b INTEGER (0..255) OPTIONAL, ... }
         * rec1value Rocket ::= { a TRUE, b 255 }
         * 40 FF FF
         * */
        ASN1Sequence sequence6 = new ASN1Sequence(true);
        sequence6.setElement(0, false, false, new ASN1Boolean(true), null);
        sequence6.setElement(1, false, true, new ASN1Integer(255, 0, 255), null);
        log.debug("   sequence: {}", HexEncoder.encodeString(sequence6.getEncoded()));
        Assert.assertEquals(HexEncoder.encodeString(sequence6.getEncoded(), false), "40 FF FF".replaceAll(" ", ""));
        ASN1Sequence decodeSequence6 = new ASN1Sequence(true);
        decodeSequence6.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence6.setElement(1, false, true, new ASN1Integer(0, 255), null);
        decodeSequence6.fromByteArray(sequence6.getEncoded());
        Assert.assertEquals(sequence6, decodeSequence6);

        /*
         * Rocket ::= SEQUENCE { a BOOLEAN, b INTEGER (0..255), ... }
         * rec1value Rocket ::= { a FALSE, b 255 }
         * 00 00 FF
         * */
        ASN1Sequence sequence7 = new ASN1Sequence(true);
        sequence7.setElement(0, false, false, new ASN1Boolean(false), null);
        sequence7.setElement(1, false, false, new ASN1Integer(255, 0, 255), null);
        log.debug("   sequence: {}", HexEncoder.encodeString(sequence7.getEncoded()));
        Assert.assertEquals(HexEncoder.encodeString(sequence7.getEncoded(), false), "00 00 FF".replaceAll(" ", ""));
        ASN1Sequence decodeSequence7 = new ASN1Sequence(true);
        decodeSequence7.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence7.setElement(1, false, false, new ASN1Integer(0, 255), null);
        decodeSequence7.fromByteArray(sequence7.getEncoded());
        Assert.assertEquals(sequence7, decodeSequence7);

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
        log.debug("   sequence: {}", HexEncoder.encodeString(sequence8.getEncoded()));
        Assert.assertEquals(HexEncoder.encodeString(sequence8.getEncoded(), false), "00 00 FF".replaceAll(" ", ""));
        ASN1Sequence decodeSequence8 = new ASN1Sequence(true);
        decodeSequence8.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence8.setElement(1, false, false, new ASN1Integer( 0, 255), null);
        decodeSequence8.setElement(0, true, false, null, null);
        decodeSequence8.setElement(1, true, false, null, null);
        decodeSequence8.fromByteArray(sequence8.getEncoded());
        Assert.assertEquals(sequence8, decodeSequence8);

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
        log.debug("   sequence: {}", HexEncoder.encodeString(sequence9.getEncoded()));
        Assert.assertEquals(HexEncoder.encodeString(sequence9.getEncoded(), false), "00 00 FF".replaceAll(" ", ""));
        ASN1Sequence decodeSequence9 = new ASN1Sequence(true);
        decodeSequence9.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence9.setElement(1, false, false, new ASN1Integer(0, 255), null);
        decodeSequence9.setElement(0, true, false, null, new ASN1Boolean(false));
        decodeSequence9.setElement(1, true, false, null, null);
        decodeSequence9.fromByteArray(sequence9.getEncoded());
        Assert.assertEquals(sequence9, decodeSequence9);

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
        log.debug("   sequence: {}", HexEncoder.encodeString(sequence10.getEncoded()));
        Assert.assertEquals(HexEncoder.encodeString(sequence10.getEncoded(), false), "C0 00 FF 02 06 80 01 FF".replaceAll(" ", ""));
        ASN1Sequence decodeSequence10 = new ASN1Sequence(true);
        decodeSequence10.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence10.setElement(1, false, true, new ASN1Integer(0, 255), null);
        decodeSequence10.setElement(0, true, false, new ASN1Boolean(), null);
        decodeSequence10.setElement(1, true, false, null, null);
        decodeSequence10.fromByteArray(sequence10.getEncoded());
        Assert.assertEquals(sequence10, decodeSequence10);

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
        log.debug("   sequence: {}", HexEncoder.encodeString(sequence11.getEncoded()));
        Assert.assertEquals(HexEncoder.encodeString(sequence11.getEncoded(), false), "E0 00 FF 7F 02 06 80 01  FF".replaceAll(" ", ""));
        ASN1Sequence decodeSequence11 = new ASN1Sequence(true);
        decodeSequence11.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence11.setElement(1, false, true, new ASN1Integer(0, 255), null);
        decodeSequence11.setElement(2, false, true, new ASN1Integer(0, 255), null);
        decodeSequence11.setElement(0, true, false, new ASN1Boolean(), null);
        decodeSequence11.setElement(1, true, false, null, null);
        decodeSequence11.fromByteArray(sequence11.getEncoded());
        Assert.assertEquals(sequence11, decodeSequence11);

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
        log.debug("   sequence: {}", HexEncoder.encodeString(sequence12.getEncoded()));
        Assert.assertEquals(HexEncoder.encodeString(sequence12.getEncoded(), false), "80 00 FF 02 06 C0 01 FF  01 00".replaceAll(" ", ""));
        ASN1Sequence decodeSequence12 = new ASN1Sequence(true);
        decodeSequence12.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence12.setElement(1, false, false, new ASN1Integer(0, 255), null);
        decodeSequence12.setElement(0, true, false, new ASN1Boolean(), null);
        decodeSequence12.setElement(1, true, false, new ASN1Boolean(), null);
        decodeSequence12.fromByteArray(sequence12.getEncoded());
        Assert.assertEquals(sequence12, decodeSequence12);

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
        log.debug("   sequence: {}", HexEncoder.encodeString(sequence13.getEncoded()));
        Assert.assertEquals(HexEncoder.encodeString(sequence13.getEncoded(), false), "80 00 FF 02 06 C0 01 FF  01 00".replaceAll(" ", ""));
        ASN1Sequence decodeSequence13 = new ASN1Sequence(true);
        decodeSequence13.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence13.setElement(1, false, false, new ASN1Integer( 0, 255), null);
        decodeSequence13.setElement(0, true, true, new ASN1Boolean(), null);
        decodeSequence13.setElement(1, true, false, new ASN1Boolean(), new ASN1Boolean(true));
        decodeSequence13.fromByteArray(sequence13.getEncoded());
        Assert.assertEquals(sequence13, decodeSequence13);

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
        log.debug("   sequence: {}", HexEncoder.encodeString(sequence14.getEncoded()));
        Assert.assertEquals(HexEncoder.encodeString(sequence14.getEncoded(), false), "00 00 FF".replaceAll(" ", ""));
        ASN1Sequence decodeSequence14 = new ASN1Sequence(true);
        decodeSequence14.setElement(0, false, false, new ASN1Boolean(), null);
        decodeSequence14.setElement(1, false, false, new ASN1Integer(0, 255), null);
        decodeSequence14.setElement(0, true, true, null, null);
        decodeSequence14.setElement(1, true, false, null, new ASN1Boolean(true));
        decodeSequence14.fromByteArray(sequence14.getEncoded());
        Assert.assertEquals(sequence14, decodeSequence14);
        log.debug(decodeSequence14.toObjectString());
        log.debug(decodeSequence14.toJsonString());
    }

    @Test
    public void testSequenceOf() throws IOException, CodecException {
        ASN1SequenceOf<ASN1Boolean> sequenceOf = new ASN1SequenceOf<>(new ASN1Boolean[]{ new ASN1Boolean(true), new ASN1Boolean(false) });
        log.debug("   sequence-of: {}", HexEncoder.encodeString(sequenceOf.getEncoded()));
        ASN1SequenceOf<ASN1Boolean> decodeSequenceOf = new ASN1SequenceOf<>(ASN1Boolean::new);
        decodeSequenceOf.fromByteArray(sequenceOf.getEncoded());
        Assert.assertEquals(sequenceOf, decodeSequenceOf);
        log.debug(decodeSequenceOf.toObjectString());
        log.debug(decodeSequenceOf.toJsonString());
    }

    @Test
    public void testTag() throws IOException, CodecException {
        ASN1Tag tag = new ASN1Tag(ASN1Tag.TagClass.CONTEXT_SPECIFIC, 16512);
        log.debug("   tag: {}", HexEncoder.encodeString(tag.getEncoded()));
        ASN1Tag decodeAsn1Tag = new ASN1Tag();
        decodeAsn1Tag.fromByteArray(tag.getEncoded());
        Assert.assertEquals(tag, decodeAsn1Tag);
        log.debug(decodeAsn1Tag.toObjectString());
        log.debug(decodeAsn1Tag.toJsonString());
    }

    @Test
    public void testChoice() throws CodecException {
        ASN1Choice choice = new ASN1Choice(EnumeratedType.B, new ASN1Boolean(true));
        log.debug("   choice: {}", HexEncoder.encodeString(choice.getEncoded()));
        ASN1Choice decodeChoice = new ASN1Choice(EnumeratedType.class);
        decodeChoice.fromByteArray(choice.getEncoded());
        Assert.assertEquals(choice, decodeChoice);
        EnumeratedType choice1 = choice.getChoice();
        ASN1Boolean component = choice.getValue();
        log.debug(decodeChoice.toObjectString());
        log.debug(decodeChoice.toJsonString());
    }

    enum EnumeratedType implements ASN1Choice.ASN1ChoiceEnum {
        A {
            @Override
            public ASN1Object getInstance() {
                return new ASN1Boolean();
            }
            @Override
            public boolean isExtension() {
                return false;
            }
        },
        B {
            @Override
            public ASN1Object getInstance() {
                return new ASN1Boolean();
            }
            @Override
            public boolean isExtension() {
                return true;
            }
        };
    }
}