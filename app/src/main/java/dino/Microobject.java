package dino;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Microobject implements Serializable {

	protected int collisionLevel = 0;
	protected transient Image image;
	protected String name;
	protected int[] frames = { 0, 2 };
	protected double[] dnormalized = new double[2];
	protected float speed = 0.03f;
	protected int speedOfFrames = 6;
	private double[] pos = new double[2];
	private double[] dpos = new double[2];
	private int[] startPos = new int[2];
	private int[] targetPos = new int[2];
	private double distance;
	private int scale = 120;
	private boolean active = false;
	private boolean randomMindset = true;
	private int nextStepPause = 100;
	private int pathIndex;
	private List<int[]> path;
	private int[] fatherland = new int[2];
	private boolean loveFatherland = true;

	Microobject() {

	}

	Microobject(int y, int x) {
		pos[0] = y;
		pos[1] = x;
		speedSettings(pos);
	}

	public void drawMicroobject(GraphicsContext gc, float[] view) {
		gc.setStroke(Color.BLACK);
		gc.drawImage(image, frames[0] * 80, frames[1] * 80, 80, 80, (pos[1] - view[1]) * 80 - 20,
				(pos[0] - view[0]) * 80 - 20, scale, scale);
		//strokeText("", 80 * (pos[1] - view[1])+20,  80 * (pos[0] - view[0])-10);
	}

	public void update(int costyl) {
		spriteLogic(costyl);

		if (active) {
			calculateFrameDirection();
			calculateDnormalized();
			MicroobjectLogic();
		}

	}

	private void calculateDnormalized() {
		dpos[0] = path.get(pathIndex)[0] - pos[0];
		dpos[1] = path.get(pathIndex)[1] - pos[1];
		distance = Math.sqrt(dpos[0] * dpos[0] + dpos[1] * dpos[1]);
		dnormalized[0] = dpos[0] / distance;
		dnormalized[1] = dpos[1] / distance;
	}

	protected void calculateFrameDirection() { // Якщо в картинці є тільки 4 направлення
		if (dnormalized[1] >= 0.6)
			frames[1] = 3;
		else if (dnormalized[1] <= -0.6)
			frames[1] = 1;
		else if (dnormalized[0] >= 0.8)
			frames[1] = 2;
		else if (dnormalized[0] <= -0.8)
			frames[1] = 0;
	}

	private void MicroobjectLogic() {
		if (pathIndex > 0) {
			if (Map.map[path.get(pathIndex)[0]][path.get(pathIndex)[1]] > collisionLevel) {
				generatePath(targetPos[0], targetPos[1]);
				frames[0] = 0;
				frames[1] = 2;
				return;
			}
		}
		pos[0] += dnormalized[0] * speed;
		pos[1] += dnormalized[1] * speed;
		speedSettings(pos);
		if (Math.sqrt(
				Math.pow(path.get(pathIndex)[0] - pos[0], 2) + Math.pow(path.get(pathIndex)[1] - pos[1], 2)) < speed) {
			pathIndex--;
			if (pathIndex <= -1) {
				active = false;
				frames[0] = 0;
				frames[1] = 2;
			}
		}
	}

	private void generatePath(int y, int x) {
		startPos[0] = (int) pos[0];
		startPos[1] = (int) pos[1];

		active = false;
		if ((targetPos[1] >= 0 && targetPos[1] <= Map.width - 1)
				&& (targetPos[0] >= 0 && targetPos[0] <= Map.height - 1))
			if (Map.map[targetPos[0]][targetPos[1]] <= collisionLevel) {
				path = AStar.astar(Map.map, startPos, targetPos, collisionLevel);
				if (path != null) {
					pathIndex = path.size() - 2;
					if (pathIndex > 0)
						active = true;
				}

			}

	}

	public void rebuildPath() {
		generatePath(targetPos[0], targetPos[1]);
	}

	private void generateRandomPos() {
		if (loveFatherland) {
			targetPos[0] = (int) (fatherland[0] + (Math.random() * 14 - 6));
			targetPos[1] = (int) (fatherland[1] + (Math.random() * 14 - 6));
		} else {
			targetPos[0] = (int) (fatherland[0] + (Math.random() * 99));
			targetPos[1] = (int) (fatherland[1] + (Math.random() * 99));
		}
		if ((int) (Math.random() * 4) == 1) {
			loveFatherland = !loveFatherland;
		}
	}

	private void spriteLogic(int costyl) {
		if (active) {
			if (costyl % speedOfFrames == 0) {
				frames[0]++;
				if (frames[0] == 8)
					frames[0] = 0;
			}
		} else if (costyl % nextStepPause == 0) {
			if (randomMindset) {
				generateRandomPos();
				nextStepPause = (int) (Math.random() * 300 + 1);
			}
			randomMindset = true;
			generatePath(targetPos[0], targetPos[1]);
		}
	}

	public void setTarget(int y, int x) {
		randomMindset = false;
		active = true;
		targetPos[0] = y;
		targetPos[1] = x;
		generatePath(y, x);
	}

	public int getX() {
		return (int) pos[1];
	}

	public int getY() {
		return (int) pos[0];
	}

	public String getName() {
		return name;
	}

	public void setImage(String path) {
		image = new Image(path);
	}

	public void setFatherland(int y, int x) {
		fatherland[0] = y;
		fatherland[1] = x;
	}
	
	void speedSettings(double pos[]) {
		
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Microobject other = (Microobject) obj;
		return Arrays.equals(this.pos, other.pos) && Objects.equals(this.name, other.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(Arrays.hashCode(this.pos), this.name);
	}

}
