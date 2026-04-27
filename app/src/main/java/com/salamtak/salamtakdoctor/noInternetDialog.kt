package com.salamtak.salamtakdoctor

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import com.salamtak.R

object noConnectivityDialog {
    fun noInternetDialogFragment(mContext: Context) {

        val dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_no_internet)

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val dialogButton: TextView = dialog.findViewById(R.id.tv_refresh) as TextView
        dialogButton.setOnClickListener(View.OnClickListener { dialog.dismiss() })

        dialog.show()

    }
}