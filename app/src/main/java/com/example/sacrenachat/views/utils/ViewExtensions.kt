package com.example.sacrenachat.views.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.sacrenachat.R
import com.example.sacrenachat.utils.GeneralFunctions
import com.example.sacrenachat.utils.ValueMapping
import java.io.File
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale

/**
 *
 * TextView Extension Functions.
 *
 */


/**
 * @description Helper method to make text view Spannable.
 *
 *
 * @param str {String} part of string on which to perform change.
 * @param underlined {Boolean} whether the text should be underlined.
 * @param bold {Boolean} whether the text should be bold.
 * @param action {Lambda} Action to be performed onClick.
 */
fun TextView.makeTextLink(
    str: String, underlined: Boolean, bold: Boolean,
    color: Int?, action: (() -> Unit)? = null,
) {

    //Make Current Text of Text view as Spannable.
    val spannableString = SpannableString(this.text)

    //Assign text color as current text color or the give color.
    val textColor = color ?: this.currentTextColor

    //Make Clickable Span
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            //Perform action if there.
            action?.invoke()
        }

        override fun updateDrawState(drawState: TextPaint) {
            super.updateDrawState(drawState)
            //Assign text color and underline.
            drawState.isUnderlineText = underlined
            drawState.color = textColor
        }
    }
    //Index of the given string in the whole string, return if index not found i.e less than 0.
    val index = spannableString.indexOf(str, ignoreCase = true)
    if (index < 0) {
        return
    }

    if (bold) {
        //If Bold, make it bold.
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            index,
            index + str.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    //Set Span and Assign to tex view.
    spannableString.setSpan(
        clickableSpan,
        index,
        index + str.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    this.text = spannableString
    this.movementMethod = LinkMovementMethod.getInstance()
    this.highlightColor = Color.TRANSPARENT
}

/** Returns trimmed text for Edit text and TextView. */
val TextView.trimmedText: String
    get() = this.text.toString().trim()

/** Returns trimmed text for Edit text and TextView or null if empty. */
val TextView.trimmedTextOrNull: String?
    get() = if (this.text.toString().trim().isEmpty()) {
        null
    } else {
        this.text.toString().trim()
    }

/** Sets Color to the TextView via ContextCompat. */
fun TextView.setTextColorRes(@ColorRes color: Int) = setTextColor(context.getColorCompat(color))


/**
 *
 * General Extension Functions.
 *
 */


/** Sets Visibility of View to Visible. */
fun View.visible() {
    this.visibility = View.VISIBLE
}

/** Sets Visibility of View to Gone. */
fun View.gone() {
    this.visibility = View.GONE
}

/** Sets Visibility of View to Invisible. */
fun View.invisible() {
    this.visibility = View.INVISIBLE
}

/** Returns color via ContextCompat. */
fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

/** Helps in formatting double type. */
fun Double.format(digits: Int) = "%.${digits}f".format(this)

/** Returns first element for a list of String or empty String if list is empty */
fun List<String>.firstOrEmpty(): String = this.firstOrNull() ?: ""

/** Returns the mime type of the selected file else default value. */
fun File.getMimeType(fallback: String = "*/*"): String {
    return MimeTypeMap.getFileExtensionFromUrl(toString())
        ?.run {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                toLowerCase(Locale.getDefault())
            )
        }
        ?: fallback
}

/** Returns the mime type of the selected file else default value. */
fun Uri.getMimeType(contentResolver: ContentResolver, fallback: String = "*/*"): String {
    val mimeType: String?
    mimeType = if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
        contentResolver.getType(this)
    } else {
        val fileExtension = path?.let { uriPath ->
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uriPath)).toString())
        } ?: kotlin.run {
            MimeTypeMap.getFileExtensionFromUrl(toString())
        }
        MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(fileExtension.toLowerCase(Locale.getDefault()))
    }
    return mimeType ?: fallback
}

/** Returns LayoutInflater from ViewGroup */
val ViewGroup.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(this.context)

/** Fetches Context from root for a ViewBinding */
val <ViewBindingType : ViewBinding> ViewBindingType.context: Context
    get() = this.root.context

