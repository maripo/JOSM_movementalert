package org.maripo.josm.movementalert;

import org.openstreetmap.josm.spi.preferences.Config;

public class MovementAlertSettings {
    public static final String MOVEMENT_ALERT_THRESHOLD_KEY = "movementAlert.threshold";
    public static final String MOVEMENT_ALERT_RELATIVE_TO_ZOOM_LEVEL = "movementAlert.thresholdRelativeToZoom";
	public static final String MOVEMENT_ALERT_ENABLE_KEY_DIALOG = "movementAlert.dialog";
	public static final String MOVEMENT_ALERT_ENABLE_KEY = "message." + MOVEMENT_ALERT_ENABLE_KEY_DIALOG;
	public static final double RELATIVE_THRESHOLD_RATIO = 0.4;
	
    private static int THRESHOLD_DEFAULT = 50;
	private static MovementAlertSettings instance;
	private boolean isEnabled = true;
	private boolean isThresholdRelativeToZoom = false;
	private int threshold = THRESHOLD_DEFAULT;
	
	public static MovementAlertSettings sharedInstance () {
		if (instance == null) {
			instance = new MovementAlertSettings();
		}
		return instance;
	}
	public void load() {
		String thresholdStr = Config.getPref().get(MOVEMENT_ALERT_THRESHOLD_KEY);
		threshold = THRESHOLD_DEFAULT;
		if (thresholdStr != null) {
			try {
				int v = Integer.parseInt(thresholdStr);
				if (v > 0) {
					threshold = v;
				}
			} catch (NumberFormatException e) {
				// e.printStackTrace();
			}
		}
		isEnabled = !"false".equals(Config.getPref().get(MOVEMENT_ALERT_ENABLE_KEY));
		isThresholdRelativeToZoom = "true".equals(Config.getPref().get(MOVEMENT_ALERT_RELATIVE_TO_ZOOM_LEVEL));
	}
	public void save (String thresholdStr, boolean isEnabled, boolean isThresholdRelativeToZoom) {
		try {
			int newThreshold = Integer.parseInt(thresholdStr);
			if (newThreshold > 0) {
				Config.getPref().put(MOVEMENT_ALERT_THRESHOLD_KEY, Integer.toString(newThreshold));
				threshold = newThreshold;
			}
		} catch (NumberFormatException e) {
			// e.printStackTrace();
		}
		this.isEnabled = isEnabled;
		this.isThresholdRelativeToZoom = isThresholdRelativeToZoom;
		Config.getPref().put(MOVEMENT_ALERT_ENABLE_KEY, Boolean.toString(isEnabled));
		Config.getPref().put(MOVEMENT_ALERT_RELATIVE_TO_ZOOM_LEVEL, Boolean.toString(isThresholdRelativeToZoom));
		
	}
	public boolean isEnabled() {
		return isEnabled;
	}
	public boolean isThresholdRelativeToZoom () {
		return isThresholdRelativeToZoom;
	}
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	public int getThreshold() {
		return threshold;
	}
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
}
