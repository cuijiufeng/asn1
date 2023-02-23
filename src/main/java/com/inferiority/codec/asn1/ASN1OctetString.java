package com.inferiority.codec.asn1;

import com.inferiority.codec.ASN1InputStream;
import com.inferiority.codec.ASN1OutputStream;
import com.inferiority.codec.Codeable;
import com.inferiority.codec.utils.Nullable;

import java.io.EOFException;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author cuijiufeng
 * @Class ASN1String
 * @Date 2023/2/7 14:15
 */
public class ASN1OctetString extends ASN1Object {
    private byte[] data;
    private Integer minimum;
    private Integer maximum;

    public ASN1OctetString(byte[] data) {
        Objects.requireNonNull(data, "string cannot be null");
        this.data = Arrays.copyOf(data, data.length);
    }

    public ASN1OctetString(@Nullable Integer minimum, @Nullable Integer maximum) {
        if (Objects.nonNull(minimum) && Objects.nonNull(maximum) && minimum > maximum) {
            throw new IllegalArgumentException(String.format("the minimum value of %s is greater than the maximum value of %s", minimum, maximum));
        }
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public ASN1OctetString(byte[] data, @Nullable Integer minimum, @Nullable Integer maximum) {
        Objects.requireNonNull(data, "string cannot be null");
        if (Objects.nonNull(minimum) && data.length < minimum) {
            throw new IllegalArgumentException(String.format("%d length is less than %d", data.length, minimum));
        }
        if (Objects.nonNull(maximum) && data.length > maximum) {
            throw new IllegalArgumentException(String.format("%d length is greater than %d", data.length, maximum));
        }
        if (Objects.nonNull(minimum) && Objects.nonNull(maximum) && minimum > maximum) {
            throw new IllegalArgumentException(String.format("the minimum value of %s is greater than the maximum value of %s", minimum, maximum));
        }
        this.data = Arrays.copyOf(data, data.length);;
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

    @Override
    public boolean asn1Equals(Codeable obj) {
        if (!(obj instanceof ASN1OctetString)) {
            return false;
        }
        ASN1OctetString that = (ASN1OctetString) obj;
        if (!Objects.equals(minimum, that.minimum)) return false;
        if (!Objects.equals(maximum, that.maximum)) return false;
        return Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(data);
        result = 31 * result + (minimum != null ? minimum.hashCode() : 0);
        result = 31 * result + (maximum != null ? maximum.hashCode() : 0);
        return result;
    }
}
