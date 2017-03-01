package com.anna.maps;

/**
 * Created by PARSEA on 15.02.2017.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.anna.R;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.Maneuver;
import com.here.android.mpa.routing.Route;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;

import java.lang.ref.WeakReference;
import java.util.EnumSet;
import java.util.List;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
    private boolean paused;
    private NavigationManager navigationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.fragment_here_maps, container, false);
        from = getString(R.string.current_position);
        initialize();
        return ll;
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

        LinearLayout ll = (LinearLayout) getView().findViewById(R.id.navigation_adress_fields);
        ll.setVisibility(View.INVISIBLE);

        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.directionsbutton);
        fab.setVisibility(View.INVISIBLE);

        CardView infos = (CardView) getView().findViewById(R.id.card_navigation_info);
        infos.setVisibility(View.VISIBLE);

        pm.addListener(new WeakReference<PositioningManager.OnPositionChangedListener>(positionListener));

        navigationManager = NavigationManager.getInstance();
        map.setMapScheme(Map.Scheme.CARNAV_TRAFFIC_DAY);
        map.getPositionIndicator().setVisible(true);
        ;
        map.setTilt(45, Map.Animation.NONE);

        navigationManager.setRoute(mapRoute.getRoute());
        navigationManager.setNaturalGuidanceMode(EnumSet.of(NavigationManager.NaturalGuidanceMode.TRAFFIC_LIGHT, NavigationManager.NaturalGuidanceMode.STOP_SIGN, NavigationManager.NaturalGuidanceMode.JUNCTION));
        navigationManager.setTrafficAvoidanceMode(NavigationManager.TrafficAvoidanceMode.DYNAMIC);
        navigationManager.addRerouteListener(new WeakReference<NavigationManager.RerouteListener>(rerouteListener));

        navigationManager.setRealisticViewMode(NavigationManager.RealisticViewMode.DAY);
        navigationManager.addRealisticViewAspectRatio(NavigationManager.AspectRatio.AR_4x3);
        navigationManager.addRealisticViewListener(new WeakReference<NavigationManager.RealisticViewListener>(realisticViewListener));

        navigationManager.setMap(map);
        navigationManager.setMapUpdateMode(NavigationManager.MapUpdateMode.ROADVIEW);
        NavigationManager.Error error = navigationManager.startNavigation(mapRoute.getRoute());

        if (error != NavigationManager.Error.NONE) {
            Toast.makeText(HereMapsFragment.super.getActivity().getApplicationContext(),
                    "Navigation failed with: " + error.toString(),
                    Toast.LENGTH_SHORT)
                    .show();
        }

        initializeInfoScreen();
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

    @Override
    public void onPause() {
        if (pm != null) {
            pm.stop();
        }
        super.onPause();
        paused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        paused = false;
        if (pm != null) {
            pm.start(
                    PositioningManager.LocationMethod.GPS_NETWORK);
        }
    }

    @Override
    public void onDestroy() {
        if (pm != null) {
            pm.removeListener(
                    positionListener);
        }
        map = null;
        super.onDestroy();
    }

    private PositioningManager.OnPositionChangedListener positionListener = new
            PositioningManager.OnPositionChangedListener() {

                public void onPositionUpdated(PositioningManager.LocationMethod method,
                                              GeoPosition position, boolean isMapMatched) {
                    // set the center only when the app is in the foreground
                    // to reduce CPU consumption
                    if (!paused) {
                        map.setCenter(position.getCoordinate(),
                                Map.Animation.LINEAR);
                    }
                }

                public void onPositionFixChanged(PositioningManager.LocationMethod method,
                                                 PositioningManager.LocationStatus status) {
                }
            };

    private NavigationManager.RerouteListener rerouteListener = new NavigationManager.RerouteListener() {
        @Override
        public void onRerouteEnd(Route route) {
            super.onRerouteEnd(route);
            map.removeMapObject(mapRoute);
            mapRoute.setRoute(route);
            map.addMapObject(mapRoute);
            navigationManager.setRoute(route);
            Toast.makeText(HereMapsFragment.super.getContext(), "reroute end", Toast.LENGTH_SHORT).show();
        }
    };

    private void initializeInfoScreen() {

        Thread speedThread = new Thread() {
            @Override
            public void run() {
                TextView speedView = (TextView) getView().findViewById(R.id.navigation_speed);
                double speed = getCurrentPosition().getSpeed();
                while (true) {
                    if (speed != getCurrentPosition().getSpeed()) {
                        speed = getCurrentPosition().getSpeed();
                        speedView.setText(speed + " km/h");
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.e("", e.toString());
                    }
                }
            }
        };
        speedThread.start();
    }

    private NavigationManager.RealisticViewListener realisticViewListener = new NavigationManager.RealisticViewListener() {
        public void onRealisticViewShow(NavigationManager.AspectRatio ratio, Image junction, Image signpost) {
            if (junction.getType() == Image.Type.SVG) {
                // full size is too big (will cover most of the screen), so cut the size in half
                Bitmap bmpImage = junction.getBitmap((int) (junction.getWidth() * 0.5),
                        (int) (junction.getHeight() * 0.5));
                if (bmpImage != null) {
                    ImageView imageView = (ImageView) getView().findViewById(R.id.navigation_images);
                    imageView.setImageBitmap(bmpImage);
                    imageView.setVisibility(View.VISIBLE);
                }
            }
        }
    };
}
