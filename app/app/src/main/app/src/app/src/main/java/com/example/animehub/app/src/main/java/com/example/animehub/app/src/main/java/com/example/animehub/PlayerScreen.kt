package com.example.animehub

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun PlayerScreen() {
    val ctx = LocalContext.current
    val player = remember { ExoPlayer.Builder(ctx).build() }
    DisposableEffect(Unit) { onDispose { player.release() } }

    var url by remember { mutableStateOf("") }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { play(player, it) }
    }

    Column(Modifier.fillMaxSize()) {
        AndroidView(
            factory = { PlayerView(it).apply { this.player = player } },
            modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)
        )
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { picker.launch(arrayOf("video/*")) }) { Text("اختر ملف فيديو") }
            OutlinedTextField(
                url, { url = it }, Modifier.fillMaxWidth(), singleLine = true,
                label = { Text("أو الصق رابط فيديو (mp4 / m3u8)") }
            )
            Button(onClick = { if (url.isNotBlank()) play(player, Uri.parse(url.trim())) }) { Text("شغّل الرابط") }
        }
    }
}

private fun play(p: ExoPlayer, uri: Uri) {
    p.setMediaItem(MediaItem.fromUri(uri)); p.prepare(); p.play()
}
