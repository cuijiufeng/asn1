package com.inferiority.codec.asn1;

import com.inferiority.codec.Codeable;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author cuijiufeng
 * @Class ASN1IA5String
 * @Date 2023/2/7 13:15
 */
public class ASN1PrintableString extends ASN1String {

    public ASN1PrintableString(String string) {
        super(string);
        if (notPrintableString(string)) {
            throw new IllegalArgumentException("string contains illegal characters");
        }
    }

    public ASN1PrintableString(Integer minimum, Integer maximum) {
        super(minimum, maximum);
    }

    public ASN1PrintableString(String string, Integer minimum, Integer maximum) {
        super(string, minimum, maximum);
        if (notPrintableString(string)) {
            throw new IllegalArgumentException("string contains illegal characters");
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
