package dino;

import javafx.scene.image.Image;

public class LizardAmphibia extends Lizard {

	LizardAmphibia(int x, int y) {
		super(x, y);
		image = new Image("lizard_amphibia.png");
		collisionLevel = 3;
		name = "Ящір-амфібія";
	}
	@Override
	protected void speedSettings(double[] pos) {
		if(Map.map[(int)pos[0]][(int)pos[1]] == 3) {
			speed = 0.06f;
			speedOfFrames = 3;
		}
		else {
			speed = 0.03f;
			speedOfFrames = 6;
		}
	}
}
