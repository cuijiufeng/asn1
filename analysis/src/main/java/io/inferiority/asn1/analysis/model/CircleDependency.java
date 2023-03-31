package io.inferiority.asn1.analysis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author cuijiufeng
 * @date 2023/3/19 11:40
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CircleDependency {
    private Object[] args;
    private Definition ret;
}
