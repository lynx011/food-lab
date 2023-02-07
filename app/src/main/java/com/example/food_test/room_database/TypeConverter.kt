package com.example.food_test.room_database

import androidx.room.TypeConverter
import androidx.room.TypeConverters

@TypeConverters
class MealTypeConverter {

    @TypeConverter
    fun anyToString(attribute : Any?) : String {
        if(attribute == null){
            return ""
        }
        return attribute.toString()
    }

    @TypeConverter
    fun stringToAny(attribute: String?) : Any {
        if (attribute == null) {
            return ""
        }
        return attribute
    }
}