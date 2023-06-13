package dino;

import javafx.animation.AnimationTimer; 

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.AnchorPane;

public class Game {
	private static GraphicsContext gc;
	private static GraphicsContext gc2;
	private static GraphicsContext gc3;
	AnchorPane root;
	Scene theScene;
	Canvas canvas, canvas2, canvas3;
	private static List<Microobject> mobList = new ArrayList<>();
	private static List<Microobject> listSelected = new ArrayList<>();
	private static Image[] textures = new Image[7];
	private final static int oneElementResolution = 80;
	private static int screenWidth;
	private static int screenHeight;
	private int texturesCountWidth;
	private int texturesCountHeight;
	public static float[] view = { 0, 0 };
	private static float screenMovementSpeed = 0.1f;
	private static float[] stump = { 0, 0 };
	private static float miniMapSizeElement;
	private static float miniMapStartLeftCorner;

	private static float[] viewBorder = new float[2];
	private float viewBorderSteps;
	private static float[] viewBorderSize = new float[2];
	private static Microobject selected;

	static int currentBlock = 1;
	static Swamp swamp;
	static Forest forest;
	static Mountain mountain;

	ArrayList<String> input = new ArrayList<String>();

	Game(int _screenWidth, int _screenHeight, Stage stage, String title) {
		screenWidth = _screenWidth;
		screenHeight = _screenHeight;
		root = new AnchorPane();

		theScene = new Scene(root);
		stage.setScene(theScene);
		canvas = new Canvas(screenWidth, screenHeight);
		canvas2 = new Canvas(screenWidth, screenHeight);
		canvas3 = new Canvas(screenWidth, screenHeight);
		root.getChildren().add(canvas);
		root.getChildren().add(canvas2);
		root.getChildren().add(canvas3);

		gc = canvas.getGraphicsContext2D();
		gc2 = canvas2.getGraphicsContext2D();
		gc3 = canvas3.getGraphicsContext2D();

		texturesCountWidth = screenWidth / oneElementResolution + 1; // Щоб при плавному переміщенні камери в екран
																		// влазило зразу 2 текстурки
		texturesCountHeight = screenHeight / oneElementResolution + 1;

		for (int i = 0; i < 7; i++) {
			textures[i] = new Image("t" + i + ".png");
		}

		Map.initMap(100, 100);
		Map.initChunk(texturesCountWidth, texturesCountHeight);
		miniMapSizeElement = screenHeight / (Map.height-1) / 2;
		miniMapStartLeftCorner = screenWidth - miniMapSizeElement * (Map.width-1);

		viewBorderSteps = screenMovementSpeed * (miniMapSizeElement + 1);
		viewBorder[1] = miniMapStartLeftCorner;
		viewBorderSize[1] = (texturesCountWidth-1) * miniMapSizeElement;
		viewBorderSize[0] = (texturesCountHeight-1) * miniMapSizeElement;
		viewBorder[0] = 0;

		MapGenerator.generate(Map.map);
		GameMenu.init(root, stage);
		swamp = new Swamp(20, 40);
		forest = new Forest(40, 10);
		mountain = new Mountain(40, 50);

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				mobList.add(new Lizard(46 + i, 16 + j));
				mobList.add(new Pterodactyl(46 + i, 56 + j));
				mobList.add(new LizardAmphibia(26 + i, 46 + j));
			}
		}
		for (Microobject obj : mobList) {
			giveMicroobjectFatherland(obj);
		}

		Map.transformate(view);
		drawMiniMap();
		drawViewBorder();

		theScene.setOnKeyPressed((EventHandler<KeyEvent>) new EventHandler<KeyEvent>() {
			public void handle(KeyEvent e) {
				String code = e.getCode().toString();
				if (!input.contains(code))
					input.add(code);
			}
		});

		theScene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent e) {
				String code = e.getCode().toString();
				input.remove(code);
			}
		});

		theScene.setOnMouseClicked(event -> {
			if (!(event.getX() >= miniMapStartLeftCorner && event.getY() <= miniMapSizeElement * Map.height)) {
				int[] temp = transformMousePositionToElements(event.getX(), event.getY(), 0);
				Map.map[temp[0]][temp[1]] = currentBlock;
				Map.transformate(view);
				drawMiniMap();
			} else {
				float[] temp = transformMousePositionToElements(event.getX(), event.getY());

				view[1] = (int) temp[1];
				view[0] = (int) temp[0];

				viewBorder[1] = (float) event.getX() - viewBorderSize[1] / 2;
				viewBorder[0] = (float) event.getY() - viewBorderSize[0] / 2;

				if (view[1] < 0) {
					view[1] = 0;
					viewBorder[1] = miniMapStartLeftCorner;
				}
				if (view[1] > Map.width - texturesCountWidth) {
					view[1] = Map.width - texturesCountWidth;
					viewBorder[1] = screenWidth - viewBorderSize[1];
				}

				if (view[0] < 0) {
					view[0] = 0;
					viewBorder[0] = 0;
				}

				if (view[0] > Map.height - texturesCountHeight) {
					view[0] = Map.height - texturesCountHeight;
					viewBorder[0] = miniMapSizeElement * Map.height - viewBorderSize[0];
				}

				findStump();
				Map.transformate(view);
				drawViewBorder();
			}
		});
	}

	public void run() {
		new AnimationTimer() {
			
			private final int targetFps = 60;
			private int costyl = targetFps / 10;
			private final long secondInNano = 1000000000L;
			private final long oneFrame = secondInNano / targetFps;
			private long lastFrame = 0;
			long elapsedTime = 0;
			public void handle(long now) {

				elapsedTime += now - lastFrame;
				lastFrame = now;

				if (elapsedTime >= oneFrame) {

					if (!input.isEmpty()) {
						if (input.get(0).matches("DIGIT(.*)")) {
							screenMovementSpeed = 0.02f * Float.parseFloat(Character.toString(input.get(0).charAt(5)));
							viewBorderSteps = screenMovementSpeed * miniMapSizeElement;
						}

						if (input.get(0).matches("DELETE")) {
							deleteAll();
						}

						if (input.contains("G"))
							generateNewMap();

						if (input.contains("S")) {
							if (view[0] <= Map.height - texturesCountHeight - screenMovementSpeed) {
								view[0] += screenMovementSpeed;
								viewBorder[0] += viewBorderSteps;
							}
						}
						if (input.contains("W"))
							if (view[0] >= screenMovementSpeed) {
								view[0] -= screenMovementSpeed;
								viewBorder[0] -= viewBorderSteps;
							}
						if (input.contains("A"))
							if (view[1] >= screenMovementSpeed) {
								view[1] -= screenMovementSpeed;
								viewBorder[1] -= viewBorderSteps;
							}
						if (input.contains("D"))
							if (view[1] <= Map.width - texturesCountWidth - screenMovementSpeed) {
								view[1] += screenMovementSpeed;
								viewBorder[1] += viewBorderSteps;
							}
						Map.transformate(view);
						findStump();
						drawViewBorder();
					}
					costyl++;
					elapsedTime = 0;

					drawTextures();
					for (Microobject mob : mobList) {
						mob.update(costyl);
						mob.drawMicroobject(gc, view);
					}
				}
			}
		}.start();

	}

	private static void drawTextures() {
		for (int i = 0; i < Map.imagineHeight; i++)
			for (int j = 0; j < Map.imagineWidth; j++)
				gc.drawImage(textures[Map.chunk[i][j]], oneElementResolution * j - stump[1],
						i * oneElementResolution - stump[0]);
	}

	private static void drawMiniMap() {
		for (int i = 0; i < Map.height; i++)
			for (int j = 0; j < Map.width; j++)
				gc2.drawImage(textures[Map.map[i][j]], miniMapSizeElement * j + miniMapStartLeftCorner,
						miniMapSizeElement * i, miniMapSizeElement, miniMapSizeElement);
		gc2.setStroke(Color.RED);
		gc2.setLineWidth(5);
		gc2.strokeRect(miniMapStartLeftCorner, 0, screenWidth - miniMapStartLeftCorner,
				miniMapSizeElement * Map.height);
	}

	private void findStump() {
		stump[0] = (view[0] - (int) view[0]) * oneElementResolution;
		stump[1] = (view[1] - (int) view[1]) * oneElementResolution;
	}

	private static void drawViewBorder() {
		gc3.clearRect(0, 0, screenWidth, screenHeight);
		gc3.setStroke(Color.BLACK);
		gc3.setLineWidth(1);
		gc3.strokeRect(viewBorder[1], viewBorder[0], viewBorderSize[1], viewBorderSize[0]);
	}

	static void createMicroobject(Microobject obj) {
		mobList.add(obj);
		giveMicroobjectFatherland(obj);
		GameMenu.sendMessageToTextArea(
				obj.getName() + " створений на координатах(" + obj.getX() + ";" + obj.getY() + ")\n");
	}

	static void deleteMicroobject() {
		if (selected != null) {
			GameMenu.sendMessageToTextArea(
					selected.getName() + " видалений із координат(" + selected.getX() + ";" + selected.getY() + ")\n");
			mobList.remove(selected);
		} else {
			GameMenu.sendMessageToTextArea("На обраній координаті мікрообʼєкта не знайдено\n");
		}
	}

	static void deleteAll() {
		if (mobList.size() > 0)
			GameMenu.sendMessageToTextArea("Видалені всі обʼєкти\n");
		else {
			GameMenu.sendMessageToTextArea("Немає кого видаляти\n");
		}
		mobList.clear();
	}

	static void deleteFromSelected() {
		if (listSelected.remove(selected) == true) {
			GameMenu.sendMessageToTextArea("Видалений з виділених\n");
		} else
			GameMenu.sendMessageToTextArea("Немає у списку виділених\n");
	}
	
	static void deleteSelected() {
		if(listSelected.size() == 0) {
			GameMenu.sendMessageToTextArea("Немає, кого видаляти\n");
			return;
		}
		for(Microobject obj : listSelected) {
			if(mobList.contains(obj)) {
				mobList.remove(obj);
			}
		}
		listSelected.clear();
		GameMenu.sendMessageToTextArea("Видалені виділені\n");
	}

	static Microobject chooseMicroobject(int y, int x) {
		for (Microobject obj : mobList) {
			if (obj.getX() == x && obj.getY() == y) {
				GameMenu.sendMessageToTextArea(
						obj.getName() + " виділений на координатах(" + obj.getX() + ";" + obj.getY() + ")\n");
				return obj;
			}
		}
		return null;
	}

	static void addMicroobjectToSelected() {
		if (selected != null) {
			boolean dublicate = false;
			for (Microobject obj : listSelected) {
				if (obj == selected)
					dublicate = true;
			}
			if (!dublicate) {
				listSelected.add(selected);
				GameMenu.sendMessageToTextArea(selected.getName() + " доданий у список виділених на координатах("
						+ selected.getX() + ";" + selected.getY() + ")\n");
			}
		}
		else
			GameMenu.sendMessageToTextArea("Мікрообʼєекта для додавання не знайдено\n");
		
	}

	static void sendSelectedMicroobjectsToOnePosition(int y, int x) {
		for (Microobject obj : listSelected) {
			if (obj != null)
				obj.setTarget(y, x);
		}
		if (listSelected.size() > 0)
			GameMenu.sendMessageToTextArea("Виділені мікрообʼєкти направлені в одну точку\n");
		else
			GameMenu.sendMessageToTextArea("Немає виділених мікрообʼєктів\n");
	}

	static void sendMicroobjectsToOnePosition(int y, int x) {
		for (Microobject obj : mobList) {
			obj.setTarget(y, x);
		}
		if (mobList.size() > 0)
			GameMenu.sendMessageToTextArea("Мікрообʼєкти направлені в одну точку\n");
		else
			GameMenu.sendMessageToTextArea("Немає кого відправляти\n");
	}

	static int[] transformMousePositionToElements(double mouseX, double mouseY, int mode) {
		int[] buffer = new int[2];
		buffer[0] = (int) ((mouseY + stump[0]) / 80 + (int) view[0]);
		buffer[1] = (int) ((mouseX + stump[1]) / 80 + (int) view[1]);
		if (mode == 1)
			selected = chooseMicroobject(buffer[0], buffer[1]);
		return buffer;
	}

	private float[] transformMousePositionToElements(double mouseX, double mouseY) {
		float[] buffer = new float[2];
		buffer[1] = ((float) mouseX - miniMapStartLeftCorner) / miniMapSizeElement - texturesCountWidth / 2;
		buffer[0] = ((float) mouseY / miniMapSizeElement - texturesCountHeight / 2);
		return buffer;
	}

	public static void generateNewMap() {
		GameMenu.sendMessageToTextArea("Згенерована нова карта\n");
		Map.map = new int[Map.height + 1][Map.width + 1];
		MapGenerator.generate(Map.map);
		swamp = new Swamp(20, 40);
		forest = new Forest(40, 10);
		mountain = new Mountain(40, 50);
		Map.transformate(view);
		drawTextures();
		drawMiniMap();

	}

	private static void giveMicroobjectFatherland(Microobject obj) {
		if (obj instanceof Lizard) {
			obj.setFatherland(forest.getY() + 7, forest.getX() + 7);
		}
		if (obj instanceof LizardAmphibia) {
			obj.setFatherland(swamp.getY() + 7, swamp.getX() + 7);
		}
		if (obj instanceof Pterodactyl) {
			obj.setFatherland(mountain.getY() + 7, mountain.getX() + 7);
		}
	}

	public static void initWorld(GameWorld world) {
		Map.map = world.getMap();
		view = world.getView();
		viewBorder = world.getViewBorder();
		stump = world.getStump();
		screenMovementSpeed = world.getScreenMovementSpeed();
		mobList = world.getMobList();
		listSelected = world.getListSelected();
		Map.transformate(view);
		drawViewBorder();
		drawTextures();
		drawMiniMap();
	}

	public static GameWorld getWorld() {
		GameWorld world = new GameWorld(Map.map, view, viewBorder, screenMovementSpeed, stump, mobList, listSelected);
		return world;
	}

}
