package org.maripo.josm.movementalert;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.preferences.DefaultTabPreferenceSetting;
import org.openstreetmap.josm.gui.preferences.PreferenceSetting;
import org.openstreetmap.josm.gui.preferences.PreferenceTabbedPane;
import org.openstreetmap.josm.gui.widgets.JosmTextField;
import org.openstreetmap.josm.tools.GBC;

public class MovementAlertPreferences extends DefaultTabPreferenceSetting implements PreferenceSetting {

    private final JRadioButton movementAlertOn = new JRadioButton("On");
    private final JRadioButton movementAlertOff = new JRadioButton("Off");

    private final JRadioButton thresholdAbsolute = new JRadioButton(tr("Distance"));
    private final JRadioButton thresholdRelative = new JRadioButton(tr("Relative to zoom level"));
    
    private final JosmTextField movementAlertThreshold = new JosmTextField(6);
    
    
    private MovementAlertPreferences () {
        super("movementalert", tr("MovementAlert settings"), tr("Settings for MovementAlert plugin"));
    }
    
    public static MovementAlertPreferences createInstance () {
    	return new MovementAlertPreferences();
    }
    
	@Override
	public void addGui(PreferenceTabbedPane gui) {
		
        ButtonGroup alertButtons = new ButtonGroup();
        JPanel p = new JPanel(new GridBagLayout());
        loadPrefs();
        
        alertButtons.add(movementAlertOff);
        alertButtons.add(movementAlertOn);
        
        p.add(movementAlertOff, GBC.eol());
        p.add(movementAlertOn, GBC.eol());
        p.add(new JLabel(tr("Alert threshold")), GBC.eol().insets(25, 0, 0, 0));
        
        ButtonGroup thresholdTypeButtons = new ButtonGroup();
        thresholdTypeButtons.add(thresholdAbsolute);
        thresholdTypeButtons.add(thresholdRelative);
        // Absolute
        p.add(thresholdAbsolute, GBC.std().insets(40, 0, 0, 0));
        p.add(movementAlertThreshold, GBC.std());
        p.add(new JLabel("(m)"), GBC.eol().fill(GBC.HORIZONTAL));
        
        // Relative
        p.add(thresholdRelative, GBC.eol().insets(40, 0, 0, 0).fill(GBC.HORIZONTAL));
        
        // Filler
        p.add(Box.createVerticalGlue(), GBC.eol().fill(GBC.VERTICAL));
        
        createPreferenceTabWithScrollPane(gui, p);

	}

	private void loadPrefs() {
		MovementAlertSettings settings = MovementAlertSettings.sharedInstance();
		settings.load();
		movementAlertThreshold.setText(Integer.toString(settings.getThreshold()));
		
		movementAlertOn.setSelected(settings.isEnabled());
		movementAlertOff.setSelected(!settings.isEnabled());
		
		thresholdAbsolute.setSelected(!settings.isThresholdRelativeToZoom());
		thresholdRelative.setSelected(settings.isThresholdRelativeToZoom());
	}

	@Override
	public boolean ok() {
		save();
		return false;
	}
	
	private void save() {
		MovementAlertSettings.sharedInstance().save(movementAlertThreshold.getText(), 
				movementAlertOn.isSelected(),
				thresholdRelative.isSelected());	
	}

	@Override
	public boolean isExpert() {
		return false;
	}
}
