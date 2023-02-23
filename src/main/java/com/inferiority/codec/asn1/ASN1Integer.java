package com.inferiority.codec.asn1;

import com.inferiority.codec.ASN1InputStream;
import com.inferiority.codec.ASN1OutputStream;
import com.inferiority.codec.Codeable;
import com.inferiority.codec.utils.Nullable;

import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Objects;

/**
 * @author cuijiufeng
 * @Class ASN1Integer
 * @Date 2023/2/3 13:45
 */
public class ASN1Integer extends ASN1Object {
    private static final BigInteger BYTE_UNSIGNED_MAX = new BigInteger("+FF", 16);
    private static final BigInteger WORD_UNSIGNED_MAX = new BigInteger("+FFFF", 16);
    private static final BigInteger D_WORD_UNSIGNED_MAX = new BigInteger("+FFFFFFFF", 16);
    private static final BigInteger Q_WORD_UNSIGNED_MAX = new BigInteger("+FFFFFFFFFFFFFFFF", 16);
    private static final BigInteger BYTE_SIGNED_MIN = new BigInteger("-80", 16);
    private static final BigInteger BYTE_SIGNED_MAX = new BigInteger("+7F", 16);
    private static final BigInteger WORD_SIGNED_MIN = new BigInteger("-8000", 16);
    private static final BigInteger WORD_SIGNED_MAX = new BigInteger("+7FFF", 16);
    private static final BigInteger D_WORD_SIGNED_MIN = new BigInteger("-80000000", 16);
    private static final BigInteger D_WORD_SIGNED_MAX = new BigInteger("+7FFFFFFF", 16);
    private static final BigInteger Q_WORD_SIGNED_MIN = new BigInteger("-8000000000000000", 16);
    private static final BigInteger Q_WORD_SIGNED_MAX = new BigInteger("+7FFFFFFFFFFFFFFF", 16);

    private BigInteger value;
    private final BigInteger minValue;
    private final BigInteger maxValue;

    public ASN1Integer(long value, long minValue, long maxValue) {
        if (minValue > maxValue) {
            throw new IllegalArgumentException(String.format("the minimum value of %d is greater than the maximum value of %d", minValue, maxValue));
        }
        if (value < minValue) {
            throw new IllegalArgumentException(String.format("%d is less than the minimum value of %d", value, minValue));
        }
        if (value > maxValue) {
            throw new IllegalArgumentException(String.format("%d is greater than the maximum value of %d", value, maxValue));
        }
        this.value = BigInteger.valueOf(value);
        this.minValue = BigInteger.valueOf(minValue);
        this.maxValue = BigInteger.valueOf(maxValue);
    }

    public ASN1Integer(BigInteger value, @Nullable BigInteger minValue, @Nullable BigInteger maxValue) {
        Objects.requireNonNull(value, "value cannot be null");
        if (Objects.nonNull(minValue) && value.compareTo(minValue) < 0) {
            throw new IllegalArgumentException(String.format("%s is less than the minimum value of %s", value, minValue));
        }
        if (Objects.nonNull(maxValue) && value.compareTo(maxValue) > 0) {
            throw new IllegalArgumentException(String.format("%s is greater than the maximum value of %s", value, maxValue));
        }
        if (Objects.nonNull(minValue) && Objects.nonNull(maxValue) && minValue.compareTo(maxValue) > 0) {
            throw new IllegalArgumentException(String.format("the minimum value of %s is greater than the maximum value of %s", minValue, maxValue));
        }
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public ASN1Integer(long minValue, long maxValue) {
        if (minValue > maxValue) {
            throw new IllegalArgumentException(String.format("the minimum value of %d is greater than the maximum value of %d", minValue, maxValue));
        }
        this.minValue = BigInteger.valueOf(minValue);
        this.maxValue = BigInteger.valueOf(maxValue);
    }

    public ASN1Integer(@Nullable BigInteger minValue, @Nullable BigInteger maxValue) {
        if (Objects.nonNull(minValue) && Objects.nonNull(maxValue) && minValue.compareTo(maxValue) > 0) {
            throw new IllegalArgumentException(String.format("the minimum value of %s is greater than the maximum value of %s", minValue, maxValue));
        }
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public void encode(ASN1OutputStream os) {
        byte[] valueBytes = value.toByteArray();
        int signedOctets = valueBytes[0] == 0 && valueBytes.length > 1 ? 1 : 0;
        int fillOctets = -1;
        if (Objects.isNull(minValue) || Objects.isNull(maxValue) || (fillOctets = determineOctets() - (valueBytes.length - signedOctets)) < 0) {
            os.writeLengthDetermine(valueBytes.length - signedOctets);
        }
        //填充字节,大端序先填充高位
        while (fillOctets-- > 0) {
            if (this.value.signum() == -1) {
                os.write(-1);
            } else {
                os.write(0);
            }
        }
        os.write(valueBytes, signedOctets, valueBytes.length - signedOctets);
    }

    @Override
    public void decode(ASN1InputStream is) throws IOException {
        int length;
        if (Objects.isNull(minValue) || Objects.isNull(maxValue) || determineOctets() < 0) {
            length = is.readLengthDetermine();
        } else {
            length = determineOctets();
        }
        byte[] valueBytes = new byte[length];
        if (length != is.read(valueBytes, 0, length)) {
            throw new EOFException(String.format("expected to read %s bytes", length));
        }
        if (isUnsigned()) {
            this.value = new BigInteger(1, valueBytes);
        } else {
            this.value = new BigInteger(valueBytes);
        }
    }

    public boolean isUnsigned() {
        return Objects.nonNull(minValue) && minValue.compareTo(BigInteger.ZERO) >= 0;
    }

    /**
     * 此integer确定占用几个字节长度
     * @return int 返回-1时表示不确定的
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

    public BigInteger getValue() {
        return this.value;
    }

    public int intValue() {
        if (this.value.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0 || this.value.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
            throw new IllegalArgumentException(String.format("%s is out of the integer value range", this.value));
        }
        return this.value.intValue();
    }

    public long longValue() {
        if (this.value.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0 || this.value.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
            throw new IllegalArgumentException(String.format("%s is out of the integer long range", this.value));
        }
        return this.value.longValue();
    }

    @Override
    public boolean asn1Equals(Codeable obj) {
        if (!(obj instanceof ASN1Integer)) {
            return false;
        }
        ASN1Integer that = (ASN1Integer) obj;
        if (!Objects.equals(maxValue, that.maxValue)) return false;
        if (!Objects.equals(minValue, that.minValue)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (minValue != null ? minValue.hashCode() : 0);
        result = 31 * result + (maxValue != null ? maxValue.hashCode() : 0);
        return result;
    }
}
