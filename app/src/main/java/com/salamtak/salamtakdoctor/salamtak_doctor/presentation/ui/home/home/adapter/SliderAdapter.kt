package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.salamtak.R
import com.salamtak.core.usecase.UseCaseModule
import com.salamtak.databinding.ItemBanerSliderBinding
import com.salamtak.domain.entities.response.HomeAdsData

class SliderAdapter(
    private val context: Context,
    private val list: ArrayList<HomeAdsData>
) : PagerAdapter() {

    override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj
    override fun getCount(): Int = list.size

    @SuppressLint("InflateParams")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = ItemBanerSliderBinding.inflate(
            LayoutInflater.from(context),
            container,
            false
        )

        val item = list[position]
        val image = binding.sliderImage
        val youtubeView = binding.thirdPartyPlayerView

        if (!item.IsVideo) {
            // عرض الصورة فقط
            youtubeView.visibility = View.GONE
            image.visibility = View.VISIBLE

            Glide.with(context)
                .load(item.VideoLink)
                .placeholder(R.drawable.app_logo)
                .into(image)
        } else {
            // عرض فيديو يوتيوب
            image.visibility = View.GONE
            youtubeView.visibility = View.VISIBLE

            youtubeView.enableAutomaticInitialization = false

            val listener = object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    val videoId = extractVideoId(item.VideoLink)
                    youTubePlayer.cueVideo(videoId, 0f)
                }
            }

            val options = IFramePlayerOptions.Builder(context)
                .controls(1) // إظهار أزرار التحكم الافتراضية
                .build()

            youtubeView.initialize(listener, options)
        }

        (container as ViewPager).addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        (container as ViewPager).removeView(obj as View)
    }

    private fun extractVideoId(url: String): String {
        return when {
            url.contains("v=") -> url.substringAfter("v=").substringBefore("&")
            url.contains("youtu.be/") -> url.substringAfter("youtu.be/").substringBefore("?")
            else -> url
        }
    }
}
