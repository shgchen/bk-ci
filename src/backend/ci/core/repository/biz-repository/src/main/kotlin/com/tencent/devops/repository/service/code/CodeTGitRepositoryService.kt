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
package com.tencent.devops.repository.service.code

import com.tencent.devops.common.api.constant.RepositoryMessageCode
import com.tencent.devops.common.api.enums.ScmType
import com.tencent.devops.common.api.exception.ErrorCodeException
import com.tencent.devops.common.api.exception.OperationException
import com.tencent.devops.common.api.util.HashUtil
import com.tencent.devops.common.service.utils.MessageCodeUtil
import com.tencent.devops.model.repository.tables.records.TRepositoryRecord
import com.tencent.devops.repository.dao.RepositoryCodeGitDao
import com.tencent.devops.repository.dao.RepositoryDao
import com.tencent.devops.repository.pojo.CodeTGitRepository
import com.tencent.devops.repository.pojo.auth.RepoAuthInfo
import com.tencent.devops.repository.pojo.credential.RepoCredentialInfo
import com.tencent.devops.repository.pojo.enums.RepoAuthType
import com.tencent.devops.repository.service.CredentialService
import com.tencent.devops.repository.service.scm.IScmService
import com.tencent.devops.scm.pojo.TokenCheckResult
import com.tencent.devops.scm.utils.code.git.GitUtils
import com.tencent.devops.ticket.pojo.enums.CredentialType
import org.apache.commons.lang3.StringUtils
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CodeTGitRepositoryService @Autowired constructor(
    private val repositoryDao: RepositoryDao,
    private val repositoryCodeGitDao: RepositoryCodeGitDao,
    private val dslContext: DSLContext,
    private val scmService: IScmService,
    private val credentialService: CredentialService
) : CodeRepositoryService<CodeTGitRepository> {
    override fun repositoryType(): String {
        return CodeTGitRepository::class.java.name
    }

    override fun create(projectId: String, userId: String, repository: CodeTGitRepository): Long {
        val credentialInfo = checkCredentialInfo(projectId = projectId, repository = repository)
        var repositoryId = 0L
        dslContext.transaction { configuration ->
            val transactionContext = DSL.using(configuration)
            repositoryId = repositoryDao.create(
                dslContext = transactionContext,
                projectId = projectId,
                userId = userId,
                aliasName = repository.aliasName,
                url = repository.getFormatURL(),
                type = ScmType.CODE_TGIT
            )
            // Git项目ID
            val gitProjectId = getGitProjectId(repo = repository, token = credentialInfo.token)
            repositoryCodeGitDao.create(
                dslContext = transactionContext,
                repositoryId = repositoryId,
                projectName = GitUtils.getProjectName(repository.url),
                userName = repository.userName,
                credentialId = repository.credentialId,
                authType = repository.authType,
                gitProjectId = gitProjectId
            )
        }
        return repositoryId
    }

    override fun edit(
        userId: String,
        projectId: String,
        repositoryHashId: String,
        repository: CodeTGitRepository,
        record: TRepositoryRecord
    ) {
        // 提交的参数与数据库中类型不匹配
        if (!StringUtils.equals(record.type, ScmType.CODE_TGIT.name)) {
            throw OperationException(MessageCodeUtil.getCodeLanMessage(RepositoryMessageCode.TGIT_INVALID))
        }
        // 凭证信息
        val credentialInfo = checkCredentialInfo(projectId = projectId, repository = repository)
        val repositoryId = HashUtil.decodeOtherIdToLong(repositoryHashId)
        var gitProjectId = 0L
        // 需要更新gitProjectId
        if (record.url != repository.url) {
            logger.info(
                "repository url unMatch,need change gitProjectId,sourceUrl=[${record.url}] " +
                    "targetUrl=[${repository.url}]"
            )
            // Git项目ID
            gitProjectId = getGitProjectId(
                repo = repository,
                token = credentialInfo.token
            )
        }
        dslContext.transaction { configuration ->
            val transactionContext = DSL.using(configuration)
            repositoryDao.edit(
                dslContext = transactionContext,
                repositoryId = repositoryId,
                aliasName = repository.aliasName,
                url = repository.getFormatURL()
            )
            repositoryCodeGitDao.edit(
                dslContext = transactionContext,
                repositoryId = repositoryId,
                projectName = GitUtils.getProjectName(repository.url),
                userName = repository.userName,
                credentialId = repository.credentialId,
                authType = repository.authType,
                gitProjectId = gitProjectId
            )
        }
    }

    override fun compose(repository: TRepositoryRecord): CodeTGitRepository {
        val record = repositoryCodeGitDao.get(dslContext, repository.repositoryId)
        return CodeTGitRepository(
            aliasName = repository.aliasName,
            url = repository.url,
            credentialId = record.credentialId,
            projectName = record.projectName,
            userName = record.userName,
            authType = RepoAuthType.parse(record.authType),
            projectId = repository.projectId,
            repoHashId = HashUtil.encodeOtherLongId(repository.repositoryId),
            repoProjectId = record.gitProjectId
        )
    }

    fun checkToken(
        repoCredentialInfo: RepoCredentialInfo,
        repository: CodeTGitRepository
    ): TokenCheckResult {
        val checkResult: TokenCheckResult = when (repository.authType) {
            RepoAuthType.SSH -> {
                if (repoCredentialInfo.token.isEmpty()) {
                    throw OperationException(
                        message = MessageCodeUtil.getCodeLanMessage(RepositoryMessageCode.GIT_TOKEN_EMPTY)
                    )
                }
                if (repoCredentialInfo.privateKey.isEmpty()) {
                    throw OperationException(
                        message = MessageCodeUtil.getCodeLanMessage(RepositoryMessageCode.USER_SECRET_EMPTY)
                    )
                }
                scmService.checkPrivateKeyAndToken(
                    projectName = GitUtils.getProjectName(repository.getFormatURL()),
                    url = repository.getFormatURL(),
                    type = ScmType.CODE_TGIT,
                    privateKey = repoCredentialInfo.privateKey,
                    passPhrase = repoCredentialInfo.passPhrase,
                    token = repoCredentialInfo.token,
                    region = null,
                    userName = repository.userName
                )
            }
            RepoAuthType.HTTP -> {
                if (repoCredentialInfo.credentialType == CredentialType.USERNAME_PASSWORD.name) {
                    logger.info("TGit check type is username+password,don't check, return")
                    return TokenCheckResult(result = true, message = "")
                }
                scmService.checkUsernameAndPassword(
                    projectName = GitUtils.getProjectName(repository.getFormatURL()),
                    url = repository.getFormatURL(),
                    type = ScmType.CODE_TGIT,
                    username = repoCredentialInfo.username,
                    password = repoCredentialInfo.password,
                    token = repoCredentialInfo.token,
                    region = null,
                    repoUsername = repository.userName
                )
            }
            RepoAuthType.HTTPS -> {
                if (repoCredentialInfo.credentialType == CredentialType.USERNAME_PASSWORD.name) {
                    logger.info("TGit check type is username+password,don't check, return")
                    return TokenCheckResult(result = true, message = "")
                }
                scmService.checkUsernameAndPassword(
                    projectName = GitUtils.getProjectName(repository.getFormatURL()),
                    url = repository.getFormatURL(),
                    type = ScmType.CODE_TGIT,
                    username = repoCredentialInfo.username,
                    password = repoCredentialInfo.password,
                    token = repoCredentialInfo.token,
                    region = null,
                    repoUsername = repository.userName
                )
            }
            else -> {
                throw ErrorCodeException(
                    errorCode = RepositoryMessageCode.REPO_TYPE_NO_NEED_CERTIFICATION,
                    params = arrayOf(repository.authType!!.name)
                )
            }
        }
        return checkResult
    }

    /**
     * 检查凭证信息
     */
    private fun checkCredentialInfo(projectId: String, repository: CodeTGitRepository): RepoCredentialInfo {
        val repoCredentialInfo = getCredentialInfo(
            projectId = projectId,
            repository = repository
        )
        val checkResult = checkToken(
            repoCredentialInfo = repoCredentialInfo,
            repository = repository
        )
        if (!checkResult.result) {
            logger.warn("Fail to check the repo token & private key because of ${checkResult.message}")
            throw OperationException(checkResult.message)
        }
        return repoCredentialInfo
    }

    /**
     * 获取Git项目ID
     */
    fun getGitProjectId(repo: CodeTGitRepository, token: String): Long {
        logger.info("the repo is:$repo")
        val repositoryProjectInfo = scmService.getProjectInfo(
            projectName = GitUtils.getProjectName(repo.getFormatURL()),
            url = repo.getFormatURL(),
            type = ScmType.CODE_TGIT,
            token = token
        )
        logger.info("the gitProjectInfo is:$repositoryProjectInfo")
        return repositoryProjectInfo?.id ?: 0L
    }

    override fun getAuthInfo(repositoryIds: List<Long>): Map<Long, RepoAuthInfo> {
        return repositoryCodeGitDao.list(
            dslContext = dslContext,
            repositoryIds = repositoryIds.toSet()
        )?.associateBy({ it -> it.repositoryId }, {
            RepoAuthInfo(it.authType ?: RepoAuthType.SSH.name, it.credentialId)
        }) ?: mapOf()
    }

    /**
     * 获取凭证信息
     */
    fun getCredentialInfo(projectId: String, repository: CodeTGitRepository): RepoCredentialInfo {
        // 凭证信息
        return credentialService.getCredentialInfo(
            projectId = projectId,
            repository = repository
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CodeTGitRepositoryService::class.java)
    }
}
