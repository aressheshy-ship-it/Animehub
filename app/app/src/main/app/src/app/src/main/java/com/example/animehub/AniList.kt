package com.example.animehub

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@Serializable data class Title(val romaji: String? = null, val english: String? = null)
@Serializable data class Cover(val large: String? = null)
@Serializable data class Anime(
    val id: Int,
    val title: Title,
    val coverImage: Cover? = null,
    val description: String? = null,
    val episodes: Int? = null,
    val averageScore: Int? = null,
    val genres: List<String> = emptyList()
) { val name get() = title.english ?: title.romaji ?: "?" }

object AniList {
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }
    private const val Q = """
      query(${'$'}search: String) {
        Page(perPage: 30) {
          media(search: ${'$'}search, type: ANIME, sort: [POPULARITY_DESC]) {
            id title { romaji english } coverImage { large }
            description(asHtml: false) episodes averageScore genres
          }
        }
      }"""

    suspend fun search(query: String?): List<Anime> = withContext(Dispatchers.IO) {
        val vars = buildJsonObject { if (!query.isNullOrBlank()) put("search", query) }
        val body = buildJsonObject { put("query", Q); put("variables", vars) }
            .toString().toRequestBody("application/json".toMediaType())
        val req = Request.Builder().url("https://graphql.anilist.co").post(body).build()
        client.newCall(req).execute().use { r ->
            val root = json.parseToJsonElement(r.body!!.string()).jsonObject
            val media = root["data"]!!.jsonObject["Page"]!!.jsonObject["media"]!!
            json.decodeFromJsonElement<List<Anime>>(media)
        }
    }
}
