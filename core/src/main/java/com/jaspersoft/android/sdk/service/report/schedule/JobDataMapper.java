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

package com.jaspersoft.android.sdk.service.report.schedule;

import com.jaspersoft.android.sdk.network.entity.schedule.JobDescriptor;
import com.jaspersoft.android.sdk.service.data.schedule.JobData;
import com.jaspersoft.android.sdk.service.data.schedule.JobOutputFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Tom Koptel
 * @since 2.3
 */
class JobDataMapper {
    public JobData transform(JobDescriptor jobDescriptor, SimpleDateFormat dateTimeFormat) {
        JobData.Builder builder = new JobData.Builder();
        builder.withId(jobDescriptor.getId());
        builder.withLabel(jobDescriptor.getLabel());
        builder.withDescription(jobDescriptor.getDescription());
        builder.withVersion(jobDescriptor.getVersion());
        builder.withLabel(jobDescriptor.getLabel());
        builder.withUsername(jobDescriptor.getUsername());

        Date creationDate;
        try {
            creationDate = dateTimeFormat.parse(jobDescriptor.getCreationDate());
        } catch (ParseException e) {
            creationDate = null;
        }
        builder.withCreationDate(creationDate);

        Collection<String> outputFormat = jobDescriptor.getOutputFormats().getOutputFormat();
        List<JobOutputFormat> formats = new ArrayList<>(outputFormat.size());
        for (String format : outputFormat) {
            formats.add(JobOutputFormat.valueOf(format));
        }
        builder.withOutputFormats(formats);

        return builder.build();
    }
}
