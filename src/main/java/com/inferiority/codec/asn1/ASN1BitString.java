package com.inferiority.codec.asn1;

import com.inferiority.codec.ASN1InputStream;
import com.inferiority.codec.ASN1OutputStream;
import com.inferiority.codec.Codeable;
import com.inferiority.codec.utils.Nullable;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Objects;

/**
 * @author cuijiufeng
 * @Class ASN1BitString
 * @Date 2023/2/7 16:04
 */
public class ASN1BitString extends ASN1Object {
    private BigInteger bits;
    private Integer bitSize;

    public ASN1BitString(String bitString, @Nullable Integer bitSize) {
        if (!bitString.matches("[01]+")) {
            throw new IllegalArgumentException(String.format("%s is not a binary string", bitString));
        }
        if (Objects.nonNull(bitSize) && bitString.length() > bitSize) {
            throw new IllegalArgumentException(String.format("The length of %s in fixed mode cannot be greater than %d", bitString, bitSize));
        }
        this.bits = new BigInteger(bitString, 2);
        this.bitSize = bitSize;
    }

    @Override
    public void encode(ASN1OutputStream os) {
        if (Objects.nonNull(bitSize)) {
            //固定大小
            //将bitSize调整到8的倍数
            int bitSize = (this.bitSize + 7) & ~7;
            if (this.bits.equals(BigInteger.ZERO)) {
                //处理BigInteger等于0的情况
                for (int i = 0; i < bitSize / 8; i++) {
                    os.write(0);
                }
            } else {
                BigInteger bitData = this.bits.shiftLeft(bitSize - this.bits.bitLength());
                byte[] valueBytes = bitData.toByteArray();
                int signedOctets = valueBytes[0] == 0 && valueBytes.length > 1 ? 1 : 0;
                os.write(valueBytes, signedOctets, valueBytes.length - signedOctets);
            }
        } else {
            //00000000 00110000=03 04 00 30
            //00000000 00000011=03 00 00 03

            //00000000 00000000=01 00

            //00000011 0000    =02 00 03    00000011
            //00001100 00      =02 02 0C    00001100
            //00000011 00      =02 00 03
            //00000000 11      =03 06 00 C0

            //非固定大小
            byte[] valueBytes = this.bits.toByteArray();
            int signedOctets = valueBytes[0] == 0 && valueBytes.length > 1 ? 1 : 0;
            //1.length prefix
            os.writeLengthDetermine(this.bits.equals(BigInteger.ZERO) ? valueBytes.length - signedOctets : valueBytes.length - signedOctets + 1);
            //2.unused-bit count prefix
            if (!this.bits.equals(BigInteger.ZERO)) {
                // TODO: 2023/2/7
                os.write(0);
            }
            //3.bit data
            //填充高字节0

            os.write(valueBytes, signedOctets, valueBytes.length - signedOctets);
        }
    }

    @Override
    public void decode(ASN1InputStream is) throws IOException {

    }

    @Override
    public boolean asn1Equals(Codeable obj) {
        return false;
    }
}
