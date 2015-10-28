/*
 * Copyright (C) 2015 TIBCO Jaspersoft Corporation. All rights reserved.
 * http://community.jaspersoft.com/project/mobile-sdk-android
 *
 * Unless you have purchased a commercial license agreement from TIBCO Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of TIBCO Jaspersoft Mobile SDK for Android.
 *
 * TIBCO Jaspersoft Mobile SDK is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TIBCO Jaspersoft Mobile SDK is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TIBCO Jaspersoft Mobile SDK for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.sdk.service.data;

import com.jaspersoft.android.sdk.service.data.report.PageRange;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


/**
 * @author Tom Koptel
 * @since 2.0
 */
public class PageRangeTest {
    @Rule
    public ExpectedException mExpectedException = ExpectedException.none();

    @Test
    public void shouldParseRange() {
        PageRange pageRange = new PageRange("1-10");
        assertThat(pageRange.getLowerBound(), is(1));
        assertThat(pageRange.getUpperBound(), is(10));
    }

    @Test
    public void upperBoundUndefinedIfMissing() {
        PageRange pageRange = new PageRange("1");
        assertThat(pageRange.getLowerBound(), is(1));
        assertThat(pageRange.getUpperBound(), is(Integer.MAX_VALUE));
    }

    @Test
    public void shouldNotBeRangeIfUpperBoundIsMissing() {
        PageRange pageRange = new PageRange("1");
        assertThat(pageRange.isRange(), is(false));
    }

    @Test
    public void shouldNotBeRangeIfHasUpperBound() {
        PageRange pageRange = new PageRange("1-10");
        assertThat(pageRange.isRange(), is(true));
    }

    @Test
    public void throwsNumberFormatExceptionIfLowerBoundNotANumber() {
        mExpectedException.expect(NumberFormatException.class);
        new PageRange("q");
    }

    @Test
    public void throwsNumberFormatExceptionIfUpperBoundNotANumber() {
        mExpectedException.expect(NumberFormatException.class);
        new PageRange("1-q");
    }
}