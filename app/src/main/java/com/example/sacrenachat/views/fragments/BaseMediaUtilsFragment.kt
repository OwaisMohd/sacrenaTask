package com.example.sacrenachat.views.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.canhub.cropper.CropImage
import com.example.sacrenachat.R
import com.example.sacrenachat.views.activities.inflate
import com.example.sacrenachat.views.utils.ImageUtils
import com.example.sacrenachat.views.utils.copyInputStreamToFile
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import java.io.File


abstract class BaseMediaUtilsFragment<ViewBindingType : ViewBinding> :
    BaseFragment<ViewBindingType>() {

    companion object {
        private const val TAG = "BaseMediaUtilsFragment"
        private const val GALLERY_IMAGE_REQUEST = 444
        private const val REQUEST_IMAGE_CAPTURE = 543
        private const val GALLERY_VIDEO_REQUEST = 445
        private const val REQUEST_VIDEO_CAPTURE = 544
    }

    // Variables
    private var currentPhotoPath: String = ""       // To be used for camera image intent.
    private var currentVideoPath: String = ""       // To be used for camera video intent.
    private val croppingEnabled = false

    private val cropGalleryIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { act ->
            handleCropping(act.data, isCameraImage = false)
        }

    private val cropCameraIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { act ->
            handleCropping(act.data, isCameraImage = true)
        }

    /**
     * Utility function to show media options bottom sheet.
     *
     * @param pickVideo represents whether to show image options or video options.
     */
    fun showMediaBottomSheet(pickVideo: Boolean = false) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = (view as ViewGroup).inflate(
            layoutRes = R.layout.show_picture_options_bottom_sheet
        )

        val tvCamera = view.findViewById<TextView>(R.id.tvCamera)
        val tvGallery = view.findViewById<TextView>(R.id.tvGallery)
        val tvCancel = view.findViewById<TextView>(R.id.tvCancel)

        tvCamera.text = if (pickVideo) {
            getString(R.string.take_video)
        } else {
            getString(R.string.take_photo)
        }

        tvCamera.setOnClickListener {
            bottomSheetDialog.dismiss()
            if (pickVideo) {
                openCameraVideoIntent()
            } else {
                openCameraImageIntent()
            }
        }

        tvGallery.setOnClickListener {
            bottomSheetDialog.dismiss()
            if (pickVideo) {
                openActivityForResult(ImageUtils.fetchGalleryVideoIntent(), GALLERY_VIDEO_REQUEST)
            } else {
                openActivityForResult(ImageUtils.fetchGalleryImageIntent(), GALLERY_IMAGE_REQUEST)
            }
        }

        tvCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    /**
     * Utility function to open an intent for clicking image via camera.
     */
    private fun openCameraImageIntent() {
        // Get Temporary File and set currentPhoto Path to it.
        ImageUtils.getImageFile(requireContext())?.let { imageFile ->
            currentPhotoPath = imageFile.absolutePath
            openActivityForResult(
                ImageUtils.fetchCameraImageIntent(imageFile, requireContext()),
                REQUEST_IMAGE_CAPTURE
            )
        }
    }

    /**
     * Utility function to open an intent for clicking video via camera.
     */
    private fun openCameraVideoIntent() {
        // Get Temporary File and set currentVideo Path to it.
        ImageUtils.getVideoFile(requireContext())?.let { videoFile ->
            currentVideoPath = videoFile.absolutePath
            openActivityForResult(
                ImageUtils.fetchCameraVideoIntent(videoFile, requireContext()),
                REQUEST_VIDEO_CAPTURE
            )
        }
    }


    /**
     * Utility function to handle result of gallery picking intent.
     *
     * @param uri is the uri of the selected image via gallery.
     */
    private fun handleGalleryImage(uri: Uri) {
        // Get Temporary File and copy the URI data to it.
        ImageUtils.getImageFile(requireContext())?.let { imageFile ->
            requireContext().contentResolver.openInputStream(uri)?.let { inputStream ->
                viewLifecycleOwner.lifecycleScope.launch {
                    imageFile.copyInputStreamToFile(inputStream)
                }
            }
            // Compress the Image and override the temporary file.
            viewLifecycleOwner.lifecycleScope.launch {
                val path = ImageUtils.compressImageFile(
                    requireContext(), imageFile.absolutePath,
                    Uri.fromFile(imageFile)
                )
                if (path.isNotEmpty()) {
                    onGettingImageFile(File(path))
                }
            }
        }
    }

    /**
     * Utility function to handle result of camera image click intent.
     */
    private fun handleCameraImage() {
        // Compress the Image and override the temporary file.
        val imageFile = File(currentPhotoPath)
        viewLifecycleOwner.lifecycleScope.launch {
            val path = ImageUtils.compressImageFile(
                requireContext(), imageFile.absolutePath,
                Uri.fromFile(imageFile)
            )
            if (path.isNotEmpty()) {
                onGettingImageFile(File(path))
            }
        }
    }

    /**
     * Utility function to enable or disable cropping of image.
     */
    private fun handleCropping(data: Intent?, isCameraImage: Boolean) {
        if (isCameraImage) {
            handleCameraImage()
            return
        }

        // Check for Result in case of Gallery Image and forward the cropped URI.
        val result = data?.getParcelableExtra<Parcelable>(
            CropImage.CROP_IMAGE_EXTRA_RESULT
        ) as? CropImage.ActivityResult?
        if (result != null && result.isSuccessful) {
            result.uriContent?.let { uri ->
                handleGalleryImage(uri)
            }
        } else {
            result?.originalUri?.let { uri ->
                handleGalleryImage(uri)
            }
        }
    }

    /**
     * Utility function to enable or disable cropping of image.
     */
//    fun croppingEnabled(status: Boolean) {
//        this.croppingEnabled = status
//    }

    override fun doOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.doOnActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                if (croppingEnabled) {
//                    cropGalleryIntent.launch(ImageUtils.fetchImageCropIntent(requireContext(), uri))
                } else {
                    handleGalleryImage(uri)
                }
            }
        }
        // For Image Request the result will be stored at the file path we provided.
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            if (croppingEnabled) {
                val uri = Uri.fromFile(File(currentPhotoPath))
//                cropCameraIntent.launch(ImageUtils.fetchImageCropIntent(requireContext(), uri))
            } else {
                handleCameraImage()
            }
        }
        if (requestCode == GALLERY_VIDEO_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                onGettingVideoUri(uri)
            }
        }
        // For Video Request the result will be stored at the file path we provided.
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {
            val videoFile = File(currentVideoPath)
            onGettingVideoUri(Uri.fromFile(videoFile))
        }
    }

    abstract fun onGettingImageFile(file: File)
    abstract fun onGettingVideoUri(uri: Uri)
}