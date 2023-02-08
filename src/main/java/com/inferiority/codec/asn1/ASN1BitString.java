package com.inferiority.codec.asn1;

import com.inferiority.codec.ASN1InputStream;
import com.inferiority.codec.ASN1OutputStream;
import com.inferiority.codec.Codeable;
import com.inferiority.codec.utils.Nullable;

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author cuijiufeng
 * @Class ASN1BitString
 * @Date 2023/2/7 16:04
 */
public class ASN1BitString extends ASN1Object {
    //大端序存储数据
    private byte[] bits;
    private final Integer bytes;
    private Integer maxValidBit;

    public ASN1BitString(byte b) {
        this.bytes = 1;
        this.bits = new byte[1];
    }

    public ASN1BitString(@Nullable Integer bytes, @Nullable Integer maxValidBit) {
        this.bytes = bytes;
        this.maxValidBit = maxValidBit;
    }

    public ASN1BitString(byte[] bits, @Nullable Integer maxValidBit, boolean fixed) {
        this.bytes = fixed ? bits.length : null;
        this.maxValidBit = maxValidBit;
        this.bits = Arrays.copyOf(bits, bits.length);
    }

    @Override
    public void encode(ASN1OutputStream os) {
        if (Objects.nonNull(bytes)) {
            //固定大小
            os.write(this.bits, 0, this.bits.length);
        } else {
            //非固定大小
            if (Objects.isNull(maxValidBit)) {
                //1.length prefix
                os.writeLengthDetermine(this.bits.length + 1);
                //2.unused-bit count prefix
                os.write(0);
                //3.bit data
                os.write(this.bits, 0, this.bits.length);
            } else {
                int lastValidIdx = this.bits.length - 1;
                while (lastValidIdx >= 0 && this.bits[lastValidIdx] == 0) {
                    lastValidIdx--;
                }
                int count = unusedBitCountPrefix(lastValidIdx != -1 ? this.bits[lastValidIdx] : 0);
                //1.length prefix
                os.writeLengthDetermine(lastValidIdx + 1 + 1);
                //2.unused-bit count prefix
                os.write(count);
                //3.bit data
                os.write(this.bits, 0, lastValidIdx + 1);
            }
        }
    }

    @Override
    public void decode(ASN1InputStream is) throws IOException {
        if (Objects.nonNull(bytes)) {
            //固定大小
            int length = this.bytes;
            this.bits = new byte[length];
            if (length != is.read(this.bits, 0, length)) {
                throw new EOFException(String.format("expected to read %s bytes", length));
            }
        } else {
            //非固定大小
            int length = is.readLengthDetermine();
            this.bits = new byte[length - 1];
            long ignore = is.skip(1);
            if (this.bits.length != 0 && this.bits.length != is.read(this.bits, 0, length - 1)) {
                throw new EOFException(String.format("expected to read %s bytes", length - 1));
            }
            if (Objects.nonNull(maxValidBit)) {
                this.bits = Arrays.copyOf(this.bits, Math.max(length - 1, ((maxValidBit + 7) & ~7) / 8));
            }
        }
    }

    public boolean getBit(int position) {
        if (position < 0) {
            throw new IllegalArgumentException("position cannot be less than 0");
        }
        int i = position / 8;
        int b = 7 - position % 8;
        return (this.bits[i] & 1 << b) != 0;
    }

    public void setBit(int position, boolean flag) {
        if (position < 0) {
            throw new IllegalArgumentException("position cannot be less than 0");
        }
        int i = position / 8;
        int b = 7 - position % 8;
        if (flag) {
            this.bits[i] |= (1 << b);
        } else {
            this.bits[i] &= (~(1 << b));
        }
    }

    private int unusedBitCountPrefix(byte b) {
        if (b == 0) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < 8; i++) {
            if ((b & 0x01) == 0) {
                count++;
            } else {
                break;
            }
            b >>>= 1;
        }
        return count;
    }

    @Override
    public boolean asn1Equals(Codeable obj) {
        if (!(obj instanceof ASN1BitString)) {
            return false;
        }
        ASN1BitString that = (ASN1BitString) obj;
        if (!Objects.equals(bytes, that.bytes)) return false;
        if (!Objects.equals(maxValidBit, that.maxValidBit)) return false;
        return Arrays.equals(bits, that.bits);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(bits);
        result = 31 * result + (bytes != null ? bytes.hashCode() : 0);
        result = 31 * result + (maxValidBit != null ? maxValidBit.hashCode() : 0);
        return result;
    }
}
