package org.nxcloudce.api.presentation.dto

import org.nxcloudce.api.domain.run.model.*
import org.nxcloudce.api.domain.run.usecase.EndRunRequest
import java.time.LocalDateTime
import java.util.UUID

sealed class RunDto {
  abstract val branch: String?
  abstract val runGroup: String
  abstract val ciExecutionId: String?
  abstract val ciExecutionEnv: String?
  abstract val machineInfo: MachineInfo
  abstract val meta: Any
  abstract val vcsContext: String?

  data class Start(
    override val branch: String?,
    override val runGroup: String,
    override val ciExecutionId: String?,
    override val ciExecutionEnv: String?,
    override val machineInfo: MachineInfo,
    override val meta: Any,
    override val vcsContext: String?,
    val distributedExecutionId: String?,
    val hashes: Collection<String>,
  ) : RunDto()

  data class End(
    override val branch: String?,
    override val runGroup: String,
    override val ciExecutionId: String?,
    override val ciExecutionEnv: String?,
    override val machineInfo: MachineInfo,
    override val meta: Any,
    override val vcsContext: String?,
    val tasks: Collection<Task>,
    val linkId: String,
    val run: RunData,
  ) : RunDto() {
    fun toRunRequest(): EndRunRequest.Run =
      EndRunRequest.Run(
        command = run.command,
        startTime = run.startTime,
        endTime = run.endTime,
        branch = branch,
        runGroup = runGroup,
        inner = run.inner,
        distributedExecutionId = run.distributedExecutionId,
        ciExecutionId = ciExecutionId,
        ciExecutionEnv = ciExecutionEnv,
        machineInfo = machineInfo,
//        meta = meta,
        vcsContext = vcsContext,
        linkId = linkId,
      )

    fun toTaskRequests(): List<EndRunRequest.Task> =
      tasks.map { task ->
        EndRunRequest.Task(
          taskId = task.taskId,
          hash = Hash(task.hash),
          projectName = task.projectName,
          target = task.target,
          startTime = task.startTime,
          endTime = task.endTime,
          cacheStatus = CacheStatus.from(task.cacheStatus),
          status = task.status,
          uploadedToStorage = task.uploadedToStorage,
          params = task.params,
          terminalOutput = task.terminalOutput,
          artifactId = task.artifactId?.let { ArtifactId(it.toString()) },
        )
      }

    data class Task(
      val taskId: String,
      val hash: String,
      val projectName: String,
      val target: String,
      val startTime: LocalDateTime,
      val endTime: LocalDateTime,
      val cacheStatus: String,
      val status: Int,
      val uploadedToStorage: Boolean,
      val params: String,
      val terminalOutput: String,
      val hashDetails: HashDetails,
      val artifactId: UUID?,
    ) {
      data class HashDetails(
        val nodes: Map<String, String>,
        val runtime: Any,
        val implicitDeps: Any,
      )
    }

    data class RunData(
      val command: String,
      val startTime: LocalDateTime,
      val endTime: LocalDateTime,
      val branch: String?,
      val runGroup: String?,
      val inner: Boolean,
      val distributedExecutionId: String?,
    )
  }
}
