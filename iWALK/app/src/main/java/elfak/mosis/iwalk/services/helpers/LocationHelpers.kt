package elfak.mosis.iwalk.services.helpers

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import java.util.concurrent.TimeUnit

class LocationHelpers {

    companion object {
        private const val REQUEST_CHECK_SETTINGS: Int = 0x1

        fun buildLocationRequest() : LocationRequest {
            return LocationRequest.create().apply {
                interval = TimeUnit.SECONDS.toMillis(60)
                fastestInterval = TimeUnit.SECONDS.toMillis(10)
                maxWaitTime = TimeUnit.MINUTES.toMillis(2)
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
        }
        fun buildLocationSettings(locationRequest:LocationRequest, activity: Activity) {
            val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            val result: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(activity)
                .checkLocationSettings(builder.build())

            result.addOnCompleteListener { task -> checkLocationSettings(task, activity) }
        }
        private fun checkLocationSettings(settingsResult: Task<LocationSettingsResponse>, activity: Activity) {
            try{
                val response: LocationSettingsResponse = settingsResult.getResult(ApiException::class.java)
            }
            catch(ex: ApiException) {
                when(ex.statusCode){
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        var resolvable: ResolvableApiException = ex as ResolvableApiException
                        resolvable.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS)
                    }
                }
            }
        }
    }
}