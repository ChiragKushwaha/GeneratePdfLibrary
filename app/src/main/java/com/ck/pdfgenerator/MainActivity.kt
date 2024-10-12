package com.ck.pdfgenerator

import android.content.Context
import android.os.Bundle
import android.util.Log.d
import android.util.Log.e
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ck.pdfgenerator.ui.theme.PdfGeneratorTheme
import com.ck.pdfgenerator.utils.enums.PageOrientation
import com.ck.pdfgenerator.utils.enums.PageSize
import com.ck.pdfgenerator.utils.models.Config
import com.ck.pdfgenerator.utils.models.PdfConfig

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PdfGeneratorTheme {
                GeneratePdf(this)
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratePdf(context: Context) {
    val pdfFuture = GeneratePdfLibrary(
        context, Config(
            pageSize = PageSize.A4,
            pageOrientation = PageOrientation.PORTRAIT
        )
    ).generatePdf(PdfConfig(
        name = "HelloWorld",
        header = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Blue),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                content = {
                    Text("Header", color = Red)
                    Text("right", color = Red)
                }
            )
        },
        footer = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                content = {
                    Text("Footer", color = Yellow)
                }
            )
        },
        body = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                content = {
                    Text(
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed arcu risus, condimentum eu orci tristique, imperdiet lobortis lectus. Quisque sit amet metus nec quam aliquet eleifend. Vestibulum vel scelerisque quam. Nulla ut sem urna. Aenean sed elit ut mauris semper placerat at ut diam. Vestibulum ultricies et augue eu viverra. Nunc blandit risus eget pharetra egestas. Proin rhoncus pellentesque nibh, quis elementum erat ultrices ac. Duis id vulputate quam. Morbi ac nibh viverra, interdum libero quis, viverra eros. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nulla sem nulla, bibendum id cursus suscipit, hendrerit ut dui. Praesent quis lorem blandit, sagittis elit nec, consectetur massa. Maecenas gravida mollis rhoncus. Aliquam erat volutpat. Proin hendrerit quam eget eros lobortis congue.Sed ut dolor tortor. Aenean risus nisi, commodo nec auctor nec, egestas ut orci. Etiam id ex tristique turpis egestas blandit sit amet et lorem. Quisque pretium, ipsum sit amet pellentesque fermentum, dui nisl consequat ipsum, tincidunt ultrices mauris neque vel mauris. Sed in nulla ut est interdum convallis et in nisi. Maecenas purus tortor, hendrerit eget eleifend in, posuere ac velit. Fusce purus leo, efficitur quis eleifend pellentesque, tempor vel sapien. Nam malesuada enim id tellus blandit, tempor porttitor libero ultrices. Pellentesque erat augue, efficitur vitae scelerisque vitae, molestie placerat arcu. Etiam egestas vehicula magna non tempor. In hac habitasse platea dictumst.Integer consectetur dapibus quam, non lobortis nibh ultricies molestie. Duis eros arcu, bibendum sit amet velit sit amet, scelerisque tincidunt dui. Aenean suscipit, turpis vitae porttitor interdum, ex odio maximus tellus, id mattis ipsum nibh in lorem. Fusce in mauris leo. Phasellus blandit pellentesque porta. Ut varius risus quis ligula scelerisque, ut posuere lectus varius. Etiam mattis in tortor in dapibus. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Suspendisse turpis turpis, facilisis et fermentum a, placerat eu nulla. Praesent id purus at lacus porttitor bibendum at vel leo. Aliquam neque mi, varius non magna sed, convallis tempus elit. Aenean sed magna in sem venenatis faucibus. Mauris ullamcorper nisi quam, non sollicitudin tellus sodales non.Suspendisse tristique felis ipsum, ac cursus nisl suscipit et. Phasellus tristique lobortis est eu consectetur. Donec hendrerit, odio nec pulvinar hendrerit, ex lacus commodo nunc, eu interdum dolor purus et dolor. Nam ac nunc mollis, interdum dui in, euismod turpis. Praesent dictum purus ultrices dignissim convallis. Nullam aliquam, tellus vitae ornare laoreet, nisl lectus posuere massa, ac porttitor libero libero eu quam. Donec a feugiat dui, sed varius lacus.Fusce efficitur sapien vitae enim viverra, nec malesuada massa fringilla. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam tristique nisi eu augue placerat tempus. Quisque malesuada iaculis congue. Maecenas porta diam odio, sed pharetra elit maximus ac. Curabitur maximus sodales arcu eu laoreet. Phasellus cursus mauris nec tellus sodales fermentum. Donec nec lorem sit amet sem condimentum consectetur venenatis nec ex. Cras luctus est urna, vitae euismod est aliquet egestas. Donec vitae magna metus.",
                        color = Black
                    )
                    Text(
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed arcu risus, condimentum eu orci tristique, imperdiet lobortis lectus. Quisque sit amet metus nec quam aliquet eleifend. Vestibulum vel scelerisque quam. Nulla ut sem urna. Aenean sed elit ut mauris semper placerat at ut diam. Vestibulum ultricies et augue eu viverra. Nunc blandit risus eget pharetra egestas. Proin rhoncus pellentesque nibh, quis elementum erat ultrices ac. Duis id vulputate quam. Morbi ac nibh viverra, interdum libero quis, viverra eros. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nulla sem nulla, bibendum id cursus suscipit, hendrerit ut dui. Praesent quis lorem blandit, sagittis elit nec, consectetur massa. Maecenas gravida mollis rhoncus. Aliquam erat volutpat. Proin hendrerit quam eget eros lobortis congue.Sed ut dolor tortor. Aenean risus nisi, commodo nec auctor nec, egestas ut orci. Etiam id ex tristique turpis egestas blandit sit amet et lorem. Quisque pretium, ipsum sit amet pellentesque fermentum, dui nisl consequat ipsum, tincidunt ultrices mauris neque vel mauris. Sed in nulla ut est interdum convallis et in nisi. Maecenas purus tortor, hendrerit eget eleifend in, posuere ac velit. Fusce purus leo, efficitur quis eleifend pellentesque, tempor vel sapien. Nam malesuada enim id tellus blandit, tempor porttitor libero ultrices. Pellentesque erat augue, efficitur vitae scelerisque vitae, molestie placerat arcu. Etiam egestas vehicula magna non tempor. In hac habitasse platea dictumst.Integer consectetur dapibus quam, non lobortis nibh ultricies molestie. Duis eros arcu, bibendum sit amet velit sit amet, scelerisque tincidunt dui. Aenean suscipit, turpis vitae porttitor interdum, ex odio maximus tellus, id mattis ipsum nibh in lorem. Fusce in mauris leo. Phasellus blandit pellentesque porta. Ut varius risus quis ligula scelerisque, ut posuere lectus varius. Etiam mattis in tortor in dapibus. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Suspendisse turpis turpis, facilisis et fermentum a, placerat eu nulla. Praesent id purus at lacus porttitor bibendum at vel leo. Aliquam neque mi, varius non magna sed, convallis tempus elit. Aenean sed magna in sem venenatis faucibus. Mauris ullamcorper nisi quam, non sollicitudin tellus sodales non.Suspendisse tristique felis ipsum, ac cursus nisl suscipit et. Phasellus tristique lobortis est eu consectetur. Donec hendrerit, odio nec pulvinar hendrerit, ex lacus commodo nunc, eu interdum dolor purus et dolor. Nam ac nunc mollis, interdum dui in, euismod turpis. Praesent dictum purus ultrices dignissim convallis. Nullam aliquam, tellus vitae ornare laoreet, nisl lectus posuere massa, ac porttitor libero libero eu quam. Donec a feugiat dui, sed varius lacus.Fusce efficitur sapien vitae enim viverra, nec malesuada massa fringilla. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam tristique nisi eu augue placerat tempus. Quisque malesuada iaculis congue. Maecenas porta diam odio, sed pharetra elit maximus ac. Curabitur maximus sodales arcu eu laoreet. Phasellus cursus mauris nec tellus sodales fermentum. Donec nec lorem sit amet sem condimentum consectetur venenatis nec ex. Cras luctus est urna, vitae euismod est aliquet egestas. Donec vitae magna metus.",
                        color = Black
                    )
                }
            )
        }
    ))
    pdfFuture.thenAccept { pdfPath ->
        d("PDF", "PDF Generated Successfully at: $pdfPath")
    }.exceptionally { throwable ->
        e("PDF", "PDF Generation Failed", throwable)
        null
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PdfGeneratorTheme {
        Greeting("Android")
    }
}