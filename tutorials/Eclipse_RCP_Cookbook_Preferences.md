# Eclipse RCP Cookbook – The Decoration Recipe (Eclipse Preferences)

When you are baking cookies with your kids, the most funny thing for the kids is to decorate the cookies. Some like chocolate flakes, some more the pink sugar pops, others want to use different colors on top.

The users of an Eclipse application are also not the same and do not all like the same settings. To provide a way to customize an Eclipse application, you can use **Preferences** that a user can configure.

This recipe will explain and show how to add a preference page to an Eclipse 4 application and how to handle preference changes. 

## Ingredients

This recipe is based on the [Eclipse RCP Cookbook – The Topping Recipe](Eclipse_RCP_Cookbook_p2.md). To get started fast with this recipe, the recipe is prepared for you on GitHub.

To use the prepared recipe, import the project by cloning the Git repository:

- _File → Import → Git → Projects from Git_
- Click _Next_
- Select _Clone URI_
- Enter URI _https://github.com/fipro78/e4-cookbook-basic-recipe.git_
- Click _Next_
- Select the **p2_update** branch
- Click _Next_
- Choose a directory where you want to store the checked out sources
- Select _Import existing Eclipse projects_
- Click _Finish_

## Preparation

### Step 1: Update the Target Platform

In a plain Eclipse 4 application, you don't want to use the _Compatibility Layer_. As the existing preferences support has dependencies to `org.eclipse.ui` (e.g. the `ScopedPreferenceStore` is located in `org.eclipse.ui.workbench`) or at least consumes classes from there, I implemented an alternative approach, that does not make use of `org.eclipse.ui` and provides a way to contribute preference pages to a dialog, without the need to define _Extension Points_. The bundle that provides this feature is available as an update site on GitHub.

- Open the target definition _org.fipro.eclipse.tutorial.target.target_ in the project _org.fipro.eclipse.tutorial.target_

- Update the Software Sites in the opened _Target Definition Editor_ 
    - Alternative A
        - Switch to the _Source_ tab and add the following snippet to the editor  
    ```xml
    <target name="E4 Cookbook Target Platform" sequenceNumber="1568034040">
        <locations>
            <location 
                includeAllPlatforms="false" 
                includeConfigurePhase="false" 
                includeMode="planner" 
                includeSource="true" 
                type="InstallableUnit">

                <unit 
                    id="org.eclipse.equinox.executable.feature.group" 
                    version="3.8.2400.v20240213-1244"/>
                <unit 
                    id="org.eclipse.sdk.feature.group" 
                    version="4.31.0.v20240229-1022"/>
                    
                <unit 
                    id="org.eclipse.equinox.core.feature.feature.group" 
                    version="1.15.0.v20240214-0846"/>
                <unit 
                    id="org.eclipse.equinox.p2.core.feature.feature.group" 
                    version="1.7.100.v20240220-1431"/>
                    
                <repository 
                    location="https://download.eclipse.org/releases/2024-03"/>
            </location>
            <location 
                includeAllPlatforms="false" 
                includeConfigurePhase="false" 
                includeMode="planner" 
                includeSource="true" 
                type="InstallableUnit">
                
                <unit 
                    id="org.fipro.e4.service.preferences.feature.feature.group" 
                    version="0.5.0.202406031042"/>

                <repository
                    location="https://github.com/fipro78/e4-preferences/raw/master/releases/0.5.0"/>
            </location>
        </locations>
    </target>
    ```

    - Alternative B
        - Select _Add..._
        - Select _Software Site_
        - Click _Next_
        - Enter _https://github.com/fipro78/e4-preferences/raw/master/releases/0.5.0_ in _Work with:_
        - Select _E4 Preferences Service_
        - Click _Finish_
- Switch to the _Definition_ tab
    - Wait until the Target Definition is completely resolved (check the progress at the bottom right)
    - Reload and activate the target platform by clicking _Reload Target Platform_ in the upper right corner of the Target Definition Editor

### Step 2: Prepare the application plug-in

To provide the user an option to change preferences, a handler will be added to the application plug-in that can be triggered via a menu entry in the main menu and opens the preferences dialog .

