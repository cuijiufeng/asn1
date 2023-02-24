package com.inferiority.asn1.codec.utils;

/**
 * @author cuijiufeng
 * @Class HexEncoder
 * @Date 2023/2/24 8:56
 */
public class HexEncoder {
    private static final char[] LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private static final char[] UPPER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    public static String encodeString(final byte[] data) {
        return encodeString(data, false);
    }

    public static String encodeString(final byte[] data, boolean toLower) {
        return String.valueOf(encode(data, toLower ? LOWER : UPPER));
    }

    public static char[] encode(final byte[] data, final char[] toDigits) {
        char[] out = new char[data.length << 1];
        for (int i = 0; i < data.length; i++) {
            out[i << 1] = toDigits[(0xF0 & data[i]) >>> 4];
            out[(i << 1) + 1] = toDigits[0x0F & data[i]];
        }
        return out;
    }

    public static byte[] decodeString(final String data) {
        return decode(data.toCharArray());
    }

    public static byte[] decode(final char[] data) {
        if ((data.length & 0x01) != 0) {
            throw new IllegalArgumentException("odd number of characters.");
        }
        byte[] out = new byte[data.length >>> 1];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) ((toDigit(data[i << 1]) << 4) | toDigit(data[(i << 1) + 1]));
        }
        return out;
    }

    private static int toDigit(final char ch) {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new IllegalArgumentException("illegal hexadecimal character '" + ch + "'");
        }
        return digit;
    }
}
