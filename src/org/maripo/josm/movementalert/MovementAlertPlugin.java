package org.maripo.josm.movementalert;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;

import org.maripo.josm.movementalert.MovementMonitor.MovementListener;
import org.openstreetmap.gui.jmapviewer.OsmMercator;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerAddEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerChangeListener;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerOrderChangeEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerRemoveEvent;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.gui.preferences.PreferenceSetting;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

public class MovementAlertPlugin extends Plugin implements LayerChangeListener, MovementListener {

	MovementMonitor monitor;

	public MovementAlertPlugin(PluginInformation info) {
		super(info);
		MovementAlertSettings.sharedInstance().load();
		monitor = new MovementMonitor();
		monitor.setMovementListener(this);
		MainApplication.getLayerManager().addLayerChangeListener(this);
		Toolkit.getDefaultToolkit().addAWTEventListener(monitor, AWTEvent.MOUSE_EVENT_MASK);
	}

	@Override
	public void layerAdded(LayerAddEvent evt) {
		Layer _layer = evt.getAddedLayer();
		if (_layer instanceof OsmDataLayer) {
			OsmDataLayer layer = (OsmDataLayer)_layer;
			layer.data.addDataSetListener(monitor);
			layer.data.addSelectionListener(monitor);
		}
	}

	@Override
	public void layerRemoving(LayerRemoveEvent evt) {
		// Nothing to do
	}

	@Override
	public void layerOrderChanged(LayerOrderChangeEvent evt) {
		// Nothing to do
	}

	static class ConfirmMoveDialog extends ExtendedDialog {
		ConfirmMoveDialog() {
			super(Main.parent, tr("Move elements"), "OK");
			setButtonIcons("ok");
		}
	}

    @Override
    public PreferenceSetting getPreferenceSetting() {
        return MovementAlertPreferences.createInstance();
    }
    private Rectangle getMapBounds () {
    	for (Component component : MainApplication.getMainPanel().getComponents()) {
			if (component instanceof MapFrame) {
				return ((MapFrame)component).mapView.getBounds();
			}
		}
    	return null;
    }
    
	@Override
	public void onMove (LatLon fromCoord, LatLon toCoord, Point fromPoint, Point toPoint) {
		
		MovementAlertSettings conf = MovementAlertSettings.sharedInstance();
		if (!conf.isEnabled()) {
			return;
		}
		double geoDistance = OsmMercator.MERCATOR_256.getDistance(fromCoord.lat(), fromCoord.lon(), 
				toCoord.lat(), toCoord.lon());
		
		boolean shouldAlert;
		if (conf.isThresholdRelativeToZoom()) {
			// Compare screen size and screen points
			shouldAlert = false;
			double screenDistance = fromPoint.distance(toPoint);
			Rectangle mapBounds = getMapBounds();
			if (mapBounds!=null) {
				double diagonalLength = Math.sqrt(Math.pow(mapBounds.getWidth(),2) + 
						Math.pow(mapBounds.getHeight(),2));
				shouldAlert = (screenDistance > diagonalLength * MovementAlertSettings.RELATIVE_THRESHOLD_RATIO);
			}
		} else {
			// Compare geographical positions
			shouldAlert = (geoDistance > MovementAlertSettings.sharedInstance().getThreshold());
		}
		

		if (shouldAlert) {
			new ConfirmMoveDialog()
					.setContent(tr("The object was moved {0}m.", String.format("%.1f", geoDistance)))
					.toggleEnable(MovementAlertSettings.MOVEMENT_ALERT_ENABLE_KEY_DIALOG)
					.showDialog();
		}
		
	}
}
