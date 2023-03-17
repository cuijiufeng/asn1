package com.inferiority.asn1.codec.oer;

import com.inferiority.asn1.codec.Codeable;
import com.inferiority.asn1.codec.utils.Nullable;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author cuijiufeng
 * @Class ASN1IA5String
 * @Date 2023/2/7 13:15
 */
public class ASN1PrintableString extends ASN1String {

    public ASN1PrintableString(String string) {
        super(ASN1String.toByteArray(string), null, null);
        if (notPrintableString(string)) {
            throw new IllegalArgumentException("string contains illegal characters");
        }
    }

    public ASN1PrintableString(@Nullable Integer minimum, @Nullable Integer maximum) {
        super(minimum, maximum);
    }

    public ASN1PrintableString(String string, @Nullable Integer minimum, @Nullable Integer maximum) {
        super(ASN1String.toByteArray(string), minimum, maximum);
        if (notPrintableString(string)) {
            throw new IllegalArgumentException("string contains illegal characters");
        }
        if (Objects.nonNull(minimum) && string.length() < minimum) {
            throw new IllegalArgumentException(String.format("%s length is less than %d", string, minimum));
        }
        if (Objects.nonNull(maximum) && string.length() > maximum) {
            throw new IllegalArgumentException(String.format("%s length is greater than %d", string, maximum));
        }
    }

    @Override
    public boolean asn1Equals(Codeable obj) {
        if (!(obj instanceof ASN1PrintableString)) {
            return false;
        }
        ASN1PrintableString that = (ASN1PrintableString) obj;
        if (!Objects.equals(getMinimum(), that.getMinimum())) return false;
        if (!Objects.equals(getMaximum(), that.getMaximum())) return false;
        return Arrays.equals(getData(), that.getData());
    }

    public static boolean notPrintableString(String str) {
        for (char ch : str.toCharArray()) {
            if (ch > 0x7f) {
                return true;
            }
            if ('a' <= ch && ch <= 'z') {
                continue;
            }
            if ('A' <= ch && ch <= 'Z') {
                continue;
            }
            if ('0' <= ch && ch <= '9') {
                continue;
            }
            switch (ch) {
                case ' ':
                case '\'':
                case '(':
                case ')':
                case '+':
                case '-':
                case '.':
                case ':':
                case '=':
                case '?':
                case '/':
                case ',':
                    continue;
            }
            return true;
        }
        return false;
    }
}
