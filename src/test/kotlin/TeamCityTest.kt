import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import org.example.dtos.*
import org.example.utils.TeamCityApiClient
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
    private val projectName = "SimpleMavenSample"
    private val githubUrl = "https://github.com/mkjetbrains/SimpleMavenSample"
    private val buildConfigurationName = "MavenBuildTest"
    private lateinit var httpClient: HttpClient
    private lateinit var apiClient: TeamCityApiClient
    private lateinit var projectId: String

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
        val project = ProjectRequestDto(projectName)

        projectId = apiClient.createResource("projects", project)

        assertTrue(apiClient.checkResourceExists("projects/id:$projectId"))
    }

    @Test
    @DisplayName("Should create a vcs root successfully")
    fun testCreateVcsRoot() {
        val project = ProjectRequestDto(projectName)
        projectId = apiClient.createResource("projects", project)
        val vcsRoot = VcsRootRequestDto(githubUrl, "jetbrains.git", Project(projectId),
            Properties(arrayListOf(Property("authMethod", "ANONYMOUS"), Property("branch", "refs/heads/master"), Property("url", githubUrl)))
        )

        val vcsRootId = apiClient.createResource("vcs-roots", vcsRoot)

        assertTrue(apiClient.checkResourceExists("vcs-roots/id:$vcsRootId"))
    }

    @Test
    @DisplayName("Should create a build type successfully")
    fun testCreateBuildType() {
        val project = ProjectRequestDto(projectName)
        projectId = apiClient.createResource("projects", project)
        val buildType = BuildTypeRequestDto(buildConfigurationName, Project(projectId), BuildTypeSteps(
                    arrayListOf(BuildTypeStep("MavenBuildTestStep", "Maven2", Properties(
                        arrayListOf(Property("goals", "clean test"), Property("localRepoScope", "agent"), Property("maven.path", "%teamcity.tool.maven.DEFAULT%"), Property("pomLocation", "ch-simple/pom.xml")))))))

        val buildTypeId = apiClient.createResource("buildTypes", buildType)

        assertTrue(apiClient.checkResourceExists("buildTypes/id:$buildTypeId"))
    }

    @Test
    @DisplayName("Should create a build configuration vcs root entry successfully")
    fun testCreateBuildConfigurationVcsRootEntry() {
        val project = ProjectRequestDto(projectName)
        projectId = apiClient.createResource("projects", project)
        val vcsRoot = VcsRootRequestDto(githubUrl, "jetbrains.git", Project(projectId),
            Properties(arrayListOf(Property("authMethod", "ANONYMOUS"), Property("branch", "refs/heads/master"), Property("url", githubUrl)))
        )
        val vcsRootId = apiClient.createResource("vcs-roots", vcsRoot)
        val buildType = BuildTypeRequestDto(buildConfigurationName, Project(projectId), BuildTypeSteps(
            arrayListOf(BuildTypeStep("MavenBuildTestStep", "Maven2", Properties(
                arrayListOf(Property("goals", "clean test"), Property("localRepoScope", "agent"), Property("maven.path", "%teamcity.tool.maven.DEFAULT%"), Property("pomLocation", "ch-simple/pom.xml")))))))
        val buildTypeId = apiClient.createResource("buildTypes", buildType)
        val vcsRootEntry = VcsRootEntryDto(vcsRootId, VcsRoot(vcsRootId))

        val vcsRootEntryId = apiClient.createResource("buildTypes/id:$buildTypeId/vcs-root-entries", vcsRootEntry)

        assertTrue(apiClient.checkResourceExists("buildTypes/id:$buildTypeId/vcs-root-entries/$vcsRootEntryId"))
    }

    @Test
    @DisplayName("Should execute a build configuration successfully")
    fun testExecuteBuildConfiguration() {
        val project = ProjectRequestDto(projectName)
        projectId = apiClient.createResource("projects", project)
        val vcsRoot = VcsRootRequestDto(githubUrl, "jetbrains.git", Project(projectId),
            Properties(arrayListOf(Property("authMethod", "ANONYMOUS"), Property("branch", "refs/heads/master"), Property("url", githubUrl)))
        )
        val vcsRootId = apiClient.createResource("vcs-roots", vcsRoot)
        val buildType = BuildTypeRequestDto(buildConfigurationName, Project(projectId), BuildTypeSteps(
            arrayListOf(BuildTypeStep("MavenBuildTestStep", "Maven2", Properties(
                arrayListOf(Property("goals", "clean test"), Property("localRepoScope", "agent"), Property("maven.path", "%teamcity.tool.maven.DEFAULT%"), Property("pomLocation", "ch-simple/pom.xml")))))))
        val buildTypeId = apiClient.createResource("buildTypes", buildType)
        val vcsRootEntry = VcsRootEntryDto(vcsRootId, VcsRoot(vcsRootId))
        apiClient.createResource("buildTypes/id:$buildTypeId/vcs-root-entries", vcsRootEntry)
        val buildExecutionRequest = BuildExecutionRequestDto(BuildExecutionBuildType(buildTypeId))

        val buildExecutionId = apiClient.createResource("buildQueue", buildExecutionRequest)
        val executionStatus = apiClient.getBuildExecutionStatus(buildExecutionId)

        assertEquals("SUCCESS", executionStatus)
    }
}