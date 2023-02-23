package com.inferiority.codec.asn1;

import com.inferiority.codec.ASN1InputStream;
import com.inferiority.codec.ASN1OutputStream;
import com.inferiority.codec.utils.Nullable;

import java.io.EOFException;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author cuijiufeng
 * @Class ASN1String
 * @Date 2023/2/7 14:15
 */
public abstract class ASN1String extends ASN1Object {
    private byte[] data;
    private Integer minimum;
    private Integer maximum;

    public ASN1String(String string) {
        Objects.requireNonNull(string, "string cannot be null");
        this.data = toByteArray(string);
    }

    public ASN1String(@Nullable Integer minimum, @Nullable Integer maximum) {
        if (Objects.nonNull(minimum) && Objects.nonNull(maximum) && minimum > maximum) {
            throw new IllegalArgumentException(String.format("the minimum value of %s is greater than the maximum value of %s", minimum, maximum));
        }
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public ASN1String(String string, @Nullable Integer minimum, @Nullable Integer maximum) {
        if (string == null) {
            throw new NullPointerException("string cannot be null");
        }
        if (Objects.nonNull(minimum) && string.length() < minimum) {
            throw new IllegalArgumentException(String.format("%s length is less than %d", string, minimum));
        }
        if (Objects.nonNull(maximum) && string.length() > maximum) {
            throw new IllegalArgumentException(String.format("%s length is greater than %d", string, maximum));
        }
        if (Objects.nonNull(minimum) && Objects.nonNull(maximum) && minimum > maximum) {
            throw new IllegalArgumentException(String.format("the minimum value of %s is greater than the maximum value of %s", minimum, maximum));
        }
        this.data = toByteArray(string);
        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    public void encode(ASN1OutputStream os) {
        if (this.minimum == null || this.maximum == null || !Objects.equals(this.minimum, this.maximum)) {
            os.writeLengthDetermine(this.data.length);
        }
        os.write(this.data, 0, this.data.length);
    }

    @Override
    public void decode(ASN1InputStream is) throws EOFException {
        int length;
        if (this.minimum == null || this.maximum == null || !Objects.equals(this.minimum, this.maximum)) {
            length = is.readLengthDetermine();
        } else {
            length = this.minimum;
        }
        this.data = new byte[length];
        if (length != is.read(this.data, 0, length)) {
            throw new EOFException(String.format("expected to read %s bytes", length));
        }
    }

    public byte[] getData() {
        return data;
    }

    public Integer getMinimum() {
        return minimum;
    }

    public Integer getMaximum() {
        return maximum;
    }

    public String getString() {
        return new String(this.data);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(data);
        result = 31 * result + (minimum != null ? minimum.hashCode() : 0);
        result = 31 * result + (maximum != null ? maximum.hashCode() : 0);
        return result;
    }

    private byte[] toByteArray(String string) {
        byte[] bytes = new byte[string.length()];
        for (int i = 0; i != bytes.length; i++) {
            char ch = string.charAt(i);
            bytes[i] = (byte)ch;
        }
        return bytes;
    }
}
