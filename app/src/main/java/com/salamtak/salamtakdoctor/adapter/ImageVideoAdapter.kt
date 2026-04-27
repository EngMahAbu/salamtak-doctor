package com.salamtak.salamtakdoctor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.salamtak.databinding.ItemLayoutSliderBinding
import com.salamtak.domain.entities.response.HomeAdsData

class ImageVideoAdapter(private val del: IDelete) :
    ListAdapter<HomeAdsData, ImageVideoAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemLayoutSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = getItem(position)

        if (model.IsVideo) {
            holder.binding.youtubePlayerView.visibility = View.VISIBLE
            holder.binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    extractYoutubeId(model.VideoLink)?.let { videoId ->
                        youTubePlayer.cueVideo(videoId,0f)
                        youTubePlayer.loadVideo(videoId, 0f)
                        youTubePlayer.pause()
                    }
                }
            })
        } else {
            holder.binding.youtubePlayerView.visibility = View.GONE
        }

        holder.binding.ivHome.setOnClickListener {
            del.deleteVideo(model.Id)
        }
    }


    class ViewHolder(val binding: ItemLayoutSliderBinding) :
        RecyclerView.ViewHolder(binding.root)

    private fun extractYoutubeId(url: String): String? {
        return when {
            url.contains("youtu.be/") ->
                url.substringAfter("youtu.be/").substringBefore("?")
            url.contains("watch?v=") ->
                url.substringAfter("watch?v=").substringBefore("&")
            url.contains("shorts/") ->
                url.substringAfter("shorts/").substringBefore("?")
            else -> null
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<HomeAdsData>() {
        override fun areItemsTheSame(oldItem: HomeAdsData, newItem: HomeAdsData): Boolean {
            return oldItem.Id == newItem.Id
        }

        override fun areContentsTheSame(oldItem: HomeAdsData, newItem: HomeAdsData): Boolean {
            return oldItem == newItem
        }
    }

    interface IDelete {
        fun deleteVideo(videoId: Int)
    }
}
