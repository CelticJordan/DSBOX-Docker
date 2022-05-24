package es.uvigo.esei.dsbox.gui.utils;

import es.uvigo.esei.dsbox.core.model.HostType;
import es.uvigo.esei.dsbox.core.model.OperativeSystemType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

public class Utils {

    public static InputStream loadResource(String resourcePath) {
        return Utils.class.getClassLoader().getResourceAsStream(resourcePath);
    }

    public static InputStream resolveIconURL(String iconURL) {
        if (iconURL != null) {
            InputStream result = tryOpenAsFile(iconURL);
            if (result == null) {
                // Try as a local jar resource
                result = Utils.class.getClassLoader().getResourceAsStream(iconURL);
            }
            if (result == null) {
                // Try as a external URL
                try {
                    URL url = new URL(iconURL);
                    result = url.openStream();
                } catch (MalformedURLException ex) {
                    result = null;
                } catch (IOException ex) {
                    result = null;
                }
            }
            return result;
        }
        return null;
    }

    private static InputStream tryOpenAsFile(String iconURL) {
        File iconFile = new File(iconURL);
        if (iconFile.exists() && iconFile.isFile() && iconFile.canRead()) {
            try {
                return new FileInputStream(iconFile);
            } catch (FileNotFoundException ex) {
                return null; // Unable to open
            }
        }
        return null; // Unable to open
    }

    public static Node createHostTypeImage(HostType hostType, String defaultImage, int size) {
        return createHostTypeImage(hostType, defaultImage, size, false);
    }

    public static Node createHostTypeImage(HostType hostType, String defaultImage, int size, boolean addHostTypeName) {
        InputStream iconInputStream = Utils.resolveIconURL(hostType.getIconURL());
        StackPane image;
        if (iconInputStream != null) { // Icon was found
            image = new StackPane(new ImageView(new Image(iconInputStream, size, size, true, true)));
        } else {
            ImageView defaultHostImage = new ImageView(new Image(Utils.loadResource(defaultImage), size, size, true, true));
            ImageView osImage = createOSImage(hostType.getOs(), Math.round(0.4f * size));
            image = new StackPane(defaultHostImage, osImage);
            image.setAlignment(Pos.CENTER);
            if (addHostTypeName) {
                osImage.setTranslateY(-Math.round(0.225f * size));
                Label hostTypeLabel = new Label(hostType.getName());
                hostTypeLabel.setAlignment(Pos.CENTER);
                hostTypeLabel.setFont(Font.font(0.8 * Font.getDefault().getSize()));
                hostTypeLabel.setPrefWidth(Math.round(0.8f * size));
                image.getChildren().add(hostTypeLabel);
                hostTypeLabel.setTranslateY(Math.round(0.025f * size));

            } else {
                osImage.setTranslateY(-Math.round(0.2f * size));
            }
        }
        return image;
    }

    private static ImageView createOSImage(OperativeSystemType os, int size) {
        if (os == OperativeSystemType.GNU_LINUX) {
            return new ImageView(new Image(Utils.loadResource("images/misc/linuxLogo.png"), size, size, true, true));
        }
        if (os == OperativeSystemType.MS_WINDOWS) {
            return new ImageView(new Image(Utils.loadResource("images/misc/windowsLogo.png"), size, size, true, true));
        }
        return new ImageView();
    }
}
