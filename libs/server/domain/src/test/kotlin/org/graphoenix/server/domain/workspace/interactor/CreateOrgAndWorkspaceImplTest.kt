package org.graphoenix.server.domain.workspace.interactor

import ch.tutteli.atrium.api.fluent.en_GB.its
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.every
import io.mockk.mockk
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.vertx.RunOnVertxContext
import io.quarkus.test.vertx.UniAsserter
import io.smallrye.mutiny.Uni
import org.graphoenix.server.domain.organization.model.Organization
import org.graphoenix.server.domain.organization.model.OrganizationId
import org.graphoenix.server.domain.workspace.gateway.AccessTokenRepository
import org.graphoenix.server.domain.workspace.gateway.OrganizationCreationService
import org.graphoenix.server.domain.workspace.gateway.WorkspaceRepository
import org.graphoenix.server.domain.workspace.model.*
import org.graphoenix.server.domain.workspace.usecase.CreateOrgAndWorkspaceRequest
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@QuarkusTest
class CreateOrgAndWorkspaceImplTest {
  private val mockWorkspaceRepository = mockk<WorkspaceRepository>()
  private val mockOrgCreation = mockk<OrganizationCreationService>()
  private val mockAccessTokenRepository = mockk<AccessTokenRepository>()
  private val createOrgAndWorkspace = CreateOrgAndWorkspaceImpl(mockWorkspaceRepository, mockOrgCreation, mockAccessTokenRepository)

  @Test
  @RunOnVertxContext
  fun `should create org, workspace and default access token`(uniAsserter: UniAsserter) {
    // Given
    val dummyOrgId = OrganizationId("org-id")
    val dummyWorkspaceId = WorkspaceId("workspace-id")
    val dummyRequest =
      CreateOrgAndWorkspaceRequest(workspaceName = "test workspace", installationSource = "junit", nxInitDate = LocalDateTime.now())

    every { mockOrgCreation.createOrg("test workspace") } returns
      Uni.createFrom().item(Organization(dummyOrgId, "test workspace"))
    every { mockWorkspaceRepository.create(dummyRequest, dummyOrgId) } returns
      Uni
        .createFrom()
        .item(
          Workspace(dummyWorkspaceId, dummyOrgId, "test workspace", "junit", null),
        )
    every { mockAccessTokenRepository.createDefaultAccessToken(dummyWorkspaceId) } returns
      Uni
        .createFrom()
        .item(
          AccessToken {
            id = AccessTokenId("token-ID")
            workspaceId = dummyWorkspaceId
            accessLevel = AccessLevel.READ_WRITE
            name = "default"
            publicId = AccessTokenPublicId()
            encodedValue = "base64content"
          },
        )

    // When
    uniAsserter.assertThat(
      { createOrgAndWorkspace(dummyRequest) { it } },
      { response ->
        // Then
        expect(response) {
          its { workspace.name }.toEqual("test workspace")
          its { workspace.id }.toEqual(dummyWorkspaceId)
          its { accessToken.name }.toEqual("default")
          its { accessToken.accessLevel }.toEqual(AccessLevel.READ_WRITE)
        }
      },
    )
  }
}
