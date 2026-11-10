package com.example.animehub

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AnimeViewModel : ViewModel() {
    var items by mutableStateOf<List<Anime>>(emptyList()); private set
    var loading by mutableStateOf(false); private set
    var error by mutableStateOf<String?>(null); private set

    init { search(null) }

    fun search(q: String?) {
        viewModelScope.launch {
            loading = true; error = null
            try { items = AniList.search(q) } catch (e: Exception) { error = e.message }
            loading = false
        }
    }

    fun byId(id: Int) = items.firstOrNull { it.id == id }
}
