import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converter {

    private val gson = Gson()

    @TypeConverter
    fun fromList(list: List<String>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromListList(value: List<List<String>>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toListList(value: String): List<List<String>> {
        val type = object : TypeToken<List<List<String>>>() {}.type
        return Gson().fromJson(value, type)
    }
}
