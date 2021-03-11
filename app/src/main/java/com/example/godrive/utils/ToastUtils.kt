package com.example.godrive.utils

import android.content.Context
import android.widget.Toast


class ToastUtils {
    companion object {
        fun showLongText(context: Context?, text: String?) {
            Toast.makeText(context, text, Toast.LENGTH_LONG).show()
        }

        fun showLongText(context: Context?, resId: Int) {
            Toast.makeText(context, resId, Toast.LENGTH_LONG).show()
        }

        fun showShortText(context: Context?, text: String?) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }

        fun showShortText(context: Context?, resId: Int) {
            Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
        }
    }
}
