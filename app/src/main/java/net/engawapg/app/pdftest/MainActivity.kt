package net.engawapg.app.pdftest

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import net.engawapg.app.pdftest.databinding.ActivityMainBinding
import java.io.FileOutputStream
import java.io.OutputStream

fun Uri.getFileOutputStream(context: Context): FileOutputStream? {
    val contentResolver = context.contentResolver
    val parcelFileDescriptor = contentResolver.openFileDescriptor(this, "w")
    return parcelFileDescriptor?.let {
        FileOutputStream(it.fileDescriptor)
    }
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pdfButton.setOnClickListener { onClickPdf() }
    }

    private fun onClickPdf() {
        launcher.launch("PDFTest.pdf")
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
        uri.getFileOutputStream(this)?.let { createPdf(it) }
    }

    private fun createPdf(outputStream: OutputStream) {

        val document = PdfDocument()

        for (pageNumber in 0 until 3) {
            /* pageNumberは使われていないっぽい。ソースコードを見ても使われていない。全ページ同じ数字を指定しても、出来上がるPDFに違いななかった。
               w, hは72dpiで指定するので、A4なら595x842になる。
             */
            val pageInfo = PdfDocument.PageInfo.Builder(
                595, 842, pageNumber
//            1080, 2340, pageNumber
            ).create()

            val page = document.startPage(pageInfo)

            drawPage(page)

            document.finishPage(page)
        }

        document.writeTo(outputStream)

        document.close()
    }

    private fun drawPage(page: PdfDocument.Page) {
        val paint = Paint().apply {
            color = 0xff000000.toInt()
            textSize = 20f
        }
        page.canvas.drawText("Test PDF page.${page.info.pageNumber}", 100f, 100f, paint )
    }

//    private fun drawPage(page: PdfDocument.Page) {
//        val contentView = binding.root
//        contentView.draw(page.canvas)
//    }

//    private fun getOutputStream(): OutputStream {
//        val file = File(filesDir, "document.pdf")
//        return FileOutputStream(file)
//    }
}