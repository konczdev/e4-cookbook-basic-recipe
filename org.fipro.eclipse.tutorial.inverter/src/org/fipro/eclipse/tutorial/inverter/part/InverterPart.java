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