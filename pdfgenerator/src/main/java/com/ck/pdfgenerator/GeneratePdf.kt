package com.ck.pdfgenerator

import android.app.Activity
import android.content.Context
import android.graphics.pdf.PdfDocument
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.drawToBitmap
import com.ck.pdfgenerator.utils.enums.PageOrientation
import com.ck.pdfgenerator.utils.models.Config
import com.ck.pdfgenerator.utils.models.PdfConfig
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.CompletableFuture

class GeneratePdfLibrary(private val context: Context, private val config: Config) {
    val dpi = context.resources.displayMetrics.densityDpi
    val pageSize = config.pageSize.toPixels(dpi)
    val pageOrientation = config.pageOrientation

    fun generatePdf(pdfConfig: PdfConfig): CompletableFuture<String> {
        val future = CompletableFuture<String>()

        // Create a ComposeView for header, footer, and body
        val headerView = ComposeView(context).apply {
            setContent { pdfConfig.header() }
        }
        val footerView = ComposeView(context).apply {
            setContent { pdfConfig.footer() }
        }
        val bodyView = ComposeView(context).apply {
            setContent { pdfConfig.body() }
        }

        // Measure the content height
        val parentLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                pageSize.first,
                pageSize.second
            )
            setPadding(0, 0, 0, 0) // Ensure no padding is applied
            addView(
                headerView, LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 0) // Ensure no margin is applied
                }
            )
            addView(
                bodyView, LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 0) // Ensure no margin is applied
                }
            )
            addView(
                footerView, LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 0) // Ensure no margin is applied
                }
            )
        }

        // Add the parent layout to the window's decor view
        (context as Activity).runOnUiThread {
            (context.window.decorView as ViewGroup).addView(parentLayout)
        }

        // Wait until the ComposeViews are laid out
        parentLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (parentLayout.width > 0 && parentLayout.height > 0) {
                    parentLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    // Create a PdfDocument
                    val pdfDocument = PdfDocument()
                    val (width, height) = if (pageOrientation == PageOrientation.PORTRAIT) {
                        pageSize.first to pageSize.second
                    } else {
                        pageSize.second to pageSize.first
                    }

                    // Function to create a new page
                    fun createPage(pageNumber: Int, content: LinearLayout) {
                        val pageInfo =
                            PdfDocument.PageInfo.Builder(width, height, pageNumber).create()
                        val page = pdfDocument.startPage(pageInfo)
                        val canvas = page.canvas
                        val bitmap = content.drawToBitmap()
                        canvas.drawBitmap(bitmap, 0f, 0f, null)
                        pdfDocument.finishPage(page)
                    }

                    // Measure the height of the body content
                    val bodyHeight = bodyView.height
                    val availableHeight = height - headerView.height - footerView.height

                    // Create pages based on the body content height
                    var pageNumber = 1
                    var remainingHeight = bodyHeight
                    while (remainingHeight > 0) {
                        createPage(pageNumber, parentLayout)
                        pageNumber++
                        remainingHeight -= availableHeight

                        // If there is remaining content, update the bodyView with the remaining content
                        if (remainingHeight > 0) {
                            bodyView.setContent { pdfConfig.body() }
                        }
                    }

                    // Write the PDF document to a byte array
                    val stream = ByteArrayOutputStream()
                    pdfDocument.writeTo(stream)
                    pdfDocument.close()

                    // Save the PDF file to the cache directory
                    val cacheDir = context.cacheDir
                    val file = File(cacheDir, "${pdfConfig.name}.pdf")
                    val outputStream = file.outputStream()
                    outputStream.write(stream.toByteArray())
                    outputStream.close()

                    // Return the file URL
                    future.complete(file.absolutePath)

                    // Remove the parent layout from the window's decor view
                    (context.window.decorView as ViewGroup).removeView(parentLayout)
                }
            }
        })

        return future
    }
}