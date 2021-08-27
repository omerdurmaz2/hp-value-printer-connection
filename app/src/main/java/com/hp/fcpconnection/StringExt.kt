package com.hp.fcpconnection

import java.util.*


fun String.convertLettersToEnglish(): String {
    return this.replace("ç", "c")
        .replace("ğ", "g")
        .replace("ı", "i")
        .replace("ö", "o")
        .replace("ş", "s")
        .replace("ü", "u")
        .replace("Ç", "C")
        .replace("Ğ", "G")
        .replace("İ", "I")
        .replace("Ö", "O")
        .replace("Ş", "S")
        .replace("Ü", "U")
}

fun String.receiptLength(): String {
    return if (this.length > 20) this.substring(0, 20).uppercase(Locale.getDefault())
    else this.uppercase(Locale.getDefault())
}