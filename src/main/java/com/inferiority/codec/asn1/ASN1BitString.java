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
            BigInteger bitData = this.bits.shiftLeft(8 - this.bitSize % 8);
            //固定大小
            byte[] valueBytes = bitData.toByteArray();
            int signedOctets = valueBytes[0] == 0 && valueBytes.length > 1 ? 1 : 0;
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
