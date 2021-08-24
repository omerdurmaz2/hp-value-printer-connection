package com.hp.fcpconnection


fun String.convertLettersToEnglish(): String {
    return this.replace("ç", "\u00f7")
        .replace("ğ", "\u011f")
        .replace("ı", "\u0131")
        .replace("ö", "\u00f6")
        .replace("ş", "\u015f")
        .replace("ü", "\u00fc")
        .replace("Ç", "\u00c7")
        .replace("Ğ", "\u011e")
        .replace("İ", "\u0130")
        .replace("Ö", "\u00d6")
        .replace("Ş", "\u015e")
        .replace("Ü", "\u00dc")
}
