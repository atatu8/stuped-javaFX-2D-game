package dino;

public class MapGenerator {
	public static void generate(int[][] map) {
		PerlinNoise noise = new PerlinNoise();
		noise.setSeed(Math.random());
		noise.setDefaultSize(20);

		double noise_result;

		for (int i = 0; i < Map.height; i++)
			for (int j = 0; j < Map.width; j++) {
				noise_result = noise.noise(i, j);

				if (noise_result > -0.4 && noise_result < -0.2)
					map[i][j] = 3;

				if (noise_result > -0.2 && noise_result < -0.1)
					map[i][j] = 1;

				if (noise_result > 0 && noise_result < 0.04)
					map[i][j] = 6;

				if (noise_result > 0.4) {
					map[i][j] = 4;
				}

				if (noise_result > 0.55) {
					map[i][j] = 5;
				}

			}

	}
}
