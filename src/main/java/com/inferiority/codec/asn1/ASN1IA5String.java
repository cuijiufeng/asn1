package com.inferiority.codec.asn1;

import com.inferiority.codec.Codeable;
import com.inferiority.codec.utils.Nullable;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author cuijiufeng
 * @Class ASN1IA5String
 * @Date 2023/2/7 13:15
 */
public class ASN1IA5String extends ASN1String {

    public ASN1IA5String(String string) {
        super(string);
        if (notIA5String(string)) {
            throw new IllegalArgumentException(String.format("%s contains illegal characters", string));
        }
    }

    public ASN1IA5String(@Nullable Integer minimum, @Nullable Integer maximum) {
        super(minimum, maximum);
    }

    public ASN1IA5String(String string, @Nullable Integer minimum, @Nullable Integer maximum) {
        super(string, minimum, maximum);
        if (notIA5String(string)) {
            throw new IllegalArgumentException(String.format("%s contains illegal characters", string));
        }
    }

    @Override
    public boolean asn1Equals(Codeable obj) {
        if (!(obj instanceof ASN1IA5String)) {
            return false;
        }
        ASN1IA5String that = (ASN1IA5String) obj;
        if (!Objects.equals(getMinimum(), that.getMinimum())) return false;
        if (!Objects.equals(getMaximum(), that.getMaximum())) return false;
        return Arrays.equals(getData(), that.getData());
    }

    public static boolean notIA5String(String str) {
        for (char ch : str.toCharArray()) {
            if (ch > 0x7f) {
                return true;
            }
        }
        return false;
    }
}
