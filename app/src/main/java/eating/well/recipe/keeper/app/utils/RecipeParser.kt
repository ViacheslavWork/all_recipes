package eating.well.recipe.keeper.app.utils

import eating.well.recipe.keeper.app.data.database.entity.Category
import eating.well.recipe.keeper.app.data.database.entity.MethodPointEntity
import eating.well.recipe.keeper.app.data.database.entity.RecipeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

private const val TAG = "RecipeParser"

object RecipeParser {
    suspend fun putRecipesFun(urlOfListRecipes: String, category: Category): List<RecipeEntity> =
        withContext(Dispatchers.IO) {
            val document: Document =
                Jsoup.connect(urlOfListRecipes).get()
            val listBlock =
                document.getElementsByClass("comp mntl-taxonomysc-article-list mntl-document-card-list mntl-card-list mntl-block")
            val elements =
                listBlock[0].getElementsByClass("comp mntl-card-list-items mntl-document-card mntl-card card")
//        fillDataFromDetails(fillDataFromList(elements))
            val recipesFromList = fillDataFromList(elements = elements, category = category)
            val recipesWithDetails = fillDataFromDetails(recipesFromList)
            return@withContext recipesWithDetails
        }

    private fun fillDataFromList(elements: Elements, category: Category): List<RecipeEntity> {
        val recipes = mutableListOf<RecipeEntity>()
        for (item in elements) {
            val title = item.getElementsByClass("card__title-text").text()
            val image = item.select("img")
            val absoluteUrlImage = item.select("img").attr("src")
            val ref = item.attr("href")
            val id = item.attr("id")

            val entity =
                RecipeEntity(
                    0,
                    title = title,
                    "",
                    absoluteUrlImage,
                    "",
                    ref,
                    mutableListOf(),
                    mutableListOf(),
                    category = category,
                )


            recipes.add(entity)

        }
        return recipes.toList()
    }

    private fun fillDataFromDetails(recipesPar: List<RecipeEntity>): List<RecipeEntity> {
        val recipes = recipesPar.toMutableList()
        recipes.forEach {
            val document: Document =
                Jsoup.connect(it.detailUrl).get()

            var imageDetails = ""
            imageDetails = getDetailImageUrl(imageDetails, document)


            var totalTime = "0"
            if (document.getElementsByClass("loc total-time project-meta__total-time").size != 0) {
                totalTime = document
                    .getElementsByClass("loc total-time project-meta__total-time")[0]
                    .getElementsByClass("meta-text__data").text()
            }

            var ingredients = document
                .getElementsByClass("structured-ingredients__list-item")

            if (ingredients.isEmpty()) {
                ingredients = document
                    .getElementsByClass("simple-list__item js-checkbox-trigger ingredient text-passage")
            }
            val methodPoints = document
                .getElementsByClass("comp mntl-sc-block-group--LI mntl-sc-block mntl-sc-block-startgroup")
            val points = mutableListOf<MethodPointEntity>()
            methodPoints.forEach { element ->
                val entity = MethodPointEntity(
                    element.getElementsByClass("mntl-sc-block-subheading__text").text(),
                    element.getElementsByClass("comp mntl-sc-block mntl-sc-block-html").text()
                )
                points.add(entity)
            }
            var serving: String? = ""
            document.getElementsByClass("loc recipe-serving project-meta__recipe-serving")
                .let { elements ->
                    if (elements.size != 0) {
                        serving = elements[0]?.getElementsByClass("meta-text__data")?.text()
                    }
                }

            it.detailImage = imageDetails
            it.prepareTime = totalTime
            it.ingredients.addAll(ingredients.map { ingredient -> ingredient.text() })
            it.method.addAll(points)
            it.serving = serving ?: ""
        }

        //remove recipes without method or ingredients
        recipes.toList().forEach {
            if (it.ingredients.isEmpty() || it.method.isEmpty()) {
                recipes.remove(it)
            }
        }
        return recipes.toList()
    }

    private fun getDetailImageUrl(
        imageDetails: String,
        document: Document
    ): String {
        var imageDetails1 = imageDetails
        imageDetails1 = if (document
                .getElementsByClass("comp mntl-sc-block lifestyle-sc-block-image mntl-sc-block-image figure-landscape figure-high-res").size != 0
        ) {
            document
                .getElementsByClass("comp mntl-sc-block lifestyle-sc-block-image mntl-sc-block-image figure-landscape figure-high-res")[0]
                .getElementsByClass("img-placeholder")
                .select("img").attr("src")
        } else {
            document
                .getElementsByClass("comp article-header__media primary-media figure-wrapper--article mntl-block")[0]
                .getElementsByClass("img-placeholder")
                .select("img").attr("src")
        }
        return imageDetails1
    }
}