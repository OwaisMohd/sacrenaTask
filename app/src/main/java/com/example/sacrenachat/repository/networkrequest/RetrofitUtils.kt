package com.example.sacrenachat.repository.networkrequest

import com.example.sacrenachat.views.utils.getMimeType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.net.URLConnection

object RetrofitUtils {

    private val MULTIPART_FORM_DATA = "multipart/form-data"

    fun stringToRequestBody(string: String): RequestBody {
        return string.toRequestBody(MULTIPART_FORM_DATA.toMediaTypeOrNull())
    }

    fun imageToRequestBody(file: File): RequestBody {
        return file.asRequestBody(file.getMimeType().toMediaTypeOrNull())
    }

    fun createPartFromFile(partName: String, file: File): MultipartBody.Part {
        // create RequestBody instance from file
        val requestFile = file.asRequestBody(file.getMimeType().toMediaTypeOrNull())

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    fun createPartFromInputStream(
        partName: String, inputStream: InputStream,
        defaultMimeType: String = "*/*",
    ): MultipartBody.Part {
        // Try to guess mime type else assign default.
        val mimeType = try {
            URLConnection.guessContentTypeFromStream(inputStream) ?: defaultMimeType
        } catch (e: Exception) {
            defaultMimeType
        }
        return MultipartBody.Part.createFormData(
            partName, "${System.currentTimeMillis()}",
            inputStream.readBytes().toRequestBody(mimeType.toMediaTypeOrNull())
        )
    }

    fun createJsonRequestBody(vararg params: Pair<String, Any>) =
        JSONObject(mapOf(*params)).toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

}