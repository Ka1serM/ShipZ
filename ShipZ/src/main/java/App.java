import Administration.Game;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application
{

    @Override
    public void start(Stage stage) throws Exception
    {
        Game.getInstance(stage);
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}