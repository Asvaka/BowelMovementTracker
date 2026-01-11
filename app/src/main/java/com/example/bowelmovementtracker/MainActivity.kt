package com.example.bowelmovementtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bowelmovementtracker.ui.theme.BowelMovementTrackerTheme
import java.time.DayOfWeek
import java.time.LocalDate


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
    val dateItems = dates.map { ListItem(it) }
    CalendarMonths(dates = dateItems, modifier)
}

@Composable
fun CalendarMonths(dates: List<ListItem>, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        // header for start padding
        WeekdayNames(modifier = Modifier.padding(start = GutterWidth))
        StickyLabelList(
            items = dates,
            modifier = Modifier.fillMaxSize(),
        ) { item ->
            // item factory:
            // get today's date to mark it later
            val isToday = item.date == LocalDate.now()

            // draw a Box for each item (date)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    // alternate background color for even and odd months
                    .background(
                        if (item.date.month.value % 2 == 0) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f)
                        else Color.Transparent
                    )
                    // make them all the same size
                    .weight(1f)
                    // make them square
                    .aspectRatio(1f)
            ) {
                // draw a circle behind today's date
                if (isToday)
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .fillMaxSize()
                    )
                // draw the date as text and adapt the color to the today's date marker
                Text(
                    text = item.date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Justify,
                    color = if (isToday) MaterialTheme.colorScheme.onPrimary
                    else LocalContentColor.current
                )
            }
        }
    }
}

@Composable
private fun WeekdayNames(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        val headerLabels = weekdays()
        headerLabels.forEach {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1F)
                    .aspectRatio(1F)
            ) {
                Text(text = it.take(2), textAlign = TextAlign.Justify)
            }
        }
    }
    HorizontalDivider(modifier = Modifier.fillMaxWidth())
}

fun weekdays(): List<String> = DayOfWeek.entries.map {
    it.displayName()
}

fun DayOfWeek.displayName(): String =
    getDisplayName(textStyle, locale)

private val textStyle = java.time.format.TextStyle.FULL
private val locale = java.util.Locale.getDefault()

interface StickyListItem {
    val label: String // displayed name of the month
}

data class ListItem(
    val date: LocalDate,
) : StickyListItem {
    override val label: String
        get() = date.month.getDisplayName(
            // Java's TextStyle and Locale!
            java.time.format.TextStyle.SHORT, java.util.Locale.getDefault()
        )
}

@Composable
fun StickyLabelList(
    items: List<ListItem>,
    modifier: Modifier = Modifier,
    gutterWidth: Dp = GutterWidth,
    labelTextStyle: TextStyle = TextStyle(
        fontSize = 26.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End
    ),
    labelColor: Color =  MaterialTheme.colorScheme.secondary,
    itemFactory: @Composable() (LazyGridScope.(ListItem) -> Unit),
) {
    val state = rememberLazyGridState()
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val gutterWidthPx = with(density) {
        gutterWidth.toPx()
    }
    var itemHeightPx by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
        // clip to avoid "leaking" sticky labels outside thee top of container
            .clip(RectangleShape)
            .drawWithCache {
                onDrawBehind {
                    // remember currently used label outside loop
                    var label: String? = null
                    if (itemHeightPx == 0) itemHeightPx =
                        state.layoutInfo.visibleItemsInfo.firstOrNull()?.size?.height ?: 0

                    state.layoutInfo.visibleItemsInfo.forEachIndexed { relativeIndex, itemInfo ->
                        // we are only interested in the last item of a row to get its label
                        // that way a row has only one label
                        if (itemInfo.column != 6) return@forEachIndexed

                        // the label of the current item
                        val itemLabel = items.getOrNull(itemInfo.index)?.label

                        if (itemLabel != null && itemLabel != label) {
                            label = itemLabel

                            // get the label from the last item in the next row
                            val nextLabel = items.getOrNull(itemInfo.index + 7)?.label

                            val textLayout = textMeasurer.measure(
                                text = AnnotatedString(
                                    itemLabel.take(3)
                                ),
                                style = labelTextStyle,
                            )

                            // center the label horizontally and vertically
                            val horizontalOffset = (gutterWidthPx - textLayout.size.width) / 2
                            val verticalOffset = (itemHeightPx - textLayout.size.height) / 2

                            // get the dynamic item offset to move label with item
                            val verticalItemOffset = itemInfo.offset.y.toFloat()

                            // calculate the vertical label offset:
                            // a label sticks to the top of container if it is
                            // attached to the last item of the first visible row == relativeIndex is 6,
                            //and the next label is still the same
                            // else it gets scrolled off the view with the last item it is connected to
                            val verticalLabelOffset =
                                if (relativeIndex == 6 && label == nextLabel) 0f
                                else verticalItemOffset

                            drawText(
                                textLayoutResult = textLayout,
                                color = labelColor,
                                topLeft = Offset(
                                    x = horizontalOffset,
                                    y = verticalLabelOffset + verticalOffset,
                                ),
                            )
                        }
                    }
                }
            }
    ) {
        // inside the Box inside the StickyLabelList
        LazyVerticalGrid(
            state = state,
            columns = GridCells.Fixed(7),
            modifier = Modifier.padding(start = gutterWidth)
        ) {
            items(items, key = { it.date }) { item ->
                this@LazyVerticalGrid.itemFactory(item)
            }
        }
    }
}

private val GutterWidth = 80.dp

val dates = dates()

private fun dates(): List<LocalDate> {
    val dates = mutableListOf<LocalDate>()
    var date = LocalDate.of(2024, 1, 1)
    while (date <= LocalDate.of(2024, 12, 31)) {
        dates.add(date)
        date = date.plusDays(1)
    }
    return dates
}


@Preview
@Composable
fun MainPagePreview() {
    BowelMovementTrackerTheme {
        TrackerMainPage()
    }
}