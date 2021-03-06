/*
 * Copyright (C) 2016 TIBCO Jaspersoft Corporation. All rights reserved.
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

package com.jaspersoft.android.sdk.network;

import com.jaspersoft.android.sdk.network.entity.server.EncryptionKey;
import com.jaspersoft.android.sdk.test.MockResponseFactory;
import com.jaspersoft.android.sdk.test.WebMockRule;
import com.jaspersoft.android.sdk.test.resource.ResourceFile;
import com.jaspersoft.android.sdk.test.resource.TestResource;
import com.jaspersoft.android.sdk.test.resource.inject.TestResourceInjector;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.jaspersoft.android.sdk.test.matcher.IsRecorderRequestContainsHeader.containsHeader;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public class AuthRestApiTest {
    private final String LOCATION_SUCCESS = "/scripts/bower_components/js-sdk/src/common/auth/loginSuccess.json;jsessionid=7D86AE28407432B728694DF649DB5E8F";
    private final String LOCATION_ERROR = "login.html;jsessionid=395364A98787A1C42D5FEB0E9F58CF9F?error=1";

    @Rule
    public final WebMockRule mWebMockRule = new WebMockRule();
    @Rule
    public final ExpectedException mExpectedException = ExpectedException.none();

    private AuthRestApi apiUnderTest;

    @ResourceFile("json/encryption_key.json")
    TestResource mKey;

    @Before
    public void setup() {
        TestResourceInjector.inject(this);
        Server server = Server.builder()
                .withBaseUrl(mWebMockRule.getRootUrl())
                .build();
        NetworkClient networkClient = server.newNetworkClient().build();
        networkClient.getClient().setFollowRedirects(false);
        apiUnderTest = new AuthRestApi(networkClient);
    }

    @Test
    public void should_parse_success_full_location_path() throws Exception {
        MockResponse mockResponse = MockResponseFactory.create302()
                .addHeader("Set-Cookie", "cookie1=12")
                .addHeader("Location", mWebMockRule.getRootUrl() + LOCATION_SUCCESS);
        mWebMockRule.enqueue(mockResponse);

        apiUnderTest.springAuth("joeuser", "joeuser", null, null);
    }

    @Test
    public void should_parse_success_relative_location_path() throws Exception {
        MockResponse mockResponse = MockResponseFactory.create302()
                .addHeader("Set-Cookie", "cookie1=12")
                .addHeader("Location", LOCATION_SUCCESS);
        mWebMockRule.enqueue(mockResponse);

        apiUnderTest.springAuth("joeuser", "joeuser", null, null);
    }

    @Test
    public void should_parse_error_full_location_path() throws Exception {
        mExpectedException.expect(HttpException.class);

        MockResponse mockResponse = MockResponseFactory.create302()
                .addHeader("Location", mWebMockRule.getRootUrl() + LOCATION_ERROR)
                .addHeader("Set-Cookie", "cookie1");
        mWebMockRule.enqueue(mockResponse);

        apiUnderTest.springAuth("joeuser", "joeuser", "null", null);
    }

    @Test
    public void should_parse_error_relative_location_path() throws Exception {
        mExpectedException.expect(HttpException.class);

        MockResponse mockResponse = MockResponseFactory.create302()
                .addHeader("Location", LOCATION_ERROR)
                .addHeader("Set-Cookie", "cookie1");
        mWebMockRule.enqueue(mockResponse);

        apiUnderTest.springAuth("joeuser", "joeuser", "null", null);
    }

    @Test
    public void shouldRiseErrorForHttpException() throws Exception {
        mExpectedException.expect(HttpException.class);

        mWebMockRule.enqueue(MockResponseFactory.create500());

        apiUnderTest.springAuth("joeuser", "joeuser", "null", null);
    }

    @Test
    public void shouldReturnEncryptionKeyIfApiAvailable() throws Exception {
        MockResponse encryptionKey = MockResponseFactory.create200()
                .setBody(mKey.asString());
        mWebMockRule.enqueue(encryptionKey);

        EncryptionKey keyResponse = apiUnderTest.requestEncryptionMetadata();
        assertThat(keyResponse, Is.is(notNullValue()));
    }

    @Test
    public void shouldReturnEmptyEncryptionKeyIfApiNotAvailable() throws Exception {
        String malformedJson = "{Error: Key generation is off}";
        MockResponse encryptionKey = MockResponseFactory.create200()
                .setBody(malformedJson);

        mWebMockRule.enqueue(encryptionKey);

        EncryptionKey keyResponse = apiUnderTest.requestEncryptionMetadata();
        assertThat(keyResponse.isAvailable(), Is.is(false));
    }

    @Test
    public void should_include_header_csrf_header() throws Exception {
        String rootUrl = mWebMockRule.getRootUrl();
        MockResponse cookieResponse = MockResponseFactory.create302()
                .addHeader("Location", rootUrl + LOCATION_SUCCESS)
                .addHeader("Set-Cookie", "cookie=12");

        mWebMockRule.enqueue(cookieResponse);

        apiUnderTest.springAuth("joeuser", "joeuser", null, null);

        String xdm = rootUrl.substring(0, rootUrl.length() - 1);
        RecordedRequest response = mWebMockRule.get().takeRequest();
        assertThat(response, containsHeader("x-jasper-xdm", xdm));
    }

    @Test
    public void shouldReturnEmptyEncryptionKeyIfApiReturns401Error() throws Exception {
        MockResponse anonymousCookie = MockResponseFactory.create200()
                .setBody("6.1")
                .addHeader("Set-Cookie", "cookie1=12");

        String malformedJson = "{Error: Key generation is off}";
        MockResponse encryptionKey = MockResponseFactory.create401()
                .setBody(malformedJson);

        mWebMockRule.enqueue(anonymousCookie);
        mWebMockRule.enqueue(encryptionKey);

        EncryptionKey keyResponse = apiUnderTest.requestEncryptionMetadata();
        assertThat(keyResponse.isAvailable(), is(false));
    }
}
