
package com.example.myapplication_damaradihyan_2301_pab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val Ivory = Color(0xFFFAF6F0)
private val Paper = Color(0xFFFFFDF9)
private val DustyPink = Color(0xFFB86F82)
private val Rose = Color(0xFFE7C3CA)
private val RoseLight = Color(0xFFF4E4E5)
private val Ink = Color(0xFF3F3033)
private val Muted = Color(0xFF89777A)
private val Sage = Color(0xFF8D9D83)

private val morningSteps = listOf(
    "Facial wash",
    "Toner",
    "Serum",
    "Moisturizer",
    "Sunscreen"
)

private val nightSteps = listOf(
    "Makeup remover",
    "Facial wash",
    "Toner",
    "Serum",
    "Night moisturizer"
)

private val skinTypes = listOf(
    "Normal",
    "Kering",
    "Berminyak",
    "Kombinasi",
    "Sensitif"
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val storage = GlowUpStorage(applicationContext)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Ivory
                ) {
                    GlowUpApp(storage)
                }
            }
        }
    }
}

@Composable
private fun GlowUpApp(storage: GlowUpStorage) {
    val data by storage.data.collectAsState(
        initial = GlowUpData()
    )

    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    fun updateData(newData: GlowUpData) {
        scope.launch {
            storage.save(newData)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Ivory)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(22.dp))

            when (selectedTab) {
                0 -> DashboardScreen(
                    data = data,
                    onMorningChange = { index, checked ->
                        val updated = data.morningDone.toMutableList()
                        updated[index] = checked
                        updateData(data.copy(morningDone = updated))
                    },
                    onNightChange = { index, checked ->
                        val updated = data.nightDone.toMutableList()
                        updated[index] = checked
                        updateData(data.copy(nightDone = updated))
                    },
                    onWaterChange = { updateData(data.copy(water = it)) }
                )

                1 -> ProductsScreen(
                    products = data.products,
                    onProductsChange = {
                        updateData(data.copy(products = it))
                    }
                )

                2 -> ProgressScreen(data = data)

                3 -> ProfileScreen(
                    data = data,
                    onDataChange = ::updateData
                )
            }

            Spacer(Modifier.height(24.dp))
        }

        NavigationBar(
            containerColor = Paper,
            contentColor = DustyPink
        ) {
            val tabs = listOf(
                Triple("♡", "Rutinitas", 0),
                Triple("✧", "Produk", 1),
                Triple("❀", "Progres", 2),
                Triple("○", "Profil", 3)
            )

            tabs.forEach { (symbol, label, index) ->
                NavigationBarItem(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    icon = {
                        Text(
                            text = symbol,
                            fontSize = 23.sp,
                            color = if (selectedTab == index) {
                                DustyPink
                            } else {
                                Muted
                            }
                        )
                    },
                    label = {
                        Text(
                            text = label,
                            fontSize = 11.sp
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun PageHeading(
    eyebrow: String,
    title: String,
    subtitle: String
) {
    Text(
        text = eyebrow.uppercase(),
        color = DustyPink,
        fontSize = 11.sp,
        letterSpacing = 2.sp,
        fontWeight = FontWeight.SemiBold
    )

    Spacer(Modifier.height(6.dp))

    Text(
        text = title,
        color = Ink,
        fontSize = 29.sp,
        lineHeight = 35.sp,
        fontWeight = FontWeight.SemiBold
    )

    Spacer(Modifier.height(5.dp))

    Text(
        text = subtitle,
        color = Muted,
        fontSize = 14.sp,
        lineHeight = 21.sp
    )

    Spacer(Modifier.height(22.dp))
}

@Composable
private fun SoftCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Paper
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    trailing: String = ""
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = Ink,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        if (trailing.isNotBlank()) {
            Text(
                text = trailing,
                color = DustyPink,
                fontSize = 12.sp
            )
        }
    }

    Spacer(Modifier.height(12.dp))
}

@Composable
private fun DashboardScreen(
    data: GlowUpData,
    onMorningChange: (Int, Boolean) -> Unit,
    onNightChange: (Int, Boolean) -> Unit,
    onWaterChange: (Int) -> Unit
) {
    PageHeading(
        eyebrow = "YOUR PERSONAL GLOW SPACE",
        title = "Hello, ${data.name.ifBlank { "Cantik" }} ♡",
        subtitle = "Luangkan waktu untuk dirimu hari ini."
    )

    val completed = data.morningDone.count { it } +
            data.nightDone.count { it }

    val total = morningSteps.size + nightSteps.size
    val progress = completed.toFloat() / total.toFloat()

    SoftCard {
        Text(
            "YOUR DAILY GLOW",
            color = DustyPink,
            fontSize = 11.sp,
            letterSpacing = 1.8.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                "${(progress * 100).toInt()}%",
                fontSize = 38.sp,
                fontWeight = FontWeight.SemiBold,
                color = Ink
            )

            Spacer(Modifier.width(8.dp))

            Text(
                "of your routine",
                color = Muted,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 7.dp)
            )
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = DustyPink,
            trackColor = RoseLight
        )

        Spacer(Modifier.height(12.dp))

        Text(
            "$completed dari $total langkah selesai. " +
                    "Pelan-pelan, kamu sudah melakukan yang terbaik.",
            color = Muted,
            fontSize = 13.sp,
            lineHeight = 20.sp
        )
    }

    Spacer(Modifier.height(22.dp))

    SectionTitle("Morning ritual", "☀️ Pagi")

    SoftCard {
        ChecklistContent(
            steps = morningSteps,
            checkedItems = data.morningDone,
            onCheckedChange = onMorningChange
        )
    }

    Spacer(Modifier.height(20.dp))

    SectionTitle("Evening ritual", "🌙 Malam")

    SoftCard {
        ChecklistContent(
            steps = nightSteps,
            checkedItems = data.nightDone,
            onCheckedChange = onNightChange
        )
    }

    Spacer(Modifier.height(20.dp))

    SectionTitle("Hydration corner", "💧")

    SoftCard {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Minum air putih",
                    color = Ink,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    "${data.water} dari 8 gelas hari ini",
                    color = Muted,
                    fontSize = 13.sp
                )
            }

            Text("💧", fontSize = 30.sp)
        }

        Spacer(Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = { (data.water / 8f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = Sage,
            trackColor = Color(0xFFE7EBDD)
        )

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    onWaterChange((data.water - 1).coerceAtLeast(0))
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = RoseLight,
                    contentColor = Ink
                ),
                shape = CircleShape
            ) {
                Text("−", fontSize = 20.sp)
            }

            Button(
                onClick = {
                    onWaterChange(data.water + 1)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DustyPink
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("+ Tambah 1 gelas")
            }
        }
    }

    Spacer(Modifier.height(18.dp))

    Text(
        "♡  Consistency over perfection.",
        modifier = Modifier.fillMaxWidth(),
        color = DustyPink,
        fontSize = 13.sp
    )
}

