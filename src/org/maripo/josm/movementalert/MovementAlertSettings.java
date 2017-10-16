package org.maripo.josm.movementalert;

import org.openstreetmap.josm.Main;

public class MovementAlertSettings {
    public static final String MOVEMENT_ALERT_THRESHOLD_KEY = "movementAlert.threshold";
	public static final String MOVEMENT_ALERT_ENABLE_KEY_DIALOG = "movementAlert.dialog";
	public static final String MOVEMENT_ALERT_ENABLE_KEY = "message." + MOVEMENT_ALERT_ENABLE_KEY_DIALOG;
	
    private static int THRESHOLD_DEFAULT = 50;
	private static MovementAlertSettings instance;
	private boolean isEnabled = true;
	private int threshold = THRESHOLD_DEFAULT;
	
	public static MovementAlertSettings sharedInstance () {
		if (instance == null) {
			instance = new MovementAlertSettings();
		}
		return instance;
	}
	public void load() {
		String thresholdStr = Main.pref.get(MOVEMENT_ALERT_THRESHOLD_KEY);
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
		isEnabled = "true".equals(Main.pref.get(MOVEMENT_ALERT_ENABLE_KEY));
	}
	public void save (String thresholdStr, boolean isEnabled) {
		try {
			int newThreshold = Integer.parseInt(thresholdStr);
			if (newThreshold > 0) {
				Main.pref.put(MOVEMENT_ALERT_THRESHOLD_KEY, Integer.toString(newThreshold));
				threshold = newThreshold;
			}
		} catch (NumberFormatException e) {
			// e.printStackTrace();
		}
		this.isEnabled = isEnabled;
		Main.pref.put(MOVEMENT_ALERT_ENABLE_KEY, Boolean.toString(isEnabled));
		
	}
	public boolean isEnabled() {
		return isEnabled;
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
