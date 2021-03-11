package com.example.godrive.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.godrive.MainActivity


class ActivityUtils {
    companion object {
        fun startActivity(
            packageContext: Context,
            activityClass: Class<*>?
        ) {
            val intent = Intent(packageContext, activityClass)
            packageContext.startActivity(intent)
        }

        fun parse(context: Context?): Activity? {
            return context as? Activity
        }

        fun parse(activity: Activity): Context? {
            return activity.applicationContext
        }

        fun toMainActivity(context: Context?): MainActivity? {
            return context as? MainActivity
        }
    }
}
