package com.inferiority.codec.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class HexEncoderTest {

    @Test
    public void encodeString() {
        log.debug("{}", HexEncoder.encodeString(new byte[]{0x12, (byte) 0xae}));
        log.debug("{}", HexEncoder.decodeString("12Ae"));
    }
}