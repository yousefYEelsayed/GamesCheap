package com.yousefelsayed.gamescheap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.yousefelsayed.gamescheap.R
import com.yousefelsayed.gamescheap.model.SearchModelItem

class SearchResultsRecyclerAdapter(private var mList: List<SearchModelItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val firstItemViewHolder = 0
    private val itemsViewHolder = 1
    var onItemClick: ((SearchModelItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == itemsViewHolder){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.search_result_recycler_item, parent, false)
            ItemsViewHolder(view)
        }else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.search_first_recycler_item, parent, false)
            FirstItemViewHolder(view)
        }
    }
    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder.itemViewType == itemsViewHolder){
            val itemsViewHolder: ItemsViewHolder = holder as ItemsViewHolder
            Glide.with(itemsViewHolder.gameImage).clear(itemsViewHolder.gameImage)
        }
    }
    override fun getItemViewType(position: Int): Int {
        return if (position == 0) firstItemViewHolder
        else itemsViewHolder
    }
    override fun onBindViewHolder(baseHolder: RecyclerView.ViewHolder, position: Int) {
        val itemViewModel = mList[position]
        if (getItemViewType(position) == itemsViewHolder){
            val itemsViewHolder: ItemsViewHolder = baseHolder as ItemsViewHolder
            itemsViewHolder.gameImage.visibility = View.VISIBLE
            itemsViewHolder.gameImage.doOnLayout {
                Glide.with(itemsViewHolder.gameImage)
                    .load(itemViewModel.thumb.replace("capsule_sm_120.jpg?","header.jpg?"))
                    .apply(RequestOptions().override(itemsViewHolder.gameImage.width,itemsViewHolder.gameImage.height))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .placeholder(R.color.imageLoadingColor)
                    .error(R.drawable.no_image_found)
                    .into(itemsViewHolder.gameImage)
            }
            itemsViewHolder.gameTitle.text = itemViewModel.external
            if (itemViewModel.cheapest == "0.00") itemsViewHolder.gameCurrentPrice.text = baseHolder.itemView.context.getString(R.string.Free_String)
            else itemsViewHolder.gameCurrentPrice.text = baseHolder.itemView.context.getString(R.string.priceText,itemViewModel.cheapest)
        }else{
            val firstItemViewHolder: FirstItemViewHolder = baseHolder as FirstItemViewHolder
            firstItemViewHolder.firstTextView.text = itemViewModel.external
        }
    }
    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ItemsViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(mList[adapterPosition])
            }
        }
        val gameImage: ImageView = itemView.findViewById(R.id.gameImageView)
        val gameTitle: TextView = itemView.findViewById(R.id.gameTitleTextView)
        val gameCurrentPrice: TextView = itemView.findViewById(R.id.currentGamePrice)
        val layout: ConstraintLayout = itemView.findViewById(R.id.gameMainLayout)
    }
    inner class FirstItemViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
        val firstTextView: TextView = itemView.findViewById(R.id.firstResultItemTextLayout)
    }

}