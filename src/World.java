import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.lang.reflect.Field;

import org.json.JSONObject;

public class World implements Runnable {

	private final Config config;
	public final CirclePicture parent;
	private final CirclePicture child;
	private int generation = 0;

	public World(final Config config) {
		this.config = config;

		parent = new CirclePicture(config.pictureConfig);
		child = new CirclePicture(config.pictureConfig);
		parent.initRandom();
		parent.draw();
		parent.calculateFitness(config.TARGET);
	}

	public void step() {
		child.set(parent);
		child.mutate(config.MUTATION_RATE, config.MUTATION_AMOUNT);
		if (!child.isMutated()) {
			return;
		}

		child.draw();
		child.calculateFitness(config.TARGET);

		if (child.getFitness() > parent.getFitness()) {
			parent.set(child);
		}
		generation++;
	}

	public void run() {
		while (true) {
			step();
		}
	}

	public int getGeneration() {
		return generation;
	}

	public Circle[] getCircles() {
		return parent.circles;
	}

	public double getPictureFitness() {
		return parent.getFitness();
	}

	public static float lerp(float a, float b, float amount) {
		return (a * (1.0f - amount)) + (b * amount);
	}

	public Config getConfig() {
		return config;
	}

	public String toString() {
		return generation + " " + parent.getFitness();
	}

	public static class Config {

		protected float MUTATION_RATE = 0.01f;
		protected float MUTATION_AMOUNT = 0.1f;
		protected final CirclePicture.Config pictureConfig;
		protected final Image TARGET;
		protected final BufferedImage BUFFERED_TARGET;
		private final JSONObject config;

		public Config(final BufferedImage BUFFERED_TARGET, final JSONObject config) {
			this.config = config;
			config.put("width", BUFFERED_TARGET.getWidth());
			config.put("height", BUFFERED_TARGET.getHeight());
			this.BUFFERED_TARGET = BUFFERED_TARGET;
			TARGET = createImage(BUFFERED_TARGET);
			pictureConfig = new CirclePicture.Config(TARGET.width,TARGET.height);
			if (config.has("mutationRate"))
				MUTATION_RATE = config.getFloat("mutationRate");
			if (config.has("mutationAmount"))
				MUTATION_AMOUNT = config.getFloat("mutationAmount");
			if (config.has("minRadius"))
				pictureConfig.setMinRadius(config.getInt("minRadius"));
			if (config.has("maxRadius"))
				pictureConfig.setMaxRadius(config.getInt("maxRadius"));
			if (config.has("circleCount"))
				pictureConfig.setCircleCount(config.getInt("circleCount"));
			if (config.has("minAlpha"))
				pictureConfig.setMinAlpha(config.getInt("minAlpha"));
			if (config.has("backgroundColor"))
				pictureConfig.setBackgroundColor(nameToColor(config.getString("backgroundColor")));
		}

		public Config(final BufferedImage BUFFERED_TARGET) {
			this(BUFFERED_TARGET, new JSONObject());
		}

		public Config setMutationRate(final float MUTATION_RATE) {
			this.MUTATION_RATE = MUTATION_RATE;
			return this;
		}

		public Config setMutationAmount(final float MUTATION_AMOUNT) {
			this.MUTATION_AMOUNT = MUTATION_AMOUNT;
			return this;
		}

		public Config setMinRadius(final int MIN_RADIUS) {
			pictureConfig.setMinRadius(MIN_RADIUS);
			return this;
		}

		public Config setMaxRadius(final int MAX_RADIUS) {
			pictureConfig.setMaxRadius(MAX_RADIUS);
			return this;
		}

		public Config setCircleCount(final int CIRCLE_COUNT) {
			pictureConfig.setCircleCount(CIRCLE_COUNT);
			return this;
		}
		
		public Config setMinAlpha(final int MIN_ALPHA) {
			pictureConfig.setMinAlpha(MIN_ALPHA);
			return this;
		}

		private static Image createImage(BufferedImage image) {
			if (image.getType() != BufferedImage.TYPE_INT_RGB) {
				return null;
			}

			int[] imagePixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
			RGBColor[] pixels = new RGBColor[imagePixels.length];

			for (int i = 0; i < pixels.length; i++) {
				int pixel = imagePixels[i];
				int r = (pixel >> 16) & 0xFF;
				int g = (pixel >> 8) & 0xFF;
				int b = pixel & 0xFF;
				pixels[i] = new RGBColor(r, g, b);
			}

			return new Image(image.getWidth(), image.getHeight(), pixels);
		}

		private Color nameToColor(String name) {
			try {
				Field field = Class.forName("java.awt.Color").getField(name);
				return (Color) field.get(null);
			} catch (Exception e) {
				throw new IllegalArgumentException("Invalid color name");
			}
		}

		public JSONObject getConfig() {
			return config;
		}
	}

}
