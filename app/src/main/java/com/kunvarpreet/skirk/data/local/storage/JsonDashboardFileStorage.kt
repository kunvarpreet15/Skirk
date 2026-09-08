package com.kunvarpreet.skirk.data.local.storage

import android.content.Context
import androidx.core.util.AtomicFile
import com.kunvarpreet.skirk.domain.model.Dashboard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream

/**
 * Persists the structured dashboard configuration tree to disk using atomic writes and JSON serialization.
 * Ensures data survives process death and application restarts without corruption.
 */
class JsonDashboardFileStorage(
    private val context: Context,
    private val fileName: String = "dashboard_config.json"
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val storageFile: File
        get() = File(context.filesDir, fileName)

    suspend fun loadDashboard(): Dashboard? = withContext(Dispatchers.IO) {
        val file = storageFile
        if (!file.exists() || file.length() == 0L) {
            return@withContext null
        }
        try {
            val content = file.readText()
            json.decodeFromString<Dashboard>(content)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun saveDashboard(dashboard: Dashboard) = withContext(Dispatchers.IO) {
        try {
            val atomicFile = AtomicFile(storageFile)
            val jsonString = json.encodeToString(dashboard)
            val bytes = jsonString.toByteArray(Charsets.UTF_8)
            var outputStream: FileOutputStream? = null
            try {
                outputStream = atomicFile.startWrite()
                outputStream.write(bytes)
                atomicFile.finishWrite(outputStream)
            } catch (e: Exception) {
                if (outputStream != null) {
                    atomicFile.failWrite(outputStream)
                }
                throw e
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun clear() = withContext(Dispatchers.IO) {
        if (storageFile.exists()) {
            storageFile.delete()
        }
    }
}
