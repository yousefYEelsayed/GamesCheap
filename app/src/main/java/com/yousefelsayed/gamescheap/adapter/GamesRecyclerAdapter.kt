package com.yousefelsayed.gamescheap.adapter

import android.graphics.Paint
import android.util.Log
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

class GamesRecyclerAdapter(private val gamesList: List<GamesItem>): RecyclerView.Adapter<GamesRecyclerAdapter.ViewHolder>() {

    var onItemClick: ((GamesItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesRecyclerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.games_recycler_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: GamesRecyclerAdapter.ViewHolder, position: Int) {
        val itemViewModel = gamesList[position]
        holder.viewLayout.doOnLayout {
            Glide.with(holder.gameImage)
                .load(itemViewModel.thumb.replace("capsule_sm_120.jpg?","header.jpg?"))
                .apply(RequestOptions().override(holder.viewLayout.width,holder.viewLayout.height))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .placeholder(R.color.imageLoadingColor)
                .error(R.drawable.no_image_found)
                .into(holder.gameImage)

        }
        holder.gameTitle.text = itemViewModel.title
        holder.gameImage.setImageDrawable(null)
        if (itemViewModel.salePrice == "0.00") holder.gamePrice.text = holder.itemView.context.getString(R.string.Free_String)
        else holder.gamePrice.text = holder.itemView.context.getString(R.string.priceText,itemViewModel.salePrice)
        holder.oldPrice.text = holder.itemView.context.getString(R.string.priceText,itemViewModel.normalPrice)
        holder.oldPrice.paintFlags = holder.oldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        val percentage = (itemViewModel.salePrice.toDouble() / itemViewModel.normalPrice.toDouble()) * 100
        if (percentage.toString() == "0.0") holder.gamePercentageSaved.text = holder.itemView.context.getString(R.string.hundred_percent)
        else holder.gamePercentageSaved.text = holder.itemView.context.getString(R.string.percentageText,percentage.toInt().toString())

    }
    override fun getItemCount(): Int {
        return gamesList.size
    }
    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        if(itemCount > 120){
            Log.d("Debug","ViewRecycled Cleared")
            Glide.with(holder.gameImage).clear(holder.gameImage)
        }
    }


    inner class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(gamesList[adapterPosition])
            }
        }
        val gameImage: ImageView = itemView.findViewById(R.id.gameImageView)
        val gameTitle: TextView = itemView.findViewById(R.id.gameTitle)
        val gamePrice: TextView = itemView.findViewById(R.id.gamePrice)
        val oldPrice: TextView = itemView.findViewById(R.id.oldPriceTextView)
        val gamePercentageSaved: TextView = itemView.findViewById(R.id.gamePercentageSaved)
        val viewLayout: ConstraintLayout = itemView.findViewById(R.id.gameMainLayout)
    }

}