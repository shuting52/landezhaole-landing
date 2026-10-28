package com.landezhaole.landing

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri

@Composable
fun LandingApp() {
    var dark by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val categories = remember { loadSiteData(context) }
    val total = remember(categories) { categories.sumOf { it.sites.size } }
    val newCount = remember(categories) {
        categories.sumOf { cat -> cat.sites.count { it.badge == "NEW" } }
    }

    var query by rememberSaveable { mutableStateOf("") }
    var catIndex by rememberSaveable { mutableStateOf(0) }
    var subId by rememberSaveable { mutableStateOf("all") }

    val selectedCat = categories.getOrNull(catIndex)
    val searching = query.isNotBlank()

    // Search ignores category/sub and scans every category; otherwise show the
    // selected category, filtered by the selected sub-category ("all" = no filter).
    val shown: List<Pair<SiteCategory, Site>> = remember(categories, query, catIndex, subId) {
        if (searching) {
            val q = query.trim().lowercase()
            categories.flatMap { cat ->
                cat.sites.filter {
                    it.title.lowercase().contains(q) ||
                        it.desc.lowercase().contains(q) ||
                        it.url.lowercase().contains(q)
                }.map { cat to it }
            }
        } else if (selectedCat != null) {
            selectedCat.sites
                .filter { subId == "all" || it.sub == subId }
                .map { selectedCat to it }
        } else {
            emptyList()
        }
    }

    fun openSite(site: Site) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, site.url.toUri())
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "打不开这个链接", Toast.LENGTH_SHORT).show()
        }
    }

    LandingTheme(dark = dark) {
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp),
        ) {
            Row(
                Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text("懒得找了", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "共 $total 个站点 · $newCount 个 NEW · 数据 v1.1.44",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = { dark = !dark }) {
                    Icon(
                        if (dark) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                        contentDescription = "切换浅深",
                    )
                }
            }

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                placeholder = { Text("搜站点名、介绍…") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "清空")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
            )

            if (!searching && categories.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                ) {
                    items(categories.size) { i ->
                        Chip(
                            label = categories[i].name,
                            selected = i == catIndex,
                            onClick = {
                                catIndex = i
                                subId = "all"
                            },
                        )
                    }
                }
                val subs = selectedCat?.subs.orEmpty()
                if (subs.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp),
                    ) {
                        item {
                            Chip(label = "全部", selected = subId == "all") { subId = "all" }
                        }
                        items(subs.size) { i ->
                            Chip(
                                label = subs[i].name,
                                selected = subId == subs[i].id,
                                onClick = { subId = subs[i].id },
                            )
                        }
                    }
                }
                selectedCat?.let {
                    Text(
                        it.name,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
            } else if (searching) {
                Text(
                    "在全部 ${categories.size} 个分类中找到 ${shown.size} 个结果",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }

            if (categories.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("数据没读出来，请重新安装", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else if (shown.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("没有找到，换个词试试", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                ) {
                    // Key by position, not by URL: the same URL can appear in
                    // more than one category, and duplicate Lazy keys would crash.
                    itemsIndexed(shown, key = { index, _ -> index }) { _, (cat, site) ->
                        SiteCard(
                            site = site,
                            categoryName = if (searching) cat.name else null,
                            onClick = { openSite(site) },
                        )
                    }
                    item {
                        Text(
                            "点卡片在浏览器打开 · 共 $total 个站点",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Chip(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val fg = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
    Box(
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, color = fg)
    }
}

@Composable
private fun SiteCard(site: Site, categoryName: String?, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            Box(
                Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(avatarColorFor(site.title)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    site.title.firstOrNull()?.toString().orEmpty(),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        site.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (site.badge.isNotBlank()) {
                        Spacer(Modifier.width(6.dp))
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (site.badge == "NEW") Color(0xFFE84D3D) else Color(0xFFD97706),
                                )
                                .padding(horizontal = 7.dp, vertical = 3.dp),
                        ) {
                            Text(site.badge, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                if (categoryName != null) {
                    Text(categoryName, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
                if (site.desc.isNotBlank()) {
                    Text(
                        site.desc,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                Text(
                    site.url,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Spacer(Modifier.width(8.dp))
            Icon(
                Icons.Filled.OpenInNew,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
