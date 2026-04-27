package com.salamtak.salamtakdoctor.salamtak_doctor.showImage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.salamtak.R
import com.salamtak.core.localDataBase.UserData
import com.salamtak.databinding.FragmentShowImageBinding
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.ImageUtil

class ShowImageFragment : Fragment() {

    private lateinit var binding: FragmentShowImageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val image = arguments?.getString("Image") ?: ""

        binding = FragmentShowImageBinding.inflate(inflater, container, false)

        ImageUtil.loadPicture(binding.fullImage, image)

        return binding.root

    }

}