import Administration.Verwaltung;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application
{
    @Override
    public void start(Stage stage) throws Exception
    {
        Verwaltung.getInstance(stage);
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}