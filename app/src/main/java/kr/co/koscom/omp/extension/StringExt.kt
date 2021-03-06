package kr.co.koscom.omp.extension

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


fun String?.toNumberFormat(): String {
    this?.let {
        val number = it.toLong()
        return NumberFormat.getNumberInstance(Locale.KOREA).format(number)
    }?: run {
        return "0"
    }
}

/**
 * "#" 해시태그 붙이기
 */
fun String?.toHashTag(): String{
    return this?.let {
        "#${this}"
    }?: run {
        ""
    }
}

/**
 * "Y" or "N" Boolean으로 리턴
 */
fun String?.isY(): Boolean{
    return this?.let {
        it == "Y"
    }?: run {
        false
    }
}

/**
 * Y or N 으로 리턴
 */
fun Boolean.toYN(): String {
    return if(this) "Y" else "N"
}


/**
 * 숫자에 천단위마다 콤마 넣기
 *
 * @param num Double
 * @return String
 */
fun Double.toStringNumberFormat(): String {
    var number = "0"
    try {
        val df = DecimalFormat("#")
        df.roundingMode = RoundingMode.UP
        number = df.format(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return number.toNumberFormat()
}

fun Float.toStringNumberFormat(): String {
    var number = "0"
    try {
        val df = DecimalFormat("#")
        df.roundingMode = RoundingMode.UP
        number = df.format(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return number.toNumberFormat()
}

fun Long.toStringNumberFormat(): String {
    var number = "0"
    try {
        val df = DecimalFormat("#")
        df.roundingMode = RoundingMode.UP
        number = df.format(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return number.toNumberFormat()
}

fun Int.toStringNumberFormat(): String {
    var number = "0"
    try {
        val df = DecimalFormat("#")
        df.roundingMode = RoundingMode.UP
        number = df.format(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return number.toNumberFormat()
}


/**
 * Date Format 변경 - yyyy-MM-dd hh:mm:ss -> yyyy년 MM월 dd일 hh시 mm분 ss초
 *
 * @param strDate
 * @return
 */
fun String?.toYYYYMMDDSSHHMMSS(): String {
    return this?.let {date->
        val timestamp = getTimestampFromStringFormat(this, "yyyy-MM-dd hh:mm:ss")
        return getDateFormatFromTimestamp(timestamp, "yyyy년 MM월 dd일 hh시 mm분 ss초")
    }?: ""
}

/**
 * 날짜 포맷 변경
 *
 * @param time   Timestamp
 * @param format 지정 포맷
 * @return String
 */
private fun getDateFormatFromTimestamp(time: Long, format: String): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = time
    val formatter = SimpleDateFormat(format)

    return formatter.format(calendar.time)
}

/**
 * String Date -> Timestamp
 *
 * @param strDate 날짜 문자열
 * @param format  날짜 포맷
 * @return Timestamp
 */
private fun getTimestampFromStringFormat(strDate: String, format: String): Long {
    var date = Date()
    try {
        val simpleDateFormat = SimpleDateFormat(format)
        date = simpleDateFormat.parse(strDate)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return date.time
}

