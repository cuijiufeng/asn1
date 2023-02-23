package com.inferiority.codec.asn1;

import com.inferiority.codec.ASN1InputStream;
import com.inferiority.codec.ASN1OutputStream;
import com.inferiority.codec.Codeable;

import java.io.IOException;
import java.util.Objects;

/**
 * @author cuijiufeng
 * @Class ASN1Tag
 * @Date 2023/2/23 11:44
 */
public class ASN1Tag extends ASN1Object {
    private TagClass tagClass;
    private long tagNumber;

    public ASN1Tag() {
    }

    public ASN1Tag(TagClass tagClass, long tagNumber) {
        Objects.requireNonNull(tagClass, "tag class cannot be null");
        this.tagClass = tagClass;
        this.tagNumber = tagNumber;
    }

    @Override
    public void encode(ASN1OutputStream os) {
        if (tagNumber < 0x3f) {
            //If the tag number is less than 63, it shall be encoded into bits 6 to 1 of the first (and only) octet.
            os.write((int) (tagClass.type | tagNumber));
        } else {
            //If the tag number is greater or equal to 63, it shall be encoded into an initial octet followed by one or more subsequent octets
            //a) Bits 6 to 1 of the initial octet shall be set to '111111'B.
            os.write(tagClass.type | 0x3f);
            //b) The tag number shall be encoded into bits 7 to 1 of each subsequent octet
            byte[] bytes = new byte[10];
            int idx = bytes.length - 1;

            for (long t = tagNumber; t != 0; t >>>= 7) {
                if (idx == bytes.length - 1) {
                    //e) Bit 8 of the final subsequent octet shall be set to 0.
                    bytes[idx--] = (byte) (t & 0x7f);
                } else {
                    //d) Bit 8 of each subsequent octet except the last shall be set to 1.
                    bytes[idx--] = (byte) (t & 0x7f | 0x80);
                }
            }
            os.write(bytes, idx + 1, bytes.length - (idx + 1));
        }
    }

    @Override
    public void decode(ASN1InputStream is) throws IOException {
        byte type = is.readByte();
        this.tagClass = TagClass.valueOf(type);
        if ((type & 0x3f) < 0x3f) {
            this.tagNumber = type & 0x3f;
        } else {
            byte b;
            do {
                b = is.readByte();
                this.tagNumber <<= 7;
                this.tagNumber |= (b & 0x7f);
            } while ((b & 0x80) != 0);
        }
    }

    public TagClass getTagClass() {
        return tagClass;
    }

    public long getTagNumber() {
        return tagNumber;
    }

    @Override
    public boolean asn1Equals(Codeable obj) {
        if (!(obj instanceof ASN1Tag)) {
            return false;
        }
        ASN1Tag that = (ASN1Tag) obj;
        if (tagNumber != that.tagNumber) return false;
        return tagClass == that.tagClass;
    }

    @Override
    public String toObjectString() {
        return null;
    }

    @Override
    public int hashCode() {
        int result = tagClass != null ? tagClass.hashCode() : 0;
        result = 31 * result + (int) (tagNumber ^ (tagNumber >>> 32));
        return result;
    }

    public enum TagClass {
        UNIVERSAL((byte) 0x00), APPLICATION((byte) 0x40), CONTEXT_SPECIFIC((byte) 0x80), PRIVATE((byte) 0xc0);

        protected byte type;

        TagClass(byte type) {
            this.type = type;
        }

        public static TagClass valueOf(byte type) {
            switch (type & 0xc0) {
                case 0x00: return UNIVERSAL;
                case 0x40: return APPLICATION;
                case 0x80: return CONTEXT_SPECIFIC;
                case 0xc0:
                default: return PRIVATE;
            }
        }
    }
}
