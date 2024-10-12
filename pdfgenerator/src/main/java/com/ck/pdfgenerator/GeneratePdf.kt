package com.ck.pdfgenerator

import android.app.Activity
import android.content.Context
import android.graphics.pdf.PdfDocument
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.drawToBitmap
import com.ck.pdfgenerator.utils.enums.PageOrientation
import com.ck.pdfgenerator.utils.models.Config
import com.ck.pdfgenerator.utils.models.PdfConfig
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.CompletableFuture

class GeneratePdfLibrary(private val context: Context, private val config: Config) {
    private val dpi = context.resources.displayMetrics.densityDpi
    private val pageSize = config.pageSize.toPixels(dpi)
    private val pageOrientation = config.pageOrientation

    fun generatePdf(pdfConfig: PdfConfig): CompletableFuture<String> {
        val future = CompletableFuture<String>()

        val headerView = createComposeView { pdfConfig.header() }
        val footerView = createComposeView { pdfConfig.footer() }
        val bodyView = createComposeView { pdfConfig.body() }

        val parentLayout = createParentLayout(headerView, bodyView, footerView)

        addViewToWindow(parentLayout)

        measureAndGeneratePdf(parentLayout, headerView, bodyView, footerView, pdfConfig, future)

        return future
    }

    private fun createComposeView(content: @Composable () -> Unit): ComposeView {
        return ComposeView(context).apply {
            setContent { content() }
        }
    }

    private fun createParentLayout(
        headerView: ComposeView,
        bodyView: ComposeView,
        footerView: ComposeView
    ): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(pageSize.first, pageSize.second)
            setPadding(0, 0, 0, 0)

            addView(headerView, createLayoutParams())
            addView(bodyView, createLayoutParams())
            addView(footerView, createLayoutParams())
        }
    }

    private fun createLayoutParams(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 0, 0)
        }
    }

    private fun addViewToWindow(parentLayout: LinearLayout) {
        (context as Activity).runOnUiThread {
            (context.window.decorView as ViewGroup).addView(parentLayout)
        }
    }

    private fun measureAndGeneratePdf(
        parentLayout: LinearLayout,
        headerView: ComposeView,
        bodyView: ComposeView,
        footerView: ComposeView,
        pdfConfig: PdfConfig,
        future: CompletableFuture<String>
    ) {
        parentLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (parentLayout.width > 0 && parentLayout.height > 0) {
                    parentLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val pdfDocument = PdfDocument()
                    val (width, height) = getPageDimensions()

                    generatePdfPages(
                        parentLayout, headerView, bodyView, footerView,
                        pdfConfig, pdfDocument, width, height
                    )

                    savePdfToFile(pdfDocument, pdfConfig, future)

                    removeViewFromWindow(parentLayout)
                }
            }
        })
    }

    private fun getPageDimensions(): Pair<Int, Int> {
        return if (pageOrientation == PageOrientation.PORTRAIT) {
            pageSize.first to pageSize.second
        } else {
            pageSize.second to pageSize.first
        }
    }

    private fun generatePdfPages(
        parentLayout: LinearLayout,
        headerView: ComposeView,
        bodyView: ComposeView,
        footerView: ComposeView,
        pdfConfig: PdfConfig,
        pdfDocument: PdfDocument,
        width: Int,
        height: Int
    ) {
        val bodyHeight = bodyView.height
        val availableHeight = parentLayout.height - headerView.height - footerView.height

        var pageNumber = 1
        var remainingHeight = bodyHeight

        while (remainingHeight > 0) {
            createPage(pdfDocument, parentLayout, pageNumber, width, height)
            pageNumber++
            remainingHeight -= availableHeight

            if (remainingHeight > 0) {
                bodyView.setContent { pdfConfig.body() }
            }
        }
    }

    private fun createPage(
        pdfDocument: PdfDocument,
        content: LinearLayout,
        pageNumber: Int,
        width: Int,
        height: Int
    ) {
        val pageInfo = PdfDocument.PageInfo.Builder(width, height, pageNumber).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val bitmap = content.drawToBitmap()
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        pdfDocument.finishPage(page)
    }

    private fun savePdfToFile(
        pdfDocument: PdfDocument,
        pdfConfig: PdfConfig,
        future: CompletableFuture<String>
    ) {
        val stream = ByteArrayOutputStream()
        pdfDocument.writeTo(stream)
        pdfDocument.close()

        val file = saveToCacheDirectory(pdfConfig.name, stream)
        future.complete(file.absolutePath)
    }

    private fun saveToCacheDirectory(fileName: String, stream: ByteArrayOutputStream): File {
        val cacheDir = context.cacheDir
        val file = File(cacheDir, "$fileName.pdf")
        file.outputStream().use { it.write(stream.toByteArray()) }
        return file
    }

    private fun removeViewFromWindow(parentLayout: LinearLayout) {
        (context as Activity).runOnUiThread {
            (context.window.decorView as ViewGroup).removeView(parentLayout)
        }
    }
}
