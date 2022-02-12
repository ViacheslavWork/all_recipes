package eating.well.recipe.keeper.apps.ui.home

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import eating.well.recipe.keeper.apps.R
import eating.well.recipe.keeper.apps.model.Recipe

private const val TAG = "RecipesAdapter"

class RecipesAdapter(
    val event: MutableLiveData<RecipeListEvent> = MutableLiveData()
) :
    ListAdapter<Recipe, MoviesViewHolderList>(MovieDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolderList {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return MoviesViewHolderList(itemView)
    }

    override fun onBindViewHolder(holder: MoviesViewHolderList, position: Int) {
        getItem(position).let { recipe -> holder.onBind(recipe) }
        holder.itemView.setOnClickListener {
            if (getItem(position).isPremium) {
                event.value = RecipeListEvent.OnClosedRecipeClick(getItem(position), position)
            } else {
                event.value = RecipeListEvent.OnOpenedRecipeClick(getItem(position), position)
            }
        }
    }
}

class MoviesViewHolderList(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val receiptTV: TextView = itemView.findViewById(R.id.tv_receipt_name)
    private val receiptImage: ImageView = itemView.findViewById(R.id.iv_receipt_card)
    private val premiumImage: ImageView = itemView.findViewById(R.id.premium_label_iv)

    companion object {
        private val imageOption = RequestOptions()
            .placeholder(R.drawable.place_holder)
            .fallback(R.drawable.place_holder)
    }

    fun onBind(recipe: Recipe) {
        receiptTV.text = recipe.title
        if (recipe.isPremium) {
            makeClosed()
        } else {
            makeOpen()
        }

        val bigImage = recipe.detailImage.split("/")
        val smallImage = recipe.image.split("/")
        Glide.with(itemView.context)
            .load(Uri.parse("file:///android_asset/small_images/${smallImage[smallImage.size - 1]}"))
            .error("file:///android_asset/big_images/${bigImage[bigImage.size - 1]}")
            .apply(imageOption)
            .into(receiptImage)
    }

    private fun makeClosed() {
        receiptTV.alpha = 0.5F
        receiptImage.alpha = 0.5F
        premiumImage.visibility = View.VISIBLE
    }

    private fun makeOpen() {
        receiptTV.alpha = 1.0F
        receiptImage.alpha = 1.0F
        premiumImage.visibility = View.GONE
    }
}


private class MovieDiffCallback : DiffUtil.ItemCallback<Recipe>() {
    override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean =
        (oldItem.id == newItem.id)

    override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean =
        (oldItem == newItem)
}
