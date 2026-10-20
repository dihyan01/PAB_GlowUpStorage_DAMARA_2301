
package com.example.myapplication_damaradihyan_2301_pab

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

private val Context.glowUpDataStore by preferencesDataStore(
    name = "glowup_storage"
)

data class GlowUpData(
    val name: String = "Cantik",
    val skinType: String = "Kombinasi",
    val reminder: Boolean = true,
    val products: List<String> = emptyList(),
    val morningDone: List<Boolean> = List(5) { false },
    val nightDone: List<Boolean> = List(5) { false },
    val water: Int = 0
)

class GlowUpStorage(context: Context) {
    private val dataStore = context.applicationContext.glowUpDataStore
    private val dataKey = stringPreferencesKey("glowup_data")

    val data: Flow<GlowUpData> = dataStore.data
        .catch { error ->
            if (error is IOException) {
                emit(androidx.datastore.preferences.core.emptyPreferences())
            } else {
                throw error
            }
        }
        .map { preferences ->
            decode(preferences[dataKey] ?: "")
        }

    suspend fun save(value: GlowUpData) {
        dataStore.edit { preferences ->
            preferences[dataKey] = encode(value)
        }
    }

    private fun encode(value: GlowUpData): String {
        val json = JSONObject()
        json.put("name", value.name)
        json.put("skinType", value.skinType)
        json.put("reminder", value.reminder)
        json.put("products", JSONArray(value.products))
        json.put("morningDone", JSONArray(value.morningDone))
        json.put("nightDone", JSONArray(value.nightDone))
        json.put("water", value.water)
        return json.toString()
    }

    private fun decode(raw: String): GlowUpData {
        if (raw.isBlank()) return GlowUpData()

        return try {
            val json = JSONObject(raw)

            GlowUpData(
                name = json.optString("name", "Cantik"),
                skinType = json.optString("skinType", "Kombinasi"),
                reminder = json.optBoolean("reminder", true),
                products = jsonArrayToStrings(
                    json.optJSONArray("products")
                ),
                morningDone = jsonArrayToBooleans(
                    json.optJSONArray("morningDone")
                ),
                nightDone = jsonArrayToBooleans(
                    json.optJSONArray("nightDone")
                ),
                water = json.optInt("water", 0).coerceAtLeast(0)
            )
        } catch (_: Exception) {
            GlowUpData()
        }
    }

    private fun jsonArrayToStrings(array: JSONArray?): List<String> {
        if (array == null) return emptyList()

        return List(array.length()) { index ->
            array.optString(index, "")
        }.filter { it.isNotBlank() }
    }

    private fun jsonArrayToBooleans(array: JSONArray?): List<Boolean> {
        return List(5) { index ->
            array?.optBoolean(index, false) ?: false
        }
    }
}
