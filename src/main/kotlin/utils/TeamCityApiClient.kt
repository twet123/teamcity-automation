package org.example.utils

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.headers
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking

class TeamCityApiClient(private val client: HttpClient) {
    private val config = Config()

    fun createResource(resource: String, body: Any): String {
        val response = httpRequest(HttpMethod.Post, resource, body)

        return runBlocking {
            return@runBlocking JsonParser.extractValue("id", response.body())
        }
    }

    fun checkResourceExists(resource: String): Boolean {
        return httpRequest(HttpMethod.Get, resource).status.value == 200
    }

    fun deleteResource(resource: String): Boolean {
        return httpRequest(HttpMethod.Delete, resource).status.value == 200
    }

    fun getBuildExecutionStatus(buildExecutionId: String): String {
        var state: String? = null

        while (state == null || state != "finished") {
            val response = httpRequest(HttpMethod.Get, "buildQueue/id:$buildExecutionId")

            runBlocking {
                state = JsonParser.extractValue("state", response.body())
                println(state)
            }

            Thread.sleep(2000)
        }

        val response = httpRequest(HttpMethod.Get, "builds/id:$buildExecutionId")

        return runBlocking {
            return@runBlocking JsonParser.extractValue("status", response.body())
        }
    }

    private fun httpRequest(httpMethod: HttpMethod, resource: String, body: Any? = null): HttpResponse {
        println(config.baseUrl + resource)
        val response = runBlocking {
            client.request(config.baseUrl + resource) {
                method = httpMethod
                headers {
                    append(HttpHeaders.Accept, "application/json")
                    append(HttpHeaders.Authorization, "Bearer " + config.apiKey)
                    append(HttpHeaders.ContentType, "application/json")
                }
                setBody(body)
            }
        }

        return response
    }
}