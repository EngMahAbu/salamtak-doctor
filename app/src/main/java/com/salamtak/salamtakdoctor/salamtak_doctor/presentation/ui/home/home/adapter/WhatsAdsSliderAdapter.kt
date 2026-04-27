package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.salamtak.databinding.ItemWhatsAdsBanerSliderBinding

class WhatsAdsSliderAdapter(
    private val context: Context,
    private val videoUrls: List<String>
) : PagerAdapter() {

    override fun isViewFromObject(view: View, obj: Any): Boolean = (view == obj)

    override fun getCount(): Int = videoUrls.size

    @SuppressLint("InflateParams")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        // ✅ استخدمنا ViewBinding بدل findViewById
        val binding = ItemWhatsAdsBanerSliderBinding.inflate(
            LayoutInflater.from(context),
            container,
            false
        )

        val playerView = binding.thirdPartyPlayerView
        playerView.enableAutomaticInitialization = false

        // خيارات مشغل YouTube
        val options = IFramePlayerOptions.Builder(binding.root.context)
            .controls(1) // 1 = إظهار أدوات التحكم (play/pause/fullscreen)
            .build()

        val listener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val videoId = extractVideoId(videoUrls[position])
                youTubePlayer.cueVideo(videoId, 0f)
            }
        }

        // تهيئة المشغل
        playerView.initialize(listener, options)

        // إضافة العرض إلى الـ ViewPager
        (container as ViewPager).addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        (container as ViewPager).removeView(obj as View)
    }

    /**
     * 🔹 استخراج videoId من رابط YouTube (سواء كان رابط كامل أو ID فقط)
     */
    private fun extractVideoId(url: String): String {
        return when {
            url.contains("v=") -> url.substringAfter("v=").substringBefore("&")
            url.contains("youtu.be/") -> url.substringAfter("youtu.be/").substringBefore("?")
            else -> url
        }
    }
}
