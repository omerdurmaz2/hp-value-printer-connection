package com.hp.fcpconnection

import android.util.Log
import java.lang.Math.pow
import java.math.RoundingMode
import java.text.DecimalFormat

private val df = DecimalFormat("###.##")

fun Double.round(digit: Double): Double {
    return kotlin.math.round(this * pow(10.0, digit)) / 100
}

fun Double.percToDep(): Int {
    return when (this) {
        0.0 -> 1
        1.0 -> 2
        8.0 -> 3
        18.0 -> 4
        25.0 -> 5
        else -> 3
    }
}

fun Double.roundOffDecimal(): String {
    df.roundingMode = RoundingMode.FLOOR
    return roundInFavorOfCustomer(String.format("%.2f", this).replace(",", ".").toCharArray())
}

private fun roundInFavorOfCustomer(value: CharArray): String {
    Log.e("sss", "value: ${String(value)}")

    if (value.size <= 1) return String(value)
    else {
        value[value.size - 1] = when (value[value.size - 1]) {
            '1' -> '0'
            '2' -> '0'
            '3' -> '0'
            '4' -> '0'
            '5' -> '5'
            '6' -> '5'
            '7' -> '5'
            '8' -> '5'
            '9' -> '5'
            else -> '0'
        }
        return String(value)
    }


}

private fun roundDefault(value: CharArray): String {
    Log.e("sss", "value: ${String(value)}")
    if (value.size <= 1) return String(value)
    else {
        var arr = value

        when (value[value.size - 1]) {
            '1' -> arr[arr.size - 1] = '0'
            '2' -> arr[arr.size - 1] = '0'
            '3' -> arr[arr.size - 1] = '0'
            '4' -> arr[arr.size - 1] = '0'
            '5' -> arr[arr.size - 1] = '5'
            '6' -> {

                arr = df.format((String(value).toDouble() + 0.10)).replace(",", ".").toCharArray()
                arr[arr.size - 1] = '0'
            }
            '7' -> {

                arr = df.format((String(value).toDouble() + 0.10)).replace(",", ".").toCharArray()
                arr[arr.size - 1] = '0'
            }
            '8' -> {
                arr = df.format((String(value).toDouble() + 0.10)).replace(",", ".").toCharArray()
                arr[arr.size - 1] = '0'
            }
            '9' -> {
                arr = df.format((String(value).toDouble() + 0.10)).replace(",", ".").toCharArray()
                arr[arr.size - 1] = '0'
            }
            else -> arr[arr.size - 1] = '0'
        }
        Log.e("sss", "value: ${String(arr)}")

        return String(arr)
    }
}


fun Double.moneyFormat(): String {
    return DecimalFormat("##.##").format(this)
}

fun Double.moneyFormatWithCurrency(): String {
    return this.moneyFormat().plus("₺")
}