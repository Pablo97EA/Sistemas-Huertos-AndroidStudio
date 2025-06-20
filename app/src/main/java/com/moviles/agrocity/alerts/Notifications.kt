package com.moviles.agrocity.alerts



import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

fun safeToast(context: Context?, message: String) {
    context?.let {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }
}
