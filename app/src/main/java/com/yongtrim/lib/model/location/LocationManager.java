package com.yongtrim.lib.model.location;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.yongtrim.lib.ContextHelper;
import com.yongtrim.lib.util.BasePreferenceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * hair / com.yongtrim.lib.plugin
 * <p/>
 * Created by Uihyun on 15. 9. 22..
 */
public class LocationManager {

    private static LocationManager instance;
    private ContextHelper contextHelper;
    private LocationPreference preference;

    public static LocationManager getInstance(ContextHelper contextHelper) {
        if (instance == null) {
            instance = new LocationManager();
        }

        if(instance.contextHelper != contextHelper) {
            instance.setPreference(contextHelper);
        }
        instance.contextHelper = contextHelper;
        return instance;
    }

    public boolean isGpsOn() {
        boolean isOn = false;
        GPSTracker gps = new GPSTracker(contextHelper.getContext(), null);
        isOn = gps.canGetLocation();
        gps.stopUsingGPS();
        return isOn;
    }


    public LatLng getLocation() {
        LatLng latLng = preference.getLocation();

        if(latLng == null) {
            GPSTracker gps = new GPSTracker(contextHelper.getContext(), null);
            if (gps.canGetLocation()) {
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                if (latitude == 0.0) {
                    //37.555107, 126.970691 서울역
                    //37.5654, 126.97831 시청역
                    latLng = new LatLng(37.5654, 126.97831);

                } else {
                    latLng = new LatLng(latitude, longitude);
                }

            } else {
                latLng = new LatLng(37.5654, 126.97831);
            }
            gps.stopUsingGPS();

        }
//        else {
//            gps.showSettingsAlert();
//        }


        preference.putLocation(latLng);
        return latLng;
    }


    public LatLng getCurrentLocation(GPSTracker.LocationListener locationListener) {
        LatLng latLng = null;
        GPSTracker gps = new GPSTracker(contextHelper.getContext(), locationListener);
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            if (latitude == 0.0) {
            } else {
                latLng = new LatLng(latitude, longitude);
            }

        } else {
            latLng = null;
        }

        if(locationListener == null)
            gps.stopUsingGPS();

        return latLng;
    }


    public void setLocation(LatLng latLng) {
        preference.putLocation(latLng);
    }


    public void setPreference(ContextHelper contextHelper) {
        preference = new LocationPreference(contextHelper.getContext());
    }

    private class LocationPreference extends BasePreferenceUtil {
        private static final String PROPERTY_RECENT_LANGITUDE = "location_recent_langitude";
        private static final String PROPERTY_RECENT_LONGITUDE = "location_recent_longitude";

        public LocationPreference(Context context) {
            super(context);
        }

        public void putLocation(LatLng latLng) {
            put(PROPERTY_RECENT_LANGITUDE, latLng.latitude);
            put(PROPERTY_RECENT_LONGITUDE, latLng.longitude);
        }

        public LatLng getLocation() {
            double latitude = get(PROPERTY_RECENT_LANGITUDE, 0.0);
            double longitude = get(PROPERTY_RECENT_LONGITUDE, 0.0);
            if(latitude == 0.0)
                return null;
            return new LatLng(latitude, longitude);
        }
    }
}
