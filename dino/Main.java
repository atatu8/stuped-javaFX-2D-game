package dino;

import javafx.application.Application;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage stage) {

		Screen screen = Screen.getPrimary();
		int screen_width = (int) screen.getVisualBounds().getMaxX();
		int screen_height = (int) screen.getVisualBounds().getMaxY();
		Game game = new Game(screen_width, screen_height, stage, "dinoSandbox");
	//	stage.setFullScreen(true);
		game.run();

		stage.show();

	}	

}
