import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import org.example.dtos.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TeamCityTest {
    private val projectName = "SimpleMavenSample"
    private val projectId = "SimpleMavenSample"
    private val dotenv = dotenv()
    private val githubUrl = "https://github.com/mkjetbrains/SimpleMavenSample"
    private val vcsRootId = "JetBrainsMavenSample"
    private val buildConfigurationId = "MavenBuildTest"
    private val buildConfigurationName = "MavenBuildTest"

//    @AfterAll
//    fun cleanup() {
//        deleteProject()
//    }

    @Test
    fun testCreateProject() {
        createProject()
        assertEquals(getProject().status.value, 200)
        createVcsRoot()
        assertEquals(getVcsRoot().status.value, 200)
        createBuildConfiguration()
        assertEquals(getBuildConfiguration().status.value, 200)
        createBuildConfigurationVcsRootEntry()
        assertEquals(getBuildConfigurationVcsRootEntry().status.value, 200)
        assertEquals(executeBuildConfiguration().status.value, 200)
    }

    private fun createProject() : HttpResponse {
        val client = HttpClient() {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = runBlocking {
            client.post(dotenv["BASE_URL"] + "projects") {
                headers {
                    append(HttpHeaders.Accept, "application/json")
                    append(HttpHeaders.Authorization, "Bearer " + dotenv["API_KEY"])
                    append(HttpHeaders.ContentType, "application/json")
                }
                setBody(ProjectRequestDto(projectName, projectId))
            }
        }

        client.close()
        return response
    }

    private fun getProject(): HttpResponse {
        val client = HttpClient()
        val response = runBlocking {
            client.get(dotenv["BASE_URL"] + "projects/id:" + projectId) {
                headers {
                    append(HttpHeaders.Accept, "application/json")
                    append(HttpHeaders.Authorization, "Bearer " + dotenv["API_KEY"])
                }
            }
        }

        client.close()
        return response
    }

    private fun deleteProject(): HttpResponse {
        val client = HttpClient()
        val response = runBlocking {
            client.delete(dotenv["BASE_URL"] + "projects/id:" + projectId) {
                headers {
                    append(HttpHeaders.Accept, "application/json")
                    append(HttpHeaders.Authorization, "Bearer " + dotenv["API_KEY"])
                }
            }
        }

        client.close()
        return response
    }

    private fun createVcsRoot(): HttpResponse {
        val vcsRoot = VcsRootRequestDto(vcsRootId, githubUrl, "jetbrains.git", Project(projectId),
            Properties(arrayListOf(Property("authMethod", "ANONYMOUS"), Property("branch", "refs/heads/master"), Property("url", githubUrl)))
        )

        val client = HttpClient() {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = runBlocking {
            client.post(dotenv["BASE_URL"] + "vcs-roots") {
                headers {
                    append(HttpHeaders.Accept, "application/json")
                    append(HttpHeaders.Authorization, "Bearer " + dotenv["API_KEY"])
                    append(HttpHeaders.ContentType, "application/json")
                }
                setBody(vcsRoot)
            }
        }

        client.close()
        return response
    }

    private fun getVcsRoot(): HttpResponse {
        val client = HttpClient()
        val response = runBlocking {
            client.get(dotenv["BASE_URL"] + "vcs-roots/id:" + vcsRootId) {
                headers {
                    append(HttpHeaders.Accept, "application/json")
                    append(HttpHeaders.Authorization, "Bearer " + dotenv["API_KEY"])
                }
            }
        }

        client.close()
        return response
    }

    private fun createBuildConfiguration(): HttpResponse {
        val client = HttpClient() {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = runBlocking {
            client.post(dotenv["BASE_URL"] + "buildTypes") {
                headers {
                    append(HttpHeaders.Accept, "application/json")
                    append(HttpHeaders.Authorization, "Bearer " + dotenv["API_KEY"])
                    append(HttpHeaders.ContentType, "application/json")
                }
                setBody(BuildTypeRequestDto(buildConfigurationId, buildConfigurationName, Project(projectId), BuildTypeSteps(
                    arrayListOf(BuildTypeStep("MavenBuildTestStep", "Maven2", Properties(
                        arrayListOf(Property("goals", "clean test"), Property("localRepoScope", "agent"), Property("maven.path", "%teamcity.tool.maven.DEFAULT%"), Property("pomLocation", "ch-simple/pom.xml"))
                    )))
                )))
            }
        }

        client.close()
        return response
    }

    private fun getBuildConfiguration(): HttpResponse {
        val client = HttpClient()
        val response = runBlocking {
            client.get(dotenv["BASE_URL"] + "buildTypes/id:" + buildConfigurationId) {
                headers {
                    append(HttpHeaders.Accept, "application/json")
                    append(HttpHeaders.Authorization, "Bearer " + dotenv["API_KEY"])
                }
            }
        }

        client.close()
        return response
    }

    private fun createBuildConfigurationVcsRootEntry(): HttpResponse {
        val client = HttpClient() {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = runBlocking {
            client.post(dotenv["BASE_URL"] + "buildTypes/id:$buildConfigurationId/vcs-root-entries") {
                headers {
                    append(HttpHeaders.Accept, "application/json")
                    append(HttpHeaders.Authorization, "Bearer " + dotenv["API_KEY"])
                    append(HttpHeaders.ContentType, "application/json")
                }
                setBody(VcsRootEntryDto(vcsRootId, VcsRoot(vcsRootId)))
            }
        }

        client.close()
        return response
    }

    private fun getBuildConfigurationVcsRootEntry(): HttpResponse {
        val client = HttpClient()
        val response = runBlocking {
            client.get(dotenv["BASE_URL"] + "buildTypes/id:$buildConfigurationId/vcs-root-entries/$vcsRootId") {
                headers {
                    append(HttpHeaders.Accept, "application/json")
                    append(HttpHeaders.Authorization, "Bearer " + dotenv["API_KEY"])
                }
            }
        }

        client.close()
        return response
    }

    private fun executeBuildConfiguration(): HttpResponse {
        val client = HttpClient() {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = runBlocking {
            client.post(dotenv["BASE_URL"] + "buildQueue") {
                headers {
                    append(HttpHeaders.Accept, "application/json")
                    append(HttpHeaders.Authorization, "Bearer " + dotenv["API_KEY"])
                    append(HttpHeaders.ContentType, "application/json")
                }
                setBody(BuildExecutionRequestDto(BuildExecutionBuildType(buildConfigurationId)))
            }
        }

        client.close()
        return response
    }
}