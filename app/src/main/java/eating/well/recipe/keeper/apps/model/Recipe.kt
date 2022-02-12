package eating.well.recipe.keeper.apps.model

import eating.well.recipe.keeper.apps.data.database.entity.Category
import eating.well.recipe.keeper.apps.data.database.entity.MethodPointEntity
import java.io.Serializable

data class Recipe(
    var id: Long = 0,
    val title: String,
    var prepareTime: String = "",
    val image: String = "",
    var detailImage: String = "",
    val detailUrl: String = "",
    val method: MutableList<MethodPointEntity>,
    val ingredients: MutableList<String>,
    val category: Category,
    var serving: String = "",
    var isPremium: Boolean = false
) : Serializable