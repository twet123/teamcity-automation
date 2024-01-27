package org.example.utils

import io.ktor.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import java.io.File

class Config {
    var baseUrl: String? = null
        private set
        get() {
            if (field == null)
                parseConfigFile()
            return field!!
        }

    var apiKey: String? = null
        private set
        get() {
            if (field == null)
                parseConfigFile()
            return field!!
        }

    private fun parseConfigFile() {
        val configFileText: String = File("config.json").readText(Charsets.UTF_8)
        val jsonConfig: Map<String, JsonElement> = Json.parseToJsonElement(configFileText).jsonObject

        baseUrl = extractValue("BASE_URL", jsonConfig)
        apiKey = extractValue("API_KEY", jsonConfig)
    }

    private fun extractValue(property: String, jsonMap: Map<String, JsonElement>): String {
        if (jsonMap.containsKey(property))
            return jsonMap[property].toString()
        else
            throw JsonConvertException("There is no $property property in config file!")
    }
}