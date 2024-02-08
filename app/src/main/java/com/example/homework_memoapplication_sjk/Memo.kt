package com.example.homework_memoapplication_sjk

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Memo(
    val title: String,
    val content: String,
    val date: String
): Parcelable