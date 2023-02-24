package com.inferiority.asn1.analysis.model;

import java.util.List;

/**
 * @author cuijiufeng
 * @Class Namespace
 * @Date 2023/2/24 16:00
 */
public class Namespace {
    private String name;
    private List<Void> exported;
    private Void dependencies;
    private List<Definition> definitions;
}
