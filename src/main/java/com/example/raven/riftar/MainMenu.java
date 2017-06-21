package com.example.raven.riftar;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.model.Tag;
import org.mapsforge.core.model.Tile;
import org.mapsforge.core.util.MercatorProjection;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.datastore.MapReadResult;
import org.mapsforge.map.datastore.PointOfInterest;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import org.mapsforge.poi.android.storage.AndroidPoiPersistenceManagerFactory;
import org.mapsforge.poi.storage.ExactMatchPoiCategoryFilter;
import org.mapsforge.poi.storage.PoiCategoryFilter;
import org.mapsforge.poi.storage.PoiCategoryManager;
import org.mapsforge.poi.storage.PoiPersistenceManager;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainMenu extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private static final String MAP_FILE = "ontario.map";
    private MapView mapView;

    private static final String[] LOCATION_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION // Permissions
    };

    protected File getMapFileDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
    }

    protected String getMapFileName() {
        return "ontario.map";
    }

    protected MapFile getMapFile() {
        return new MapFile(new File(getMapFileDirectory(),
                this.getMapFileName()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidGraphicFactory.createInstance(this.getApplication());


        setContentView(R.layout.activity_main_menu);

        this.mapView = new MapView(this);
        setContentView(this.mapView);

        this.mapView.setClickable(true);
        this.mapView.getMapScaleBar().setVisible(true);
        this.mapView.setBuiltInZoomControls(true);
        this.mapView.setZoomLevelMin((byte) 10);
        this.mapView.setZoomLevelMax((byte) 20);

        // create a tile cache of suitable size
        TileCache tileCache = AndroidUtil.createTileCache(this, "mapcache",
                mapView.getModel().displayModel.getTileSize(), 1f,
                this.mapView.getModel().frameBufferModel.getOverdrawFactor());

        // tile renderer layer using internal render theme
        File test = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        MapDataStore mapDataStore = new MapFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), MAP_FILE));
        TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore, this.mapView.getModel().mapViewPosition, AndroidGraphicFactory.INSTANCE);
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT);

        // only once a layer is associated with a mapView the rendering starts
        this.mapView.getLayerManager().getLayers().add(tileRendererLayer);

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        double latitude = 0.0, longitude = 0.0;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            DecimalFormat df = new DecimalFormat("#.#");
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } else {
            latitude = 43.9454;
            longitude = -78.8964;
        }

        this.mapView.setCenter(new LatLong(latitude, longitude));
        this.mapView.setZoomLevel((byte) 1);

