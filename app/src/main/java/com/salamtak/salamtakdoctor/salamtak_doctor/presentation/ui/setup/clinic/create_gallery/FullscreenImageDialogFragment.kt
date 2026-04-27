package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.create_gallery

import android.app.Dialog
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.salamtak.R
import com.squareup.picasso.Picasso

class FullscreenImageDialogFragment(private val imageUrl: String) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Create a new Dialog object with no title
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)

        // Set the content view to a new ImageView
        val imageView = ImageView(requireContext())
        Picasso.get().load(imageUrl).into(imageView) // Load the image from the URL using Picasso
        imageView.setOnClickListener { dialog.dismiss() } // Dismiss the dialog when the image is clicked

        // Set the ImageView as the content view of the Dialog
        dialog.setContentView(imageView)

        return dialog
    }

}

