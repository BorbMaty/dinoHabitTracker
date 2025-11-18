package com.dinoHabitTracker.app.data.remote

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeAdapter :
    JsonDeserializer<LocalDateTime>,
    JsonSerializer<LocalDateTime> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDateTime {
        val s = json.asString
        return try {
            LocalDateTime.parse(s, DateTimeFormatter.ISO_DATE_TIME)
        } catch (_: Exception) {
            // ha a végén "Z" van vagy apró eltérés
            LocalDateTime.parse(s.replace("Z", ""))
        }
    }

    override fun serialize(
        src: LocalDateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.format(DateTimeFormatter.ISO_DATE_TIME))
    }
}
