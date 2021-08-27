package com.hp.fcpconnection

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.hp.android.possdk.IJPOSInitCompleteCallBack
import com.hp.fcpconnection.databinding.ActivityMainBinding
import jpos.*
import jpos.POSPrinterConst

import android.graphics.BitmapFactory

import android.graphics.Bitmap
import android.util.Printer
import jpos.POSPrinterConst.PTR_BMT_BMP


class MainActivity : AppCompatActivity(), IJPOSInitCompleteCallBack {

    private val escape = "\u001b"
    private val excMark = "\u0021"

    val BOLD_ON = "\u001b\u0045\u0001"
    val BOLD_OFF = "\u001b\u0045"
    val UNDERLINE_ON = "\u001b\u002d\u0031"
    val UNDERLINE_OFF = "\u001b\u002d\u0030"
    var RIGHT = "\u001b\u0061\u0032"
    var CENTER = "\u001b\u0061\u0031"
    var LEFT = "\u001b\u0061\u0030"
    var LARGE_ON = "\u001b\u0021\u0010"
    var LARGE_OFF = "\u001b\u0021\u0030"
    var MEDIUM_ON = "\u001b\u0021\u0008"
    var MEDIUM_OFF = "\u001b\u0021\u0010"
    private val compressOn = "\u001b\u0021\u0001"
    private val emphasizedOn = "\u001b\u0045\u0001"
    private val emphasizedOff = "\u001b\u0045\u0000"
    private val maxLength = 42

    private lateinit var binding: ActivityMainBinding
    private val printer = POSPrinter()
    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            Log.e("sss", "message:$msg")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        JPOSApp.start(this, this)
        binding.btnYazdir.setOnClickListener {
            connectPrinter()
        }

        printer.addStatusUpdateListener { statusUpdateEvent ->
            Log.e(
                "sss",
                "###statusUpdateEvent: " + statusUpdateEvent.status
            )
            val msg = Message()
            msg.what = statusUpdateEvent.status
            handler.sendMessage(msg)
        }

