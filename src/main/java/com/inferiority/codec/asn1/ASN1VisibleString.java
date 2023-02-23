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
public class ASN1VisibleString extends ASN1String {

    public ASN1VisibleString(String string) {
        super(string);
    }

    public ASN1VisibleString(@Nullable Integer minimum, @Nullable Integer maximum) {
        super(minimum, maximum);
    }

    public ASN1VisibleString(String string, @Nullable Integer minimum, @Nullable Integer maximum) {
        super(string, minimum, maximum);
    }

    @Override
    public boolean asn1Equals(Codeable obj) {
        if (!(obj instanceof ASN1VisibleString)) {
            return false;
        }
        ASN1VisibleString that = (ASN1VisibleString) obj;
        if (!Objects.equals(getMinimum(), that.getMinimum())) return false;
        if (!Objects.equals(getMaximum(), that.getMaximum())) return false;
        return Arrays.equals(getData(), that.getData());
    }

    @Override
    public String toObjectString() {
        return null;
    }
}
