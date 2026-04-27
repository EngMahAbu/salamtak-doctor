package com.salamtak.salamtakdoctor.adapter


import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salamtak.databinding.ItemLayoutVideosBinding
import com.salamtak.domain.entities.response.HomeAdsData


class VideosAdapter(val del:IDelete) : ListAdapter<HomeAdsData, VideosAdapter.ViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            ItemLayoutVideosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = getItem(position)
        playVideo(holder.binding.youtubePlayerView,model.VideoLink)
        holder.binding.ivHome.setOnClickListener {
             del.deleteVideo(model.Id)
        }
        holder.binding.youtube.setOnClickListener {
           playVideo(holder.binding.youtubePlayerView,model.VideoLink)
           // del.playVideo(model.VideoLink)
        }


    }

    class ViewHolder(itemBinding: ItemLayoutVideosBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        internal val binding: ItemLayoutVideosBinding = itemBinding
    }


    private class DiffCallback : DiffUtil.ItemCallback<HomeAdsData>() {
        override fun areItemsTheSame(oldItem: HomeAdsData, newItem: HomeAdsData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: HomeAdsData, newItem: HomeAdsData): Boolean {
            return true
        }
    }
    private fun playVideo(view: VideoView,videoUrl:String){
        val uri = Uri.parse(videoUrl)
        view.setVideoURI(uri)
        val mediaController = MediaController(view.context)
        mediaController.setMediaPlayer(view)
        view.setMediaController(mediaController)
        mediaController.setAnchorView(view)
        view.start()
    }

     interface IDelete{
        fun deleteVideo(videoId:Int)
        fun playVideo(videoUrl:String)
    }




}