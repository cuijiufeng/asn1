package com.inferiority.asn1.codec.oer;

import com.inferiority.asn1.codec.Codeable;
import com.inferiority.asn1.codec.utils.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author cuijiufeng
 * @Class ASN1UTF8String
 * @Date 2023/3/17 13:21
 */
public class ASN1UTF8String extends ASN1String {
    public ASN1UTF8String(String string) {
        super(string.getBytes(StandardCharsets.UTF_8), null, null);
    }

    public ASN1UTF8String(@Nullable Integer minimum, @Nullable Integer maximum) {
        super(minimum, maximum);
    }

    public ASN1UTF8String(String string, @Nullable Integer minimum, @Nullable Integer maximum) {
        super(string.getBytes(StandardCharsets.UTF_8), minimum, maximum);
    }

    @Override
    public String getString() {
        return new String(this.getData(), StandardCharsets.UTF_8);
    }

    @Override
    public boolean asn1Equals(Codeable obj) {
        if (!(obj instanceof ASN1UTF8String)) {
            return false;
        }
        ASN1UTF8String that = (ASN1UTF8String) obj;
        if (!Objects.equals(getMinimum(), that.getMinimum())) return false;
        if (!Objects.equals(getMaximum(), that.getMaximum())) return false;
        return Arrays.equals(getData(), that.getData());
    }
}
