package com.ck.pdfgenerator

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView

fun createComposeView(context: Context, content: @Composable () -> Unit): ComposeView {
    return ComposeView(context).apply {
        setContent { content() }
    }
}