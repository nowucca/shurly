/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurely.core;

public interface IntegerDrivenStringGenerator extends UniqueStringGenerator {

    String encode(int num);

    int decode(String str);
}