- Update the bundle dependencies
    - Open the file _META-INF/MANIFEST.MF_ in the project _org.fipro.eclipse.tutorial.app_
    - Switch to the _Dependencies_ tab
        - Add the following packages to the _Imported Packages_
            - `org.eclipse.e4.core.di.extensions`
            - `org.fipro.e4.service.preferences`
            - `org.osgi.service.event`
- Update the application model
    - Open the file _Application.e4xmi_ in the project _org.fipro.eclipse.tutorial.app_
    - Add a command
        - _Application → Commands → Add_
            - Set _Name_ to _Preferences_
            - Set _ID_ to _org.fipro.eclipse.tutorial.app.command.preferences_  
            (will be done automatically on setting the _Name_)
    - Add a handler
        - _Application → Handlers → Add_
            - Set _ID_ to _org.fipro.eclipse.tutorial.app.handler.preferences_
            - Set the _Command_ reference to _org.fipro.eclipse.tutorial.app.command.preferences_ via the _Find..._ dialog
            - Create a handler implementation by clicking on the _Class URI_ link
                - Set _Package_ to _org.fipro.eclipse.tutorial.app.handler_
                - Set _Name_ to _PreferencesHandler_
                - Click _Finish_
            - Implement the `PreferencesHandler` similar to the following snippet
                - Use the `@PrefMgr` annotation to get the the `PreferenceManager` injected in the `execute()` method
                - Open a `PreferenceDialog`
            ```java
            package org.fipro.eclipse.tutorial.app.handler;

            import org.eclipse.e4.core.di.annotations.Execute;
            import org.eclipse.jface.preference.PreferenceDialog;
            import org.eclipse.jface.preference.PreferenceManager;
            import org.eclipse.jface.viewers.TreeViewer;
            import org.eclipse.jface.viewers.ViewerComparator;
            import org.eclipse.swt.widgets.Composite;
            import org.eclipse.swt.widgets.Shell;
            import org.fipro.e4.service.preferences.ContributedPreferenceNode;

            public class PreferencesHandler {
	
                @Execute
                public void execute(Shell shell, PreferenceManager manager) {

                    PreferenceDialog dialog = new PreferenceDialog(shell, manager) {
                        @Override
                        protected TreeViewer createTreeViewer(Composite parent) {
                            TreeViewer viewer = super.createTreeViewer(parent);

                            viewer.setComparator(new ViewerComparator() {

                                @Override
                                public int category(Object element) {
                                    // this ensures that the General preferences page is always on top
                                    // while the other pages are ordered alphabetical
                                    if (element instanceof ContributedPreferenceNode
                                            && ("general".equals(((ContributedPreferenceNode) element).getId()))) {
                                        return -1;
                                    }
                                    return 0;
                                }
                            });
                            return viewer;
                        }
                    };
                    dialog.open();
                }
            }
            ```
    - Select _Application → Windows → Trimmed Window → Main Menu → Menu File_
        - Add a _Handled Menu Item_
        - Set the _Label_ to _Preferences_
        - Set the _Command_ reference to the _Preference_ command via _Find..._ dialog
    - Save the changes to the application model

### Step 3: Update the Product Configuration

- Open the file _org.fipro.eclipse.tutorial.app.product_ in the project _org.fipro.eclipse.tutorial.product_
- Switch to the _Contents_ tab
    - Add _org.fipro.e4.service.preferences.feature_

### Step 4: Implement and contribute a preference page for general settings

Implement a JFace `PreferencePage` for some basic settings, e.g. the application title shown in the main window:

- Right click on the project _org.fipro.eclipse.tutorial.app_
- _New → Class_
    - Set _Package_ to _org.fipro.eclipse.tutorial.app.preferences_
    - Set _Name_ to _GeneralPreferencePage_
    - Set _Superclass_ to _org.eclipse.jface.preference.PreferencePage_
    - Click _Finish_

```java
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
```

The above implementation simply provides the option to set an application title and a welcome message via preferences.

Implement the `PreferenceNodeContribution` service that contributes the `PreferencePage` to the dialog:

