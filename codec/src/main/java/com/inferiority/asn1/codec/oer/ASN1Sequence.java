package com.inferiority.asn1.codec.oer;

import com.inferiority.asn1.codec.ASN1InputStream;
import com.inferiority.asn1.codec.ASN1OutputStream;
import com.inferiority.asn1.codec.Codeable;
import com.inferiority.asn1.codec.CodecException;
import com.inferiority.asn1.codec.utils.Nullable;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author cuijiufeng
 * @Class ASN1Sequence
 * @Date 2023/2/9 9:30
 */
public class ASN1Sequence extends ASN1Object {
    private final boolean extensible;
    private final List<Element> components;
    private final List<Element> extensions;

    public ASN1Sequence(boolean extensible) {
        this.extensible = extensible;
        this.components = new ArrayList<>();
        this.extensions = new ArrayList<>();
    }

    public void setElement(int position, boolean extensible, boolean optional, @Nullable ASN1Object value, @Nullable ASN1Object defaulted) {
        this.setElement("Label", position, extensible, optional, value, defaulted);
    }

    public void setElement(String label, int position, boolean extensible, boolean optional, @Nullable ASN1Object value, @Nullable ASN1Object defaulted) {
        if (!this.extensible && extensible) {
            throw new IllegalArgumentException("please new ASN1Sequence(true)");
        }
        if (optional && Objects.nonNull(defaulted)) {
            throw new IllegalArgumentException("non-optional cannot have default value");
        }
        if (!extensible && !optional && Objects.isNull(value) && Objects.isNull(defaulted)) {
            throw new IllegalArgumentException("optional, value or default cannot be null all");
        }
        if (extensible) {
            this.extensions.add(position, new Element(label, optional, value, defaulted));
        } else {
            this.components.add(position, new Element(label, optional, value, defaulted));
        }
    }

    public <T extends ASN1Object> T getElement(int position, boolean extensible) {
        return extensible
                ? this.extensions.get(position).getComponent()
                : this.components.get(position).getComponent();
    }

    @Override
    public void encode(ASN1OutputStream os) throws CodecException {
        //a) preamble;
        preamble(os);
        //b) encodings of the components in the extension root;
        for (Element element : this.components) {
            if (Objects.nonNull(element.component)) {
                element.component.encode(os);
            }
        }
        //c) extension addition presence bitmap (optional); and
        extensionBitmap(os);
        //d) encodings of the extension additions (optional)
        for (Element element : this.extensions) {
            if (Objects.nonNull(element.component)) {
                os.writeOpenType(element.component);
            }
        }
    }

    /**
     * For a sequence type definition that has no extension marker and no components marked OPTIONAL or DEFAULT, the
     * preamble will be empty.
     * @param os
     */
    private void preamble(ASN1OutputStream os) {
        Element[] markeds = this.components.stream()
                .filter(element -> element.optional || Objects.nonNull(element.defaulted))
                .toArray(Element[]::new);
        if (!extensible && markeds.length < 1) {
            return;
        }

        int bits = extensible ? markeds.length + 1 : markeds.length;
        ASN1BitString preamble = new ASN1BitString(new byte[((bits + 7) & ~7) / 8], null, true);
        int bitIdx = 0;
        //a) extension bit (optional);
        if (extensible) {
            preamble.setBit(bitIdx++, this.extensions.stream().anyMatch(element -> Objects.nonNull(element.component)));
        }
        //b) root component presence bitmap (zero or more bits); and
        for (Element element : markeds) {
            preamble.setBit(bitIdx++, Objects.nonNull(element.component));
        }
        //c) unused bits (zero or more bits).
        //d) final
        preamble.encode(os);
    }

    private void extensionBitmap(ASN1OutputStream os) {
        //contains an extension marker and the extension bit in the preamble is set to 1
        if (extensible && this.extensions.stream().anyMatch(element -> Objects.nonNull(element.component))) {
            int bits = this.extensions.size();
            int bytes = ((bits + 7) & ~7) / 8;
            //a length determinant(comprise both the initial octet and the subsequent octets)
            os.writeLengthDetermine(1 + bytes);
            //initial octet
            os.write(8 - (bits & 7));
            //subsequent octets
            ASN1BitString bitmap = new ASN1BitString(new byte[bytes], null, true);
            for (int i = 0; i < this.extensions.size(); i++) {
                bitmap.setBit(i, Objects.nonNull(this.extensions.get(i).component));
            }
            bitmap.encode(os);
        }
    }

