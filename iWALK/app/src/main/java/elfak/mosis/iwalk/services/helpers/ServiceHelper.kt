package elfak.mosis.iwalk.services.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import elfak.mosis.iwalk.services.LocationService

class ServiceHelper {
    companion object {

        private fun userServicePreferencesEnabled(activity: Activity) : Boolean {
            val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
            val serviceEnabled = sharedPref.getInt("serviceEnabled", ServicePreference.NOT_SET.setting)
            return serviceEnabled == ServicePreference.ENABLED.setting
        }

        fun startLocationService(activity: FragmentActivity, context: Context){
            if(userServicePreferencesEnabled(activity)){
                val locationRequest = LocationHelpers.buildLocationRequest()
                LocationHelpers.buildLocationSettings(locationRequest, activity)
                if(!LocationService.isServiceStarted)
                    activity.startService(Intent(context, LocationService::class.java))
            }
        }

        fun stopLocationService(activity: FragmentActivity) {
            if(LocationService.isServiceStarted)
                activity.stopService(Intent(activity, LocationService::class.java))
        }
    }
}