- Right click on the project _org.fipro.eclipse.tutorial.app_
- _New → Class_
    - Set _Package_ to _org.fipro.eclipse.tutorial.app.preferences_
    - Set _Name_ to _ApplicationPreferencesContribution_
    - Set _Superclass_ to _org.fipro.e4.service.preferences.PreferenceNodeContribution_
    - Click _Finish_

```java
package org.fipro.eclipse.tutorial.app.preferences;

import org.fipro.e4.service.preferences.PreferenceNodeContribution;
import org.osgi.service.component.annotations.Component;

@Component(service = PreferenceNodeContribution.class)
public class ApplicationPreferencesContribution extends PreferenceNodeContribution {

	public ApplicationPreferencesContribution() {
		super("general", "General", GeneralPreferencePage.class);
	}

}
```

__*Note:*__  
If you want to provide multiple preference pages from one plugin, you can use one of the `PreferenceNodeContribution#addPreferenceNode()` methods after the `super()` call.

### Step 5: Add preference handling via model Addon

From the old Eclipse Wiki:  
 _**Addons** are objects that are instantiated by Eclipse 4's dependency injection framework. **Addons** are global and are contained under the application._

_These addon objects are created before the rendering engine actually renders the model. As such, addons can be used to alter the user interface that is produced by the rendering engine. For example, the min/max addon that comes with the Eclipse 4.x SDK tweaks the tab folders created for MPartStacks to have min/max buttons in the corner._

Add-ons can for example be used to perform actions once the app startup is completed, or even other events you fire. As our application does not contain any user interface classes so far, we will add an **Add-on** that reacts on the preference change to update the application title in the main window. Additionally it adds the ability to show a welcome message on startup.

- Update the application model
    - Open the file _Application.e4xmi_ in the project _org.fipro.eclipse.tutorial.app_
    - Add an Add-on
        - _Application → Add-ons → Add_
            - Click on _Class URI_
            - Set _Package_ to _org.fipro.eclipse.tutorial.app.addon_
            - Set _Name_ to _AppTitleAddon_
        - Replace the implementation with the following snippet
            - Get the `MApplication` injected
            - Implement an event handler method that reacts on the event topic `UIEvents.UILifeCycle.APP_STARTUP_COMPLETE` and gets the _welcome_message_ preference value injected, and shows a dialog if a message is set.
            - Implement a preference change listener method that gets the _app_title_ preference value injected and changes the title of the main window.

```java
package org.fipro.eclipse.tutorial.app.addon;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.EventTopic;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;

import jakarta.inject.Inject;

public class AppTitleAddon {

	@Inject
	private MApplication application;

	@Inject
	@Optional
	public void applicationStarted(
			@EventTopic(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE) Event event,
			@Preference(nodePath = "org.fipro.eclipse.tutorial.app", value = "welcome_message") String msg) {
		
		if (msg != null) {
			// need to ensure that the dialog is opened in the UI thread
			Display.getDefault().asyncExec(() -> {
				Shell shell = application.getContext().get(Shell.class);
				MessageDialog.openInformation(shell, "Welcome", msg);
			});
		}
	}
	
	@Inject
	@Optional
	public void setAppTitle(
			@Preference(nodePath = "org.fipro.eclipse.tutorial.app", value = "app_title") String title) {
	
		if (title == null || title.isBlank()) {
			title = "Eclipse Cookbook Application";
		}
		
		application.getChildren().get(0).setLabel(title);
	}
}
```

The preference change listener method uses the `@Preference` annotation in combination with `@Inject`. We also use `@Optional` to avoid errors if no value for the specified preference is available when the injection processing happens.

The `@Preference` annotation has two parameters:
- The _nodePath_ parameter is the file name used to save the preference values to disk. By default, this is the Bundle-SymbolicName of the plugin. 
- The _value_ parameter specifies the preference key for the value which should be injected.

### Step 6: Implement and contribute a JFace `PreferencePage` for the `InverterPart`

- Update the bundle dependencies
    - Open the file _META-INF/MANIFEST.MF_ in the project _org.fipro.eclipse.tutorial.inverter_
    - Switch to the _Dependencies_ tab
        - Add the following packages to the _Imported Packages_
            - `org.eclipse.e4.core.di.annotations`
            - `org.fipro.e4.service.preferences`

