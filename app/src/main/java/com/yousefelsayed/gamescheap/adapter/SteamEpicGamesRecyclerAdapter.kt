package com.yousefelsayed.gamescheap.adapter

import android.graphics.Paint
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
import com.yousefelsayed.gamescheap.model.GamesItem

class SteamEpicGamesRecyclerAdapter(private val mList: List<GamesItem>) : RecyclerView.Adapter<SteamEpicGamesRecyclerAdapter.ViewHolder>() {

    var onItemClick: ((GamesItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.steam_epicgames_recycler_item_design, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemViewModel = mList[position]
        holder.layout.doOnLayout {
            Glide.with(holder.gameImage)
                .load(itemViewModel.thumb.replace("capsule_sm_120.jpg?","header.jpg?"))
                .apply(RequestOptions().override(holder.layout.width,holder.layout.height))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .placeholder(R.color.imageLoadingColor)
                .error(R.drawable.no_image_found)
                .into(holder.gameImage)
        }
        holder.gameImage.setImageDrawable(null)
        holder.gameTitle.text = itemViewModel.title
        if (itemViewModel.salePrice == "0.00") holder.gameCurrentPrice.text = holder.itemView.context.getString(R.string.Free_String)
        else holder.gameCurrentPrice.text = holder.itemView.context.getString(R.string.priceText,itemViewModel.salePrice)
        holder.gameOldPrice.text = holder.itemView.context.getString(R.string.priceText,itemViewModel.normalPrice)
        holder.gameOldPrice.paintFlags = holder.gameOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        val percentage = (itemViewModel.salePrice.toDouble() / itemViewModel.normalPrice.toDouble()) * 100
        if (percentage.toString() == "0.0") holder.gamePercentageSaved.text = holder.itemView.context.getString(R.string.hundred_percent)
        else holder.gamePercentageSaved.text = holder.itemView.context.getString(R.string.percentageText,percentage.toInt().toString())
    }
    override fun getItemCount(): Int {
        return mList.size
    }
    inner class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(mList[adapterPosition])
            }
        }
        val gameImage: ImageView = itemView.findViewById(R.id.gameImageView)
        val gameTitle: TextView = itemView.findViewById(R.id.gameTitleTextView)
        val gameCurrentPrice: TextView = itemView.findViewById(R.id.currentGamePrice)
        val gameOldPrice: TextView = itemView.findViewById(R.id.oldGamePrice)
        val gamePercentageSaved: TextView = itemView.findViewById(R.id.gamePercentageSaved)
        val layout: ConstraintLayout = itemView.findViewById(R.id.gameMainLayout)
    }
}

