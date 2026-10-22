package com.salamtak.salamtakdoctor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
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
        holder.binding.youtubePlayerView.visibility = View.VISIBLE
        holder.cue(videoIdOf(model.image))
    }

    // Supports "...watch?v=ID&..." links as before
    private fun videoIdOf(url: String?): String? =
        url?.split("=")?.getOrNull(1)?.substringBefore("&")?.takeIf { it.isNotBlank() }

    class ViewHolder(itemBinding: ItemLayoutCapsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemLayoutCapsBinding = itemBinding

        private var player: YouTubePlayer? = null
        private var pendingVideoId: String? = null
        private var cuedVideoId: String? = null

        init {
            // One listener per player view; adding it on every bind stacked listeners
            // and made each recycled player reload its video again
            binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    player = youTubePlayer
                    pendingVideoId?.let { cue(it) }
                }
            })
        }

        fun cue(videoId: String?) {
            if (videoId == null) return
            val ready = player
            if (ready == null) {
                pendingVideoId = videoId
                return
            }
            // Already showing this video: keep it as is instead of reloading
            if (videoId == cuedVideoId) return
            // cueVideo shows the thumbnail without buffering; play starts on tap
            ready.cueVideo(videoId, 0f)
            cuedVideoId = videoId
            pendingVideoId = null
        }
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
