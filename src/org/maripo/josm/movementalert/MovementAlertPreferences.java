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
    private final JosmTextField movementAlertThreshold = new JosmTextField();
    
    
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
        p.add(new JLabel(tr("Alert threshold (m)")), GBC.std().insets(20, 0, 0, 0));
        p.add(movementAlertThreshold, GBC.eol().fill(GBC.HORIZONTAL).insets(0, 0, 0, 5));
        p.add(Box.createVerticalGlue(), GBC.eol().fill(GBC.VERTICAL));
        
        createPreferenceTabWithScrollPane(gui, p);

	}

	private void loadPrefs() {
		MovementAlertSettings settings = MovementAlertSettings.sharedInstance();
		settings.load();
		movementAlertThreshold.setText(Integer.toString(settings.getThreshold()));
		movementAlertOn.setSelected(settings.isEnabled());
		movementAlertOff.setSelected(!settings.isEnabled());
	}

	@Override
	public boolean ok() {
		save();
		return false;
	}
	
	private void save() {
		MovementAlertSettings.sharedInstance().save(movementAlertThreshold.getText(), movementAlertOn.isSelected());	
	}

	@Override
	public boolean isExpert() {
		return false;
	}
}
