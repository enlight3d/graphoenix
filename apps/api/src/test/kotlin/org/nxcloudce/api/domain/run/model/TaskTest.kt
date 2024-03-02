package org.nxcloudce.api.domain.run.model

import ch.tutteli.atrium.api.fluent.en_GB.its
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.junit.jupiter.api.Test
import org.nxcloudce.api.domain.workspace.model.WorkspaceId
import java.time.LocalDateTime

class TaskTest {
  @Test
  fun `should build a new instance of Task`() {
    val dummyStartTime = LocalDateTime.now()
    val dummyEndTime = dummyStartTime.plusHours(1)
    val task =
      Task {
        taskId = TaskId("task-id")
        runId = RunId("run-id")
        workspaceId = WorkspaceId("workspace-id")
        hash = Hash("hash-value")
        projectName = "apps/api"
        target = "test"
        startTime = dummyStartTime
        endTime = dummyEndTime
        cacheStatus = CacheStatus.CACHE_MISS
        status = 0
        uploadedToStorage = true
        params = "params"
        terminalOutput = "terminal-output"
        artifactId = ArtifactId("artifact-id")
      }

    expect(task) {
      its { taskId.value }.toEqual("task-id")
      its { runId.value }.toEqual("run-id")
      its { workspaceId.value }.toEqual("workspace-id")
      its { hash.value }.toEqual("hash-value")
      its { projectName }.toEqual("apps/api")
      its { target }.toEqual("test")
      its { startTime }.toEqual(dummyStartTime)
      its { endTime }.toEqual(dummyEndTime)
      its { cacheStatus }.toEqual(CacheStatus.CACHE_MISS)
      its { status }.toEqual(0)
      its { uploadedToStorage }.toEqual(true)
      its { params }.toEqual("params")
      its { terminalOutput }.toEqual("terminal-output")
      its { artifactId?.value }.toEqual("artifact-id")
    }
  }
}
