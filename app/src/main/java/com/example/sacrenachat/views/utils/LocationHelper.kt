package com.example.sacrenachat.views.utils

//import android.annotation.SuppressLint
//import android.app.Activity.RESULT_OK
//import android.location.Geocoder
//import android.os.Looper
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.result.IntentSenderRequest
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.fragment.app.Fragment
//import com.afollestad.assent.Permission
//import com.afollestad.assent.askForPermissions
//import com.afollestad.assent.isAllGranted
//import com.example.sacrenachat.R
//import com.google.android.gms.common.api.ResolvableApiException
//import com.google.android.gms.location.*
//import java.util.*
//
//class LocationHelper(
//        private val fragment: Fragment,
//        private val locationRequestType: LocationRequestType,
//) {
//
//    companion object {
//        private const val TAG = "LocationHelper"
//    }
//
//    // Variables
//    private val context
//        get() = fragment.requireContext()
//
//    private val mFusedLocationClient by lazy {
//        LocationServices.getFusedLocationProviderClient(context)
//    }
//
//    // Location Objects
//    private var latitude: Double? = null
//    private var longitude: Double? = null
//    private lateinit var locationRequest: LocationRequest
//    private lateinit var locationCallback: LocationCallback
//    private lateinit var locationHelperCallback: LocationHelperCallback
//
//    private val resolutionForResult =
//            fragment.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
//                if (result.resultCode == RESULT_OK) {
//                    // If location is turned on via dialog, get current location coordinates.
//                    getCurrentLocation(true)
//                }
//            }
//
//    /**
//     * @description This method fetches the current location of the user with the help of the GPS.
//     */
//    fun fetchCurrentLocation(locationHelperCallback: LocationHelperCallback) {
//        // Assign to top level location helper.
//        this.locationHelperCallback = locationHelperCallback
//
//        // If permission is already granted, proceed to fetch location else ask for permissions.
//        if (fragment.isAllGranted(fetchRequiredPermission())) {
//            fetchCoordinates()
//        } else {
//            fragment.askForPermissions(fetchRequiredPermission()) { result ->
//                if (result.isAllGranted(fetchRequiredPermission())) {
//                    fetchCoordinates()
//                } else {
//                    Toast.makeText(context, context.getString(R.string.location_permission_needed),
//                            Toast.LENGTH_SHORT).show()
//                    locationHelperCallback.onLocationAccessDenied()
//                }
//            }
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun fetchCoordinates() {
//        mFusedLocationClient.lastLocation.addOnSuccessListener { location ->
//            if (location != null) {
//                longitude = location.longitude
//                latitude = location.latitude
//                reverseGeocode()
//            } else {
//                getCurrentLocation(true)
//            }
//        }
//    }
//
//
//    /**
//     * @description This method fetches the current location of user, firstly by seeing if the GPS
//     * is enabled and all settings require are satisfied or not, else it asks for those settings
//     * and then fetches the location based on that.
//     *
//     * @param canRetry represents that if we should again try fetching the location after some time
//     * or not.
//     */
//    @SuppressLint("MissingPermission")
//    private fun getCurrentLocation(canRetry: Boolean) {
//
//
//        /**
//         *  @interval This method sets the rate in milliseconds at which your app prefers to
//         *  receive location updates.
//         *  @fastestInterval This method sets the fastest rate in milliseconds at which your app
//         *  can handle location updates.
//         *  @priority This method sets the priority of the request, which gives the Google Play
//         *  services location services a strong hint about which location sources to use.
//         */
//        locationRequest = LocationRequest.create().apply {
//            interval = 1000
//            fastestInterval = 1000
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        }
//
//        val mBuilder = LocationSettingsRequest.Builder()
//                .addLocationRequest(locationRequest)
//                .setNeedBle(true)
//                .setAlwaysShow(true)
//
//        val result = LocationServices.getSettingsClient(context)
//                .checkLocationSettings(mBuilder.build())
//
//        // See if the location settings are satisfied else show dialog for granting location.
//        result.addOnSuccessListener {
//            mFusedLocationClient.lastLocation.addOnSuccessListener { location ->
//                if (location != null) {
//                    latitude = location.latitude
//                    longitude = location.longitude
//                    reverseGeocode()
//                } else if (canRetry) {
//                    getCurrentLocation(false)
//                    Log.d("locationStatus", "location fetched failed returned empty")
//                }
//            }.addOnFailureListener {
//                Log.d(TAG, "getCurrentLocation: $it")
//            }
//        }
//
//
//        result.addOnFailureListener { exception ->
//            if (exception is ResolvableApiException) {
//                try {
//                    val intentSenderRequest =
//                            IntentSenderRequest.Builder(exception.resolution).build()
//                    resolutionForResult.launch(intentSenderRequest)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
//
//        // Location Callback to fetch location updates.
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(result: LocationResult) {
//                if (result.locations.isNotEmpty()) {
//                    longitude = result.lastLocation.longitude
//                    latitude = result.lastLocation.latitude
//
//                    reverseGeocode()
//
//                    // Remove callbacks once Location is fetched successfully.
//                    mFusedLocationClient.removeLocationUpdates(locationCallback)
//                }
//
//                super.onLocationResult(result)
//            }
//        }
//
//        // If Location is still null, start callbacks for regular Location Updates.
//        if (latitude == null || longitude == null) {
//            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,
//                    Looper.getMainLooper())
//        } else {
//            mFusedLocationClient.removeLocationUpdates(locationCallback)
//        }
//    }
//
//    // Find the address via Location coordinates
//    private fun reverseGeocode() {
//        try {
//            val geocoder = Geocoder(context, Locale.getDefault())
//            if (latitude != null && longitude != null) {
//                val addresses = geocoder.getFromLocation(latitude!!, longitude!!, 1)
//
//                // Get address via Location
//                if (addresses.isNotEmpty() && addresses.first().countryName != null
//                        && addresses.first().locality != null) {
//                    val textAddress = "${addresses.first().locality}, ${addresses.first().countryName}"
//                    locationHelperCallback.onLocationFetchSuccess(latitude!!,
//                            longitude!!, textAddress)
//                } else {
//                    locationHelperCallback.onLocationFetchSuccess(latitude!!, longitude!!, "")
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//
//            // If reverse geocode fails, send empty location address.
//            if (latitude != null && longitude != null) {
//                locationHelperCallback.onLocationFetchSuccess(latitude!!, longitude!!, "")
//            }
//        }
//    }
//
//
//    private fun fetchRequiredPermission(): Permission {
//        return when (locationRequestType) {
//            LocationRequestType.FINE_LOCATION -> Permission.ACCESS_FINE_LOCATION
//            LocationRequestType.COARSE_LOCATION -> Permission.ACCESS_COARSE_LOCATION
//        }
//    }
//}
//
//interface LocationHelperCallback {
//    fun onLocationFetchSuccess(latitude: Double, longitude: Double, address: String)
//    fun onLocationAccessDenied()
//}
//
//enum class LocationRequestType {
//    FINE_LOCATION,
//    COARSE_LOCATION
//}