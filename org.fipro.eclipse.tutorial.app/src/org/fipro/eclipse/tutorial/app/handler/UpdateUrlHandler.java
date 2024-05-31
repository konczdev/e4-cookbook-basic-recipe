package org.fipro.eclipse.tutorial.app.handler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

public class UpdateUrlHandler {
	
	@Execute
	public void execute(
			IProvisioningAgent agent,
			UISynchronize sync, 
			IWorkbench workbench,
			Shell shell) {

		InputDialog input = new InputDialog(shell, "Update", "Enter the URL to the update site", null, null);
		if (Window.OK == input.open()) {
			String updateUrl = input.getValue();
			
			// simple check if an online url or a local url is entered
			if (updateUrl != null && !updateUrl.isBlank() && !updateUrl.startsWith("http")) {
				// no http or https, check if the value is a local directory
				Path path = Paths.get(updateUrl);
				if (Files.exists(path) && Files.isDirectory(path)) {
					UpdateHandler update = new UpdateHandler();
					update.execute(agent, path.toUri().toString(), sync, workbench);
				} else {
					MessageDialog.openError(null, "Error", "Invalid URL - Update Site does not exist or is not a directory");
				}
			}
		}
	}
}