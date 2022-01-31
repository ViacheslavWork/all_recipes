package eating.well.recipe.keeper.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import eating.well.recipe.keeper.app.data.database.entity.RecipeEntity
import eating.well.recipe.keeper.app.data.database.entity.Recipes
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*


class FileIOManager(private val context: Context) {
    private val TAG = "FileIOManager"
    private var imageCounter: Int = 0
    fun writeToFile(recipes: List<RecipeEntity>, fileName: String) {
        val path = context.getExternalFilesDir(null)
        val letDirectory = File(path, "LET")
        letDirectory.mkdirs()
        val file = File(letDirectory, fileName)
        val fos = FileOutputStream(file)
        val oos = ObjectOutputStream(fos)
        oos.writeObject(Recipes(recipes))
    }

    fun writePathsToFile(recipes: List<RecipeEntity>, fileName: String) {
        val path = context.getExternalFilesDir(null)
        val letDirectory = File(path, "LET")
        letDirectory.mkdirs()
        val file = File(letDirectory, fileName)
        file.bufferedWriter().use { out ->
            recipes.forEach { recipeEntity ->
//                out.writeLn(recipeEntity.image)
                out.writeLn(recipeEntity.detailImage)
            }
        }
    }

    private fun BufferedWriter.writeLn(line: String) {
        this.write(line)
        this.newLine()
    }

    private fun countDuplicates(recipes: List<RecipeEntity>) {
        var counterDuplicates = 0
        recipes.forEachIndexed { i, recipe ->
            recipes.forEachIndexed { j, recipe2 ->
                if (i != j) {
                    if (recipe.detailImage == recipe2.detailImage) {
                        counterDuplicates++
                    }
                }
            }
        }
    }

    fun saveImage(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            writeImageToExternalStorage(
                Glide.with(context)
                    .asBitmap()
                    .load(url) // sample image
                    .placeholder(android.R.drawable.progress_indeterminate_horizontal) // need placeholder to avoid issue like glide annotations
                    .error(android.R.drawable.stat_notify_error) // need error to avoid issue like glide annotations
                    .submit()
                    .get(), url = url
            )
        }
    }

    private fun writeImageToExternalStorage(image: Bitmap, url: String) {
        val path = context.getExternalFilesDir(null)
        val letDirectory = File(path, "big_images")
        letDirectory.mkdirs()
        val file = File(letDirectory, url.split("/").let { it[it.size - 1] })
        val fos = FileOutputStream(file)
        image.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.close()
    }

    fun readFromFile(context: Context, fileName: String): List<RecipeEntity> {
        val path = context.getExternalFilesDir(null)
        val letDirectory = File(path, "LET")
        letDirectory.mkdirs()
        val file = File(letDirectory, fileName)
        val fis = FileInputStream(file)
        val ois = ObjectInputStream(fis)
        val recipes = ois.readObject() as Recipes
        return recipes.recipesList
    }

    fun readFromAssetFile(context: Context, fileName: String): List<RecipeEntity> {
        val inputStream = context.assets.open(fileName)
        val ois = ObjectInputStream(inputStream)
        val recipes = ois.readObject() as Recipes
        return recipes.recipesList
    }
}