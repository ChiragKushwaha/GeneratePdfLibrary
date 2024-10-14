# GeneratePdfLibrary [![](https://jitpack.io/v/ChiragKushwaha/GeneratePdfLibrary.svg)](https://jitpack.io/#ChiragKushwaha/GeneratePdfLibrary)

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

### Step 1: Add the Library Dependency

Add the library dependency to your `build.gradle.kts` file. The library is hosted on GitHub, so you
can add it using JitPack.

```kotlin
// Add JitPack repository in your settings.gradle.kts file
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

// Add the dependency in your app-level build.gradle.kts file
dependencies {
    implementation("com.github.ChiragKushwaha:GeneratePdfLibrary:0.1.2")
}
```

### Step 2: Sync the Project

Sync your project with Gradle files to ensure the dependency is downloaded and included in your
project.

### Step 3: Initialize the Library

Create an instance of GeneratePdfLibrary in your activity or fragment.

```kotlin
val pdfLibrary = GeneratePdfLibrary.getInstance(context)
```

### Step 4: Define PDF Content

Create a PdfConfig object that defines the header, body, and footer composables.

```kotlin
val pdfConfig = PdfConfig(
    name = "example",
    header = { HeaderComposable() },
    body = { BodyComposable() },
    footer = { FooterComposable() }
)
```

### Step 5: Generate the PDF

Call the generatePdf method with the PdfConfig object.

```kotlin
pdfLibrary.generatePdf(pdfConfig)
```

## Example

Here is a complete example of how to use the GeneratePdfLibrary in an Android application:

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

## License

This project is licensed under the MIT License - see the LICENSE file for details.