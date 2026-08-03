package com.github.yingzhuo.bayonet.beandef;

/**
 * 布尔组合逻辑，用于声明多个条件之间的组合方式。
 *
 * @author 应卓
 * @since 4.1.1
 */
public enum Logic {

    /** 任一条件满足即通过。 */
    OR,

    /** 全部条件满足才通过。 */
    AND

}
