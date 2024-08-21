package GUI.ansichten.unteransichten;

import javafx.beans.binding.NumberBinding;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;


public class Buttonleiste extends Pane {

    // IV
    /** Hier liegen die Buttons drin */
    private final HBox inner;

    public Buttonleiste() {

        // Hier kommen die Buttons rein
        inner = new HBox();
        this.getChildren().add(inner);

        // Buttonabstand
        inner.setSpacing(15.0);
        inner.setSpacing(15.0);

        this.addAll();
    }

    /**
     * Ersatz für this.getChildren().add(Node)
     * @param node Neues Kind
     */
    public void add(Node node) {
        this.inner.getChildren().add(node);
    }

    /**
     * Ersatz für this.getChildren().addAll(Node... nodes)
     * @param nodes Neue Kinder
     */
    public void addAll(Node... nodes) {
        this.inner.getChildren().addAll(nodes);
    }


    /**
     * Bindet den Innenabbstand der Buttonleiste an einen Numberbinding
     * @param linkesOffset  Innenabstand links
     * @param rechtesOffset Innenabstand Rechts
     */
    public void bindInnerSpacing(NumberBinding linkesOffset, NumberBinding rechtesOffset) {

        // Breite und Abstände zu den Kanten binden
        inner.layoutXProperty().bind(linkesOffset);
        inner.prefHeightProperty().bind(this.heightProperty());
        inner.prefWidthProperty().bind(this.widthProperty().subtract(linkesOffset.add(rechtesOffset)));
    }

}
