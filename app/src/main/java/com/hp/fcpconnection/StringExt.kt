package com.hp.fcpconnection


fun String.convertLettersToEnglish(): String {
    return this.replace("ç", "c")
        .replace("ğ","g")
        .replace("ı","i")
        .replace("ö","o")
        .replace("ş","s")
        .replace("ü","u")
        .replace("Ç","C")
        .replace("Ğ","G")
        .replace("İ","I")
        .replace("Ö","O")
        .replace("Ş","S")
        .replace("Ü","U")
}