package eating.well.recipe.keeper.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import eating.well.recipe.keeper.app.model.Recipe
import java.io.Serializable

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val title: String,
    var prepareTime: String = "",
    val image: String = "",
    var detailImage: String = "",
    val detailUrl: String = "",
    @TypeConverters(MethodPointEntityListConverter::class)
    val method: MutableList<MethodPointEntity>,
    @TypeConverters(StringListConverter::class)
    val ingredients: MutableList<String>,
    val category: Category,
    var serving: String = ""
) : Serializable {
    companion object {
        fun fromRecipe(recipe: Recipe): RecipeEntity {
            return RecipeEntity(
                id = recipe.id,
                title = recipe.title,
                prepareTime = recipe.prepareTime,
                image = recipe.image,
                detailImage = recipe.detailImage,
                detailUrl = recipe.detailUrl,
                method = recipe.method,
                ingredients = recipe.ingredients,
                category = recipe.category,
                serving = recipe.serving
            )
        }
    }
}

enum class Category {
    PASTA, DIET, METHOD, INGREDIENTS, TIME_EASE, CUISINE
}

object StringListConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}

fun RecipeEntity.toRecipe(): Recipe {
    return Recipe(
        id = id,
        title = title,
        prepareTime = prepareTime,
        image = image,
        detailImage = detailImage,
        detailUrl = detailUrl,
        method = method,
        ingredients = ingredients,
        category = category,
        serving = serving
    )
}

