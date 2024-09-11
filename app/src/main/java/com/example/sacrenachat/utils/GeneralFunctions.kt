package com.example.sacrenachat.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateUtils
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.example.sacrenachat.BuildConfig
import com.example.sacrenachat.repository.networkrequest.WebConstants
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.util.*

object GeneralFunctions {

    private const val ALPHA_NUMERIC_CHARS =
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
    private const val NUMERIC_CHARS = "1234567890"
    const val JPEG_FILE_PREFIX = "IMG_"
    private const val JPEG_FILE_SUFFIX = ".jpg"
    const val MP4_FILE_PREFIX = "VID_"
    private const val MP4_FILE_SUFFIX = ".mp4"
    private const val THREE_GPP_FILE_PREFIX = "AUD_"
    private const val THREE_GPP_FILE_SUFFIX = ".3gp"
    private const val MIN_PASSWORD_LENGTH = 6
    private const val MAX_PASSWORD_LENGTH = 15

    val isAboveLollipopDevice: Boolean
        get() = Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT

    fun hideKeyboard(fragment: androidx.fragment.app.Fragment) {
        (fragment.requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(fragment.view?.rootView?.windowToken, 0)
    }

    fun showKeyboard(context: Context) {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
    }

    @Throws(IOException::class)
    fun setUpImageFile(directory: String): File? {
        var imageFile: File? = null
        if (Environment.MEDIA_MOUNTED == Environment
                .getExternalStorageState()
        ) {
            val storageDir = File(directory)
            if (!storageDir.mkdirs()) {
                if (!storageDir.exists()) {
                    Log.d("CameraSample", "failed to create directory")
                    return null
                }
            }

            imageFile = File.createTempFile(
                JPEG_FILE_PREFIX
                        + System.currentTimeMillis() + "_",
                JPEG_FILE_SUFFIX, storageDir
            )
        }
        return imageFile
    }

    @Throws(IOException::class)
    fun setUpVideoFile(directory: String): File? {
        var videoFile: File? = null
        if (Environment.MEDIA_MOUNTED == Environment
                .getExternalStorageState()
        ) {
            val storageDir = File(directory)
            if (!storageDir.mkdirs()) {
                if (!storageDir.exists()) {
                    Log.d("CameraSample", "failed to create directory")
                    return null
                }
            }

            videoFile = File.createTempFile(
                MP4_FILE_PREFIX
                        + System.currentTimeMillis() + "_",
                MP4_FILE_SUFFIX, storageDir
            )
        }
        return videoFile
    }

    fun getThumbnail(context: Context, uriString: String): File? {
        val bitmap = ThumbnailUtils.createVideoThumbnail(
            uriString,
            MediaStore.Video.Thumbnails.FULL_SCREEN_KIND
        )
        val file =
            GeneralFunctions.setUpImageFile(Constants.LOCAL_STORAGE_BASE_PATH_FOR_USER_POST_PHOTOS)!!
        try {
            val fOut: OutputStream?
            fOut = FileOutputStream(file)
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 85, fOut)
            fOut.flush()
            fOut.close()
            MediaStore.Images.Media.insertImage(
                context.contentResolver,
                file.absolutePath, file.name,
                file.name
            )
            return file
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getAge(day: Int, month: Int, year: Int): Int {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()

        dob.set(year, month, day)

//        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

//        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
//            age--
//        }

        return today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
    }

    fun changeDateFormat(
        dob: String, currentFormat: String,
        requiredFormat: String
    ): String {
        try {
            return SimpleDateFormat(requiredFormat, Locale.US)
                .format(SimpleDateFormat(currentFormat, Locale.US).parse(dob))
//            return  SimpleDateFormat("h:mm a d MMM", Locale.US).format(SimpleDateFormat("yyyy-MM-dd H:mm:ss", Locale.US).parse(("2019-04-11T11:10:49.274Z".substring(0,"2019-04-11T11:10:49.274Z".indexOf("."))).replace("T"," ")))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    fun dateFromInt(date: Int, month: Int, year: Int): String {
        return "$date/$month/$year"
    }

    fun getDateCurrentTimeZone(timestamp: Long): String {
        try {
            val calendar = Calendar.getInstance()
            val tz = TimeZone.getDefault()
            calendar.timeInMillis = timestamp
//            calendar.timeInMillis = timestamp * 1000
//            calendar.add(Calendar.MILLISECOND, tz.rawOffset)
            val sdf = SimpleDateFormat("h:mm a d MMM yyyy")
            calendar.timeZone = TimeZone.getDefault()
            val currentTimeZone = calendar.time as Date
            return sdf.format(currentTimeZone)
        } catch (e: Exception) {
        }
        return ""
    }

    fun generateRandomString(randomStringLength: Int): String {
        val buffer = StringBuffer()
        val charactersLength = ALPHA_NUMERIC_CHARS.length
        for (i in 0 until randomStringLength) {
            val index = Math.random() * charactersLength
            buffer.append(ALPHA_NUMERIC_CHARS[index.toInt()])
        }
        return buffer.toString()
    }

    fun generateRandomNumber(randomStringLength: Int): Long {
        val buffer = StringBuffer()
        val charactersLength = NUMERIC_CHARS.length
        for (i in 0 until randomStringLength) {
            val index = Math.random() * charactersLength
            buffer.append(NUMERIC_CHARS[index.toInt()])
        }
        return buffer.toString().toLong()
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return null != target && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length in MIN_PASSWORD_LENGTH..MAX_PASSWORD_LENGTH
    }

    fun getResizedImage(path: String, identifier: String): String {
        return WebConstants.ACTION_BASE_URL_FOR_MEDIA + path + "/$identifier"
    }

    fun getVideoPath(identifier: String): String {
        return WebConstants.ACTION_BASE_URL_FOR_MEDIA + "videos" + "/$identifier"
    }

    fun getLocalImageUri(file: File): Uri {
        return Uri.parse("file://$file")
    }


    fun shareData(context: Context, body: String, subject: String, title: String) {
        val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, body)
        context.startActivity(Intent.createChooser(sharingIntent, title))
    }

    fun shareDataWhatsapp(context: Context, body: String) {
        val whatsappIntent = Intent(Intent.ACTION_SEND)
        whatsappIntent.type = "text/plain"
        whatsappIntent.setPackage("com.whatsapp")
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, body)
        try {
            context.startActivity(whatsappIntent)
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(context, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * @description Returns a dialog using the given context to show the current build info.
     */
    fun fetchBuildInfoDialog(context: Context): MaterialDialog {
        val apiEndPoint = WebConstants.ACTION_BASE_URL_FOR_APIS.split("/", limit = 4).lastOrNull()
        val imageBucket = WebConstants.ACTION_BASE_URL_FOR_MEDIA.split("/", limit = 4).lastOrNull()
        val socketEndPoint = if (WebConstants.CHAT_SERVER_URL.isNotEmpty()) {
            WebConstants.CHAT_SERVER_URL
        } else {
            "Nil"
        }
        val time = try {
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochMilli(BuildConfig.BUILD_TIME.toLong()))
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }

        return MaterialDialog(context).apply {
            title(text = "Build Info")
            message(text = "API: $apiEndPoint\n\nImageBucket: $imageBucket\n\nSocket: $socketEndPoint\n\nDate: $time")
        }
    }

    /**
     * Time Helper Methods
     * @description Frontend methods takes in input as Instant.toString() i.e ISO 8601 Format
     * and return according to required format for frontend.
     *
     * Backend methods takes in input the frontend formatted strings and return it in ISO 8601 format.
     *
     * Default values for formats are described in WebConstants.
     */
    fun getDateForFrontEnd(
        mInputTime: String,
        mOutputFormat: String = WebConstants.DATE_FORMAT_DISPLAY
    ): String {
        return try {
            // Convert Date from UTC according to Local Time Zone
            val outputFormatter = DateTimeFormatter.ofPattern(mOutputFormat, Locale.getDefault())
            val inputDate = Instant.parse(mInputTime).atZone(ZoneId.systemDefault())
            outputFormatter.format(inputDate)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun getDateForBackEnd(
        mInputTime: String,
        mInputFormat: String = WebConstants.DATE_FORMAT_DISPLAY
    ): String {
        return try {
            val inputFormatter = DateTimeFormatterBuilder().apply {
                parseCaseInsensitive()
                appendPattern(mInputFormat)
            }.toFormatter()
            val inputDate = LocalDate.parse(mInputTime, inputFormatter)
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(inputDate.atStartOfDay(ZoneId.systemDefault()))
        } catch (e: Exception) {
            ""
        }
    }

    fun getTimeForFrontEnd(
        mInputTime: String,
        mOutputFormat: String = WebConstants.TIME_FORMAT_DISPLAY
    ): String {
        return try {
            val outputFormatter = DateTimeFormatter.ofPattern(mOutputFormat, Locale.getDefault())
            val inputDate =
                ZonedDateTime.ofInstant(Instant.parse(mInputTime), ZoneId.systemDefault())
            outputFormatter.format(inputDate).toUpperCase(Locale.getDefault())
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun getTimeForBackEnd(
        mInputTime: String,
        mInputFormat: String = WebConstants.TIME_FORMAT_DISPLAY
    ): String {
        return try {
            val formatter = DateTimeFormatterBuilder().apply {
                parseCaseInsensitive()
                appendPattern(mInputFormat)
            }.toFormatter()
            val selectedDate = LocalTime.parse(mInputTime, formatter).atDate(LocalDate.now())
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(selectedDate.atZone(ZoneId.systemDefault()))
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun getRelativeTimeStamp(mInputTime: String): String {
        return try {
            DateUtils.getRelativeTimeSpanString(Instant.parse(mInputTime).toEpochMilli()).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun getTimeInMillis(
        mInputTime: String,
        mInputFormat: String = WebConstants.DATE_FORMAT_DISPLAY
    ): Long {
        return try {
            val inputFormatter = DateTimeFormatterBuilder().apply {
                parseCaseInsensitive()
                appendPattern(mInputFormat)
            }.toFormatter()
            val inputDate = LocalDate.parse(mInputTime, inputFormatter)
            inputDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        } catch (e: Exception) {
            0
        }
    }
}