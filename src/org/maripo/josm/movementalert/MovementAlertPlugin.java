package org.maripo.josm.movementalert;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.AWTEvent;
import java.awt.Toolkit;

import org.maripo.josm.movementalert.MovementMonitor.MovementListener;
import org.openstreetmap.gui.jmapviewer.OsmMercator;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.gui.ExtendedDialog;
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
		System.out.println("MovementAlertApp");
		Main.getLayerManager().addLayerChangeListener(this);
		Toolkit.getDefaultToolkit().addAWTEventListener(monitor, AWTEvent.MOUSE_EVENT_MASK);
	}

	@Override
	public void layerAdded(LayerAddEvent evt) {
		Layer layer = evt.getAddedLayer();
		if (layer instanceof OsmDataLayer) {
			((OsmDataLayer) layer).data.addDataSetListener(monitor);
			((OsmDataLayer) layer).data.addSelectionListener(monitor);
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

	@Override
	public void onMove(LatLon from, LatLon to) {
		double distance = OsmMercator.MERCATOR_256.getDistance(from.lat(), from.lon(), to.lat(), to.lon());

		if (distance > MovementAlertSettings.sharedInstance().getThreshold()) {
			new ConfirmMoveDialog()
					.setContent(tr("The object was moved {0}m.", String.format("%.1f", distance)))
					.toggleEnable(MovementAlertSettings.MOVEMENT_ALERT_ENABLE_KEY_DIALOG)
					.showDialog();
		}
	}
}
