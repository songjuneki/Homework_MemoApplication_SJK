package com.example.homework_memoapplication_sjk

import android.content.Context
import android.os.SystemClock
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread

class Util {

    companion object {
        // 포커스를 주고 키보드를 올려주는 메서드
        fun showSoftKey(view: View, context: Context) {
            // 포커스를 준다
            view.requestFocus()

            thread {
                SystemClock.sleep(1000)
                val inputMethodManager = context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(view, 0)
            }
        }

        // 키보드를 내려준다
        fun hideSoftInput(activity: AppCompatActivity) {
            // 현재 포커스를 가지고 있는 view 가 있다면 키보드를 내린다
            if (activity.window.currentFocus != null) {
                val inputMethodManager = activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(activity.window.currentFocus?.windowToken, 0)
            }
        }
    }
}