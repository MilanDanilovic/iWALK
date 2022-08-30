package elfak.mosis.iwalk.map

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import elfak.mosis.iwalk.R

class CustomInfoWindowAdapter(context: Context) : GoogleMap.InfoWindowAdapter {

    private var mWindow:View = LayoutInflater.from(context).inflate(R.layout.custom_info_window,null)


    private fun renderWindowText(marker: Marker,view: View){
        val title = marker.title
        val tvTitle = view.findViewById<TextView>(R.id.marker_title)

        if(!title.equals("")){
            tvTitle.text = title
        }

        val snippet = marker.snippet
        val tvSnippet = view.findViewById<TextView>(R.id.marker_snippet)

        if(!snippet.equals("")){
            tvSnippet.text = snippet
        }

    }

    override fun getInfoContents(marker: Marker): View? {
        if(marker.title==null && marker.snippet==null){
            return null
        }
        renderWindowText(marker,mWindow)
        return mWindow
    }

    override fun getInfoWindow(marker: Marker): View? {
        if(marker.title==null && marker.snippet==null){
            return null
        }
        renderWindowText(marker,mWindow)
        return mWindow
    }
}
