# GeneratePdfLibrary

The `GeneratePdfLibrary` is a Kotlin-based library designed to generate PDF documents from Compose
UI components in an Android application. It provides a streamlined way to convert UI elements into a
PDF format, ensuring that the views are properly laid out and rendered before being included in the
PDF.

## Key Features

- **Configurable Page Size and Orientation**: Supports different page sizes (e.g., A4, A5) and
  orientations (portrait, landscape).
- **Composable Content**: Utilizes Jetpack Compose to define the header, body, and footer content of
  the PDF.
- **Automatic Layout Measurement**: Measures the heights of the header, body, and footer views to
  ensure they fit correctly within the PDF pages.
- **Bitmap Generation**: Converts the Compose views into bitmaps for accurate rendering in the PDF.
- **PDF Page Generation**: Creates multiple pages if the content exceeds the available space on a
  single page.
- **File Saving**: Saves the generated PDF to the device's cache directory.

## Usage

### Initialization

Create an instance of `GeneratePdfLibrary` using the `getInstance` method.

```kotlin
val pdfLibrary = GeneratePdfLibrary.getInstance(context)
```

PDF Generation
Call the generatePdf method with a PdfConfig object that defines the header, body, and footer
composables.

```kotlin
val pdfConfig = PdfConfig(
    name = "example",
    header = { HeaderComposable() },
    body = { BodyComposable() },
    footer = { FooterComposable() }
)

pdfLibrary.generatePdf(pdfConfig)
```

Customization
Customize the page size and orientation through the Config object.

```kotlin
val customConfig = Config(
    pageSize = PageSize.A4,
    pageOrientation = PageOrientation.LANDSCAPE
)

val pdfLibrary = GeneratePdfLibrary.getInstance(context, customConfig)
```

Example
Here is a complete example of how to use the GeneratePdfLibrary:

```kotlin
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ck.pdfgenerator.GeneratePdfLibrary
import com.ck.pdfgenerator.utils.models.PdfConfig
import com.ck.pdfgenerator.ui.theme.PdfGeneratorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PdfGeneratorTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    GeneratePdf()
                }
            }
        }
    }

    @Composable
    fun GeneratePdf() {
        val pdfConfig = PdfConfig(
            name = "example",
            header = { HeaderComposable() },
            body = { BodyComposable() },
            footer = { FooterComposable() }
        )

        val pdfLibrary = GeneratePdfLibrary.getInstance(this)
        pdfLibrary.generatePdf(pdfConfig)
    }

    @Composable
    fun HeaderComposable() {
        Text(text = "Header Content")
    }

    @Composable
    fun BodyComposable() {
        Text(text = "Body Content")
    }

    @Composable
    fun FooterComposable() {
        Text(text = "Footer Content")
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        PdfGeneratorTheme {
            GeneratePdf()
        }
    }
}
```