        printer.addErrorListener {
            Log.e(
                "sss",
                "###errorListener: $it"
            )
        }
    }

    override fun onComplete() {
        Log.e("sss", "complete")
    }

    private fun connectPrinter() {
        val prime = "HPEngageOnePrimePrinter"
        val value = "HPValuePrinter"
        val h300c = "H300C"

        try {
            printer.open(value)
            printer.claim(3000)
            printer.powerNotify = JposConst.JPOS_PN_ENABLED
            printer.deviceEnabled = true


            skipLine(2)

            printTopLogo()
            printAddress()
            skipLine(1)
            printTitle("BİLGİ FİŞİ", true)
            skipLine(2)
            printSubTitle("TÜR: e-ARŞİV FATURA", true)
            skipLine(2)
            alignLeft()
            printer.printNormal(
                POSPrinterConst.PTR_S_RECEIPT,
                TitleValueQueue().add("Tarih", "22.06.1998", "Magaza No", "120")
                    .add("Saat", "09:53", "Kasa No", "1")
                    .add("Belge No", "4", "Z No", "26")
                    .create()
            )
            printLine()
            alignCenter()
            printSubTitle("E-Arşiv Gönderim Bilgileri", true)
            skipLine(1)
            alignLeft()
            printer.printNormal(
                POSPrinterConst.PTR_S_RECEIPT,
                TitleValueQueue()
                    .add("EPosta", "")
                    .add("Ünvan", "Nihai Tüketici")
                    .add("V.D", "")
                    .add("V.K.N", "11111111111")
                    .add("Telefon", "")
                    .add("Adres", "")
                    .add("Belge No", "0123456789")
                    .add("ETTN", "0123456789").create()
            )
            printLine()
            printer.printNormal(
                POSPrinterConst.PTR_S_RECEIPT,
                ProductText().create("cil200lyagmur", 0.22, 1.5, 1, 0)
            )
            printer.printNormal(
                POSPrinterConst.PTR_S_RECEIPT,
                ProductText().create("TELEFON", 7.25, 1.25, 3, 0)
            )
            printLine()

            printer.printNormal(
                POSPrinterConst.PTR_S_RECEIPT,
                TitleValueQueue().add(
                    "Toplam KDV",
                    null,
                    null,
                    "*53,30"
                )
                    .add(
                        "Toplam",
                        null,
                        null,
                        "*53.30"
                    ).bold().create()
            )

            skipLine(1)
            printLine()
            printer.printNormal(
                POSPrinterConst.PTR_S_RECEIPT,
                TitleValueQueue().add(

                    "Nakit",
                    null,
                    null,
                    "*53,30"
                )
                    .add(
                        "Para Üstü",
                        null,
                        null,
                        "*53.30"
                    ).create()
            )
            skipLine(2)
            printBarcode()
            printBottomLogo()
            skipLine(4)

            printer.cutPaper(100)
            printer.deviceEnabled = false

        } catch (e: Exception) {
            Log.e("sss", "hata: $e, ")
            e.printStackTrace()

        } finally {
            printer.release()
            printer.close()
        }
    }

    private fun alignLeft() {
        printer.printNormal(
            POSPrinterConst.PTR_S_RECEIPT, LEFT
        )
    }

    private fun alignRight() {
        printer.printNormal(
            POSPrinterConst.PTR_S_RECEIPT, RIGHT
        )
    }

    private fun alignCenter() {
        printer.printNormal(
            POSPrinterConst.PTR_S_RECEIPT, CENTER
        )
    }

    private fun printLine() {
        var text = ""
        for (i in 0 until maxLength) {
            text += "-"
        }
        printer.printNormal(
            POSPrinterConst.PTR_S_RECEIPT,
            text
        )
    }


    private fun skipLine(lCount: Int) {
        var text = ""
        if (lCount > 0) {
            for (i in 0 until lCount) {
                text += "\n"
            }
            printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, text)
        }
    }


    private fun printTopLogo() {
        val alignment = POSPrinterConst.PTR_BM_CENTER
        val logo =
            bitmapToByteArray(imageToBitmap(R.drawable.denker_logo_receipt_header))
        printer.printMemoryBitmap(
            POSPrinterConst.PTR_S_RECEIPT,
            logo,
            PTR_BMT_BMP,
            500,
            alignment
        )
    }

    private fun printBottomLogo() {
        val alignment = POSPrinterConst.PTR_BM_CENTER
        val logo =
            bitmapToByteArray(imageToBitmap(R.drawable.denker_logo_receipt_footer))
        printer.printMemoryBitmap(
            POSPrinterConst.PTR_S_RECEIPT,
            logo,
            PTR_BMT_BMP,
            250,
            alignment
        )
    }

    class TitleValueQueue() {
        private val lTitleValue: ArrayList<Map<String?, String?>?> = arrayListOf()
        private val rTitleValue: ArrayList<Map<String?, String?>?> = arrayListOf()
        private val maxLength = 42
        private var isBold = false
        private val boldOn = "\u001b\u0021\u0008"
        private val boldOff = "\u001b\u0021\u0000"


        fun add(
            title: String? = null,
            value: String? = null,
            rTitle: String? = null,
            rValue: String? = null
        ): TitleValueQueue {
            if (title == null && value == null) lTitleValue.add(null)
            else lTitleValue.add(
                mapOf(
                    "title" to title,
                    "value" to value
                )
            )

            if (rTitle == null && rValue == null) rTitleValue.add(null)
            else rTitleValue.add(
                mapOf(
                    "title" to rTitle,
                    "value" to rValue
                )
            )

            return this
        }

        fun bold(): TitleValueQueue {
            isBold = true
            return this
        }

        private fun calculateLeftSpace(title: String): String {
            var space = ""
            for (i in title.length until lTitleValue.map { (it?.get("title") ?: "").length }
                .maxOf { it }) {
                space += " "
            }
            return space
        }

        private fun calculateCenterSpace(text: String, i: Int): String {
            var space = ""
            val length =
                maxLength - text.length - rTitleValue.map { (it?.get("title") ?: "").length }
                    .maxOf { it } - rTitleValue.map { (it?.get("value") ?: "").length }
                    .maxOf { it }
            for (i in 0 until length) {
                space += " "
            }
            return space
        }


        private fun calculateRightSpace(title: String): String {
            var space = ""
            for (i in title.length until rTitleValue.map { (it?.get("title") ?: "").length }
                .maxOf { it }) {
                space += " "
            }
            return space
        }

        fun create(): String {
            var text = ""
            for (i in lTitleValue.indices) {
                var tempText = ""
                if (lTitleValue[i] != null) {
                    tempText += lTitleValue[i]?.get("title") + lTitleValue[i]?.get("title")
                        ?.let { it1 -> calculateLeftSpace(it1) } + ": " + (lTitleValue[i]?.get("value")
                        ?: "")
                }

                tempText += if (rTitleValue[i] != null) {
                    var a = calculateCenterSpace(
                        tempText,
                        i
                    )

                    if (rTitleValue[i]?.get("title") != null) {
                        a = a.substring(0, a.length - 2)
                        a += rTitleValue[i]?.get("title") + rTitleValue[i]?.get(
                            "title"
                        )
                            ?.let { it1 -> calculateRightSpace(it1) } + ": "
                    }
                    a += (rTitleValue[i]?.get("value")) + "\n"
                    Log.e("sss", "a :$a")

                    a
                } else "\n"
                text += tempText
                tempText = ""
            }
            Log.e("sss", "text = $text")

            if (isBold) {
                text = boldOn + text.replaceFirst(": ", ":") + boldOff
            }

            return text
        }
    }

    class ProductText() {
        private val maxLength = 42
        private val discountTitle = " İNDİRİM".convertLettersToEnglish()
        fun create(name: String, price: Double, discount: Double?, pcs: Int, type: Int): String {
            val priceText = "*${(price * pcs).moneyFormat()}"

            var text = ""

            text += name.receiptLength() + calculateCenterSpace(name.receiptLength() + priceText) + priceText + "\n"
            if (pcs > 1)
                text += givePadding(name.receiptLength().length) + "$pcs X *${price.moneyFormat()}\n"
            if (discount != null) {
                val discountText = "*-" + (discount * pcs).moneyFormat()
                text += discountTitle + calculateCenterSpace(discountTitle + discountText) + discountText + "\n"
            }


            return text
        }

        private fun calculateCenterSpace(text: String): String {
            var space = ""
            val length = maxLength - text.length
            for (i in 0 until length) {
                space += " "
            }
            return space
        }

        private fun givePadding(length: Int): String {
            var space = ""
            for (i in 0 until length) {
                space += " "
            }
            return space
        }
    }

    class TitleValueText() {
        private val maxLength = 42

        private fun calculateCenterSpace(text: String): String {
            var space = ""
            val length = maxLength - text.length
            for (i in 0 until length) {
                space += " "
            }
            return space
        }

        private fun givePadding(length: Int): String {
            var space = ""
            for (i in 0 until length) {
                space += " "
            }
            return space
        }

        fun create(title: String?, value: String?): String {
            var text = ""
            if (title != null) {
                text += PrinterText("TOPLAM KDV").bold().write()
            }
            if (value != null) {
                text += calculateCenterSpace("$text*$value") + PrinterText("*$value").bold().write()
            }
            return text
        }
    }

    class PrinterText(private var text: String) {
        private val escape = "\u001b"
        private val turkish = "\u001btH"
        private val boldOn = "\u001b\u0021\u0008"
        private val boldOff = "\u001b\u0021\u0000"
        private val underlineOn = "\u001b\u002d\u0031"
        private val underlineOff = "\u001b\u002d\u0030"
        private val right = "\u001b\u0061\u0032"
        private val center = "\u001ba1"
        private val left = "\u001b\u0061\u0030"
        private val largeOn = "\u001b!\u0030"
        private val largeOff = "\u001b!0"
        private val mediumOn = "\u001b!\u0030"
        private val mediumOff = "\u001b!\u0000"
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
            if (isCenter) {
                text = center + text
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


            return text.convertLettersToEnglish()
        }
    }

    private fun printAddress() {
        printer.printNormal(
            POSPrinterConst.PTR_S_RECEIPT,
            PrinterText("DENKER ELEKTRONİK TİC. LTD. ŞTİ.").skipLine().add("KADIKÖY / İSTANBUL")
                .skipLine().add(" www.denker.com.tr").center().skipLine().write()
        )
    }

    private fun printTitle(title: String, bold: Boolean) {
        val p = PrinterText(title).title().center()
        if (bold) p.bold()
        printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, p.write())
    }

    private fun printSubTitle(title: String, bold: Boolean) {
        val p = PrinterText(title).center().subTitle()
        if (bold) p.bold()
        printer.printNormal(
            POSPrinterConst.PTR_S_RECEIPT,
            p.write()
        )
    }

    private fun printQRCode() {
        val data = "0123456789"
        val symbology = POSPrinterConst.PTR_BCS_QRCODE
        val height = 150
        val width = 150
        val alignment = POSPrinterConst.PTR_BC_CENTER
        val textPosition = POSPrinterConst.PTR_BC_TEXT_NONE

        printer.printBarCode(
            POSPrinterConst.PTR_S_RECEIPT,
            data,
            symbology,
            height,
            width,
            alignment,
            textPosition
        )

    }


    private fun printBarcode() {
        val data = "3014260269401"
        val symbology = POSPrinterConst.PTR_BCS_Code128_Parsed
        val height = 110
        val width = 485
        val alignment = POSPrinterConst.PTR_BC_CENTER
        val textPosition = POSPrinterConst.PTR_BC_TEXT_NONE

        printer.printBarCode(
            POSPrinterConst.PTR_S_RECEIPT,
            data,
            symbology,
            height,
            width,
            alignment,
            textPosition
        )
    }

    private fun imageToBitmap(drawableId: Int): Bitmap {
        return BitmapFactory.decodeResource(resources, drawableId)
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        return BitmapConvertor().convertBitmapToBuffer(bitmap)
    }


}