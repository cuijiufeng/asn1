package io.inferiority.asn1.codec;

import io.inferiority.asn1.codec.oer.ASN1BitString;
import io.inferiority.asn1.codec.oer.ASN1Boolean;
import io.inferiority.asn1.codec.oer.ASN1Integer;
import io.inferiority.asn1.codec.oer.ASN1Sequence;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;


@Slf4j
public class CodecExceptionTest {

    @Test
    public void testException1() throws CodecException {
        ThrowingRunnable runnable = () -> {
            try {
                new ASN1BitString(1, new String[1]).getEncoded();
                new ASN1BitString(100, new String[1]).fromByteArray(new byte[]{-1, -1, 1});
            } catch (CodecException e) {
                log.info("", e);
                throw e;
            }
        };
        Assert.assertThrows(CodecException.class, runnable);
    }

    @Test
    public void testException2() {
        ThrowingRunnable runnable = () -> {
            try {
                ASN1Sequence sequence14 = new ASN1Sequence(true);
                sequence14.setElement(0, false, false, new ASN1Boolean(false), null);
                sequence14.setElement(1, false, false, new ASN1Integer(255, 0, 255), new ASN1Boolean(false));
                sequence14.setElement(0, true, true, null, null);
                sequence14.setElement(1, true, false, null, new ASN1Boolean(true));
                ASN1Sequence sequence = new ASN1Sequence(true);
                sequence.setElement(0, false, true, new ASN1Integer(0, 255), null);
                sequence.setElement(1, false, true, new ASN1Integer(0, 255), null);
                sequence.setElement(2, false, false, new ASN1Boolean(), null);
                sequence.setElement(0, true, false, new ASN1Boolean(), null);
                sequence.setElement(1, true, false, null, null);
                sequence.fromByteArray(sequence14.getEncoded());
            } catch (CodecException e) {
                log.info("", e);
                throw e;
            }
        };
        Assert.assertThrows(CodecException.class, runnable);
    }
}