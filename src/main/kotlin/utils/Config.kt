package org.example.utils

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

        baseUrl = JsonParser.extractValue("BASE_URL", configFileText)
        apiKey = JsonParser.extractValue("API_KEY", configFileText)
    }
}