- Right click on the project _org.fipro.eclipse.tutorial.inverter_
- _New → Class_
    - Set _Package_ to _org.fipro.eclipse.tutorial.inverter.preferences_
    - Set _Name_ to _InverterPreferencePage_
    - Set _Superclass_ to _org.eclipse.jface.preference.PreferencePage_
    - Click _Finish_

```java
package org.fipro.eclipse.tutorial.inverter.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class InverterPreferencePage extends PreferencePage {

	// Names for preferences
	private static final String INVERTER_COLOR = "inverter_color";

	// The checkboxes
	private Button checkOne;
	private Button checkTwo;

	public InverterPreferencePage() {
		super("Inverter");
		setDescription("The inverter preferences page");
	}

	/**
	 * Creates the controls for this page
	 */
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		// Get the preference store
		IPreferenceStore preferenceStore = getPreferenceStore();

		String color = preferenceStore.getString(INVERTER_COLOR);
		boolean isBlack = (color != null && !color.isEmpty()) ? "black".equals(color) : true;

		// Create the checkboxes
		checkOne = new Button(composite, SWT.RADIO);
		checkOne.setText("Text Color Black");
		checkOne.setSelection(isBlack);

		checkTwo = new Button(composite, SWT.RADIO);
		checkTwo.setText("Text Color Blue");
		checkTwo.setSelection(!isBlack);

		checkOne.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkOne.setSelection(true);
				checkTwo.setSelection(false);
			}
		});

		checkTwo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkOne.setSelection(false);
				checkTwo.setSelection(true);
			}
		});

		return composite;
	}

	/**
	 * Called when user clicks Restore Defaults
	 */
	protected void performDefaults() {
		// Get the preference store
		IPreferenceStore preferenceStore = getPreferenceStore();

		String color = preferenceStore.getString(INVERTER_COLOR);
		boolean isBlack = (color != null && !color.isEmpty()) ? "black".equals(color) : true;

		// Reset the fields to the defaults
		checkOne.setSelection(isBlack);
		checkTwo.setSelection(!isBlack);
	}

	/**
	 * Called when user clicks Apply or OK
	 * 
	 * @return boolean
	 */
	public boolean performOk() {
		// Get the preference store
		IPreferenceStore preferenceStore = getPreferenceStore();

		// Set the values from the fields
		if (checkOne != null && checkOne.getSelection()) {
			preferenceStore.setValue(INVERTER_COLOR, "black");
		} else if (checkTwo != null && checkTwo.getSelection()) {
			preferenceStore.setValue(INVERTER_COLOR, "blue");
		}

		// Return true to allow dialog to close
		return true;
	}

}
```

Implement the `PreferenceNodeContribution` service that contributes the `PreferencePage` to the dialog:

- Right click on the project _org.fipro.eclipse.tutorial.inverter_
- _New → Class_
    - Set _Package_ to _org.fipro.eclipse.tutorial.inverter.preferences_
    - Set _Name_ to _InverterPreferencesContribution_
    - Set _Superclass_ to _org.fipro.e4.service.preferences.PreferenceNodeContribution_
    - Click _Finish_

```java
package org.fipro.eclipse.tutorial.inverter.preferences;

import org.fipro.e4.service.preferences.PreferenceNodeContribution;
import org.osgi.service.component.annotations.Component;

@Component(service = PreferenceNodeContribution.class)
public class InverterPreferencesContribution extends PreferenceNodeContribution {

	public InverterPreferencesContribution() {
		super("inverter", "Inverter", InverterPreferencePage.class);
	}

}
```

### Step 7: Add preference handling in the `InverterPart`

- Open the `InverterPart`
    - _CTRL_ + _SHIFT_ + _T_
    - In the _Open Type_ dialog enter _InverterPart_ and select it in the list view
