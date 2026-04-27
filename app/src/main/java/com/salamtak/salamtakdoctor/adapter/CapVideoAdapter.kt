package com.salamtak.salamtakdoctor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.salamtak.databinding.ItemLayoutCapsBinding
import com.salamtak.domain.entities.response.Blogs

class CapVideoAdapter: ListAdapter<Blogs, CapVideoAdapter.ViewHolder>(CapVideoAdapter.DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            ItemLayoutCapsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = getItem(position)
        val videoid = model.image?.split("=")?.toTypedArray()
            holder.binding.youtubePlayerView.visibility = View.VISIBLE
            holder.binding.youtubePlayerView.apply {
                this.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                        val videoId = videoid?.get(1)
                        youTubePlayer.cueVideo(videoId!!,0f)
                        youTubePlayer.loadVideo(videoId, 0f)
                        youTubePlayer.pause()

                    }
                })
            }


    }

    class ViewHolder(itemBinding: ItemLayoutCapsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemLayoutCapsBinding = itemBinding
    }


    private class DiffCallback : DiffUtil.ItemCallback<Blogs>() {
        override fun areItemsTheSame(oldItem: Blogs, newItem: Blogs): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Blogs, newItem: Blogs): Boolean {
            return true
        }
    }



}