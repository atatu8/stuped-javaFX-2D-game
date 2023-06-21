package dino;

public class Map {
	static int width;
	static int height;
	static int imagineWidth;
	static int imagineHeight;
	static int[][] map;
	static int[][] chunk;

	static void initMap(int w, int h) {
		width = w;
		height = h;
		map = new int[height + 1][width + 1];
	}

	static void initChunk(int w, int h) {
		imagineWidth = w + 1;
		imagineHeight = h + 1;
		chunk = new int[imagineHeight][imagineWidth];
	}

	static void transformate(float[] view) {
		for (int i = 0; i < chunk.length; i++) {
			for (int j = 0; j < chunk[0].length; j++) {
				chunk[i][j] = map[(int) view[0] + i][(int) view[1] + j];
			}
		}
	}

}
