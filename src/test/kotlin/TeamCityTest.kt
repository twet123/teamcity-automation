import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import org.example.dtos.*
import org.example.utils.TeamCityApiClient
import org.example.utils.TestDataProvider
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TeamCityTest {
    private lateinit var httpClient: HttpClient
    private lateinit var apiClient: TeamCityApiClient
    private lateinit var projectId: String
    private val testDataProvider: TestDataProvider = TestDataProvider()

    @BeforeAll
    fun setup() {
        httpClient = HttpClient {
            install(ContentNegotiation) {
                json()
            }
        }
        apiClient = TeamCityApiClient(httpClient)
    }

    @AfterAll
    fun cleanup() {
        httpClient.close()
    }

    @AfterEach
    fun deleteProject() {
        apiClient.deleteResource("projects/id:$projectId")
    }

    @Test
    @DisplayName("Should create a project successfully")
    fun testCreateProject() {
        val project = testDataProvider.getObject<ProjectRequestDto>("project")

        projectId = apiClient.createResource("projects", project)

        assertTrue(apiClient.checkResourceExists("projects/id:$projectId"))
    }

    @Test
    @DisplayName("Should create a vcs root successfully")
    fun testCreateVcsRoot() {
        val project = testDataProvider.getObject<ProjectRequestDto>("project")
        projectId = apiClient.createResource("projects", project)
        val vcsRoot = testDataProvider.getObject<VcsRootRequestDto>("vcs-root")
        vcsRoot.project.id = projectId

        val vcsRootId = apiClient.createResource("vcs-roots", vcsRoot)

        assertTrue(apiClient.checkResourceExists("vcs-roots/id:$vcsRootId"))
    }

    @Test
    @DisplayName("Should create a build type successfully")
    fun testCreateBuildType() {
        val project = testDataProvider.getObject<ProjectRequestDto>("project")
        projectId = apiClient.createResource("projects", project)
        val buildType = testDataProvider.getObject<BuildTypeRequestDto>("build-type")
        buildType.project.id = projectId

        val buildTypeId = apiClient.createResource("buildTypes", buildType)

        assertTrue(apiClient.checkResourceExists("buildTypes/id:$buildTypeId"))
    }

    @Test
    @DisplayName("Should create a build configuration vcs root entry successfully")
    fun testCreateBuildConfigurationVcsRootEntry() {
        val project = testDataProvider.getObject<ProjectRequestDto>("project")
        projectId = apiClient.createResource("projects", project)
        val vcsRoot = testDataProvider.getObject<VcsRootRequestDto>("vcs-root")
        vcsRoot.project.id = projectId
        val vcsRootId = apiClient.createResource("vcs-roots", vcsRoot)
        val buildType = testDataProvider.getObject<BuildTypeRequestDto>("build-type")
        buildType.project.id = projectId
        val buildTypeId = apiClient.createResource("buildTypes", buildType)
        val vcsRootEntry = VcsRootEntryDto(vcsRootId, VcsRoot(vcsRootId))

        val vcsRootEntryId = apiClient.createResource("buildTypes/id:$buildTypeId/vcs-root-entries", vcsRootEntry)

        assertTrue(apiClient.checkResourceExists("buildTypes/id:$buildTypeId/vcs-root-entries/$vcsRootEntryId"))
    }

    @Test
    @DisplayName("Should execute a build configuration successfully")
    fun testExecuteBuildConfiguration() {
        val project = testDataProvider.getObject<ProjectRequestDto>("project")
        projectId = apiClient.createResource("projects", project)
        val vcsRoot = testDataProvider.getObject<VcsRootRequestDto>("vcs-root")
        vcsRoot.project.id = projectId
        val vcsRootId = apiClient.createResource("vcs-roots", vcsRoot)
        val buildType = testDataProvider.getObject<BuildTypeRequestDto>("build-type")
        buildType.project.id = projectId
        val buildTypeId = apiClient.createResource("buildTypes", buildType)
        val vcsRootEntry = VcsRootEntryDto(vcsRootId, VcsRoot(vcsRootId))
        apiClient.createResource("buildTypes/id:$buildTypeId/vcs-root-entries", vcsRootEntry)
        val buildExecutionRequest = BuildExecutionRequestDto(BuildExecutionBuildType(buildTypeId))

        val buildExecutionId = apiClient.createResource("buildQueue", buildExecutionRequest)
        val executionStatus = apiClient.getBuildExecutionStatus(buildExecutionId)

        assertEquals("SUCCESS", executionStatus)
    }
}