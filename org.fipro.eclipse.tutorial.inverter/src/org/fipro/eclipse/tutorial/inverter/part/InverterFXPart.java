package org.fipro.eclipse.tutorial.inverter.part;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.di.extensions.Service;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.fipro.eclipse.tutorial.service.inverter.InverterService;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.embed.swt.FXCanvas;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class InverterFXPart {
	
	@Inject
	@Service
	private InverterService inverter;
	
	@Inject
    IEventBroker broker;

	TextField input;
	Label output;
	
	Color textColor = Color.BLACK;
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		GridLayoutFactory.fillDefaults().applyTo(parent);
		
		// add FXCanvas for adding JavaFX controls to the UI
		FXCanvas canvas = new FXCanvas(parent, SWT.NONE);
		GridDataFactory
		    .fillDefaults()
		    .grab(true, true)
		    .span(3, 1)
		    .applyTo(canvas);

		// create the root layout pane
		GridPane layout = new GridPane();

		// create a Scene instance
		// set the layout container as root
		// set the background fill to the background color of the shell
		Scene scene = new Scene(layout, Color.rgb(
		    parent.getShell().getBackground().getRed(),
		    parent.getShell().getBackground().getGreen(),
		    parent.getShell().getBackground().getBlue()));

		// set the Scene to the FXCanvas
		canvas.setScene(scene);
		
		// create the controls
		Label inputLabel = new Label();
		inputLabel.setText("String to revert:");
		GridPane.setConstraints(inputLabel, 0, 0);
		GridPane.setMargin(inputLabel, new Insets(5.0));
		
		input = new TextField();
		input.setStyle("-fx-text-fill: " + (textColor == Color.BLUE ? "blue" : "black") + ";");
		GridPane.setConstraints(input, 1, 0);
		GridPane.setHgrow(input, Priority.ALWAYS);
		GridPane.setMargin(input, new Insets(5.0));
		
		Button button = new Button();
		button.setText("Revert");
		GridPane.setConstraints(button, 2, 0);
		GridPane.setMargin(button, new Insets(5.0));
		
		Label outputLabel = new Label();
		outputLabel.setText("Inverted String:");
		GridPane.setConstraints(outputLabel, 0, 1);
		GridPane.setMargin(outputLabel, new Insets(5.0));
		
		output = new Label();
		output.setTextFill(textColor);
		GridPane.setConstraints(output, 0, 2);
		GridPane.setColumnSpan(output, 3);
		GridPane.setHgrow(output, Priority.ALWAYS);
		GridPane.setHalignment(output, HPos.CENTER);
		
		// don't forget to add children to gridpane
		layout.getChildren().addAll(
				inputLabel, input, button, outputLabel, output);
		
		// add an animation for the output
		RotateTransition rotateTransition = 
				new RotateTransition(Duration.seconds(1), output);
		rotateTransition.setByAngle(360);
		
		ScaleTransition scaleTransition = 
				new ScaleTransition(Duration.seconds(1), output);
		scaleTransition.setFromX(1.0);
		scaleTransition.setFromY(1.0);
		scaleTransition.setToX(4.0);
		scaleTransition.setToY(4.0);
		
		ParallelTransition parallelTransition = 
				new ParallelTransition(rotateTransition, scaleTransition);

		// add the action listener
		button.setOnAction(event -> {
			output.setText(inverter.invert(input.getText()));
			broker.post("TOPIC_LOGGING", "triggered via button (FX)");			
	        parallelTransition.play();
		});

		input.setOnAction(event -> {
			output.setText(inverter.invert(input.getText()));
			broker.post("TOPIC_LOGGING", "triggered via field (FX)");
	        parallelTransition.play();
		});
		
	}

	@Inject
	@Optional
	public void setTextColor(
	        @Preference(nodePath = "org.fipro.eclipse.tutorial.inverter", value = "inverter_color") String color) {

	    textColor = "blue".equals(color) 
	            ? Color.BLUE
	            : Color.BLACK;

	    if (input != null) {
	        input.setStyle("-fx-text-fill: " + color + ";");
	    }

	    if (output != null) {
	        output.setTextFill(textColor);
	    }
	}
	
}