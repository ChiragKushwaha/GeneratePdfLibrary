package com.ck.pdfgenerator

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.drawToBitmap
import com.ck.pdfgenerator.utils.enums.PageOrientation
import com.ck.pdfgenerator.utils.enums.PageSize
import com.ck.pdfgenerator.utils.models.Config
import com.ck.pdfgenerator.utils.models.PdfConfig
import java.io.ByteArrayOutputStream
import java.io.File

val defaultConfig = Config(
    pageSize = PageSize.A5, pageOrientation = PageOrientation.PORTRAIT
)

class GeneratePdfLibrary(
    private val context: Context, private val config: Config = defaultConfig
) {
    private val dpi = context.resources.displayMetrics.densityDpi
    private val pageSize = config.pageSize.toPixels(dpi)
    private val pageOrientation = config.pageOrientation
    private lateinit var pdfConfigLocal: PdfConfig

    companion object {
        @Volatile
        private var instance: GeneratePdfLibrary? = null

        fun getInstance(context: Context, config: Config = defaultConfig): GeneratePdfLibrary {
            return instance ?: synchronized(this) {
                instance ?: GeneratePdfLibrary(context, config).also { instance = it }
            }
        }
    }

    // Main function to generate PDF
    fun generatePdf(pdfConfig: PdfConfig) {
        pdfConfigLocal = pdfConfig
        val headerView = createComposeView { pdfConfig.header() }
        val footerView = createComposeView { pdfConfig.footer() }
        val bodyView = createComposeView { pdfConfig.body() }

        // Measure heights of header, body, and footer
        measureViews(
            headerView, bodyView, footerView
        ) { (
                headerHeight,
                bodyHeight,
                footerHeight,
                headerBitmap,
                bodyBitmap,
                footerBitmap,
            ) ->

            val pdfDocument = generatePdfPages(
                headerBitmap, bodyBitmap, footerBitmap, headerHeight, bodyHeight, footerHeight
            )
            savePdfToFile(pdfDocument, pdfConfig)
            cleanupViews(headerView, bodyView, footerView)
        }
    }

    // Create a ComposeView with the given composable content
    private fun createComposeView(content: @Composable () -> Unit): ComposeView {
        return ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent { content() }
        }
    }

    data class ViewMeasurements(
        val headerHeight: Int,
        val bodyHeight: Int,
        val footerHeight: Int,
        val headerBitmap: Bitmap,
        val bodyBitmap: Bitmap,
        val footerBitmap: Bitmap
    )

    private fun measureViews(
        headerView: ComposeView,
        bodyView: ComposeView,
        footerView: ComposeView,
        callback: (ViewMeasurements) -> Unit
    ) {
        removeViewFromParent(headerView)
        removeViewFromParent(bodyView)
        removeViewFromParent(footerView)

        val (width, height) = getPageDimensions()

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(width, WRAP_CONTENT)
            addView(headerView)
            addView(bodyView)
            addView(footerView)
        }

        val tempLayout = ScrollView(context).apply {
            layoutParams = ViewGroup.LayoutParams(width, WRAP_CONTENT)
            addView(container)
        }

        (context as Activity).runOnUiThread {
            (context.window.decorView as ViewGroup).addView(tempLayout)
            tempLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    tempLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val headerHeight = headerView.measuredHeight
                    val bodyHeight = bodyView.measuredHeight
                    val footerHeight = footerView.measuredHeight

                    val headerBitmap = headerView.drawToBitmap()
                    val bodyBitmap = bodyView.drawToBitmap()
                    val footerBitmap = footerView.drawToBitmap()

                    callback(
                        ViewMeasurements(
                            headerHeight,
                            bodyHeight,
                            footerHeight,
                            headerBitmap,
                            bodyBitmap,
                            footerBitmap
                        )
                    )
                    removeViewFromWindow(tempLayout)
                }
            })
        }
    }

    // Generate PDF pages
    private fun generatePdfPages(
        headerBitmap: Bitmap,
        bodyBitmap: Bitmap,
        footerBitmap: Bitmap,
        headerHeight: Int,
        bodyHeight: Int,
        footerHeight: Int
    ): PdfDocument {
        val pdfDocument = PdfDocument()
        val (width, height) = getPageDimensions()
        val availableHeight = height - headerHeight - footerHeight

        var pageNumber = 1
        val totalPage = (bodyHeight / availableHeight) + 1

        while (pageNumber <= totalPage) {
            val pageInfo = PdfDocument.PageInfo.Builder(width, height, pageNumber).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            // Draw header
            canvas.drawBitmap(headerBitmap, 0f, 0f, null)

            // Draw footer
            canvas.drawBitmap(footerBitmap, 0f, (height - footerHeight).toFloat(), null)

            // Draw body
            val srcRect = Rect(
                0, (pageNumber - 1) * availableHeight, width, pageNumber * availableHeight
            )
            val destRect = Rect(
                0, headerHeight, width, headerHeight + availableHeight
            )
            canvas.drawBitmap(bodyBitmap, srcRect, destRect, null)

            pdfDocument.finishPage(page)
            pageNumber++
        }

        return pdfDocument
    }

    // Get the dimensions of the PDF page
    private fun getPageDimensions(): Pair<Int, Int> {
        return if (pageOrientation == PageOrientation.PORTRAIT) {
            pageSize.first to pageSize.second
        } else {
            pageSize.second to pageSize.first
        }
    }

    // Save the PDF to a file
    private fun savePdfToFile(pdfDocument: PdfDocument, pdfConfig: PdfConfig) {
        val stream = ByteArrayOutputStream()
        pdfDocument.writeTo(stream)
        pdfDocument.close()

        saveToCacheDirectory(pdfConfig.name, stream)
    }

    // Save the PDF to the cache directory
    private fun saveToCacheDirectory(fileName: String, stream: ByteArrayOutputStream): File {
        val cacheDir = context.cacheDir
        val file = File(cacheDir, "$fileName.pdf")
        file.outputStream().use { it.write(stream.toByteArray()) }
        return file
    }

    // Cleanup views and listeners
    private fun cleanupViews(vararg views: ComposeView) {
        views.forEach { view ->
            removeViewFromParent(view)
            view.disposeComposition()
        }
    }

    // Remove a view from its parent if it has one
    private fun removeViewFromParent(view: ComposeView) {
        (view.parent as? ViewGroup)?.removeView(view)
    }

    // Remove a view from the window
    private fun removeViewFromWindow(parentLayout: FrameLayout) {
        (context as Activity).runOnUiThread {
            (context.window.decorView as ViewGroup).removeView(parentLayout)
        }
    }
}