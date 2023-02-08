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
    private Integer bytes;
    private Integer maxVaildBit;

    public ASN1BitString(@Nullable Integer bytes, @Nullable Integer maxVaildBit) {
        this.bytes = bytes;
        this.maxVaildBit = maxVaildBit;
    }

    public ASN1BitString(byte[] bits, @Nullable Integer maxVaildBit, boolean fixed) {
        this.bytes = fixed ? bits.length : null;
        this.maxVaildBit = maxVaildBit;
        this.bits = Arrays.copyOf(bits, bits.length);
    }

    @Override
    public void encode(ASN1OutputStream os) {
        if (Objects.nonNull(bytes)) {
            //固定大小
            os.write(this.bits, 0, this.bits.length);
        } else {
            //非固定大小
            if (Objects.isNull(maxVaildBit)) {
                //1.length prefix
                os.writeLengthDetermine(this.bits.length + 1);
                //2.unused-bit count prefix
                os.write(0);
                //3.bit data
                os.write(this.bits, 0, this.bits.length);
            } else {
                int lastValidIdx = this.bits.length - 1;
                while (this.bits[lastValidIdx] == 0) {
                    lastValidIdx--;
                }
                int count = 0;
                byte b = this.bits[lastValidIdx];
                for (int i = 0; i < 8; i++) {
                    if ((b & 0x01) == 0) {
                        count++;
                    } else {
                        break;
                    }
                    b >>>= 1;
                }
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
            if (length > 1) {
                is.read();
            }
            this.bits = new byte[length > 1 ? length - 1 : length];
            if (this.bits.length != is.read(this.bits, 0, this.bits.length)) {
                throw new EOFException(String.format("expected to read %s bytes", this.bits.length));
            }
        }
    }

    @Override
    public boolean asn1Equals(Codeable obj) {
        if (!(obj instanceof ASN1BitString)) {
            return false;
        }
        ASN1BitString that = (ASN1BitString) obj;
        if (!Objects.equals(bytes, that.bytes)) return false;
        if (!Objects.equals(maxVaildBit, that.maxVaildBit)) return false;
        return Arrays.equals(bits, that.bits);
    }
}
