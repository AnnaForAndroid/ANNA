package com.anna.maps;

/**
 * Created by PARSEA on 15.02.2017.
 */

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.anna.R;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;

import java.util.EnumSet;
import java.util.List;

import android.widget.Toast;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.routing.RouteManager;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HereMapsFragment extends Fragment {

    // map embedded in the map fragment
    private Map map = null;
    // map fragment embedded in this activity
    private MapFragment mapFragment = null;
    // MapRoute for this activity
    private MapRoute mapRoute = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.fragment_here_maps, container, false);
        initialize();
        return ll;
    }

    private void initialize() {

        // Search for the map fragment to finish setup by calling init().
        getFragmentManager().findFragmentById(R.id.heremapfragment);
        final Activity activity = super.getActivity();
        mapFragment = (MapFragment) activity.getFragmentManager().findFragmentById(
                R.id.heremapfragment);
        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(
                    OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    map = mapFragment.getMap();
                    map.setMapScheme(Map.Scheme.NORMAL_TRAFFIC_DAY);
                    PositioningManager pm = PositioningManager.getInstance();
                    pm.start(PositioningManager.LocationMethod.GPS_NETWORK);
                    map.setCenter(pm.getPosition().getCoordinate(),
                            Map.Animation.NONE);
                    pm.stop();
                    map.setMapDisplayLanguage(Locale.getDefault());
                    map.setZoomLevel(
                            (map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);

                    Button button = (Button) activity.findViewById(R.id.directionsbutton);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getDirections(v);
                        }
                    });
                } else {
                    Log.e("", "ERROR: Cannot initialize Map Fragment");

                }
            }
        });
    }

    private RouteManager.Listener routeManagerListener =
            new RouteManager.Listener() {
                public void onCalculateRouteFinished(RouteManager.Error errorCode,
                                                     List<RouteResult> result) {

                    if (errorCode == RouteManager.Error.NONE &&
                            result.get(0).getRoute() != null) {

                        // create a map route object and place it on the map
                        mapRoute = new MapRoute(result.get(0).getRoute());
                        map.addMapObject(mapRoute);

                        // Get the bounding box containing the route and zoom in
                        GeoBoundingBox gbb = result.get(0).getRoute().getBoundingBox();
                        map.zoomTo(gbb, Map.Animation.NONE,
                                Map.MOVE_PRESERVE_ORIENTATION);

                    }
                }

                public void onProgress(int percentage) {

                }
            };

    // Functionality for taps of the "Get Directions" button
    public void getDirections(View view) {
        // 1. clear previous results
        if (map != null && mapRoute != null) {
            map.removeMapObject(mapRoute);
            mapRoute = null;
        }

        // 2. Initialize RouteManager
        RouteManager routeManager = new RouteManager();

        // 3. Select routing options via RoutingMode
        RoutePlan routePlan = new RoutePlan();

        RouteOptions routeOptions = new RouteOptions();
        routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);
        routeOptions.setRouteType(RouteOptions.Type.FASTEST);
        routePlan.setRouteOptions(routeOptions);

        // 4. Select Waypoints for your routes
        // START
        EditText from = (EditText) super.getActivity().findViewById(R.id.navigationFrom);
        routePlan.addWaypoint(getGeoCoordinateFromAdress(from.getText().toString()));

        // END
        EditText to = (EditText) super.getActivity().findViewById(R.id.navigationTo);
        routePlan.addWaypoint(getGeoCoordinateFromAdress(to.getText().toString()));

        // 5. Retrieve Routing information via RouteManagerListener
        RouteManager.Error error =
                routeManager.calculateRoute(routePlan, routeManagerListener);
        if (error != RouteManager.Error.NONE) {
            Toast.makeText(super.getActivity().getApplicationContext(),
                    "Route calculation failed with: " + error.toString(),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    ;

    public void startNavigation() {

        NavigationManager navigationManager = NavigationManager.getInstance();
        CoreRouter coreRouter = new CoreRouter();
        RoutePlan routePlan = new RoutePlan();
        RouteOptions routeOptions = new RouteOptions();


        navigationManager.setMap(map);

// if user wants to start real navigation, submit calculated route
// for more information on calculating a route, see the "Directions" section
        //NavigationManager.Error error = navigationManager.startNavigation(route);
    }

    public GeoCoordinate getGeoCoordinateFromAdress(String locationName) {
        Geocoder geoCoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> address = geoCoder.getFromLocationName(locationName, 1);
            double latitude = address.get(0).getLatitude();
            double longitude = address.get(0).getLongitude();

            return new GeoCoordinate(latitude, longitude);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}

