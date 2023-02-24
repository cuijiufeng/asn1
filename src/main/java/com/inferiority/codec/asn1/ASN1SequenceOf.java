package com.inferiority.codec.asn1;

import com.inferiority.codec.ASN1InputStream;
import com.inferiority.codec.ASN1OutputStream;
import com.inferiority.codec.Codeable;
import com.inferiority.codec.CodecException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author cuijiufeng
 * @Class ASN1SequenceOf
 * @Date 2023/2/23 9:00
 */
public class ASN1SequenceOf<T extends ASN1Object> extends ASN1Object implements Iterable<T> {
    private T[] sequences;
    private Supplier<T> instance;

    public ASN1SequenceOf(Supplier<T> instance) {
        Objects.requireNonNull(instance, "instance cannot be null");
        this.instance = instance;
    }

    public ASN1SequenceOf(T[] sequences) {
        Objects.requireNonNull(sequences, "sequences cannot be null");
        this.sequences = sequences;
    }

    @Override
    public void encode(ASN1OutputStream os) throws CodecException {
        ASN1Integer quantity = new ASN1Integer(BigInteger.valueOf(sequences.length), BigInteger.ZERO, null);
        quantity.encode(os);
        for (T sequence : this.sequences) {
            sequence.encode(os);
        }
    }

    @Override
    public void decode(ASN1InputStream is) throws IOException {
        ASN1Integer quantity = new ASN1Integer(BigInteger.ZERO, null);
        quantity.decode(is);
        //noinspection unchecked
        this.sequences = (T[]) Array.newInstance(ASN1Boolean.class, quantity.intValue());
        for (int i = 0; i < quantity.intValue(); i++) {
            this.sequences[i] = instance.get();
            this.sequences[i].decode(is);
        }
    }

    public T[] getSequences() {
        return this.sequences;
    }

    public int size() {
        return this.sequences.length;
    }

    @Override
    public boolean asn1Equals(Codeable obj) {
        if (!(obj instanceof ASN1SequenceOf)) {
            return false;
        }
        ASN1SequenceOf<?> that = (ASN1SequenceOf<?>) obj;
        return Arrays.equals(sequences, that.sequences);
    }

    @Override
    public String toObjectString() {
        return "{" +
                Arrays.stream(this.sequences)
                        .map(ASN1Object::toObjectString)
                        .map(str -> "\t" + str)
                        .collect(Collectors.joining(",\n", "\n", "\n"))
                + "}";
    }

    @Override
    public String toJsonString() {
        return "[" +
                Arrays.stream(this.sequences)
                        .map(ASN1Object::toJsonString)
                        .map(str -> "\t" + str)
                        .collect(Collectors.joining(",\n", "\n", "\n"))
                + "]";
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(sequences);
    }

    @Override
    public Iterator<T> iterator() {
        return Arrays.stream(this.sequences).iterator();
    }
}
