package kr.co.koscom.omp.extension

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

fun String.toNumberFormat(): String {
    val number = Integer.parseInt(this)
    return NumberFormat.getNumberInstance(Locale.KOREA).format(number)
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