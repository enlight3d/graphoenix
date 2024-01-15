package org.nxcloudce.api.persistence.gateway

import jakarta.enterprise.context.ApplicationScoped
import org.nxcloudce.api.domain.run.gateway.ArtifactRepository
import org.nxcloudce.api.domain.run.model.Artifact
import org.nxcloudce.api.domain.run.model.ArtifactId
import org.nxcloudce.api.domain.run.model.Hash
import org.nxcloudce.api.domain.workspace.model.WorkspaceId
import org.nxcloudce.api.persistence.repository.ArtifactPanacheRepository

@ApplicationScoped
class ArtifactRepositoryImpl(
  private val artifactPanacheRepository: ArtifactPanacheRepository,
) : ArtifactRepository {
  override suspend fun findByHash(
    hashes: Collection<Hash>,
    workspaceId: WorkspaceId,
  ): Collection<Artifact.Exist> =
    artifactPanacheRepository
      .findByHash(hashes.map { it.value }, workspaceId)
      .map { it.toDomain() }

  override suspend fun createWithHash(
    hashes: Collection<Hash>,
    workspaceId: WorkspaceId,
  ): Collection<Artifact.New> =
    hashes.map { hash ->
      Artifact.New(
        id = ArtifactId(),
        workspaceId = workspaceId,
        hash = hash,
        put = null,
      )
    }
}