/*
        float touchRadius = 1.0f * this.mapView.getModel().displayModel.getScaleFactor();
        long mapSize = MercatorProjection.getMapSize(this.mapView.getModel().mapViewPosition.getZoomLevel(), this.mapView.getModel().displayModel.getTileSize());
        double pixelX = MercatorProjection.longitudeToPixelX(longitude, mapSize);
        double pixelY = MercatorProjection.latitudeToPixelY(latitude, mapSize);
        int tileXMin = MercatorProjection.pixelXToTileX(pixelX - touchRadius, this.mapView.getModel().mapViewPosition.getZoomLevel(), this.mapView.getModel().displayModel.getTileSize());
        int tileXMax = MercatorProjection.pixelXToTileX(pixelX + touchRadius, this.mapView.getModel().mapViewPosition.getZoomLevel(), this.mapView.getModel().displayModel.getTileSize());
        int tileYMin = MercatorProjection.pixelYToTileY(pixelY - touchRadius, this.mapView.getModel().mapViewPosition.getZoomLevel(), this.mapView.getModel().displayModel.getTileSize());
        int tileYMax = MercatorProjection.pixelYToTileY(pixelY + touchRadius, this.mapView.getModel().mapViewPosition.getZoomLevel(), this.mapView.getModel().displayModel.getTileSize());
        Set<PointOfInterest> pointOfInterests = new HashSet<>();

        for (int tileX = tileXMin; tileX <= tileXMax; tileX++) {
            for (int tileY = tileYMin; tileY <= tileYMax; tileY++) {
                Tile tile = new Tile(tileX, tileY, this.mapView.getModel().mapViewPosition.getZoomLevel(), this.mapView.getModel().displayModel.getTileSize());
                MapReadResult mapReadResult = getMapFile().readPoiData(tile);
                for (PointOfInterest pointOfInterest : mapReadResult.pointOfInterests) {
                    pointOfInterests.add(pointOfInterest);
                }
            }
        }
*/
/*
        PoiPersistenceManager persistenceManager = null;
        try {
            persistenceManager = AndroidPoiPersistenceManagerFactory.getPoiPersistenceManager(POI_FILE);
            PoiCategoryManager categoryManager = persistenceManager.getCategoryManager();
            PoiCategoryFilter categoryFilter = new ExactMatchPoiCategoryFilter();
            categoryFilter.addCategory(categoryManager.getPoiCategoryByTitle(this.category));
            return persistenceManager.findInRect(params[0], categoryFilter, null, Integer.MAX_VALUE);
        } catch (Throwable t) {

        } finally {
            if (persistenceManager != null) {
                persistenceManager.close();
            }
        }
*/
        StringBuilder sb = new StringBuilder();

        // Filter POIs
        /*
        sb.append("*** POIS ***");
        for (PointOfInterest pointOfInterest : pointOfInterests) {
            Point layerXY = this.mapView.getMapViewProjection().toPixels(pointOfInterest.position);
            //if (layerXY.distance(new Point(0,0)) > touchRadius) {
            //    continue;
            //}
            sb.append("\n");
            List<Tag> tags = pointOfInterest.tags;
            for (Tag tag : tags) {
                sb.append("\n").append(tag.key).append("=").append(tag.value);
            }
        }
*/


    }

    @Override
    protected void onDestroy() {
        this.mapView.destroyAll();
        AndroidGraphicFactory.clearResourceMemoryCache();
        super.onDestroy();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
}




/*
private static class PoiSearchTask extends AsyncTask<BoundingBox, Void, Collection<PointOfInterest>> {
    private final WeakReference<PoiSearchViewer> weakActivity;
    private final String category;

    private PoiSearchTask(PoiSearchViewer activity, String category) {
        this.weakActivity = new WeakReference<>(activity);
        this.category = category;
    }

    @Override
    protected Collection<PointOfInterest> doInBackground(BoundingBox... params) {
        PoiPersistenceManager persistenceManager = null;
        try {
            persistenceManager = AndroidPoiPersistenceManagerFactory.getPoiPersistenceManager(POI_FILE);
            PoiCategoryManager categoryManager = persistenceManager.getCategoryManager();
            PoiCategoryFilter categoryFilter = new ExactMatchPoiCategoryFilter();
            categoryFilter.addCategory(categoryManager.getPoiCategoryByTitle(this.category));
            return persistenceManager.findInRect(params[0], categoryFilter, null, Integer.MAX_VALUE);
        } catch (Throwable t) {
            Log.e(SamplesApplication.TAG, t.getMessage(), t);
        } finally {
            if (persistenceManager != null) {
                persistenceManager.close();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Collection<PointOfInterest> pointOfInterests) {
        final PoiSearchViewer activity = weakActivity.get();
        if (activity == null) {
            return;
        }
        Toast.makeText(activity, category + ": " + (pointOfInterests != null ? pointOfInterests.size() : 0), Toast.LENGTH_SHORT).show();
        if (pointOfInterests == null) {
            return;
        }

        GroupLayer groupLayer = new GroupLayer();
        for (final PointOfInterest pointOfInterest : pointOfInterests) {
            final Circle circle = new FixedPixelCircle(pointOfInterest.getLatLong(), 16, CIRCLE, null) {
                @Override
                public boolean onTap(LatLong tapLatLong, Point layerXY, Point tapXY) {
                    // GroupLayer does not have a position, layerXY is null
                    Point circleXY = activity.mapView.getMapViewProjection().toPixels(getPosition());
                    if (this.contains(circleXY, tapXY)) {
                        Toast.makeText(activity, pointOfInterest.getName(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false;
                }
            };
            groupLayer.layers.add(circle);
        }
        activity.mapView.getLayerManager().getLayers().add(groupLayer);
        activity.redrawLayers();
    }
}
*/