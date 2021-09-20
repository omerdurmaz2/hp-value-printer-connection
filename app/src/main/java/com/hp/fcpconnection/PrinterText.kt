package com.hp.fcpconnection

class PrinterText(private var text: String) {
    private val boldOn = "\u001b\u0021\u0008"
    private val boldOff = "\u001b\u0021\u0000"
    private val underlineOn = "\u001b\u002d\u0031"
    private val underlineOff = "\u001b\u002d\u0030"
    private val right = "\u001b\u0061\u0032"
    private val center = "\u001b\u0061\u0030"
    private val left = "\u001b\u0061\u0030"
    private val largeOn = "\u001b\u0021\u0030"
    private val largeOff = "\u001b\u0021\u0030"
    private val mediumOn = "\u001b\u0021\u0030"
    private val mediumOff = "\u001b\u0021\u0000"
    private val compressOn = "\u001b\u0021\u0001"
    private val compressOff = "\u001b\u0021\u0000"
    private val nextLine = "\n"

    private var isCenter = false
    private var isLeft = false
    private var isRight = false
    private var isLarge = false
    private var isMedium = false
    private var isBold = false
    private var isUnderline = false
    private var isCompress = false

    fun center(): PrinterText {
        isCenter = true
        return this
    }

    fun left(): PrinterText {
        isLeft = true
        return this
    }

    fun right(): PrinterText {
        isRight = true
        return this
    }

    fun title(): PrinterText {
        isLarge = true
        return this
    }

    fun subTitle(): PrinterText {
        isMedium = true
        return this
    }

    fun bold(): PrinterText {
        isBold = true
        return this
    }

    fun underline(): PrinterText {
        isUnderline = true
        return this
    }

    fun compress(): PrinterText {
        isCompress = true
        return this
    }

    fun skipLine(): PrinterText {
        text += nextLine
        return this
    }

    fun add(t: String): PrinterText {
        text += t
        return this
    }

    fun write(): String {
        if (isCenter) {
            text = center + text
        }
        if (isLarge) {
            text = largeOn + text + largeOff
        }
        if (isBold) {
            text = boldOn + text + boldOff
        }
        if (isCompress) {
            text = compressOn + text + compressOff
        }
        if (isMedium) {
            text = mediumOn + text + mediumOff
        }

        if (isLeft) {
            text = left + text
        }
        if (isRight) {
            text = right + text
        }
        if (isUnderline) {
            text = underlineOn + text + underlineOff
        }


        return text.convertLettersToEnglish() +"\n"
    }
}