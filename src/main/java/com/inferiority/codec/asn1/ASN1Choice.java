package com.inferiority.codec.asn1;

import com.inferiority.codec.ASN1InputStream;
import com.inferiority.codec.ASN1OutputStream;
import com.inferiority.codec.Codeable;

import java.io.IOException;
import java.util.Objects;

/**
 * @author cuijiufeng
 * @Class ASN1Choice
 * @Date 2023/2/23 11:21
 */
public class ASN1Choice extends ASN1Object {
    private final Class<? extends Enum<? extends ASN1ChoiceEnum>> choiceClass;
    private Enum<? extends ASN1ChoiceEnum> choice;
    private ASN1Object value;

    public ASN1Choice(Class<? extends Enum<? extends ASN1ChoiceEnum>> choiceClass) {
        this.choiceClass = choiceClass;
    }

    public ASN1Choice(Enum<? extends ASN1ChoiceEnum> choice, ASN1Object value) {
        this.choice = choice;
        this.value = value;
        this.choiceClass = choice.getDeclaringClass();
    }

    @Override
    public void encode(ASN1OutputStream os) {
        ASN1Tag tag = new ASN1Tag(ASN1Tag.TagClass.CONTEXT_SPECIFIC, this.choice.ordinal());
        tag.encode(os);
        if (((ASN1ChoiceEnum) this.choice).isExtension()) {
            os.writeOpenType(this.value);
        } else {
            this.value.encode(os);
        }
    }

    @Override
    public void decode(ASN1InputStream is) throws IOException {
        ASN1Tag tag = new ASN1Tag();
        tag.decode(is);
        for (Enum<? extends ASN1ChoiceEnum> constant : this.choiceClass.getEnumConstants()) {
            if (tag.getTagNumber() == constant.ordinal()) {
                this.choice = constant;
                this.value = ((ASN1ChoiceEnum) constant).getInstance();
                if (((ASN1ChoiceEnum) constant).isExtension()) {
                    is.readOpenType(this.value);
                } else {
                    this.value.decode(is);
                }
                return;
            }
        }
    }

    public <T extends ASN1ChoiceEnum> T getChoice() {
        //noinspection unchecked
        return (T) choice;
    }

    public <T extends ASN1Object> T getValue() {
        //noinspection unchecked
        return (T) value;
    }

    @Override
    public boolean asn1Equals(Codeable obj) {
        if (!(obj instanceof ASN1Choice)) {
            return false;
        }
        ASN1Choice that = (ASN1Choice) obj;
        if (!Objects.equals(choice, that.choice)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        int result = choice != null ? choice.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    public interface ASN1ChoiceEnum {
        ASN1Object getInstance();
        boolean isExtension();
    }
}