/** Sets Visibility of ViewBinding Root View to Gone. */
fun <ViewBindingType : ViewBinding> ViewBindingType.gone() {
    this.root.gone()
}

/** Sets Visibility of ViewBinding Root View to Visible. */
fun <ViewBindingType : ViewBinding> ViewBindingType.visible() {
    this.root.visible()
}

/**
 *
 * Fragment Extension Functions.
 *
 */


/** Hides Keyboard inside a Fragment. */
fun Fragment.hideKeyboard() {
    (this.requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(this.view?.rootView?.windowToken, 0)
}

/** Shows Keyboard for a Fragment. */
fun Fragment.showKeyboard() {
    (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
}

/** Provides supportFragmentManager */
fun Fragment.supportFragmentManager(): FragmentManager {
    return requireActivity().supportFragmentManager
}

/** Makes Fragment Full Screen. */
fun Fragment.makeFragmentFullScreen() {
    if (activity != null) {
        activity!!.window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.colorTransparent)
        activity!!.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}

/** Returns Fragment to it's default size. */
fun Fragment.makeFragmentNormalSize(isStatusBarWhite: Boolean) {
    //Returns status bar to primary color for other fragments.
    if (isStatusBarWhite) {
        activity?.window?.statusBarColor = ContextCompat.getColor(
            requireContext(),
            R.color.colorWhite
        )
        activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    } else {
        activity?.window?.statusBarColor = ContextCompat.getColor(
            requireContext(),
            R.color.colorPrimaryDark
        )
        activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }
}

/** Puts Arguments in the Fragment via Bundle by taking in Generic Type. */
inline fun <FRAG : Fragment> FRAG.putArgs(block: Bundle.() -> Unit): FRAG {
    /*
     * Takes in a block of code with scope of a Bundle.
     * Applies the parameters to the Bundle and returns the Fragment with that Bundle.
     */
    return this.apply {
        arguments = Bundle().apply(block)
    }
}


/**
 *
 * EditText Extension Functions.
 *
 */


/**
 * @description Verifies EditText For Errors. Returns true if errors are present.
 *              Compares the XML Input Value with Java type and returns status as per that.
 */
fun EditText.hasErrors(): Boolean {
    //The XML Input type Value and Java Value differs by 1, thus 1 is subtracted.
    return when (this.inputType - 1) {
        InputType.TYPE_TEXT_VARIATION_PASSWORD, InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD -> {
            //Returns False to indicate there are no-errors.
            !GeneralFunctions.isValidPassword(this.trimmedText)
        }

        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS -> {
            //Returns False to indicate there are no-errors.
            !GeneralFunctions.isValidEmail(this.trimmedText)
        }

        else -> {
            //Returns True to indicate there are errors if empty or null.
            trimmedText.isNullOrEmpty()
        }
    }
}

/**
 *  @description Displays Error for a specific EditText using the StringRes or Text Message.
 *
 *
 *  @param text {String} String message to be shown (if null method will use resId to show message)
 *  @param resId {Int?} resource id is string (will be used if message value is null)
 */
fun EditText.showError(
    @StringRes resId: Int? = null,
    text: String? = null,
) {
    if (resId == null && text == null) {
        //Do Nothing if both are null.
    } else {
        this.error = text ?: context.getString(resId!!)
        this.requestFocus()
    }
}

/**
 *  @description Lambda Extension for Adding Text Change Listener to EditText.
 *               Adds TextChangeListener and invokes the lambda function to return its params.
 *
 *               inline keyword inlines the call of the function to where it is being called.
 *               crossinline keyword presents the function to not return in case of inline call.
 *
 *  @param action {Lambda} Returns the parameters to wherever the function is being called.
 */
inline fun EditText.doOnTextChanged(crossinline action: (text: String, start: Int, before: Int, count: Int) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(char: CharSequence?, start: Int, before: Int, count: Int) {
            //Invokes the Lambda function if the char is not null.
            char?.let { text ->
                action.invoke(text.toString(), start, before, count)
            }
        }

        override fun afterTextChanged(s: Editable?) {
        }
    })
}

