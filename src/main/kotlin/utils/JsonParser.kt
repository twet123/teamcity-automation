package org.example.utils

import io.ktor.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

class JsonParser {
    companion object {
        fun extractValue(property: String, json: String): String {
            val jsonMap = Json.parseToJsonElement(json).jsonObject

            if (jsonMap.containsKey(property))
                return jsonMap[property].toString().replace("\"", "")
            else
                throw JsonConvertException("There is no $property property in JSON!")
        }
    }
}