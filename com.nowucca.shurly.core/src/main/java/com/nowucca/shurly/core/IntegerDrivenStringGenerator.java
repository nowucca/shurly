/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.core;

/**
 * A string generator where the generated strings have a correspondence with the set of integers, such that:
 * for every call to {@link #getString()} yielding a string <em>s</em>,
 * there exists an integer <em>i</em> such that <em>s = encode(i)</em> and <em>decode(s) = i</em>.
 *
 * Typically an {@link IntegerDrivenStringGenerator} is used to generate URI shortening schemes
 * with repeatable non-random shortenings.
 */
public interface IntegerDrivenStringGenerator extends StringGenerator {

    String encode(int num);

    int decode(String str);
}
