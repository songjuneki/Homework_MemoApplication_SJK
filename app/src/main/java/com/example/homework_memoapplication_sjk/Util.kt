package com.example.homework_memoapplication_sjk

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale

class Util {
    companion object {

        // LocalDateTime 객체를 한글로 출력하는 메서드
        // yyyy.MM.dd HH:mm:ss 형식으로 출력한다.
        // 예) 1998.01.05 12:30:59
        fun dateString(date: LocalDateTime): String {
            return SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA).format(date)
        }
    }
}