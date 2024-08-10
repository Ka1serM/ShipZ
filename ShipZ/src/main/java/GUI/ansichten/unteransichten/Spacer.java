package GUI.ansichten.unteransichten;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Wird so breit und hoch wie möglich.
 * Kann genutzt werden, um Elemente in V- oder HBoxen auf zwei Seiten zu schieben.
 */
public class Spacer extends Pane {

    /**
     * Konstruktor Buttonspacer
     */
    public Spacer() {
        HBox.setHgrow(this, Priority.ALWAYS);
        VBox.setVgrow(this, Priority.ALWAYS);
    }
}