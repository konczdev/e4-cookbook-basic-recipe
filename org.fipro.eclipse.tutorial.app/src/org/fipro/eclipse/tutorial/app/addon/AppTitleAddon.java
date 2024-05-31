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
