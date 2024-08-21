package GUI.ansichten.unteransichten;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Kombiniert ein Label mit einem Node in einer VBox.
 * Label und Node sind zugreifbar; es wird keine Funktionalität gekapselt.
 *
 * Kann genutzt werden, um ein Textfield, IntSpinner etc mit einem Label zu versehen.
 */
public class LabelMitNodeVertikal<T extends Node> extends VBox {


    // IV
    /** Label oberhalb*/
    public Label label;

    /** Control unterhalb */
    public T node;


    /**
     * Erstellt einen VBox, die ein Label mit einem Controlelemt verbindet.
     * @param labeltext     Text des Labels
     * @param node          Node
     */
    public LabelMitNodeVertikal(final String labeltext, final T node) {
        label = new Label(labeltext);
        this.node = node;
        getChildren().addAll(label, this.node);
        setSpacing(10.0);
    }

}
