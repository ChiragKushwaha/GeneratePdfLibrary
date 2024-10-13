package com.ck.pdfgenerator

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.drawToBitmap
import com.ck.pdfgenerator.utils.enums.PageOrientation
import com.ck.pdfgenerator.utils.enums.PageSize
import com.ck.pdfgenerator.utils.models.Config
import com.ck.pdfgenerator.utils.models.PdfConfig
import java.io.ByteArrayOutputStream
import java.io.File

val defaultConfig = Config(
    pageSize = PageSize.A4,
    pageOrientation = PageOrientation.PORTRAIT
)

class GeneratePdfLibrary(
    private val context: Context, private val config: Config = defaultConfig
) {
    private val dpi = context.resources.displayMetrics.densityDpi
    private val pageSize = config.pageSize.toPixels(dpi)
    private val pageOrientation = config.pageOrientation

    companion object {
        @Volatile
        private var instance: GeneratePdfLibrary? = null

        fun getInstance(context: Context, config: Config = defaultConfig): GeneratePdfLibrary {
            return instance ?: synchronized(this) {
                instance ?: GeneratePdfLibrary(context, config).also { instance = it }
            }
        }
    }

    fun generatePdf(pdfConfig: PdfConfig) {
        val headerView = createComposeView { pdfConfig.header() }
        val footerView = createComposeView { pdfConfig.footer() }
        val bodyView = createComposeView { pdfConfig.body() }
        val parentLayout = createParentLayout(headerView, bodyView, footerView)

        addViewToWindow(parentLayout)

        measureAndGeneratePdf(
            parentLayout, headerView, bodyView, footerView, pdfConfig,
        )
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
    ): FrameLayout {
        return FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(pageSize.first, MATCH_PARENT)
            setPadding(0, 0, 0, 0)

            addView(LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                setPadding(0, 0, 0, 0)

                addView(headerView, createLayoutParams())
                addView(bodyView, createLayoutParams(WRAP_CONTENT, 1f))
            })

            addView(footerView, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = android.view.Gravity.BOTTOM
            })
        }
    }

    private fun createLayoutParams(
        height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
        weight: Float = 0f
    ): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            MATCH_PARENT,
            height,
            weight
        ).apply {
            setMargins(0, 0, 0, 0)
        }
    }

    private fun addViewToWindow(parentLayout: FrameLayout) {
        (context as Activity).runOnUiThread {
            (context.window.decorView as ViewGroup).addView(parentLayout)
        }
    }

    private fun measureAndGeneratePdf(
        parentLayout: FrameLayout,
        headerView: ComposeView,
        bodyView: ComposeView,
        footerView: ComposeView,
        pdfConfig: PdfConfig,
    ) {
        parentLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (parentLayout.width > 0 && parentLayout.height > 0) {
                    parentLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val pdfDocument = PdfDocument()
                    val (width, height) = getPageDimensions()

                    generatePdfPages(
                        headerView, bodyView, footerView,
                        pdfDocument, width, height
                    )

                    savePdfToFile(pdfDocument, pdfConfig)
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
        headerView: ComposeView,
        bodyView: ComposeView,
        footerView: ComposeView,
        pdfDocument: PdfDocument,
        width: Int,
        height: Int
    ) {
        val availableHeight = height - headerView.measuredHeight - footerView.measuredHeight

        var pageNumber = 1
        var totalPages =
            Math.ceil(bodyView.measuredHeight.toDouble() / availableHeight.toDouble()).toInt()

        while (totalPages > 0) {
            val pageInfo = PdfDocument.PageInfo.Builder(width, height, pageNumber).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            // Ensure header has non-zero dimensions before drawing
            if (headerView.measuredWidth > 0 && headerView.measuredHeight > 0) {
                val headerBitmap = headerView.drawToBitmap()
                canvas.drawBitmap(headerBitmap, 0f, 0f, null)
            }

            // Ensure footer has non-zero dimensions before drawing
            if (footerView.measuredHeight > 0 && footerView.measuredHeight > 0) {
                val footerBitmap = footerView.drawToBitmap()
                canvas.drawBitmap(footerBitmap, 0f, (height - footerView.height).toFloat(), null)
            }

            // Ensure body has non-zero dimensions before drawing
            if (bodyView.measuredWidth > 0 && bodyView.measuredHeight > 0) {
                val bodyBitmap = bodyView.drawToBitmap()
                val srcRect =
                    Rect(
                        0,
                        (pageNumber - 1) * availableHeight,
                        bodyBitmap.width,
                        pageNumber * availableHeight
                    )
                val destRect =
                    Rect(
                        0,
                        headerView.measuredHeight,
                        width,
                        headerView.measuredHeight + availableHeight
                    )
                canvas.drawBitmap(bodyBitmap, srcRect, destRect, null)
            }

            pdfDocument.finishPage(page)
            pageNumber++
            totalPages -= 1
        }
    }

    private fun savePdfToFile(pdfDocument: PdfDocument, pdfConfig: PdfConfig) {
        val stream = ByteArrayOutputStream()
        pdfDocument.writeTo(stream)
        pdfDocument.close()

        saveToCacheDirectory(pdfConfig.name, stream)
    }

    private fun saveToCacheDirectory(fileName: String, stream: ByteArrayOutputStream): File {
        val cacheDir = context.cacheDir
        val file = File(cacheDir, "$fileName.pdf")
        file.outputStream().use { it.write(stream.toByteArray()) }
        return file
    }

    private fun removeViewFromWindow(parentLayout: FrameLayout) {
        (context as Activity).runOnUiThread {
            (context.window.decorView as ViewGroup).removeView(parentLayout)
        }
    }
}