package dino;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class SaveGame {
	public static void serialize(GameWorld toSave, String path) {
		try {
			FileOutputStream fileOut = new FileOutputStream(path + ".dinosandbox");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(toSave);
			out.close();
			fileOut.close();
			GameMenu.sendMessageToTextArea("Збережений теперішній світ");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static GameWorld transformSelectedFileToGameWorld(String path) {
		try {
			FileInputStream fileIn = new FileInputStream(path);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			GameWorld obj = (GameWorld) in.readObject();
			in.close();
			fileIn.close();
			for (Microobject microobject : obj.getMobList()) {
				if (microobject instanceof Lizard) {
					microobject.setImage("lizard.png");
				}
				if (microobject instanceof LizardAmphibia) {
					microobject.setImage("lizard_amphibia.png");
				}
				if (microobject instanceof Pterodactyl) {
					microobject.setImage("pterodactyl.png");
				}
			}
			GameMenu.sendMessageToTextArea("Відкритий новий світ");
			return obj;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
