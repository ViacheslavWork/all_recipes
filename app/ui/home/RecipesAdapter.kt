package com.android.app.ui.home

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
import com.android.app.R
import com.android.app.data.database.entity.RecipeEntity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

private const val TAG = "RecipesAdapter"

class RecipesAdapter(
    val event: MutableLiveData<RecipeListEvent> = MutableLiveData()
) :
    ListAdapter<RecipeEntity, MoviesViewHolderList>(MovieDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolderList {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return MoviesViewHolderList(itemView)
    }

    override fun onBindViewHolder(holder: MoviesViewHolderList, position: Int) {
        getItem(position).let { recipeEntity -> holder.onBind(recipeEntity) }
        holder.itemView.setOnClickListener {
            event.value = RecipeListEvent.OnRecipeClick(getItem(position))
        }
    }
}

class MoviesViewHolderList(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val receiptTV: TextView = itemView.findViewById(R.id.tv_receipt_name)
    private val receiptImage: ImageView = itemView.findViewById(R.id.iv_receipt_card)

    companion object {
        private val imageOption = RequestOptions()
            .placeholder(R.drawable.bg)
            .fallback(R.drawable.bg)
    }

    fun onBind(recipeEntity: RecipeEntity) {
        receiptTV.text = recipeEntity.title
        val componentsOfAds = recipeEntity.image.split("/")
//        receiptTV.text = "recipeEntity.title"
        Glide.with(itemView.context)
            .load(Uri.parse("file:///android_asset/small_images/${componentsOfAds[componentsOfAds.size - 1]}"))
//            .apply(imageOption)
            .into(receiptImage)
//        Log.i(TAG, "image ads: ${componentsOfAds[componentsOfAds.size - 1]}");
    }
}


private class MovieDiffCallback : DiffUtil.ItemCallback<RecipeEntity>() {
    override fun areItemsTheSame(oldItem: RecipeEntity, newItem: RecipeEntity): Boolean =
        (oldItem.id == newItem.id)

    override fun areContentsTheSame(oldItem: RecipeEntity, newItem: RecipeEntity): Boolean =
        (oldItem == newItem)
}
