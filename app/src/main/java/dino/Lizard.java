package dino;

import java.io.Serializable;

import javafx.scene.image.Image;

public class Lizard extends Microobject {
	Lizard(int x, int y) {
		super(x, y);
		image = new Image("lizard.png");
		collisionLevel = 2;
		name = "Ящір";
	}
	
	@Override
	protected void speedSettings(double[] pos) {
		if(Map.map[(int)pos[0]][(int)pos[1]] == 2) {
			speed = 0.015f;
			speedOfFrames = 12;
		}
		else {
			speed = 0.03f;
			speedOfFrames = 6;
		}
	}
}
