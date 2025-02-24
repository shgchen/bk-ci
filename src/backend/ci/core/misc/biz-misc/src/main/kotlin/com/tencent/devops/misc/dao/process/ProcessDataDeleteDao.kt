/*
 * Tencent is pleased to support the open source community by making BK-CI 蓝鲸持续集成平台 available.
 *
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * BK-CI 蓝鲸持续集成平台 is licensed under the MIT license.
 *
 * A copy of the MIT License is included in this file.
 *
 *
 * Terms of the MIT License:
 * ---------------------------------------------------
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.tencent.devops.misc.dao.process

import com.tencent.devops.model.process.tables.TAuditResource
import com.tencent.devops.model.process.tables.TPipelineBuildContainer
import com.tencent.devops.model.process.tables.TPipelineBuildDetail
import com.tencent.devops.model.process.tables.TPipelineBuildHistory
import com.tencent.devops.model.process.tables.TPipelineBuildRecordContainer
import com.tencent.devops.model.process.tables.TPipelineBuildRecordModel
import com.tencent.devops.model.process.tables.TPipelineBuildRecordStage
import com.tencent.devops.model.process.tables.TPipelineBuildRecordTask
import com.tencent.devops.model.process.tables.TPipelineBuildStage
import com.tencent.devops.model.process.tables.TPipelineBuildSummary
import com.tencent.devops.model.process.tables.TPipelineBuildTask
import com.tencent.devops.model.process.tables.TPipelineBuildTemplateAcrossInfo
import com.tencent.devops.model.process.tables.TPipelineBuildVar
import com.tencent.devops.model.process.tables.TPipelineFavor
import com.tencent.devops.model.process.tables.TPipelineGroup
import com.tencent.devops.model.process.tables.TPipelineInfo
import com.tencent.devops.model.process.tables.TPipelineJobMutexGroup
import com.tencent.devops.model.process.tables.TPipelineLabel
import com.tencent.devops.model.process.tables.TPipelineLabelPipeline
import com.tencent.devops.model.process.tables.TPipelineModelTask
import com.tencent.devops.model.process.tables.TPipelinePauseValue
import com.tencent.devops.model.process.tables.TPipelineRecentUse
import com.tencent.devops.model.process.tables.TPipelineResource
import com.tencent.devops.model.process.tables.TPipelineResourceVersion
import com.tencent.devops.model.process.tables.TPipelineSetting
import com.tencent.devops.model.process.tables.TPipelineSettingVersion
import com.tencent.devops.model.process.tables.TPipelineView
import com.tencent.devops.model.process.tables.TPipelineViewGroup
import com.tencent.devops.model.process.tables.TPipelineViewTop
import com.tencent.devops.model.process.tables.TPipelineViewUserLastView
import com.tencent.devops.model.process.tables.TPipelineViewUserSettings
import com.tencent.devops.model.process.tables.TPipelineWebhookBuildLogDetail
import com.tencent.devops.model.process.tables.TPipelineWebhookBuildParameter
import com.tencent.devops.model.process.tables.TPipelineWebhookQueue
import com.tencent.devops.model.process.tables.TProjectPipelineCallback
import com.tencent.devops.model.process.tables.TProjectPipelineCallbackHistory
import com.tencent.devops.model.process.tables.TReport
import com.tencent.devops.model.process.tables.TTemplate
import com.tencent.devops.model.process.tables.TTemplatePipeline
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Suppress("TooManyFunctions", "LargeClass")
@Repository
class ProcessDataDeleteDao {

    fun deleteAuditResource(dslContext: DSLContext, projectId: String) {
        with(TAuditResource.T_AUDIT_RESOURCE) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineBuildContainer(dslContext: DSLContext, projectId: String) {
        with(TPipelineBuildContainer.T_PIPELINE_BUILD_CONTAINER) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineBuildHistory(dslContext: DSLContext, projectId: String) {
        with(TPipelineBuildHistory.T_PIPELINE_BUILD_HISTORY) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineBuildDetail(dslContext: DSLContext, projectId: String) {
        with(TPipelineBuildDetail.T_PIPELINE_BUILD_DETAIL) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineBuildStage(dslContext: DSLContext, projectId: String) {
        with(TPipelineBuildStage.T_PIPELINE_BUILD_STAGE) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineBuildTask(dslContext: DSLContext, projectId: String) {
        with(TPipelineBuildTask.T_PIPELINE_BUILD_TASK) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineBuildSummary(dslContext: DSLContext, projectId: String) {
        with(TPipelineBuildSummary.T_PIPELINE_BUILD_SUMMARY) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineBuildVar(dslContext: DSLContext, projectId: String) {
        with(TPipelineBuildVar.T_PIPELINE_BUILD_VAR) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineFavor(dslContext: DSLContext, projectId: String) {
        with(TPipelineFavor.T_PIPELINE_FAVOR) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineGroup(dslContext: DSLContext, projectId: String) {
        with(TPipelineGroup.T_PIPELINE_GROUP) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineInfo(dslContext: DSLContext, projectId: String) {
        with(TPipelineInfo.T_PIPELINE_INFO) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineJobMutexGroup(dslContext: DSLContext, projectId: String) {
        with(TPipelineJobMutexGroup.T_PIPELINE_JOB_MUTEX_GROUP) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineLabel(dslContext: DSLContext, projectId: String) {
        with(TPipelineLabel.T_PIPELINE_LABEL) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineLabelPipeline(dslContext: DSLContext, projectId: String) {
        with(TPipelineLabelPipeline.T_PIPELINE_LABEL_PIPELINE) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineModelTask(dslContext: DSLContext, projectId: String) {
        with(TPipelineModelTask.T_PIPELINE_MODEL_TASK) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelinePauseValue(dslContext: DSLContext, projectId: String) {
        with(TPipelinePauseValue.T_PIPELINE_PAUSE_VALUE) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineResource(dslContext: DSLContext, projectId: String) {
        with(TPipelineResource.T_PIPELINE_RESOURCE) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineResourceVersion(dslContext: DSLContext, projectId: String) {
        with(TPipelineResourceVersion.T_PIPELINE_RESOURCE_VERSION) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineSetting(dslContext: DSLContext, projectId: String) {
        with(TPipelineSetting.T_PIPELINE_SETTING) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineSettingVersion(dslContext: DSLContext, projectId: String) {
        with(TPipelineSettingVersion.T_PIPELINE_SETTING_VERSION) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineView(dslContext: DSLContext, projectId: String) {
        with(TPipelineView.T_PIPELINE_VIEW) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineViewUserLastView(dslContext: DSLContext, projectId: String) {
        with(TPipelineViewUserLastView.T_PIPELINE_VIEW_USER_LAST_VIEW) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineViewUserSettings(dslContext: DSLContext, projectId: String) {
        with(TPipelineViewUserSettings.T_PIPELINE_VIEW_USER_SETTINGS) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineWebhookBuildLogDetail(dslContext: DSLContext, projectId: String) {
        with(TPipelineWebhookBuildLogDetail.T_PIPELINE_WEBHOOK_BUILD_LOG_DETAIL) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineWebhookQueue(dslContext: DSLContext, projectId: String) {
        with(TPipelineWebhookQueue.T_PIPELINE_WEBHOOK_QUEUE) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deleteProjectPipelineCallback(dslContext: DSLContext, projectId: String) {
        with(TProjectPipelineCallback.T_PROJECT_PIPELINE_CALLBACK) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deleteProjectPipelineCallbackHistory(dslContext: DSLContext, projectId: String) {
        with(TProjectPipelineCallbackHistory.T_PROJECT_PIPELINE_CALLBACK_HISTORY) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deleteReport(dslContext: DSLContext, projectId: String) {
        with(TReport.T_REPORT) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deleteTemplate(dslContext: DSLContext, projectId: String) {
        with(TTemplate.T_TEMPLATE) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deleteTemplatePipeline(dslContext: DSLContext, projectId: String) {
        with(TTemplatePipeline.T_TEMPLATE_PIPELINE) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineBuildTemplateAcrossInfo(dslContext: DSLContext, projectId: String) {
        with(TPipelineBuildTemplateAcrossInfo.T_PIPELINE_BUILD_TEMPLATE_ACROSS_INFO) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineWebhookBuildParameter(dslContext: DSLContext, projectId: String) {
        with(TPipelineWebhookBuildParameter.T_PIPELINE_WEBHOOK_BUILD_PARAMETER) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineViewGroup(dslContext: DSLContext, projectId: String) {
        with(TPipelineViewGroup.T_PIPELINE_VIEW_GROUP) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineViewTop(dslContext: DSLContext, projectId: String) {
        with(TPipelineViewTop.T_PIPELINE_VIEW_TOP) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineRecentUse(dslContext: DSLContext, projectId: String) {
        with(TPipelineRecentUse.T_PIPELINE_RECENT_USE) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineBuildRecordContainer(dslContext: DSLContext, projectId: String) {
        with(TPipelineBuildRecordContainer.T_PIPELINE_BUILD_RECORD_CONTAINER) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineBuildRecordModel(dslContext: DSLContext, projectId: String) {
        with(TPipelineBuildRecordModel.T_PIPELINE_BUILD_RECORD_MODEL) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineBuildRecordStage(dslContext: DSLContext, projectId: String) {
        with(TPipelineBuildRecordStage.T_PIPELINE_BUILD_RECORD_STAGE) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }

    fun deletePipelineBuildRecordTask(dslContext: DSLContext, projectId: String) {
        with(TPipelineBuildRecordTask.T_PIPELINE_BUILD_RECORD_TASK) {
            dslContext.deleteFrom(this)
                .where(PROJECT_ID.eq(projectId))
                .execute()
        }
    }
}
