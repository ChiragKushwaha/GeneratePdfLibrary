package com.ck.pdfgenerator

import android.app.Activity
import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Build.VERSION_CODES.N
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.drawToBitmap
import com.ck.pdfgenerator.utils.models.Config
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.CompletableFuture
import kotlin.apply
import kotlin.apply
import kotlin.io.outputStream


class GeneratePdfLibrary(private val context: Context, private val config: Config) {
    val pageSize = config.pageSize
    val pageOrientation = config.pageOrientation

    fun generatePdf(composable: @Composable () -> Unit): CompletableFuture<String> {
        val future = CompletableFuture<String>()

        // Create a ComposeView and set the content
        val composeView = ComposeView(context).apply {
            setContent {
                composable()
            }
        }

        // Create a parent layout and add the ComposeView to it
        val parentLayout = FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            addView(composeView)
        }

        // Add the parent layout to the window's decor view
        (context as Activity).runOnUiThread {
            (context.window.decorView as ViewGroup).addView(parentLayout)
        }

        // Wait until the ComposeView is laid out
        composeView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (composeView.width > 0 && composeView.height > 0) {
                    composeView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    // Draw the ComposeView to a bitmap
                    val bitmap = composeView.drawToBitmap()

                    // Create a PdfDocument
                    val pdfDocument = PdfDocument()
                    val pageInfo =
                        PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
                    val page = pdfDocument.startPage(pageInfo)

                    // Draw the bitmap on the PDF page
                    val canvas = page.canvas
                    canvas.drawBitmap(bitmap, 0f, 0f, null)
                    pdfDocument.finishPage(page)

                    // Write the PDF document to a byte array
                    val stream = ByteArrayOutputStream()
                    pdfDocument.writeTo(stream)
                    pdfDocument.close()

                    // Save the PDF file to the cache directory
                    val cacheDir = context.cacheDir
                    val file = File(cacheDir, "document.pdf")
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