/**
 *  @description Lambda Extension for Adding Text Change Listener to EditText.
 *               Adds TextChangeListener and invokes the lambda function to return its params.
 *
 *               inline keyword inlines the call of the function to where it is being called.
 *               crossinline keyword presents the function to not return in case of inline call.
 *
 *  @param action {Lambda} Returns the parameters to wherever the function is being called.
 */
inline fun EditText.doBeforeTextChanged(crossinline action: (text: String, start: Int, count: Int, after: Int) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(char: CharSequence?, start: Int, count: Int, after: Int) {
            //Invokes the Lambda function if the char is not null.
            char?.let { text ->
                action.invoke(text.toString(), start, count, after)
            }
        }

        override fun onTextChanged(char: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
        }
    })
}

/**
 *  @description Lambda Extension for Adding Text Change Listener to EditText.
 *               Adds TextChangeListener and invokes the lambda function to return its params.
 *
 *               inline keyword inlines the call of the function to where it is being called.
 *               crossinline keyword presents the function to not return in case of inline call.
 *
 *  @param action {Lambda} Returns the parameters to wherever the function is being called.
 */
inline fun EditText.doAfterTextChanged(crossinline action: (text: String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(char: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            //Invokes the Lambda function if the Editable is not null.
            editable?.let { text ->
                action.invoke(text.toString())
            }
        }
    })
}


/**
 *
 * SearchView Extension Functions.
 *
 */


/**
 *  @description Lambda Extension for Adding Query Text Listener to SearchView.
 *               Adds OnQueryTextListener and invokes the lambda function to return its params.
 *
 *               inline keyword inlines the call of the function to where it is being called.
 *               crossinline keyword presents the function to not return in case of inline call.
 *
 *  @param action {Lambda} Returns the parameters to wherever the function is being called.
 */
inline fun SearchView.doOnQueryTextChange(crossinline action: (newText: String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            //Invokes the Lambda function if the text is not null.
            newText?.let { text ->
                action.invoke(text)
            }
            return false
        }
    })
}

/**
 *  @description Lambda Extension for Adding Query Text Listener to SearchView.
 *               Adds OnQueryTextListener and invokes the lambda function to return its params.
 *
 *               inline keyword inlines the call of the function to where it is being called.
 *               crossinline keyword presents the function to not return in case of inline call.
 *
 *  @param action {Lambda} Returns the parameters to wherever the function is being called.
 */
inline fun SearchView.doOnQueryTextSubmit(crossinline action: (query: String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            //Invokes the Lambda function if the Editable is not null.
            query?.let { text ->
                action.invoke(text)
            }
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return false
        }
    })
}


/**
 *
 * Date-Time/Calendar Extension Functions.
 *
 */

/** Returns ISO 8601 Formatted time string for the Calendar value. */
val Calendar.toISOString: String
    get() = this.time.toInstant().toString()

val LocalDate.toISOString: String
    get() = this.atStartOfDay(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault())
        .toString()


/**
 *
 * ImageView Extension Functions.
 *
 */


/**
 *  @description Loads Image into ImageView directly via Glide.
 *
 *
 *  @param avatarPlaceHolder {Boolean} Shows avatar placeHolder else generic placeHolder.
 */
fun ImageView.loadURL(imageURL: String, avatarPlaceHolder: Boolean) {

    //Get complete Image URL using Identifier received.
    val minURL = GeneralFunctions.getResizedImage(ValueMapping.getPathSmall(), imageURL)
    val bestURL = GeneralFunctions.getResizedImage(ValueMapping.getPathBest(), imageURL)

    if (avatarPlaceHolder) {
        Glide.with(this)
            .load(bestURL)
            .thumbnail(Glide.with(this).load(minURL))
            .placeholder(R.drawable.ic_avatar)
            .error(R.drawable.ic_avatar)
            .into(this)
    } else {
        Glide.with(this)
            .load(bestURL)
            .thumbnail(Glide.with(this).load(minURL))
            .placeholder(R.drawable.ic_placeholder_broken_img)
            .error(R.drawable.ic_placeholder_broken_img)
            .into(this)
    }
}
