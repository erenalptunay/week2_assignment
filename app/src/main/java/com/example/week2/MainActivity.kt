package com.example.week2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.week2.ui.theme.Week2_assignmentTheme

@Immutable
data class HardwareItem(
    val id: String,
    val name: String,
    val category: String,
    val cost: Double,
    val stockCount: Int
)

val hardwareList = listOf(
    HardwareItem(id = "101", name = "MacBook Pro M3 Max", category = "Geliştirici Bilgisayarı", cost = 3499.0, stockCount = 5),
    HardwareItem(id = "102", name = "NVIDIA RTX 4090 Sunucu", category = "Yapay Zeka / GPU", cost = 1599.0, stockCount = 2),
    HardwareItem(id = "103", name = "Google Pixel 8 Pro", category = "Mobil Test Cihazı", cost = 999.0, stockCount = 8),
    HardwareItem(id = "104", name = "Dell UltraSharp 49\"", category = "Monitör", cost = 1199.0, stockCount = 4),
    HardwareItem(id = "105", name = "Sony WH-1000XM5 Kulaklık", category = "Odaklanma Ekipmanı", cost = 399.0, stockCount = 12),
    HardwareItem(id = "106", name = "Raspberry Pi 5 8GB", category = "IoT Geliştirme Kiti", cost = 85.0, stockCount = 25),
    HardwareItem(id = "107", name = "HHKB Professional Keyboard", category = "Ergonomik Klavye", cost = 299.0, stockCount = 0), // Stok sınırı test verisi
    HardwareItem(id = "108", name = "AMD Ryzen Threadripper", category = "Yapay Zeka / GPU", cost = 4999.0, stockCount = 1)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Week2_assignmentTheme {
                DevClusterScreen()
            }
        }
    }
}

// Tek merkezi Stateful kapsayıcı: tüm durum burada tutulur, alt bileşenler stateless.
@Composable
fun DevClusterScreen() {
    // Ekran döndüğünde (Bundle) korunması gereken durumlar
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedIds by rememberSaveable { mutableStateOf(listOf<String>()) }

    // Sadece searchQuery değiştiğinde çalışan filtre
    val filteredItems = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            hardwareList
        } else {
            hardwareList.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                    it.category.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Seçim değiştiğinde yeniden hesaplanan toplam harcama
    val totalSpent = remember(selectedIds) {
        hardwareList.filter { it.id in selectedIds }.sumOf { it.cost }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "DevCluster",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "Toplam ödenek: $${"%,.2f".format(totalSpent)} (${selectedIds.size} kalem)",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            HardwareSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )
            LazyColumn(
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredItems, key = { it.id }) { item ->
                    HardwareCard(
                        item = item,
                        selected = item.id in selectedIds,
                        onClick = {
                            selectedIds = if (item.id in selectedIds) {
                                selectedIds - item.id
                            } else {
                                selectedIds + item.id
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HardwareSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Donanım ara") },
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun HardwareCard(
    item: HardwareItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val outOfStock = item.stockCount == 0
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (outOfStock) 0.5f else 1f)
            .clickable(enabled = !outOfStock, onClick = onClick) // önce clickable, sonra padding
            .padding(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = item.name, style = MaterialTheme.typography.titleMedium)
            Text(text = item.category, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "$${"%,.2f".format(item.cost)} • " +
                    if (outOfStock) "Stokta yok" else "Stok: ${item.stockCount}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DevClusterPreview() {
    Week2_assignmentTheme {
        DevClusterScreen()
    }
}
