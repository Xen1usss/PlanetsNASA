package ks.planetsnasa.ui.detail

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import ks.planetsnasa.ui.util.formatIsoToLocalOrSelf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanetDetailScreen(onBack: () -> Unit) {
    val vm: PlanetDetailViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()

    when (val s = state) {
        is PlanetDetailState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is PlanetDetailState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Не удалось загрузить карточку")
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = onBack) { Text("Назад") }
                        Button(onClick = { vm.refresh() }) { Text("Повторить") }
                    }
                }
            }
        }

        is PlanetDetailState.Content -> {
            val ctx = LocalContext.current
            val item = s.item
            val headerHeight = 320.dp
            val overlap = 24.dp

            val showDesc = !item.description.isNullOrBlank() &&
                    !item.description.equals(item.title, ignoreCase = true)

            if (showDesc) {
                Spacer(Modifier.height(16.dp))
                Text(text = item.description!!, style = MaterialTheme.typography.bodyLarge)
            }

            Box(Modifier.fillMaxSize()) {

                Box {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(headerHeight)
                                    .background(Color(0xFFECECEC))
                            )
                        },
                        error = {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(headerHeight)
                                    .background(Color(0xFFEEEEEE))
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(headerHeight)
                    )
                    Box(
                        Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    0f to Color(0x33000000),
                                    0.6f to Color.Transparent,
                                    1f to Color(0xAA000000)
                                )
                            )
                    )
                }

                Column(Modifier.fillMaxSize()) {
                    Spacer(Modifier.height(headerHeight - overlap)) // «подвинулось» под фото
                    Surface(
                        tonalElevation = 2.dp,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            Text(text = item.title, style = MaterialTheme.typography.headlineSmall)
                            item.date?.let {
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = it.formatIsoToLocalOrSelf(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            item.description?.let {
                                Spacer(Modifier.height(16.dp))
                                Text(text = it, style = MaterialTheme.typography.bodyLarge)
                            }
                            Spacer(Modifier.height(24.dp))
                        }
                    }
                }

                // 3) Топбар поверх картинки
                CenterAlignedTopAppBar(
                    title = {
                        Text(item.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { share(ctx, item.title, item.imageUrl) }) {
                            Icon(Icons.Filled.Share, contentDescription = "Поделиться", tint = Color.White)
                        }
                        IconButton(onClick = { saveWithDownloadManager(ctx, item.title, item.imageUrl) }) {
                            Icon(Icons.Filled.Download, contentDescription = "Сохранить", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    modifier = Modifier
                        .statusBarsPadding()
                        .height(56.dp)
                )
            }
        }
    }
}

private fun share(ctx: Context, title: String, url: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, "$title\n$url")
    }
    ctx.startActivity(Intent.createChooser(intent, "Поделиться"))
}

private fun saveWithDownloadManager(ctx: Context, title: String, url: String) {
    val dm = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val request = DownloadManager.Request(Uri.parse(url))
        .setTitle(title)
        .setDestinationInExternalPublicDir(
            Environment.DIRECTORY_PICTURES,
            "PlanetsNASA/${title.sanitize()}.jpg"
        )
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
        )
    dm.enqueue(request)
}

private fun String.sanitize(): String = replace(Regex("""[^\w\-. ]"""), "_")