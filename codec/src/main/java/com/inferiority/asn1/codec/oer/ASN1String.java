package com.inferiority.asn1.codec.oer;

import com.inferiority.asn1.codec.ASN1InputStream;
import com.inferiority.asn1.codec.ASN1OutputStream;
import com.inferiority.asn1.codec.utils.Nullable;

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

    public ASN1String(@Nullable Integer minimum, @Nullable Integer maximum) {
        if (Objects.nonNull(minimum) && Objects.nonNull(maximum) && minimum > maximum) {
            throw new IllegalArgumentException(String.format("the minimum value of %s is greater than the maximum value of %s", minimum, maximum));
        }
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public ASN1String(byte[] string, @Nullable Integer minimum, @Nullable Integer maximum) {
        Objects.requireNonNull(string, "string cannot be null");
        if (Objects.nonNull(minimum) && Objects.nonNull(maximum) && minimum > maximum) {
            throw new IllegalArgumentException(String.format("the minimum value of %s is greater than the maximum value of %s", minimum, maximum));
        }
        this.data = string;
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
    public String toObjectString() {
        return Objects.nonNull(this.data) ? new String(this.data) : "";
    }

    @Override
    public String toJsonString() {
        return "\"" + (Objects.nonNull(this.data) ? new String(this.data) : "") + "\"";
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(data);
        result = 31 * result + (minimum != null ? minimum.hashCode() : 0);
        result = 31 * result + (maximum != null ? maximum.hashCode() : 0);
        return result;
    }

    protected static byte[] toByteArray(String string) {
        byte[] bytes = new byte[string.length()];
        for (int i = 0; i != bytes.length; i++) {
            char ch = string.charAt(i);
            bytes[i] = (byte)ch;
        }
        return bytes;
    }
}
