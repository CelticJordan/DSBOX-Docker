package es.uvigo.esei.dsbox.gui.views;

import es.uvigo.esei.dsbox.gui.utils.Utils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CommandPair {

    public MenuItem menuItem;
    public Button button;

    public static final CommandPair create(String label, String imageFile, EventHandler<ActionEvent> handler, boolean addButton) {
        CommandPair pair = new CommandPair();
        pair.menuItem = new MenuItem(label);
        pair.menuItem.setOnAction(handler);
        if (addButton) {
            ImageView image = new ImageView(new Image(Utils.loadResource(imageFile), 48, 48, true, true));
            pair.button = new Button(label, image);
            pair.button.setContentDisplay(ContentDisplay.TOP);
            pair.button.setOnAction(handler);
        }
        return pair;
    }

    public static final CommandPair create(String label, String imageFile, EventHandler<ActionEvent> handler) {
        return CommandPair.create(label, imageFile, handler, true);
    }

    public static final CommandPair createWithoutButton(String label, EventHandler<ActionEvent> handler) {
        return CommandPair.create(label, null, handler, false);
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public Button getButton() {
        return button;
    }

    public void enable() {
        this.menuItem.setDisable(false);
        if (this.button != null) {
            this.button.setDisable(false);
        }
    }

    public void disable() {
        this.menuItem.setDisable(true);
        if (this.button != null) {
            this.button.setDisable(true);
        }
    }

}
