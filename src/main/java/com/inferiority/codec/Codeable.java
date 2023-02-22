package com.inferiority.codec;

import java.io.IOException;

/**
 * @Class: Codeable
 * @Date: 2023/2/3 13:37
 * @author: cuijiufeng
 */
public interface Codeable {

    void encode(ASN1OutputStream os);

    void decode(ASN1InputStream is) throws IOException;

    boolean asn1Equals(Codeable obj);

    //TODO 2023/2/3 14:36 通过配置支持打印多种格式，xml,json,simple对象
    String toString();

    //TODO 2023/2/22 9:09 编解码异常
}