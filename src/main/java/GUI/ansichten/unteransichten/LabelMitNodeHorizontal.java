package GUI.ansichten.unteransichten;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Kombiniert ein Label mit einem Node in einer HBox.
 * Label und Node sind zugreifbar; es wird keine Funktionalität gekapselt.
 *
 * Kann genutzt werden, um ein Textfield, IntSpinner etc mit einem Label zu versehen.
 */
public class LabelMitNodeHorizontal<T extends Node> extends HBox {


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
    public LabelMitNodeHorizontal(final String labeltext, final T node) {
        label = new Label(labeltext);
        this.node = node;
        setAlignment(Pos.CENTER);
        getChildren().addAll(label, this.node);
        setSpacing(10.0);
    }

}
