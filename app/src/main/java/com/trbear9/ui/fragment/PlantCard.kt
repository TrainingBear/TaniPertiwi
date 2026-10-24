package com.trbear9.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.accompanist.flowlayout.FlowRow
import com.pseudoankit.coachmark.model.ToolTipPlacement
import com.pseudoankit.coachmark.scope.CoachMarkScope
import com.trbear9.ui.util.MarkKey
import com.trbear9.ui.util.highlightConfig
import com.trbear9.ui.util.info
import com.trbear9.ui.util.DataStore
import com.trbear9.plants.parameters.blob.Plant
import com.trbear9.ui.view.PlantDetail
import com.trbear9.ui.viewmodel.getPlantImage
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

object CONS {
    val noImage = Icons.Default.Image
    val noImage2 = Icons.Default.WbSunny
}

var coached by mutableStateOf(false)
var displayed by mutableStateOf(false)
var ready by mutableStateOf(false)

@SuppressLint("NotConstructor")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachMarkScope.PlantCardDisplayer(
    score: Int = 0,
    ref: Plant?,
    showCoach: Boolean = false,          // true only for the first card on first visit
    onPositioned: () -> Unit = {},
) {
    val context = LocalContext.current
    if (ref == null) return

    val baseModifier = Modifier
        .fillMaxWidth()
        .heightIn(max = 600.dp)
        .padding(8.dp)
        .onGloballyPositioned { onPositioned() }

    val cardModifier = if (showCoach) {
        baseModifier.enableCoachMark(
            key = MarkKey.cocok,
            toolTipPlacement = ToolTipPlacement.Bottom,
            highlightedViewConfig = highlightConfig
        ) { MarkKey.cocok.tooltip(ToolTipPlacement.Bottom) }
    } else baseModifier

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        onClick = {
            DataStore.completeTanaman()
            val intent = Intent(context, PlantDetail::class.java)
            intent.putExtra("plant", ref)
            intent.putExtra("score", score)
            context.startActivity(intent)
        },
        modifier = cardModifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 9f)
                ) {
                    var isError by remember { mutableStateOf(false) }
                    var isLoading by remember { mutableStateOf(false) }
                    val model = getPlantImage(ref)
                    AsyncImage(
                        model = model,
                        contentDescription = "${ref.nama_ilmiah} image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp)),
                        onError = {
                            isError = true
                            isLoading = false
                        },
                        onLoading = {
                            isLoading = true
                            isError = false
                        },
                        onSuccess = {
                            isLoading = false
                            isError = false
                        }
                    )
                    if (isError) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                imageVector = CONS.noImage,
                                contentDescription = "${ref.nama_ilmiah ?: "no"} image",
                                modifier = Modifier
                                    .fillMaxSize(fraction = 0.5f)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                            Text(
                                text = "Gambar tidak tersedia untuk ${ref.commonName}",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    if (isLoading) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxSize(fraction = 0.5f)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                            Text(
                                text = "Memuat Gambar...",
                            )
                        }
                    }

                    if (score != 0) {
                        val star = (score / 10f) * 5
                        val half = (star - star.toInt()) > 0.1f
                        val badgeModifier = Modifier
                            .padding(end = 10.dp, top = 7.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .wrapContentSize(Alignment.Center)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .align(Alignment.TopEnd)

                        Row(
                            modifier = if (showCoach) badgeModifier.enableCoachMark(
                                key = MarkKey.skor,
                                toolTipPlacement = ToolTipPlacement.Bottom,
                                highlightedViewConfig = highlightConfig
                            ) { MarkKey.skor.tooltip(ToolTipPlacement.Bottom) } else badgeModifier
                        ) {
                            repeat(star.toInt()) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Score",
                                    tint = Color.Yellow,
                                    modifier = Modifier
                                        .size(30.dp)
                                )
                            }
                            if (half) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.StarHalf,
                                    contentDescription = "Half Score",
                                    tint = Color.Yellow,
                                    modifier = Modifier
                                        .size(30.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Plant Title
            Text(
                text = ref.commonName.split(",")[0] ?: "Tidak tersedia",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 10.dp)
            )

            // Plant Description
            Text(
                text = ref.description.take(160) +
                        if (ref.description.length > 160) "..." else "",
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 6.dp)
            )

            // Plant Tags
            FlowRow(
                modifier = Modifier.padding(top = 6.dp),
                mainAxisSpacing = 2.dp,
                crossAxisSpacing = 2.dp
            ) {
                Kat(
                    ref.difficulty,
                    Color.Black,
                    diffToColor(ref.difficulty)
                )
                val panen_min = ref.minCycle
                val panen_max = ref.maxCycle
                Kat(
                    if (panen_min != panen_max) "$panen_min-$panen_max hari"
                    else if (panen_min == "0") ""
                    else if (panen_min == "null") ""
                    else "$panen_min hari",
                    bcolor = Color.LightGray
                )
                ref.category.forEach {
                    Kat(
                        it.toString(), tcolor = Color.White,
                        bcolor = it.color
                    )
                }
            }
        }
    }
}


@Composable
fun Kat(
    text: String,
    tcolor: Color = Color.Black,
    bcolor: Color = Color(0xFFFF9800),
    icon: ImageVector? = null
) {
    if (text.isNotEmpty()) Box(
        modifier = Modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(4.dp))
            .background(bcolor)
    ) {
        Row {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Icon",
                    modifier = Modifier.padding(6.dp)
                )
            }
            Text(
                text = text,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = tcolor,
                modifier = Modifier.padding(6.dp)
            )
        }
    }
}


fun diffToColor(diff: String): Color {
    return when (diff) {
        "EASY" -> Color.Green
        "MEDIUM" -> Color.Yellow
        "HARD" -> Color.Red
        else -> Color.Gray
    }
}

fun scoreToColor(score: Int): Color {
    return when (score) {
        in 0..2 -> Color.Red
        in 3..6 -> Color.Yellow
        in 7..354 -> Color.Green
        else -> Color.Blue
    }
}

class ImageAsset {
    companion object {
        val images = mutableMapOf<String, Bitmap>()


        fun getImage(context: Context, name: String?): Bitmap? {
            if (name == null) return null
            if (images.containsKey(name)) return images[name]!!
            try {
                context.assets.open("images/$name.webp").use {
                    val decodeStream = BitmapFactory.decodeStream(it)
                    images[name] = decodeStream
                    return decodeStream
                }
            } catch (_: Exception) {
                return null
            }
        }
    }
}

fun test() {

}


