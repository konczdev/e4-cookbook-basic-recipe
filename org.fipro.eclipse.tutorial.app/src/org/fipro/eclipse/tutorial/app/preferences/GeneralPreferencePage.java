package org.fipro.eclipse.tutorial.app.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class GeneralPreferencePage extends PreferencePage {

	// Names for preferences
	private static final String APP_TITLE = "app_title";
	private static final String WELCOME_MSG = "welcome_message";

	// Text fields for user to enter preferences
	private Text fieldOne;
	private Text fieldTwo;

	public GeneralPreferencePage() {
		super("General");
		setDescription("The general preferences");
	}

	/**
	 * Creates the controls for this page
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		// Get the preference store
		IPreferenceStore preferenceStore = getPreferenceStore();

		// Create text fields.
		// Set the text in each from the preference store
		new Label(composite, SWT.LEFT).setText("Application Title:");
		fieldOne = new Text(composite, SWT.BORDER);
		fieldOne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fieldOne.setText(preferenceStore.getString(APP_TITLE));

		new Label(composite, SWT.LEFT).setText("Welcome Message:");
		fieldTwo = new Text(composite, SWT.BORDER);
		fieldTwo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fieldTwo.setText(preferenceStore.getString(WELCOME_MSG));
		
		return composite;
	}

	/**
	 * Called when user clicks Restore Defaults
	 */
	@Override
	protected void performDefaults() {
		// Get the preference store
		IPreferenceStore preferenceStore = getPreferenceStore();

		// Reset the fields to the defaults
		fieldOne.setText(preferenceStore.getDefaultString(APP_TITLE));
		fieldTwo.setText(preferenceStore.getDefaultString(WELCOME_MSG));
	}

	/**
	 * Called when user clicks Apply or OK
	 * 
	 * @return boolean
	 */
	@Override
	public boolean performOk() {
		// Get the preference store
		IPreferenceStore preferenceStore = getPreferenceStore();

		// Set the values from the fields
		if (fieldOne != null) {
			preferenceStore.setValue(APP_TITLE, fieldOne.getText());
		}
		if (fieldTwo != null) {
			preferenceStore.setValue(WELCOME_MSG, fieldTwo.getText());
		}

		// Return true to allow dialog to close
		return true;
	}
}