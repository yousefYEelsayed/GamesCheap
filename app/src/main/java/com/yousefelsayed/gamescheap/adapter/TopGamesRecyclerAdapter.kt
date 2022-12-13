package com.yousefelsayed.gamescheap.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.yousefelsayed.gamescheap.R
import com.yousefelsayed.gamescheap.model.GamesItem

class TopGamesRecyclerAdapter(private val mList: List<GamesItem>): RecyclerView.Adapter<TopGamesRecyclerAdapter.ViewHolder>() {

    var onItemClick: ((GamesItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.top_games_recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemViewModel = mList[position]
        holder.gameImageView.doOnLayout {
            Picasso.get()
                .load(itemViewModel.thumb.replace("capsule_sm_120.jpg?","header.jpg?"))
                .resize(holder.gameImageView.width,holder.gameImageView.height)
                .centerCrop()
                .into(holder.gameImageView)
        }
        holder.gameImageView.setImageDrawable(null)
        // sets the text to the textview from our itemHolder class
        holder.gameTitle.text = itemViewModel.title
        if (itemViewModel.salePrice == "0.00"){
            holder.gamePrice.text = holder.itemView.context.getString(R.string.Free_String)
        }else {
            holder.gamePrice.text = holder.itemView.context.getString(R.string.priceText,itemViewModel.salePrice)
        }
        holder.oldPrice.text = holder.itemView.context.getString(R.string.priceText,itemViewModel.normalPrice)
        holder.oldPrice.paintFlags = holder.oldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }
    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
        val gameImageView: ImageView = itemView.findViewById(R.id.gameImageView)
        val gameTitle: TextView = itemView.findViewById(R.id.gameTitle)
        val gamePrice: TextView = itemView.findViewById(R.id.gamePrice)
        val oldPrice: TextView = itemView.findViewById(R.id.oldPriceTextView)
        private val viewButton: Button = itemView.findViewById(R.id.viewButton)
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(mList[adapterPosition])
            }
            viewButton.setOnClickListener {
                onItemClick?.invoke(mList[adapterPosition])
            }
        }
    }
}