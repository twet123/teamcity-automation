package org.example.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import java.io.File

class TestDataProvider {
    inline fun <reified T> getObject(property: String): T {
        val testDataFile = File("test-data.json").readText(Charsets.UTF_8)
        val jsonData = Json.parseToJsonElement(testDataFile).jsonObject

        return Json.decodeFromJsonElement<T>(jsonData[property]!!)
    }
}