    @Override
    public void decode(ASN1InputStream is) throws IOException {
        //a) preamble;
        ASN1BitString preamble = preamble(is);
        //b) encodings of the components in the extension root;
        int bitIdx = extensible ? 1 : 0;
        for (Element element : this.components) {
            if ((!element.optional && Objects.isNull(element.defaulted)) || preamble.getBit(bitIdx++)) {
                element.component.decode(is);
            }
        }
        //c) extension addition presence bitmap (optional); and
        ASN1BitString bitmap = extensionBitmap(is);
        //d) encodings of the extension additions (optional)
        for (int i = 0; Objects.nonNull(bitmap) && i < this.extensions.size(); i++) {
            Element element = this.extensions.get(i);
            if (bitmap.getBit(i)) {
                is.readOpenType(element.component);
            }
        }
    }

    private ASN1BitString preamble(ASN1InputStream is) throws IOException {
        Element[] markeds = this.components.stream()
                .filter(element -> element.optional || Objects.nonNull(element.defaulted))
                .toArray(Element[]::new);
        if (!extensible && markeds.length < 1) {
            return null;
        }

        int bits = extensible ? markeds.length + 1 : markeds.length;
        ASN1BitString preamble = new ASN1BitString(new byte[((bits + 7) & ~7) / 8], null, true);
        preamble.decode(is);
        return preamble;
    }

    private ASN1BitString extensionBitmap(ASN1InputStream is) throws EOFException {
        if (extensible && this.extensions.stream().anyMatch(element -> Objects.nonNull(element.component))) {
            int determine = is.readLengthDetermine();
            byte initialOctet = is.readByte();
            byte[] bytes = new byte[determine - 1];
            if (bytes.length != is.read(bytes, 0, bytes.length)) {
                throw new EOFException(String.format("expected to read %s bytes", bytes.length));
            }
            return new ASN1BitString(bytes, null, true);
        }
        return null;
    }

    @Override
    public boolean asn1Equals(Codeable obj) {
        if (!(obj instanceof ASN1Sequence)) {
            return false;
        }
        ASN1Sequence that = (ASN1Sequence) obj;
        if (extensible != that.extensible) return false;
        if (!Objects.equals(components, that.components)) return false;
        return Objects.equals(extensions, that.extensions);
    }

    @Override
    public String toObjectString() {
        return "{" +
                Stream.concat(this.components.stream(), this.extensions.stream())
                        .map(Element::toObjectString)
                        .collect(Collectors.joining(",\n", "\n", "\n"))
                + "}";
    }

    @Override
    public String toJsonString() {
        return "{" +
                Stream.concat(this.components.stream(), this.extensions.stream())
                        .map(Element::toJsonString)
                        .collect(Collectors.joining(",\n", "\n", "\n"))
                + "}";
    }

    @Override
    public int hashCode() {
        int result = (extensible ? 1 : 0);
        result = 31 * result + components.hashCode();
        result = 31 * result + extensions.hashCode();
        return result;
    }

    protected static class Element {
        private final String label;
        boolean optional;
        ASN1Object component;
        ASN1Object defaulted;

        public Element(String label, boolean optional, ASN1Object component, ASN1Object defaulted) {
            this.label = label;
            this.optional = optional;
            this.component = component;
            this.defaulted = defaulted;
        }

        public <T extends ASN1Object> T getComponent() {
            //noinspection unchecked
            return (T) (Objects.nonNull(component) ? component : defaulted);
        }

        public String toObjectString() {
            return "\t" + label + ":" + getComponent();
        }

        public String toJsonString() {
            return "\t\"" + label + "\":" + getComponent();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Element element = (Element) o;
            if (optional != element.optional) return false;
            if (!Objects.equals(component, element.component)) return false;
            return Objects.equals(defaulted, element.defaulted);
        }

        @Override
        public int hashCode() {
            int result = (optional ? 1 : 0);
            result = 31 * result + (component != null ? component.hashCode() : 0);
            result = 31 * result + (defaulted != null ? defaulted.hashCode() : 0);
            return result;
        }
    }
}
