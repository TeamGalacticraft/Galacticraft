#!/usr/bin/env kotlin

@file:DependsOn("com.google.code.gson:gson:2.10.1")

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

// ---- Config
val ownerRepo = "TeamGalacticraft/Galacticraft"
val modJsonPath = "src/main/resources/fabric.mod.json"
val etagFile = File("build/contributors.etag")

val gsonPretty: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
val gson: Gson = GsonBuilder().disableHtmlEscaping().create()

data class ContributorApi(
    val login: String?,
    val html_url: String?,
    val type: String?,
    val contributions: Int?
)

fun httpGetContributors(ownerRepo: String, useETag: Boolean): Pair<List<ContributorApi>, String?> {
    val results = mutableListOf<ContributorApi>()
    val token = System.getenv("GITHUB_TOKEN")?.takeIf { it.isNotBlank() }
    var url: String? = "https://api.github.com/repos/$ownerRepo/contributors?per_page=100"
    var latestEtag: String? = null

    while (url != null) {
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            setRequestProperty("Accept", "application/vnd.github+json")
            if (token != null) setRequestProperty("Authorization", "Bearer $token")
            if (useETag && etagFile.exists()) setRequestProperty("If-None-Match", etagFile.readText().trim())
            connectTimeout = 15_000
            readTimeout = 15_000
        }

        val code = conn.responseCode
        if (results.isEmpty() && useETag && code == HttpURLConnection.HTTP_NOT_MODIFIED) {
            conn.disconnect()
            return emptyList<ContributorApi>() to null
        }
        if (code >= 400) {
            val err = conn.errorStream?.readBytes()?.toString(Charsets.UTF_8)
            conn.disconnect()
            error("GitHub API error $code while fetching $url\n$err")
        }

        conn.inputStream.use { input ->
            val listType = object : TypeToken<List<ContributorApi>>() {}.type
            val page: List<ContributorApi> = gson.fromJson(input.reader(), listType)
            results.addAll(page)
        }

        if (latestEtag == null) latestEtag = conn.getHeaderField("ETag")

        val linkHeader = conn.getHeaderField("Link")
        conn.disconnect()
        url = linkHeader
            ?.split(",")
            ?.mapNotNull { part ->
                val m = Regex("""<([^>]+)>;\s*rel="([^"]+)"""").find(part.trim())
                if (m != null && m.groupValues[2] == "next") m.groupValues[1] else null
            }
            ?.firstOrNull()
    }
    return results to latestEtag
}

fun buildContributorsPayload(raw: List<ContributorApi>): List<Map<String, Any?>> {
    val filtered = raw.filter { c ->
        val type = c.type?.lowercase() ?: ""
        val login = c.login?.lowercase() ?: ""
        !type.contains("bot") && !login.contains("bot")
    }.sortedByDescending { it.contributions ?: 0 }

    return filtered.map { c ->
        mapOf(
            "name" to (c.login ?: ""),
            "contact" to mapOf("homepage" to (c.html_url ?: ""))
        )
    }
}

fun readExistingContributors(root: JsonElement): List<Map<String, Any?>> {
    if (!root.isJsonObject) return emptyList()
    val obj = root.asJsonObject
    if (!obj.has("contributors") || !obj["contributors"].isJsonArray) return emptyList()
    return obj["contributors"].asJsonArray.mapNotNull { el ->
        if (!el.isJsonObject) return@mapNotNull null
        val o = el.asJsonObject
        mapOf(
            "name" to (o.get("name")?.asString ?: ""),
            "contact" to (o.getAsJsonObject("contact")?.let { cobj ->
                mapOf("homepage" to (cobj.get("homepage")?.asString ?: ""))
            } ?: emptyMap<String, Any?>())
        )
    }
}

// --- Main logic ---
fun updateContributors(useETag: Boolean) {
    val modJsonFile = File(modJsonPath)
    require(modJsonFile.exists()) { "fabric.mod.json not found at $modJsonPath" }

    val rootEl: JsonElement = modJsonFile.reader(Charsets.UTF_8).use { JsonParser.parseReader(it) }
    val existing = readExistingContributors(rootEl)

    val (raw, newEtag) = try {
        httpGetContributors(ownerRepo, useETag)
    } catch (e: Exception) {
        println("Failed to reach GitHub: ${e.message}. Keeping existing contributors.")
        return
    }

    if (raw.isEmpty() && newEtag == null) {
        println("Contributors unchanged (ETag 304). Skipping update.")
        return
    }

    val newContrib = buildContributorsPayload(raw)
    if (existing == newContrib) {
        println("Contributors already up-to-date. No changes written.")
        if (newEtag != null) etagFile.writeText(newEtag)
        return
    }

    rootEl.asJsonObject.add("contributors", gson.toJsonTree(newContrib))
    modJsonFile.writeText(gsonPretty.toJson(rootEl), Charsets.UTF_8)
    if (newEtag != null) etagFile.writeText(newEtag)

    println("Updated contributors in fabric.mod.json (${newContrib.size})")
}

updateContributors(useETag = true)