@Composable
private fun ChecklistContent(
    steps: List<String>,
    checkedItems: List<Boolean>,
    onCheckedChange: (Int, Boolean) -> Unit
) {
    steps.forEachIndexed { index, step ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checkedItems.getOrElse(index) { false },
                onCheckedChange = {
                    onCheckedChange(index, it)
                }
            )

            Text(
                text = step,
                color = if (checkedItems.getOrElse(index) { false }) {
                    Muted
                } else {
                    Ink
                },
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )

            if (checkedItems.getOrElse(index) { false }) {
                Text("✓", color = Sage, fontSize = 17.sp)
            }
        }
    }
}

@Composable
private fun ProductsScreen(
    products: List<String>,
    onProductsChange: (List<String>) -> Unit
) {
    var productName by rememberSaveable {
        mutableStateOf("")
    }

    PageHeading(
        eyebrow = "YOUR BEAUTY SHELF",
        title = "My Products ✧",
        subtitle = "Koleksi skincare yang menemani perjalanan glow-up kamu."
    )

    SoftCard {
        Text(
            "Tambahkan produk",
            fontSize = 18.sp,
            color = Ink,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = productName,
            onValueChange = { productName = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nama produk") },
            placeholder = { Text("Contoh: Hydrating toner") },
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                val cleanName = productName.trim()
                if (cleanName.isNotEmpty()) {
                    onProductsChange(products + cleanName)
                    productName = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = DustyPink
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("+  Tambah ke koleksi")
        }
    }

    Spacer(Modifier.height(22.dp))

    SectionTitle("Koleksi kamu", "${products.size} produk")

    if (products.isEmpty()) {
        SoftCard {
            Text("🌸", fontSize = 30.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                "Rak skincare-mu masih kosong",
                color = Ink,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(5.dp))
            Text(
                "Tambahkan produk favoritmu di atas.",
                color = Muted,
                fontSize = 13.sp
            )
        }
    } else {
        products.forEachIndexed { index, product ->
            SoftCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(15.dp),
                        color = RoseLight,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("✧", color = DustyPink, fontSize = 24.sp)
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    Text(
                        product,
                        color = Ink,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )

                    TextButton(
                        onClick = {
                            onProductsChange(
                                products.filterIndexed { itemIndex, _ ->
                                    itemIndex != index
                                }
                            )
                        }
                    ) {
                        Text("Hapus", color = DustyPink)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun ProgressScreen(data: GlowUpData) {
    val total = morningSteps.size + nightSteps.size
    val completed = data.morningDone.count { it } +
            data.nightDone.count { it }
    val progress = completed.toFloat() / total.toFloat()

    PageHeading(
        eyebrow = "YOUR LITTLE WINS",
        title = "My Progress ♡",
        subtitle = "Setiap langkah kecil tetap berarti."
    )

    SoftCard {
        Text(
            "DAILY SKINCARE PROGRESS",
            color = DustyPink,
            fontSize = 11.sp,
            letterSpacing = 1.5.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(12.dp))

        Text(
            "${(progress * 100).toInt()}%",
            fontSize = 42.sp,
            color = Ink,
            fontWeight = FontWeight.SemiBold
        )

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = DustyPink,
            trackColor = RoseLight
        )

        Spacer(Modifier.height(10.dp))

        Text(
            "$completed dari $total langkah skincare sudah kamu centang.",
            color = Muted,
            fontSize = 13.sp
        )
    }

    Spacer(Modifier.height(16.dp))

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SoftCard(modifier = Modifier.weight(1f)) {
            Text("☀️", fontSize = 25.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                "${data.morningDone.count { it }}/5",
                fontSize = 25.sp,
                color = Ink,
                fontWeight = FontWeight.SemiBold
            )
            Text("Rutinitas pagi", fontSize = 12.sp, color = Muted)
        }

        SoftCard(modifier = Modifier.weight(1f)) {
            Text("🌙", fontSize = 25.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                "${data.nightDone.count { it }}/5",
                fontSize = 25.sp,
                color = Ink,
                fontWeight = FontWeight.SemiBold
            )
            Text("Rutinitas malam", fontSize = 12.sp, color = Muted)
        }
    }

    Spacer(Modifier.height(16.dp))

    SoftCard {
        Text("💧", fontSize = 27.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            "Hidrasi hari ini",
            color = Ink,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(5.dp))
        Text(
            "${data.water} gelas tercatat",
            color = Muted,
            fontSize = 14.sp
        )
        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { (data.water / 8f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = Sage,
            trackColor = Color(0xFFE7EBDD)
        )
    }

    Spacer(Modifier.height(16.dp))

    SoftCard {
        Text("A gentle reminder ✧", color = DustyPink, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            "Glow-up bukan tentang menjadi sempurna. " +
                    "Ini tentang merawat diri dengan konsisten dan penuh kasih.",
            color = Ink,
            fontSize = 15.sp,
            lineHeight = 23.sp
        )
    }
}

@Composable
private fun ProfileScreen(
    data: GlowUpData,
    onDataChange: (GlowUpData) -> Unit
) {
    PageHeading(
        eyebrow = "YOUR SELF-CARE SPACE",
        title = "My Profile ♡",
        subtitle = "Kenali kebutuhan kulit dan kebiasaanmu."
    )

    SoftCard {
        Surface(
            shape = CircleShape,
            color = RoseLight,
            modifier = Modifier.size(76.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = data.name.firstOrNull()?.uppercase() ?: "G",
                    color = DustyPink,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Nama panggilan",
            color = Ink,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(5.dp))

        OutlinedTextField(
            value = data.name,
            onValueChange = { onDataChange(data.copy(name = it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Masukkan namamu") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(Modifier.height(18.dp))

        Text(
            "Jenis kulit",
            color = Ink,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(10.dp))

        skinTypes.forEach { skinType ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = data.skinType == skinType,
                    onCheckedChange = {
                        onDataChange(data.copy(skinType = skinType))
                    }
                )

                Text(
                    skinType,
                    color = Ink,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        HorizontalDivider(color = RoseLight)
        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Pengingat skincare",
                    color = Ink,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    "Preferensi pengingat rutinitas",
                    color = Muted,
                    fontSize = 12.sp
                )
            }

            Switch(
                checked = data.reminder,
                onCheckedChange = {
                    onDataChange(data.copy(reminder = it))
                }
            )
        }
    }

    Spacer(Modifier.height(18.dp))

    SoftCard {
        Text("Made with ♡", color = DustyPink, fontSize = 14.sp)
        Spacer(Modifier.height(6.dp))
        Text(
            "GlowUp — your personal self-care companion.",
            color = Muted,
            fontSize = 13.sp
        )
    }

    Spacer(Modifier.height(18.dp))

    Text(
        "Catatan: tombol pengingat saat ini menyimpan preferensimu, " +
                "tetapi belum menjadwalkan notifikasi Android.",
        color = Muted,
        fontSize = 12.sp,
        lineHeight = 18.sp
    )
}
