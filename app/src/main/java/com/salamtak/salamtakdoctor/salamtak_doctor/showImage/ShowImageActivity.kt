package com.salamtak.salamtakdoctor.salamtak_doctor.showImage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.salamtak.R
import com.salamtak.databinding.ActivityMainBinding
import com.salamtak.databinding.ActivityShowImageBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.ImageUtil

class ShowImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowImageBinding
    var image = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_image)

        intent.extras?.let {
            image = it.getString("Image")!!
        }

        ImageUtil.loadPicture(binding.fullImage, image)

    }
}