- Change the `input` and `output` `Text` fields to class members
- Introduce a class member for the text color that should be applied
- Add a method for the preference handling
```java
package org.fipro.eclipse.tutorial.inverter.part;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.di.extensions.Service;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fipro.eclipse.tutorial.service.inverter.InverterService;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

public class InverterPart {
	
	@Inject
	@Service
	private InverterService inverter;
	
	@Inject
    IEventBroker broker;
	
	Text input;
	Text output;
	
	Color textColor = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(3, true));
		
		Label inputLabel = new Label(parent, SWT.NONE);
		inputLabel.setText("String to revert:");
		GridDataFactory.fillDefaults().applyTo(inputLabel);
		
		input = new Text(parent, SWT.BORDER);
		input.setForeground(textColor);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(input);
		
		Button button = new Button(parent, SWT.PUSH);
		button.setText("Revert");
		GridDataFactory.defaultsFor(button).applyTo(button);
		
		Label outputLabel = new Label(parent, SWT.NONE);
		outputLabel.setText("Inverted String:");
		GridDataFactory.fillDefaults().applyTo(outputLabel);
		
		output = new Text(parent, SWT.READ_ONLY | SWT.WRAP);
		output.setForeground(textColor);
		GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(output);
		
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				output.setText(inverter.invert(input.getText()));
				broker.post("TOPIC_LOGGING", "triggered via button");
			}
		});

		input.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR
						|| e.keyCode == SWT.KEYPAD_CR) {
					output.setText(inverter.invert(input.getText()));
					broker.post("TOPIC_LOGGING", "triggered via field");
				}
			}
		});
	}

	@Inject
	@Optional
	public void setTextColor(
	        @Preference(nodePath = "org.fipro.eclipse.tutorial.inverter", value = "inverter_color") String color) {

	    textColor = "blue".equals(color) 
	            ? Display.getDefault().getSystemColor(SWT.COLOR_BLUE)
	            : Display.getDefault().getSystemColor(SWT.COLOR_BLACK);

	    if (input != null && !input.isDisposed()) {
	        input.setForeground(textColor);
	    }

	    if (output != null && !output.isDisposed()) {
	        output.setForeground(textColor);
	    }
	}
}
```

### Step 8: Add an option to restart the application (optional)

As we added a preference to specify a welcome message on application startup, it might be useful to add an option to restart the application. This is optional, as you can also stop the application and start it again.

- Open the file _Application.e4xmi_ in the project _org.fipro.eclipse.tutorial.app_
- Add a restart command
    - _Application → Commands → Add_
        - Set _Name_ to _Restart_
        - Set _ID_ to _org.fipro.eclipse.tutorial.app.command.restart_
- Add a restart handler
    - _Application → Handlers → Add_
        - Set _ID_ to _org.fipro.eclipse.tutorial.app.handler.restart_
        - Set the _Command_ reference to _org.fipro.eclipse.tutorial.app.command.restart_ via _Find..._ dialog
        - Create a handler implementation by clicking on the _Class URI_ link
            - Set _Package_ to _org.fipro.eclipse.tutorial.app.handler_
            - Set _Name_ to _RestartHandler_
            ```java
            @Execute
            public void execute(IWorkbench workbench, Shell shell) {
                if (MessageDialog.openConfirm(shell, "Restart", "Do you want to restart?")) {
                    workbench.restart();
                }
            }
            ```
- Add a _Handled Menu Item_ to the _File_ menu
    - Set the _Label_ to _Restart_
    - Set the _Command_ reference to the _Restart_ command via _Find..._ dialog

## Taste

- Start the application from within the IDE
    - Open the Product Configuration in the _org.fipro.eclipse.tutorial.product_ project
    - Select the _Overview_ tab
    - Click _Launch an Eclipse Application_ in the _Testing_ section

Alternatively you can also run the Tycho build and then start the created product as explained the [Thermomix Recipe](/tutorials/Eclipse_RCP_Cookbook_Tycho.md). 

- In the started application
    - Open the Preference Dialog via _File → Preferences_
    - Select the _General_ settings and enter values for _Application Title_ and _Welcome Message_
    - Select the _Inverter_ settings and select the _Text Color Blue_
    - Click _Apply and Close_
    - Verify the changed settings
        - The window title should now show the value you just entered
        - Enter a value in the text field to trigger the action and verify that the text is shown in blue
        - Restart the application and verify that a message dialog appears on startup showing the message you entered in the preferences.

Further information about Eclipse Preferences can be found in [Eclipse Preferences - Tutorial @vogella](https://www.vogella.com/tutorials/EclipsePreferences/article.html)
