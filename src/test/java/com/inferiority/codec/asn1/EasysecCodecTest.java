package com.inferiority.codec.asn1;

import cn.com.easysec.v2x.asn1.coer.COERBitString;
import cn.com.easysec.v2x.asn1.coer.COERInteger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

/**
 * @author cuijiufeng
 * @Class CodecTest
 * @Date 2023/2/6 10:28
 */
@Slf4j
public class EasysecCodecTest {
    @Test
    public void testInteger() throws IOException {
        ByteArrayOutputStream baosNegTwoByte = new ByteArrayOutputStream();
        COERInteger negTwoByte = new COERInteger(-32768);
        negTwoByte.encode(new DataOutputStream(baosNegTwoByte));
        log.info("{} : {}", negTwoByte.getValue(), Hex.encodeHexString(baosNegTwoByte.toByteArray()));

        ByteArrayOutputStream baosNegOneByte = new ByteArrayOutputStream();
        COERInteger negOneByte = new COERInteger(-128);
        negOneByte.encode(new DataOutputStream(baosNegOneByte));
        log.info("{} : {}", negOneByte.getValue(), Hex.encodeHexString(baosNegOneByte.toByteArray()));
        COERInteger decodeNegOneByte = new COERInteger();
        decodeNegOneByte.decode(new DataInputStream(new ByteArrayInputStream(baosNegOneByte.toByteArray())));
        log.info("{}", decodeNegOneByte.getValue());

        ByteArrayOutputStream baosZore = new ByteArrayOutputStream();
        COERInteger zore = new COERInteger(0);
        zore.encode(new DataOutputStream(baosZore));
        log.info("{} : {}", zore.getValue(), Hex.encodeHexString(baosZore.toByteArray()));

        ByteArrayOutputStream baosOneByte = new ByteArrayOutputStream();
        COERInteger oneByte = new COERInteger(255, 0, 255);
        oneByte.encode(new DataOutputStream(baosOneByte));
        log.info("{} : {}", oneByte.getValue(), Hex.encodeHexString(baosOneByte.toByteArray()));
        COERInteger decodeOneByte = new COERInteger(0, 255);
        decodeOneByte.decode(new DataInputStream(new ByteArrayInputStream(baosOneByte.toByteArray())));
        log.info("{}", decodeOneByte.getValue());


        ByteArrayOutputStream baosTwoByte = new ByteArrayOutputStream();
        COERInteger twoByte = new COERInteger(65535);
        twoByte.encode(new DataOutputStream(baosTwoByte));
        log.info("{} : {}", twoByte.getValue(), Hex.encodeHexString(baosTwoByte.toByteArray()));

        ByteArrayOutputStream baosFourByte = new ByteArrayOutputStream();
        COERInteger fourByte = new COERInteger(4294967295L);
        fourByte.encode(new DataOutputStream(baosFourByte));
        log.info("{} : {}", fourByte.getValue(), Hex.encodeHexString(baosFourByte.toByteArray()));

        ByteArrayOutputStream baosEightByte = new ByteArrayOutputStream();
        COERInteger eightByte = new COERInteger(new BigInteger("18446744073709551615"));
        eightByte.encode(new DataOutputStream(baosEightByte));
        log.info("{} : {}", eightByte.getValue(), Hex.encodeHexString(baosEightByte.toByteArray()));

        ByteArrayOutputStream baosSixteenTimesByte = new ByteArrayOutputStream();
        COERInteger sixteenTimesByte = new COERInteger(new BigInteger(
                "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16), BigInteger.ZERO, new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16));
        sixteenTimesByte.encode(new DataOutputStream(baosSixteenTimesByte));
        log.info("{} : {}", sixteenTimesByte.getValue(), Hex.encodeHexString(baosSixteenTimesByte.toByteArray()));
    }

    @Test
    public void testBitString() throws IOException {
        ByteArrayOutputStream baosNegTwoByte = new ByteArrayOutputStream();
        COERBitString bitString = new COERBitString(0xff, 8, true);
        bitString.encode(new DataOutputStream(baosNegTwoByte));
        log.info("{} : {}", bitString.getBitString(), Hex.encodeHexString(baosNegTwoByte.toByteArray()));

        baosNegTwoByte = new ByteArrayOutputStream();
        bitString = new COERBitString(0x0f, 12, true);
        bitString.encode(new DataOutputStream(baosNegTwoByte));
        log.info("{} : {}", bitString.getBitString(), Hex.encodeHexString(baosNegTwoByte.toByteArray()));
    }
}
