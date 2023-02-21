package com.inferiority.codec.asn1;

import com.inferiority.codec.ASN1InputStream;
import com.inferiority.codec.ASN1OutputStream;
import com.inferiority.codec.Codeable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author cuijiufeng
 * @Class ASN1Sequence
 * @Date 2023/2/9 9:30
 */
public class ASN1Sequence extends ASN1Object {
    private boolean extensible;
    private List<Element> components;
    private List<Element> extensions;

    public ASN1Sequence(boolean extensible) {
        this.extensible = extensible;
        this.components = new ArrayList<>();
        this.extensions = new ArrayList<>();
    }

    public void setElement(int position, boolean extensible, boolean optional, Codeable value, Codeable defaulted) {
        if (optional && Objects.nonNull(defaulted)) {
            throw new IllegalArgumentException("non-optional cannot have default value");
        }
        if (!optional && Objects.isNull(value) && Objects.isNull(defaulted)) {
            throw new IllegalArgumentException("optional, value or default cannot be null all");
        }
        if (extensible) {
            this.extensions.add(position, new Element(optional, value, defaulted));
        } else {
            this.components.add(position, new Element(optional, value, defaulted));
        }
    }

    @Override
    public void encode(ASN1OutputStream os) {
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
                os.writeOpenType((ASN1Object) element.component);
            }
        }
    }

    /**
     * For a sequence type definition that has no extension marker and no components marked OPTIONAL or DEFAULT, the
     * preamble will be empty.
     * @param os
     */
    private void preamble(ASN1OutputStream os) {
        Element[] optionalsAndDefaults = this.components.stream()
                .filter(element -> element.optional || Objects.nonNull(element.defaulted))
                .toArray(Element[]::new);
        if (!extensible && optionalsAndDefaults.length < 1) {
            return;
        }

        int bits = extensible ? optionalsAndDefaults.length + 1 : optionalsAndDefaults.length;
        ASN1BitString preamble = new ASN1BitString(new byte[((bits + 7) & ~7) / 8], null, true);
        int bitIdx = 0;
        //a) extension bit (optional);
        if (extensible) {
            preamble.setBit(bitIdx++, this.extensions.stream().anyMatch(element -> Objects.nonNull(element.component)));
        }
        //b) root component presence bitmap (zero or more bits); and
        for (Element element : optionalsAndDefaults) {
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

    }

    @Override
    public boolean asn1Equals(Codeable obj) {
        return false;
    }

    protected static class Element {
        boolean optional;
        Codeable component;
        Codeable defaulted;

        public Element(boolean optional, Codeable component, Codeable defaulted) {
            this.optional = optional;
            this.component = component;
            this.defaulted = defaulted;
        }
    }
}
