package com.anna.maps;

/**
 * Created by PARSEA on 15.02.2017.
 */

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.anna.R;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;

import java.util.List;

import android.widget.RelativeLayout;
import android.widget.Toast;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.Router;
import com.here.android.mpa.routing.RoutingError;

import java.io.IOException;
import java.util.Locale;

public class HereMapsFragment extends Fragment {

    private Map map = null;
    private MapFragment mapFragment = null;
    private MapRoute mapRoute = null;
    private String from;
    private String to = "";
    private PositioningManager pm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout rl = (RelativeLayout) inflater.inflate(R.layout.fragment_here_maps, container, false);
        from = getString(R.string.current_position);
        initialize();
        return rl;
    }

    private void initialize() {

        getFragmentManager().findFragmentById(R.id.heremapfragment);
        final Activity activity = super.getActivity();
        mapFragment = (MapFragment) activity.getFragmentManager().findFragmentById(
                R.id.heremapfragment);
        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(
                    OnEngineInitListener.Error error) {

                pm = PositioningManager.getInstance();
                pm.start(PositioningManager.LocationMethod.GPS_NETWORK);

                if (error == OnEngineInitListener.Error.NONE) {
                    map = mapFragment.getMap();
                    map.setMapScheme(Map.Scheme.NORMAL_TRAFFIC_DAY);

                    final GeoPosition currentPosition = getCurrentPosition();

                    map.setCenter(currentPosition.getCoordinate(),
                            Map.Animation.NONE);
                    pm.stop();
                    map.setMapDisplayLanguage(Locale.getDefault());
                    map.setZoomLevel(
                            (map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);

                    FloatingActionButton button = (FloatingActionButton) activity.findViewById(R.id.directionsbutton);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText fromField = (EditText) HereMapsFragment.super.getActivity().findViewById(R.id.navigationFrom);
                            EditText toField = (EditText) HereMapsFragment.super.getActivity().findViewById(R.id.navigationTo);
                            if (from.equals(fromField.getText().toString()) && to.equals(toField.getText().toString())) {
                                startNavigation();
                            } else {
                                from = fromField.getText().toString();
                                to = toField.getText().toString();
                                getDirections();
                            }
                        }
                    });
                } else {
                    Log.e("", "ERROR: Cannot initialize Map Fragment");

                }
            }
        });
    }

    private GeoPosition getCurrentPosition() {
        if (pm.hasValidPosition()) {
            return pm.getPosition();
        } else {
            return pm.getLastKnownPosition();
        }
    }

    private Router.Listener<List<RouteResult>, RoutingError> routeManagerListener = new Router.Listener<List<RouteResult>, RoutingError>() {
        @Override
        public void onProgress(int i) {

        }

        @Override
        public void onCalculateRouteFinished(List<RouteResult> routeResult, RoutingError routingError) {

            if (routingError == RoutingError.NONE &&
                    routeResult.get(0).getRoute() != null) {

                mapRoute = new MapRoute(routeResult.get(0).getRoute());
                map.addMapObject(mapRoute);

                GeoBoundingBox gbb = routeResult.get(0).getRoute().getBoundingBox();
                map.zoomTo(gbb, Map.Animation.NONE,
                        Map.MOVE_PRESERVE_ORIENTATION);

            } else if (routingError != RoutingError.NONE) {
                Toast.makeText(HereMapsFragment.super.getActivity().getApplicationContext(),
                        "Route calculation failed with: " + routingError.toString(),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    };

    public void getDirections() {
        if (map != null && mapRoute != null) {
            map.removeMapObject(mapRoute);
            mapRoute = null;
        }

        CoreRouter coreRouter = new CoreRouter();

        RoutePlan routePlan = new RoutePlan();

        RouteOptions routeOptions = new RouteOptions();
        routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);
        routeOptions.setRouteType(RouteOptions.Type.FASTEST);
        routePlan.setRouteOptions(routeOptions);

        RouteWaypoint fromWaypoint;
        if (from.equals(getString(R.string.current_position))) {
            fromWaypoint = new RouteWaypoint(getCurrentPosition().getCoordinate());
        } else {
            fromWaypoint = new RouteWaypoint(getGeoCoordinateFromAdress(from));
        }
        routePlan.addWaypoint(fromWaypoint);

        RouteWaypoint toWaypoint = new RouteWaypoint(getGeoCoordinateFromAdress(to));
        routePlan.addWaypoint(toWaypoint);

        coreRouter.calculateRoute(routePlan, routeManagerListener);
    }

    public void startNavigation() {

        NavigationManager navigationManager = NavigationManager.getInstance();
        map.setMapScheme(Map.Scheme.CARNAV_TRAFFIC_DAY);
        navigationManager.setMap(map);
        navigationManager.setMapUpdateMode(NavigationManager.MapUpdateMode.NONE);
        NavigationManager.Error error = navigationManager.startNavigation(mapRoute.getRoute());

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

