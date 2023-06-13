package dino;

import java.io.Serializable;
import java.util.List;

public class GameWorld implements Serializable {

	int[][] map;
	private float[] view;
	private float screenMovementSpeed;
	private float[] stump;
	private List<Microobject> mobList;
	private List<Microobject> listSelected;
	private float viewBorder[];

	public GameWorld(int[][] map, float[] view, float[] viewBorder, float screenMovementSpeed, float[] stump,
			List<Microobject> mobList, List<Microobject> listSelected) {
		this.map = map;
		this.view = view;
		this.viewBorder = viewBorder;
		this.screenMovementSpeed = screenMovementSpeed;
		this.stump = stump;
		this.mobList = mobList;
		this.listSelected = listSelected;
	}

	public int[][] getMap() {
		return map;
	}

	public float[] getView() {
		return view;
	}

	public float[] getViewBorder() {
		return viewBorder;
	}

	public float getScreenMovementSpeed() {
		return screenMovementSpeed;
	}

	public float[] getStump() {
		return stump;
	}

	public List<Microobject> getMobList() {
		return mobList;
	}

	public List<Microobject> getListSelected() {
		return listSelected;
	}
}
