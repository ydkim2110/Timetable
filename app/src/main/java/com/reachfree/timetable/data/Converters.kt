package com.reachfree.timetable.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.reachfree.timetable.data.model.Subject

class Converters {

    @TypeConverter
    fun fromDays(value: List<Subject.Days>): String {
        val gson = Gson()
        val typeToken = object : TypeToken<List<Subject.Days>>(){}.type
        return gson.toJson(value, typeToken)
    }

    @TypeConverter
    fun toDays(value: String): List<Subject.Days> {
        val gson = Gson()
        val typeToken = object : TypeToken<List<Subject.Days>>(){}.type
        return gson.fromJson(value, typeToken)
    }
}