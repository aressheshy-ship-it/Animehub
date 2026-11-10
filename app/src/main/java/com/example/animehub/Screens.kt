package com.example.animehub

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun HomeScreen(vm: AnimeViewModel, onOpen: (Int) -> Unit, onPlayer: () -> Unit) {
    var q by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().statusBarsPadding().padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                q, { q = it }, Modifier.weight(1f), singleLine = true,
                placeholder = { Text("ابحث عن أنمي") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { vm.search(q) })
            )
            IconButton(onClick = onPlayer) { Icon(Icons.Default.PlayArrow, "المشغل") }
        }
        if (vm.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        vm.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        LazyVerticalGrid(GridCells.Adaptive(120.dp), contentPadding = PaddingValues(4.dp)) {
            items(vm.items, key = { it.id }) { a ->
                Column(Modifier.padding(4.dp).clickable { onOpen(a.id) }) {
                    AsyncImage(
                        a.coverImage?.large, a.name,
                        Modifier.fillMaxWidth().aspectRatio(0.7f).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Text(a.name, maxLines = 2, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun DetailScreen(a: Anime?, onPlayer: () -> Unit) {
    if (a == null) { Text("غير موجود", Modifier.padding(16.dp)); return }
    Column(
        Modifier.fillMaxSize().statusBarsPadding()
            .verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        AsyncImage(
            a.coverImage?.large, a.name,
            Modifier.fillMaxWidth().height(320.dp).clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(12.dp))
        Text(a.name, style = MaterialTheme.typography.headlineSmall)
        Text("التقييم: ${a.averageScore ?: "-"}/100  •  الحلقات: ${a.episodes ?: "-"}")
        Text(a.genres.joinToString("، "), style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(8.dp))
        Text(a.description?.replace(Regex("<[^>]*>"), "") ?: "")
        Spacer(Modifier.height(16.dp))
        Button(onClick = onPlayer) { Text("افتح المشغل (ملفاتك أو روابطك)") }
    }
}
