package dino;

public class Macroobject {
	protected int x;
	protected int y;
	protected int[][] texture;

	protected void transoformMacroobjectToMap(int y, int x) {
		for (int i = 0; i < 15; i++)
			for (int j = 0; j < 15; j++)
				Map.map[i + y][j + x] = texture[i][j];
	}

	Macroobject(int y, int x) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	
}
