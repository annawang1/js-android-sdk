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

import com.jaspersoft.android.sdk.network.entity.schedule.JobFormEntity;
import com.jaspersoft.android.sdk.network.entity.schedule.JobSimpleTriggerEntity;
import com.jaspersoft.android.sdk.service.data.schedule.JobForm;

/**
 * @author Tom Koptel
 * @since 2.5
 */
class JobNoneTriggerMapper extends BaseTriggerMapper {
    final static JobNoneTriggerMapper INSTANCE = new JobNoneTriggerMapper();

    @Override
    public void mapFormOnEntity(JobForm form, JobFormEntity entity) {
        JobSimpleTriggerEntity triggerEntity = new JobSimpleTriggerEntity();

        mapCommonTriggerFieldsOnEntity(form, triggerEntity);
        triggerEntity.setOccurrenceCount(1);
        triggerEntity.setRecurrenceInterval(1);
        triggerEntity.setRecurrenceIntervalUnit("DAY");

        entity.setSimpleTrigger(triggerEntity);
    }

    @Override
    public void mapEntityOnForm(JobForm.Builder form, JobFormEntity entity) {
        mapCommonEntityTriggerFieldsOnEntity(form, entity);
    }
}
