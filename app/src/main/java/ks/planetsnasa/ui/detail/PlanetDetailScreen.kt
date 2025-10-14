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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import ks.planetsnasa.data.di.ServiceLocator

@Composable
fun PlanetDetailScreen(
    nasaId: String,
    onBack: () -> Unit
) {
    val factory = PlanetDetailVmFactory(ServiceLocator.planetRepository, nasaId)
    val vm: PlanetDetailViewModel = viewModel(factory = factory)
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

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                item.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                            }
                        },
                        actions = {
                            IconButton(onClick = { share(ctx, item.title, item.imageUrl) }) {
                                Icon(Icons.Filled.Share, contentDescription = "Поделиться")
                            }
                            IconButton(onClick = { saveWithDownloadManager(ctx, item.title, item.imageUrl) }) {
                                Icon(Icons.Filled.Download, contentDescription = "Сохранить")
                            }
                        }
                    )
                }
            ) { inner ->
                Column(
                    modifier = Modifier
                        .padding(inner)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Full-bleed image
                    Box {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(item.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = item.title,
                            loading = {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(16f / 9f)
                                        .background(Color(0xFFECECEC))
                                )
                            },
                            error = {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(16f / 9f)
                                        .background(Color(0xFFEEEEEE))
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                        )
                        // лёгкий градиент снизу
                        Box(
                            Modifier
                                .matchParentSize()
                                .background(
                                    Brush.verticalGradient(
                                        0f to Color.Transparent,
                                        0.7f to Color.Transparent,
                                        1f to Color(0x22000000)
                                    )
                                )
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Title + date + description
                    Column(Modifier.padding(horizontal = 16.dp)) {
                        Text(text = item.title, style = MaterialTheme.typography.titleLarge)
                        item.date?.let {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        item.description?.let {
                            Spacer(Modifier.height(12.dp))
                            Text(text = it, style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

// Share — через системный диалог
private fun share(ctx: Context, title: String, url: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, "$title\n$url")
    }
    ctx.startActivity(Intent.createChooser(intent, "Поделиться"))
}

// Сохранение — через DownloadManager (без ручных пермишенов на Android 10+)
private fun saveWithDownloadManager(ctx: Context, title: String, url: String) {
    val dm = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val request = DownloadManager.Request(Uri.parse(url))
        .setTitle(title)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "PlanetsNASA/${title.sanitize()}.jpg")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
    dm.enqueue(request)
}

private fun String.sanitize(): String =
    replace(Regex("""[^\w\-. ]"""), "_")