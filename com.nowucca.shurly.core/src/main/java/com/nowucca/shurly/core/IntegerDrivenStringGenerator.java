/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.core;

public interface IntegerDrivenStringGenerator extends StringGenerator {

    String encode(int num);

    int decode(String str);
}
