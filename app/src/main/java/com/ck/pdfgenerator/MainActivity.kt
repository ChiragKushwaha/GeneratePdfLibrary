package com.ck.pdfgenerator

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Green
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
        val pdf = GeneratePdfLibrary(
            this, Config(
                pageSize = PageSize.A4,
                pageOrientation = PageOrientation.PORTRAIT
            )
        )
        setContent {
            PdfGeneratorTheme {
                GeneratePdf(pdf)
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

@Composable
fun GeneratePdf(pdf: GeneratePdfLibrary) {
    val pdfFuture = pdf.generatePdf(PdfConfig(
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
                        "Lori lived her life through the lens of a camera. She never realized this until this very moment as she scrolled through thousands of images on your computer. She could remember the exact moment each photo was taken. She could remember where she had been, what she was thinking as she tried to get the shot, the smells of the surrounding area, and even the emotions that she felt taking the photo, yet she had trouble remembering what she had for breakfast. The coin hovered in the air, spinning over and over again. It reached its peak and began to descend. Both boys were pleading with it to land a certain way but the coin had already made up its mind on what it was going to do.She had been an angel for coming up on 10 years and in all that time nobody had told her this was possible. The fact that it could ever happen never even entered her mind. Yet there she stood, with the undeniable evidence sitting on the ground before her. Angels could lose their wings.There were two things that were important to Tracey. The first was her dog. Anyone that had ever met Tracey knew how much she loved her dog. Most would say that she treated it as her child. The dog went everywhere with her and it had been her best friend for the past five years. The second thing that was important to Tracey, however, would be a lot more surprising to most people“Ingredients for life,” said the backside of the truck. They mean food, but really food is only 1 ingredient of life. Life has so many more ingredients such as pain, happiness, laughter, joy, tears, and smiles. Life also has hard work, easy play, sleepless nights, and sunbathing by the ocean. Love, hatred, envy, self-assurance, and fear could be just down aisle 3 ready to be bought when needed. How I wish I could pull ingredients like these off shelves in a store. It's never good to give them details, Janice told her sister. Always be a little vague and keep them guessing. Her sister listened intently and nodded in agreement. She didn't fully understand what her sister was saying but that didn't matter. She loved her so much that she would have agreed to whatever came out of her mouth They rushed out the door, grabbing anything and everything they could think of they might need. There was no time to double-check to make sure they weren't leaving something important behind. Everything was thrown into the car and they sped off. Thirty minutes later they were safe and that was when it dawned on them that they had forgotten the most important thing of all.",
                        color = Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Lori lived her life through the lens of a camera. She never realized this until this very moment as she scrolled through thousands of images on your computer. She could remember the exact moment each photo was taken. She could remember where she had been, what she was thinking as she tried to get the shot, the smells of the surrounding area, and even the emotions that she felt taking the photo, yet she had trouble remembering what she had for breakfast. The coin hovered in the air, spinning over and over again. It reached its peak and began to descend. Both boys were pleading with it to land a certain way but the coin had already made up its mind on what it was going to do.She had been an angel for coming up on 10 years and in all that time nobody had told her this was possible. The fact that it could ever happen never even entered her mind. Yet there she stood, with the undeniable evidence sitting on the ground before her. Angels could lose their wings.There were two things that were important to Tracey. The first was her dog. Anyone that had ever met Tracey knew how much she loved her dog. Most would say that she treated it as her child. The dog went everywhere with her and it had been her best friend for the past five years. The second thing that was important to Tracey, however, would be a lot more surprising to most people“Ingredients for life,” said the backside of the truck. They mean food, but really food is only 1 ingredient of life. Life has so many more ingredients such as pain, happiness, laughter, joy, tears, and smiles. Life also has hard work, easy play, sleepless nights, and sunbathing by the ocean. Love, hatred, envy, self-assurance, and fear could be just down aisle 3 ready to be bought when needed. How I wish I could pull ingredients like these off shelves in a store. It's never good to give them details, Janice told her sister. Always be a little vague and keep them guessing. Her sister listened intently and nodded in agreement. She didn't fully understand what her sister was saying but that didn't matter. She loved her so much that she would have agreed to whatever came out of her mouth They rushed out the door, grabbing anything and everything they could think of they might need. There was no time to double-check to make sure they weren't leaving something important behind. Everything was thrown into the car and they sped off. Thirty minutes later they were safe and that was when it dawned on them that they had forgotten the most important thing of all.",
                        color = Yellow
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Lori lived her life through the lens of a camera. She never realized this until this very moment as she scrolled through thousands of images on your computer. She could remember the exact moment each photo was taken. She could remember where she had been, what she was thinking as she tried to get the shot, the smells of the surrounding area, and even the emotions that she felt taking the photo, yet she had trouble remembering what she had for breakfast. The coin hovered in the air, spinning over and over again. It reached its peak and began to descend. Both boys were pleading with it to land a certain way but the coin had already made up its mind on what it was going to do.She had been an angel for coming up on 10 years and in all that time nobody had told her this was possible. The fact that it could ever happen never even entered her mind. Yet there she stood, with the undeniable evidence sitting on the ground before her. Angels could lose their wings.There were two things that were important to Tracey. The first was her dog. Anyone that had ever met Tracey knew how much she loved her dog. Most would say that she treated it as her child. The dog went everywhere with her and it had been her best friend for the past five years. The second thing that was important to Tracey, however, would be a lot more surprising to most people“Ingredients for life,” said the backside of the truck. They mean food, but really food is only 1 ingredient of life. Life has so many more ingredients such as pain, happiness, laughter, joy, tears, and smiles. Life also has hard work, easy play, sleepless nights, and sunbathing by the ocean. Love, hatred, envy, self-assurance, and fear could be just down aisle 3 ready to be bought when needed. How I wish I could pull ingredients like these off shelves in a store. It's never good to give them details, Janice told her sister. Always be a little vague and keep them guessing. Her sister listened intently and nodded in agreement. She didn't fully understand what her sister was saying but that didn't matter. She loved her so much that she would have agreed to whatever came out of her mouth They rushed out the door, grabbing anything and everything they could think of they might need. There was no time to double-check to make sure they weren't leaving something important behind. Everything was thrown into the car and they sped off. Thirty minutes later they were safe and that was when it dawned on them that they had forgotten the most important thing of all.",
                        color = Green
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Lori lived her life through the lens of a camera. She never realized this until this very moment as she scrolled through thousands of images on your computer. She could remember the exact moment each photo was taken. She could remember where she had been, what she was thinking as she tried to get the shot, the smells of the surrounding area, and even the emotions that she felt taking the photo, yet she had trouble remembering what she had for breakfast. The coin hovered in the air, spinning over and over again. It reached its peak and began to descend. Both boys were pleading with it to land a certain way but the coin had already made up its mind on what it was going to do.She had been an angel for coming up on 10 years and in all that time nobody had told her this was possible. The fact that it could ever happen never even entered her mind. Yet there she stood, with the undeniable evidence sitting on the ground before her. Angels could lose their wings.There were two things that were important to Tracey. The first was her dog. Anyone that had ever met Tracey knew how much she loved her dog. Most would say that she treated it as her child. The dog went everywhere with her and it had been her best friend for the past five years. The second thing that was important to Tracey, however, would be a lot more surprising to most people“Ingredients for life,” said the backside of the truck. They mean food, but really food is only 1 ingredient of life. Life has so many more ingredients such as pain, happiness, laughter, joy, tears, and smiles. Life also has hard work, easy play, sleepless nights, and sunbathing by the ocean. Love, hatred, envy, self-assurance, and fear could be just down aisle 3 ready to be bought when needed. How I wish I could pull ingredients like these off shelves in a store. It's never good to give them details, Janice told her sister. Always be a little vague and keep them guessing. Her sister listened intently and nodded in agreement. She didn't fully understand what her sister was saying but that didn't matter. She loved her so much that she would have agreed to whatever came out of her mouth They rushed out the door, grabbing anything and everything they could think of they might need. There was no time to double-check to make sure they weren't leaving something important behind. Everything was thrown into the car and they sped off. Thirty minutes later they were safe and that was when it dawned on them that they had forgotten the most important thing of all.",
                        color = Color.Cyan
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