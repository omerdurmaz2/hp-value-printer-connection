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


            printTopLogo()
            printAddress()
            printTitle("BİLGİ FİŞİ", true)
            printSubTitle("TÜR: e-ARŞİV FATURA", true)
            skipLine(1)
            printTitleValue("Tarih", "22.06.1998","Mağaza No","120")
            skipLine(1)
            printTimeFiscalId("15:54", 99)
            printReceiptNoZNo(456, 99)
            printLine()
            printSubTitle("E-Arşiv Gönderim Bilgileri", true)
            printLine()
            printTitleValue("EPosta", "",)
            printTitleValue("Ünvan", "Nihai Tüketici",)
            printTitleValue("V.D", "",)
            printTitleValue("V.K.N", "11111111111",)
            printTitleValue("Telefon", "",)
            printTitleValue("Adres", "",)
            printTitleValue("Belge No", "0123456789",)
            printTitleValue("ETTN", "0123456789",)
            printLine()
            printBottomLogo()
            skipLine(2)

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

    private fun printDateShopNumber(date: String, shopNo: Int) {
        printer.printNormal(
            POSPrinterConst.PTR_S_RECEIPT,
            "\u001b\u0061\u0030Tarih \t:$date  \u001b\u0061\u0032 Mağaza No \t:$shopNo"
        )
    }

    private fun printLine() {
        printer.printNormal(
            POSPrinterConst.PTR_S_RECEIPT,
            "------------------------------------------"
        )
    }

    private fun printTitleValue(title: String?=null, value: String?=null, rTitle:String?=null,rValue:String?=null) {

        if (title!=null) {

        }
        else if(rTitle!=null){

        }

        printer.printNormal(
            POSPrinterConst.PTR_S_RECEIPT,
            ""
        )
    }

    private fun printTimeFiscalId(time: String, fiscalId: Int) {
        printer.printNormal(
            POSPrinterConst.PTR_S_RECEIPT,
            "Saat\t: $time\t\tKasa No\t\t:$fiscalId"
        )
    }

    private fun printReceiptNoZNo(rNo: Int, zNo: Int) {
        printer.printNormal(
            POSPrinterConst.PTR_S_RECEIPT,
            "Belge No: $rNo\t\tZ No\t\t:$zNo"
        )
    }

    private fun skipLine(lCount: Int) {
        var text = ""
        if (lCount > 0) {
            for (i in 0..lCount) {
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
        private val mediumOn = "\u001b!\u0010"
        private val mediumOff = "\u001b!\u0000"
        private val compressOn = "\u001b!\u0001"
        private val compressOff = "\u001b!\u0000"
        private val nextLine = "\u000A"

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
            when {
                isLarge -> {
                    text = largeOn + text + largeOff
                }
                isMedium -> {
                    text = mediumOn + text + mediumOff
                }
                isBold -> {
                    text = boldOn + text + boldOff
                }
                isCenter -> {
                    text = center + text
                }
                isLeft -> {
                    text = left + text
                }
                isRight -> {
                    text = right + text
                }
                isUnderline -> {
                    text = underlineOn + text + underlineOff
                }
                isCompress -> {
                    text = compressOn + text + compressOff
                }
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
        if (bold) p.bold().compress()
        printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, p.write())
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