package com.inferiority.codec.asn1;

import com.inferiority.codec.ASN1InputStream;
import com.inferiority.codec.ASN1OutputStream;
import com.inferiority.codec.Codeable;

import java.io.IOException;

/**
 * @author cuijiufeng
 * @Class ASN1Sequence
 * @Date 2023/2/9 9:30
 */
public class ASN1Sequence extends ASN1Object {
    private boolean extensible;
    private Codeable[] elements;

    @Override
    public void encode(ASN1OutputStream os) {
        //a) preamble;
        preamble(os);
        //b) encodings of the components in the extension root;
        //c) extension addition presence bitmap (optional); and
        //d) encodings of the extension additions (optional)
    }

    @Override
    public void decode(ASN1InputStream is) throws IOException {

    }

    /**
     * For a sequence type definition that has no extension marker and no components marked OPTIONAL or DEFAULT, the
     * preamble will be empty.
     * @param os
    */
    private void preamble(ASN1OutputStream os) {
        if (!extensible && hasOptionals() && hasDefaults()) {
            return;
        }
        byte preamble = 0;
        //a) extension bit (optional);
        if (!extensible && hashExtensible()) {
            preamble |= 0x80;
        }
        //b) root component presence bitmap (zero or more bits); and
        //c) unused bits (zero or more bits).

        ASN1BitString
    }

    @Override
    public boolean asn1Equals(Codeable obj) {
        return false;
    }
}
