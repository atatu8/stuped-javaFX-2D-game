package dino;

import javafx.scene.image.Image;

public class Pterodactyl extends LizardAmphibia {

	Pterodactyl(int x, int y) {
		super(x, y);
		image = new Image("pterodactyl.png");
		collisionLevel = 6;
		name = "Птеродактиль";
	}

	@Override
	protected void calculateFrameDirection() {
		if (Math.abs(dnormalized[0]) > 0.2 && Math.abs(dnormalized[0]) < 0.8) {

			if (dnormalized[0] < 0 && dnormalized[1] < 0)
				frames[1] = 4;
			if (dnormalized[0] > 0 && dnormalized[1] > 0)
				frames[1] = 7;
			if (dnormalized[0] < 0 && dnormalized[1] > 0)
				frames[1] = 6;
			if (dnormalized[0] > 0 && dnormalized[1] < 0)
				frames[1] = 5;
		} else {
			if (dnormalized[1] >= 0.9)
				frames[1] = 3;
			else if (dnormalized[1] <= -0.9)
				frames[1] = 1;
			else if (dnormalized[0] >= 0.9)
				frames[1] = 2;
			else if (dnormalized[0] <= -0.9)
				frames[1] = 0;
		}
	}
	
	@Override
	protected void speedSettings(double[] pos) {
		
	}
}
