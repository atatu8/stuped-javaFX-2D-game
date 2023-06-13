package dino;

import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import java.io.File;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.scene.control.ContextMenu;

public class GameMenu {
	static TextArea textArea = new TextArea();
	private static int[] objPos = new int[2];
	private static Stage stage;

	static void init(AnchorPane root, Stage _stage) {
		stage = _stage;

		textArea.setPrefColumnCount(40);
		textArea.setPrefRowCount(10);
		textArea.setEditable(false);
		AnchorPane.setBottomAnchor(textArea, 0.0);

		Button button = new Button("Sample Button");
		root.getChildren().add(button);
		root.getChildren().add(textArea);

		MenuBar menuBar = new MenuBar();
		Menu menu = new Menu("Налаштування світу");
		MenuItem mb1 = new MenuItem("Зберегти світ");
		MenuItem mb2 = new MenuItem("Відкрити світ");
		MenuItem mb3 = new MenuItem("Згенерувати новий світ");
		MenuItem mb4 = new MenuItem("Видалити всі мікрообʼєкти");

		mb1.setOnAction(event -> createFile());
		mb2.setOnAction(event -> openFile());
		mb3.setOnAction(event -> Game.generateNewMap());
		mb4.setOnAction(event -> Game.deleteAll());

		menu.getItems().addAll(mb1, mb2, mb3, mb4);
		menuBar.getMenus().addAll(menu);
		root.getChildren().add(menuBar);

		ContextMenu contextMenu = new ContextMenu();
		MenuItem item1 = new MenuItem("Створити тут Ящіра");
		MenuItem item2 = new MenuItem("Створити тут Ящіра-амфібію");
		MenuItem item3 = new MenuItem("Створити тут Птеродактля");
		MenuItem item4 = new MenuItem("Видалити мікрообʼєект");
		MenuItem item5 = new MenuItem("Направити всіх в обрану координату");
		MenuItem item6 = new MenuItem("Додати в виділені");
		MenuItem item7 = new MenuItem("Видалити з виділених");
		MenuItem itemExtra = new MenuItem("Видалити виділені");
		MenuItem item8 = new MenuItem("Направити виділених в обрану координату");

		Menu menu2 = new Menu("Обрати потрібну текстуру");

		MenuItem textureItem1 = new MenuItem("Обрати траву");
		MenuItem textureItem2 = new MenuItem("Обрати пісок");
		MenuItem textureItem3 = new MenuItem("Обрати болото");
		MenuItem textureItem4 = new MenuItem("Обрати воду");
		MenuItem textureItem5 = new MenuItem("Обрати камінь");
		MenuItem textureItem6 = new MenuItem("Обрати сніг");

		textureItem1.setOnAction(event -> Game.currentBlock = 0);
		textureItem2.setOnAction(event -> Game.currentBlock = 1);
		textureItem3.setOnAction(event -> Game.currentBlock = 2);
		textureItem4.setOnAction(event -> Game.currentBlock = 3);
		textureItem5.setOnAction(event -> Game.currentBlock = 4);
		textureItem6.setOnAction(event -> Game.currentBlock = 5);

		menu2.getItems().addAll(textureItem1, textureItem2, textureItem3, textureItem4, textureItem5, textureItem6);

		item1.setOnAction(event -> Game.createMicroobject(new Lizard(objPos[0], objPos[1])));
		item2.setOnAction(event -> Game.createMicroobject(new LizardAmphibia(objPos[0], objPos[1])));
		item3.setOnAction(event -> Game.createMicroobject(new Pterodactyl(objPos[0], objPos[1])));
		item4.setOnAction(event -> Game.deleteMicroobject());
		item6.setOnAction(event -> Game.addMicroobjectToSelected());
		item5.setOnAction(event -> Game.sendMicroobjectsToOnePosition(objPos[0], objPos[1]));
		item7.setOnAction(event -> Game.deleteFromSelected());
		itemExtra.setOnAction(event -> Game.deleteSelected());
		item8.setOnAction(event -> Game.sendSelectedMicroobjectsToOnePosition(objPos[0], objPos[1]));

		contextMenu.getItems().addAll(item1, item2, item3, item4, item5, item6, item7, itemExtra, item8, menu2);

		root.setOnContextMenuRequested(event -> {
			contextMenu.show(root, event.getScreenX(), event.getScreenY());
			event.consume();
			objPos = Game.transformMousePositionToElements(event.getX(), event.getY(), 1);
		});

	}

	static void sendMessageToTextArea(String message) {
		textArea.appendText(message);
	}

	static void createFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.setTitle("Збереження файлу");
		fileChooser.setInitialFileName("saveGame");

		File selectedDirectory = fileChooser.showSaveDialog(stage);
		if (selectedDirectory != null) {
			GameWorld toSave = Game.getWorld();
			SaveGame.serialize(toSave, selectedDirectory.getAbsolutePath());
		}

	}

	static void openFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Data Files", "*.dinosandbox"));
		fileChooser.setTitle("Оберіть файл");
		File selectedFile = fileChooser.showOpenDialog(stage);
		if (selectedFile != null) {
			GameWorld toLoad = SaveGame.transformSelectedFileToGameWorld(selectedFile.getAbsolutePath());
			Game.initWorld(toLoad);
		}
	}

}
