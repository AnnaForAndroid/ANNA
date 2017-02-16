package com.anna.maps;

/**
 * Created by PARSEA on 15.02.2017.
 */

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.anna.R;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;

import java.util.Locale;

public class HereMapsFragment extends Fragment {

    // map embedded in the map fragment
    private Map map = null;

    // map fragment embedded in this activity
    private MapFragment mapFragment = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.fragment_here_maps, container, false);
        initialize();
        return ll;
    }

    private void initialize() {

        // Search for the map fragment to finish setup by calling init().
        getFragmentManager().findFragmentById(R.id.heremapfragment);
        mapFragment = (MapFragment) super.getActivity().getFragmentManager().findFragmentById(
                R.id.heremapfragment);
        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(
                    OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    map = mapFragment.getMap();
                    map.setMapScheme(Map.Scheme.NORMAL_TRAFFIC_DAY);
                    map.setCenter(new GeoCoordinate(123,123),
                            Map.Animation.NONE);
                    map.setMapDisplayLanguage(Locale.getDefault());
                    map.setZoomLevel(
                            (map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);
                } else {
                    System.out.println("ERROR: Cannot initialize Map Fragment");

                }
            }
        });
    }

    public void startNavigation(){
        NavigationManager navigationManager = NavigationManager.getInstance();

//set the map where the navigation will be performed
        navigationManager.setMap(getMap());

// if user wants to start real navigation, submit calculated route
// for more information on calculating a route, see the "Directions" section
        NavigationManager.Error error = navigationManager.startNavigation(route);
    }
}

