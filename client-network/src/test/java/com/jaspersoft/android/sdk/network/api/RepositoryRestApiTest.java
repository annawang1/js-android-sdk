/*
 * Copyright � 2015 TIBCO Software, Inc. All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of Jaspersoft Mobile for Android.
 *
 * Jaspersoft Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Jaspersoft Mobile for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.sdk.network.api;

import com.jaspersoft.android.sdk.network.api.auth.Token;
import com.jaspersoft.android.sdk.network.entity.resource.ResourceSearchResponse;
import com.jaspersoft.android.sdk.test.MockResponseFactory;
import com.jaspersoft.android.sdk.test.WebMockRule;
import com.jaspersoft.android.sdk.test.resource.ResourceFile;
import com.jaspersoft.android.sdk.test.resource.TestResource;
import com.jaspersoft.android.sdk.test.resource.inject.TestResourceInjector;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author Tom Koptel
 * @since 2.0
 */
public class RepositoryRestApiTest {

    @ResourceFile("json/all_resources.json")
    TestResource searchResponse;

    @Rule
    public final WebMockRule mWebMockRule = new WebMockRule();
    @Rule
    public final ExpectedException mExpectedException = ExpectedException.none();

    private RepositoryRestApi restApiUnderTest;

    @Mock
    Token<?> mToken;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TestResourceInjector.inject(this);
        restApiUnderTest = new RepositoryRestApi.Builder()
                .token(mToken)
                .baseUrl(mWebMockRule.getRootUrl())
                .build();
    }

    @Test
    public void shouldReturnEmptyResponseForNoContentResponse() {
        mWebMockRule.enqueue(MockResponseFactory.create204());

        ResourceSearchResponse response = restApiUnderTest.searchResources(null);
        assertThat(response.getResources(), is(empty()));
    }

    @Test
    public void requestForSearchShouldParseHeaderResultCount() {
        MockResponse mockResponse = MockResponseFactory.create200()
                .setBody(searchResponse.asString())
                .addHeader("Result-Count", "100");
        mWebMockRule.enqueue(mockResponse);

        ResourceSearchResponse response = restApiUnderTest.searchResources(null);
        assertThat(response.getResultCount(), is(100));
    }

    @Test
    public void requestForSearchShouldParseHeaderTotalCount() {
        MockResponse mockResponse = MockResponseFactory.create200()
                .setBody(searchResponse.asString())
                .addHeader("Total-Count", "1000");
        mWebMockRule.enqueue(mockResponse);

        ResourceSearchResponse response = restApiUnderTest.searchResources(null);
        assertThat(response.getTotalCount(), is(1000));
    }

    @Test
    public void requestForSearchShouldParseHeaderStartIndex() {
        MockResponse mockResponse = MockResponseFactory.create200()
                .setBody(searchResponse.asString())
                .addHeader("Start-Index", "5");
        mWebMockRule.enqueue(mockResponse);

        ResourceSearchResponse response = restApiUnderTest.searchResources(null);
        assertThat(response.getStartIndex(), is(5));
    }

    @Test
    public void requestForSearchShouldParseHeaderNextOffset() {
        MockResponse mockResponse = MockResponseFactory.create200()
                .setBody(searchResponse.asString())
                .addHeader("Next-Offset", "10");
        mWebMockRule.enqueue(mockResponse);

        ResourceSearchResponse response = restApiUnderTest.searchResources(null);
        assertThat(response.getNextOffset(), is(10));
    }

    @Test
    public void requestForReportResourceShouldNotAcceptNullUri() {
        mExpectedException.expect(NullPointerException.class);
        mExpectedException.expectMessage("Report uri should not be null");

        restApiUnderTest.requestReportResource(null);
    }

    @Test
    public void requestForDashboardResourceShouldNotAcceptNullUri() {
        mExpectedException.expect(NullPointerException.class);
        mExpectedException.expectMessage("Dashboard uri should not be null");

        restApiUnderTest.requestDashboardResource(null);
    }

    @Test
    public void requestForLegacyDashboardResourceShouldNotAcceptNullUri() {
        mExpectedException.expect(NullPointerException.class);
        mExpectedException.expectMessage("Legacy dashboard uri should not be null");

        restApiUnderTest.requestLegacyDashboardResource(null);
    }

    @Test
    public void requestForFolderResourceShouldNotAcceptNullUri() {
        mExpectedException.expect(NullPointerException.class);
        mExpectedException.expectMessage("Folder uri should not be null");

        restApiUnderTest.requestFolderResource(null);
    }

    @Test
    public void searchResourcesShouldThrowRestErrorOn500() {
        mExpectedException.expect(RestError.class);

        mWebMockRule.enqueue(MockResponseFactory.create500());

        restApiUnderTest.searchResources(null);
    }

    @Test
    public void requestReportResourceShouldThrowRestErrorOn500() {
        mExpectedException.expect(RestError.class);

        mWebMockRule.enqueue(MockResponseFactory.create500());

        restApiUnderTest.requestReportResource("any_id");
    }

    @Test
    public void requestDashboardResourceShouldThrowRestErrorOn500() {
        mExpectedException.expect(RestError.class);

        mWebMockRule.enqueue(MockResponseFactory.create500());

        restApiUnderTest.requestDashboardResource("any_id");
    }

    @Test
    public void requestLegacyDashboardResourceShouldThrowRestErrorOn500() {
        mExpectedException.expect(RestError.class);

        mWebMockRule.enqueue(MockResponseFactory.create500());

        restApiUnderTest.requestLegacyDashboardResource("any_id");
    }

    @Test
    public void requestFolderResourceShouldThrowRestErrorOn500() {
        mExpectedException.expect(RestError.class);

        mWebMockRule.enqueue(MockResponseFactory.create500());

        restApiUnderTest.requestFolderResource("any_id");
    }

    @Test
    public void searchEndpointShouldHandleMultipleResourceTypes() throws Exception {
        MockResponse response = MockResponseFactory.create200()
                .setBody("{\"resourceLookup\": []}");
        mWebMockRule.enqueue(response);

        Map<String, Object> params = new HashMap<>();
        params.put("folderUri", "/");

        Set<String> types = new HashSet<>();
        types.add("reportUnit");
        types.add("dashboard");
        params.put("type", types);

        restApiUnderTest.searchResources(params);

        RecordedRequest request = mWebMockRule.get().takeRequest();
        assertThat(request.getPath(), is("/rest_v2/resources?folderUri=/&type=reportUnit&type=dashboard"));
    }

    @Test
    public void searchEndpointShouldNotAcceptNullVorTypeKey() throws Exception {
        mExpectedException.expect(IllegalStateException.class);
        mExpectedException.expectMessage("Found null for key 'type'. Ensure this to be not a null");

        Map<String, Object> params = new HashMap<>();
        params.put("type", null);

        restApiUnderTest.searchResources(params);
    }
}
