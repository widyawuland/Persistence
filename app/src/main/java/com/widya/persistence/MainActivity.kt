package com.widya.persistence

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.lifecycleScope
import com.widya.persistence.ui.theme.PersistenceTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inisialisasiData()

        setContent {
            PersistenceTheme {
                var daftarWarna by remember { mutableStateOf<List<Color>>(emptyList()) }
                var sedangMemuat by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    withContext(Dispatchers.IO) {
                        try {
                            val db = ColorDatabase.getInstances(this@MainActivity)
                            daftarWarna = db.colorDao().getAll()
                        } catch (e: Exception) {
                            Log.e("DatabaseError", "Gagal mengambil data: ${e.message}")
                        } finally {
                            sedangMemuat = false
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Daftar Warna",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    when {
                        sedangMemuat -> Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }

                        daftarWarna.isEmpty() -> Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Tidak ada data warna", fontSize = 16.sp)
                        }

                        else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(daftarWarna) { warna ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(text = warna.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                            Text(text = warna.hex, fontSize = 14.sp)
                                            Text(text = "ID: ${warna.val_id}", fontSize = 12.sp)
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Box(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .background(
                                                    try {
                                                        ComposeColor(android.graphics.Color.parseColor(warna.hex))
                                                    } catch (e: Exception) {
                                                        ComposeColor.Gray
                                                    },
                                                    shape = CircleShape
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun inisialisasiData() {
        val merah = Color("Merah", "#FF0000")
        val biru = Color("Biru", "#0000FF")

        lifecycleScope.launch(Dispatchers.IO) {
            val db = ColorDatabase.getInstances(this@MainActivity)
            val data = db.colorDao().getAll()
            if (data.isEmpty()) {
                db.colorDao().insert(merah, biru)
                Log.d("DatabaseInit", "Data warna ditambahkan")
            }
        }
    }
}