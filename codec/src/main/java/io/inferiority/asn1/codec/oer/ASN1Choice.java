package io.inferiority.asn1.codec.oer;

import io.inferiority.asn1.codec.ASN1InputStream;
import io.inferiority.asn1.codec.ASN1OutputStream;
import io.inferiority.asn1.codec.Codeable;
import io.inferiority.asn1.codec.CodecException;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.IOException;
import java.util.Objects;

/**
 * @author cuijiufeng
 * @Class ASN1Choice
 * @Date 2023/2/23 11:21
 */
public class ASN1Choice<T extends Enum<T> & ASN1Choice.ASN1ChoiceEnum> extends ASN1Object {
    private final Class<T> choiceClass;
    private T choice;
    private ASN1Object value;

    @SuppressWarnings("unchecked")
    public ASN1Choice() {
        try {
            this.choiceClass = (Class<T>) ((ParameterizedTypeImpl) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            Objects.requireNonNull(choiceClass, "enum class cannot be null");
        } catch (Exception e) {
            throw new UnsupportedOperationException("please call ASN1Enumerated(Class<T> enumClass) method");
        }
    }

    public ASN1Choice(Class<T> choiceClass) {
        Objects.requireNonNull(choiceClass, "choice class cannot be null");
        this.choiceClass = choiceClass;
    }

    public ASN1Choice(T choice, ASN1Object value) {
        Objects.requireNonNull(choice, "choice cannot be null");
        Objects.requireNonNull(value, "value cannot be null");
        this.choice = choice;
        this.value = value;
        this.choiceClass = choice.getDeclaringClass();
    }

    @Override
    public void encode(ASN1OutputStream os) throws CodecException {
        ASN1Tag tag = new ASN1Tag(ASN1Tag.TagClass.CONTEXT_SPECIFIC, this.choice.ordinal());
        tag.encode(os);
        if (this.choice.isExtension()) {
            os.writeOpenType(this.value);
        } else {
            this.value.encode(os);
        }
    }

    @Override
    public void decode(ASN1InputStream is) throws IOException {
        ASN1Tag tag = new ASN1Tag();
        tag.decode(is);
        for (T constant : this.choiceClass.getEnumConstants()) {
            if (tag.getTagNumber() == constant.ordinal()) {
                this.choice = constant;
                this.value = constant.getInstance();
                if (constant.isExtension()) {
                    is.readOpenType(this.value);
                } else {
                    this.value.decode(is);
                }
                return;
            }
        }
    }

    public T getChoice() {
        return choice;
    }

    public <E extends ASN1Object> E getValue() {
        //noinspection unchecked
        return (E) value;
    }

    @Override
    public boolean asn1Equals(Codeable obj) {
        if (!(obj instanceof ASN1Choice)) {
            return false;
        }
        ASN1Choice<?> that = (ASN1Choice<?>) obj;
        if (!Objects.equals(choice, that.choice)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public String toObjectString() {
        return this.choice.name() + ":" + this.value.toObjectString();
    }

    @Override
    public String toJsonString() {
        return "{" +
                "\n\t\"" + this.choice.name() + "\":" + this.value.toJsonString() +
                "\n}";
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
