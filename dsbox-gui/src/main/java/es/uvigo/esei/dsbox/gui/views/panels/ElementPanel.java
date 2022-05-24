package es.uvigo.esei.dsbox.gui.views.panels;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public abstract class ElementPanel extends Parent {

    protected Button save;
    protected Button cancel;

    protected EventHandler<ActionEvent> saveActionHandler;
    protected EventHandler<ActionEvent> cancelActionHandler;

    public ElementPanel() {
    }

    public void setSaveActionHandler(EventHandler<ActionEvent> saveActionHandler) {
        this.saveActionHandler = saveActionHandler;
        if (save != null) {
            save.setOnAction(saveActionHandler);
        }
    }

    public void setCancelActionHandler(EventHandler<ActionEvent> cancelActionHandler) {
        this.cancelActionHandler = cancelActionHandler;
        if (cancel != null) {
            cancel.setOnAction(cancelActionHandler);
        }
    }

    protected Parent createButtons() {
        return createButtons("Save", "Cancel", false);
    }

    protected Parent createButtons(String saveLabel, String cancelLabel, boolean verticalLayout) {

        save = new Button(saveLabel);
        save.setDefaultButton(true);
        if (saveActionHandler != null) {
            save.setOnAction(saveActionHandler);
        }

        cancel = new Button(cancelLabel);
        cancel.setCancelButton(true);
        if (cancelActionHandler != null) {
            cancel.setOnAction(cancelActionHandler);
        }

        double maxWidth = Math.max(cancel.getPrefWidth(), save.getPrefWidth());
        cancel.setPrefWidth(maxWidth);
        save.setPrefWidth(maxWidth);

        if (verticalLayout) {
            VBox buttons = new VBox(8, save, cancel);
            buttons.setAlignment(Pos.TOP_LEFT);
            return buttons;
        } else {
            HBox buttons = new HBox(8, save, cancel);
            buttons.setAlignment(Pos.CENTER);
            return buttons;
        }
    }

    protected ChangeListener<String> defaultStringPropertyListener() {
        return (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            save.setDisable(false);
        };
    }

    protected void addPropertyControl(GridPane grid, int row, String label, Control control) {
        addPropertyControl(grid, row, label, control, null);
    }

    protected void addPropertyControl(GridPane grid, int row, String label, Control control, String description) {
        if (description != null) {
            control.setTooltip(new Tooltip(description));
        }
        grid.addRow(row, new Label(label), control);
    }
    
    public void disableCancelButton() {
        cancel.setDisable(true);
        cancel.setVisible(false);
    }

    protected abstract void initComponents();

    public abstract void updateModelValues();
}
