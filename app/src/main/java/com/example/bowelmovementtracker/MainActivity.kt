package com.example.bowelmovementtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.bowelmovementtracker.ui.theme.BowelMovementTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BowelMovementTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TrackerMainPage(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TrackerMainPage(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize()) {
        BowelMovementCalendar(Modifier.weight(0.5F))

        Box(Modifier.weight(0.5F)) {
            Text(
                text = "This is where the details of each individual day will go"
            )
        }
    }
}

@Composable
fun BowelMovementCalendar(modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            text = "This is where the calendar will go",
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainPagePreview() {
    BowelMovementTrackerTheme {
        TrackerMainPage()
    }
}