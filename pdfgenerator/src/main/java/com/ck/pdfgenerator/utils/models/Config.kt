package com.ck.pdfgenerator.utils.models

import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import com.ck.pdfgenerator.utils.enums.PageOrientation
import com.ck.pdfgenerator.utils.enums.PageSize

@Keep
data class Config(
    val pageSize: PageSize = PageSize.A4,
    val pageOrientation: PageOrientation = PageOrientation.PORTRAIT,
)

@Keep
data class PdfConfig(
    val name: String,
    val header: (@Composable () -> Unit),
    val footer: (@Composable () -> Unit),
    val body: (@Composable () -> Unit),
)