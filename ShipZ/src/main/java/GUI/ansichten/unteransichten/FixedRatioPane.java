package GUI.ansichten.unteransichten;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class FixedRatioPane extends StackPane {

    /** Dieses Pane behaelt seine Proportion */
    private final StackPane inner;

    /** Eingestellte, fixierte Ratio */
    private final SimpleDoubleProperty ratio = new SimpleDoubleProperty();

    /**
     * Erstellet ein zemtriertes Pane mit maximaler Höhe und Breite unter Einhaltung übergener Proportionen.
     * Kinder muessen dem Element mit this.getChildren().add(Node) hinzugefuegt werden.
     * @param ratio         Einzuhaltendes Seitenhältnis (Breite / Hoehe)
     */
    public FixedRatioPane(double ratio) {

        this.inner = new StackPane();

        // Ratio Property setzen
        this.ratio.set(ratio);

        // Bindings setzen
        this.inner.prefWidthProperty().bind(Bindings.min(this.widthProperty().divide(this.ratio), this.heightProperty()).multiply(this.ratio));
        this.inner.prefHeightProperty().bind(Bindings.min(this.widthProperty().divide(this.ratio), this.heightProperty()));
        this.inner.maxWidthProperty().bind(Bindings.min(this.widthProperty().divide(this.ratio), this.heightProperty()).multiply(this.ratio));
        this.inner.maxHeightProperty().bind(Bindings.min(this.widthProperty().divide(this.ratio), this.heightProperty()));

        // Inner this als Kind hinzufügen
        this.getChildren().add(this.inner);


        // Debug
        /*this.setStyle(
                " -fx-border-width: 2px;" +
                "-fx-border-color: #00ff00;"
        );

        this.inner.setStyle(
                " -fx-border-width: 2px;" +
                "-fx-border-color: #ff0000;"
        );*/
    }

    /**
     * Setzt die fixierte Ratio dieses Panes.
     * @param ratio Neue Ratio
     */
    public void setRatio(double ratio) {
        this.ratio.set(ratio);
    }

    /**
     * Ersatz für this.getChildren().add(Node)
     * @param node Neues Kind
     */
    public void add(Node node) {
        this.inner.getChildren().add(node);
    }

    /**
     * Ersatz für this.getChildren().clear()
     */
    public void clearChildren() {
        this.inner.getChildren().clear();
    }

    /**
     * Ersatz für this.getLayoutXProperty
     * @return Binding des horizontalen Innenabstands
     */
    public DoubleProperty getInnerLayoutXProperty() {
        return this.inner.layoutXProperty();
    }

}
