package com.inferiority.codec.asn1;

import com.inferiority.codec.ASN1InputStream;
import com.inferiority.codec.ASN1OutputStream;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Objects;

/**
 * @author cuijiufeng
 * @Class ASN1Integer
 * @Date 2023/2/3 13:45
 */
public class ASN1Integer extends ASN1Object {
    private static final BigInteger BYTE_UNSIGNED_MAX = new BigInteger("FF", 16);
    private static final BigInteger WORD_UNSIGNED_MAX = new BigInteger("FFFF", 16);
    private static final BigInteger D_WORD_UNSIGNED_MAX = new BigInteger("FFFFFFFF", 16);
    private static final BigInteger Q_WORD_UNSIGNED_MAX = new BigInteger("FFFFFFFFFFFFFFFF", 16);
    private static final BigInteger BYTE_SIGNED_MIN = new BigInteger("80", 16);
    private static final BigInteger BYTE_SIGNED_MAX = new BigInteger("7F", 16);
    private static final BigInteger WORD_SIGNED_MIN = new BigInteger("8000", 16);
    private static final BigInteger WORD_SIGNED_MAX = new BigInteger("7FFF", 16);
    private static final BigInteger D_WORD_SIGNED_MIN = new BigInteger("80000000", 16);
    private static final BigInteger D_WORD_SIGNED_MAX = new BigInteger("7FFFFFFF", 16);
    private static final BigInteger Q_WORD_SIGNED_MIN = new BigInteger("8000000000000000", 16);
    private static final BigInteger Q_WORD_SIGNED_MAX = new BigInteger("7FFFFFFFFFFFFFFF", 16);

    private BigInteger value;
    private BigInteger minValue;
    private BigInteger maxValue;

    public ASN1Integer(Long value, Long minValue, Long maxValue) {
        //min要<=value,max要>=value
        this.value = BigInteger.valueOf(value);
        this.minValue = BigInteger.valueOf(minValue);
        this.maxValue = BigInteger.valueOf(maxValue);
    }

    public ASN1Integer(BigInteger value, BigInteger minValue, BigInteger maxValue) {
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public ASN1Integer(byte[] data) throws IOException {
        super(data);
    }

    @Override
    protected void encode(ASN1OutputStream os) {
        byte[] valueBytes = value.toByteArray();
        int signedOctets = valueBytes[0] == 0 && valueBytes.length > 1 ? 1 : 0;
        int fillOctets = -1;
        if (Objects.isNull(minValue) || Objects.isNull(maxValue) || (fillOctets = determineOctets() - (valueBytes.length - signedOctets)) < 0) {
            os.writeLengthDetermine(valueBytes.length - signedOctets);
        }
        //填充字节,大端序先填充高位
        while (fillOctets-- > 0) {
            os.write(0);
        }
        os.write(valueBytes, signedOctets, valueBytes.length - signedOctets);
    }

    @Override
    protected void decode(ASN1InputStream is) {

    }

    /**
     * 此integer确定占用几个字节长度
     * @return int 返回-1时表示不确定的
     * @throws
    */
    private int determineOctets() {
        if(minValue.compareTo(BigInteger.ZERO) >=0 && maxValue.compareTo(BYTE_UNSIGNED_MAX) <= 0
                || minValue.compareTo(BYTE_SIGNED_MIN) >=0 && maxValue.compareTo(BYTE_SIGNED_MAX) <= 0) {
            return 1;
        } else if(minValue.compareTo(BigInteger.ZERO) >=0 && maxValue.compareTo(WORD_UNSIGNED_MAX) <= 0
                || minValue.compareTo(WORD_SIGNED_MIN) >=0 && maxValue.compareTo(WORD_SIGNED_MAX) <= 0) {
            return 2;
        } else if(minValue.compareTo(BigInteger.ZERO) >=0 && maxValue.compareTo(D_WORD_UNSIGNED_MAX) <= 0
                || minValue.compareTo(D_WORD_SIGNED_MIN) >=0 && maxValue.compareTo(D_WORD_SIGNED_MAX) <= 0) {
            return 4;
        } else if(minValue.compareTo(BigInteger.ZERO) >=0 && maxValue.compareTo(Q_WORD_UNSIGNED_MAX) <= 0
                || minValue.compareTo(Q_WORD_SIGNED_MIN) >=0 && maxValue.compareTo(Q_WORD_SIGNED_MAX) <= 0) {
            return 8;
        }
        return -1;
    }
}
