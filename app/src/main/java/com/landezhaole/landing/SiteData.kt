package com.landezhaole.landing

import android.content.Context
import org.json.JSONObject

/** One sub-category inside a category, e.g. "美国/海外免翻". */
data class SiteSub(
    val id: String,
    val name: String,
)

/** One site card. Field names in the bundled JSON are short: t=title, u=url, d=desc, b=badge, s=sub. */
data class Site(
    val title: String,
    val url: String,
    val desc: String,
    val badge: String,
    val sub: String,
)

/** One top-level category from the website data.json. */
data class SiteCategory(
    val id: String,
    val name: String,
    val desc: String,
    val subs: List<SiteSub>,
    val sites: List<Site>,
)

/**
 * Loads the bundled website data (assets/landezhaole-data.json).
 * Uses org.json, which ships with Android, so no extra JSON dependency is needed.
 * Returns an empty list on any parse failure instead of crashing the app.
 */
fun loadSiteData(context: Context): List<SiteCategory> {
    return try {
        val text = context.assets.open("landezhaole-data.json")
            .bufferedReader(Charsets.UTF_8).use { it.readText() }
        val root = JSONObject(text)
        val cats = root.getJSONArray("cats")
        buildList {
            for (i in 0 until cats.length()) {
                val cat = cats.getJSONObject(i)
                val subs = buildList {
                    val arr = cat.optJSONArray("subs") ?: return@buildList
                    for (j in 0 until arr.length()) {
                        val s = arr.getJSONObject(j)
                        add(SiteSub(s.optString("id"), s.optString("name")))
                    }
                }
                val sites = buildList {
                    val arr = cat.optJSONArray("cards") ?: return@buildList
                    for (j in 0 until arr.length()) {
                        val c = arr.getJSONObject(j)
                        add(
                            Site(
                                title = c.optString("t"),
                                url = c.optString("u"),
                                desc = c.optString("d"),
                                badge = c.optString("b"),
                                sub = c.optString("s"),
                            )
                        )
                    }
                }
                add(
                    SiteCategory(
                        id = cat.optString("id"),
                        name = cat.optString("name"),
                        desc = cat.optString("desc"),
                        subs = subs,
                        sites = sites,
                    )
                )
            }
        }
    } catch (_: Exception) {
        emptyList()
    }
}
