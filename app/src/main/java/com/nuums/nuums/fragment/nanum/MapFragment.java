package com.nuums.nuums.fragment.nanum;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nuums.nuums.R;
import com.nuums.nuums.model.nanum.Nanum;
import com.yongtrim.lib.fragment.ABaseFragment;
import com.yongtrim.lib.message.PushMessage;

/**
 * nuums / com.nuums.nuums.fragment.nanum
 * <p/>
 * Created by Uihyun on 16. 2. 5..
 */
public class MapFragment extends ABaseFragment {
    final static String TAG = "MapFragment";

    private GoogleMap map;

    Nanum nanum;


    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        contextHelper.getActivity().setupActionBar("나눔자의 위치보기");
        contextHelper.getActivity().setBackButtonVisibility(false);
        contextHelper.getActivity().setImageButtonAndVisiable(R.drawable.del);

        if(contextHelper.getActivity().getIntent().hasExtra("nanum")) {
            nanum = Nanum.getNanum(contextHelper.getActivity().getIntent().getStringExtra("nanum"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_map, container, false);

//        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            public void onGlobalLayout() {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                } else {
//                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                }
//                ivPoint.setY(ivPoint.getY() - ivPoint.getHeight() / 2);
//            }
//        });


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        map = mapFragment.getMap();

        setUpMap();

        moveCamera(nanum.getLocation());


        // 첫번째 마커 설정.
        MarkerOptions optFirst = new MarkerOptions();
        optFirst.position(nanum.getLocation());// 위도 • 경도
        //optFirst.snippet("Snippet");
        optFirst.icon(BitmapDescriptorFactory
                .fromResource(R.drawable.mappoint));
        map.addMarker(optFirst).showInfoWindow();

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    public void onButtonClicked(View v) {
        switch (v.getId()) {
            case R.id.actionbarImageButton:
                contextHelper.getActivity().finish();
                break;
        }
    }


    public void onEvent(PushMessage pushMessage) {
    }


    private void moveCamera(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void setUpMap() {
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Showing / hiding your current location
        map.setMyLocationEnabled(false);
        // Enable / Disable zooming controls
        map.getUiSettings().setZoomControlsEnabled(false);

        // Enable / Disable my location button
        map.getUiSettings().setMyLocationButtonEnabled(false);

        // Enable / Disable Compass icon
        map.getUiSettings().setCompassEnabled(true);

        // Enable / Disable Rotate gesture
        map.getUiSettings().setRotateGesturesEnabled(false);

        // Enable / Disable zooming functionality
        map.getUiSettings().setZoomGesturesEnabled(true);

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
            }
        });
//        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(final Marker marker) {
//                unselectAllMarker();
//                selectMaker(marker);
//                return true;
//            }
//        });
    }


}


