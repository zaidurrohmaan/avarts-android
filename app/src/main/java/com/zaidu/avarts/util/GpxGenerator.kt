package com.zaidu.avarts.util

import com.zaidu.avarts.data.database.entities.TrackPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object GpxGenerator {

    fun generate(trackPoints: List<TrackPoint>, title: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        val gpx = StringBuilder()
        gpx.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        gpx.append("<gpx xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd\" creator=\"Avarts\" version=\"1.1\" xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\" xmlns:gpxx=\"http://www.garmin.com/xmlschemas/GpxExtensions/v3\">\n")
        gpx.append(" <metadata>\n")
        gpx.append("  <time>${sdf.format(Date(trackPoints.first().time))}</time>\n")
        gpx.append(" </metadata>\n")
        gpx.append(" <trk>\n")
        gpx.append("  <name>$title</name>\n")
        gpx.append("  <type>running</type>\n")
        gpx.append("  <trkseg>\n")

        trackPoints.forEach { trackPoint ->
            if (!trackPoint.isPaused) {
                gpx.append("   <trkpt lat=\"${trackPoint.lat}\" lon=\"${trackPoint.lon}\">\n")
                gpx.append("    <ele>${trackPoint.altitude}</ele>\n")
                gpx.append("    <time>${sdf.format(Date(trackPoint.time))}</time>\n")
                gpx.append("   </trkpt>\n")
            }
        }

        gpx.append("  </trkseg>\n")
        gpx.append(" </trk>\n")
        gpx.append("</gpx>")

        return gpx.toString()
    }
}