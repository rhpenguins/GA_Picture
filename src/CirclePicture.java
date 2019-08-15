import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class CirclePicture {

	private Config config;
	private final BufferedImage canvas;
	public int[] pixels;
	protected final Circle[] circles;
	private double fitness = 0;
	private Graphics2D g;
	private boolean mutated = false;
	private int worstCaseFitness;

	public CirclePicture(Config config) {
		
		GraphicsConfiguration gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration();
		this.config = config;
		this.canvas = gfxConfig.createCompatibleImage(config.WIDTH, config.HEIGHT, BufferedImage.TYPE_INT_RGB);
		this.pixels = new int[canvas.getWidth() * canvas.getHeight()];
		circles = new Circle[config.CIRCLE_COUNT];
		worstCaseFitness = 255 * 3 * pixels.length;
		
		for(int i = 0; i < circles.length; i++) {
			circles[i] = new Circle();
		}
	}

	public void initRandom() {
		for (int i = 0; i < circles.length; i++) {
			circles[i] = Circle.random(config);
			circles[i].normalValues[3] = 0;
		}
	}

	public void draw() {
		g = (Graphics2D) canvas.getGraphics();
		g.setColor(config.BACKGROUND_COLOR);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		for (int i = 0; i < circles.length; i++) {
			circles[i].render(g);
		}
	}

	public void mutate(final float MUTATION_RATE, final float MUTATION_AMOUNT) {
		mutated = false;
		for (int i = 0; i < config.CIRCLE_COUNT; i++) {
			if (Math.random() < MUTATION_RATE) {
				mutated = true;
				circles[i].mutate(MUTATION_AMOUNT);
				circles[i].updateValues(config);
			}
		}
	}

	public void calculateFitness(Image target) {
		loadPixels();
		fitness = 1 - (compare(this, target) / worstCaseFitness);
	}

	public void loadPixels() {
		WritableRaster raster = canvas.getRaster();
		raster.getDataElements(0, 0, canvas.getWidth(), canvas.getHeight(), pixels);
	}

	public static double compare(CirclePicture picture, Image image) {
		if (image.pixels.length != picture.pixels.length) {
			throw new IllegalArgumentException("Image sizes must be the same");
		}

		double score = 0;
		for (int i = 0; i < image.pixels.length; i++) {
			int pixel = picture.pixels[i];
			double redDiff = Math.abs(((pixel >> 16) & 0xFF) - (image.pixels[i].r));
			double greenDiff = Math.abs(((pixel >> 8) & 0xFF) - (image.pixels[i].g));
			double blueDiff = Math.abs((pixel & 0xFF) - (image.pixels[i].b));
			score += (redDiff) + (greenDiff) + (blueDiff);
		}
		return score;
	}

	public void set(CirclePicture other) {	
		if (other.config.CIRCLE_COUNT != config.CIRCLE_COUNT) {
			throw new RuntimeException("Pictures must be the same type");
		}
		for (int i = 0; i < config.CIRCLE_COUNT; i++) {
			if (!circles[i].equals(other.circles[i])) {
				circles[i].set(other.circles[i]);
			}
		}
		fitness = other.getFitness();
	}
	
	public double getFitness() {
		return fitness;
	}

	public boolean isMutated() {
		return mutated;
	}

	public static class Config {
		protected int CIRCLE_COUNT = 100;
		protected final int WIDTH;
		protected final int HEIGHT;
		protected int MIN_RADIUS = 3;
		protected int MAX_RADIUS = 7;
		protected float MIN_ALPHA = 0.3f;
		protected Color BACKGROUND_COLOR = Color.WHITE;

		public Config(final int WIDTH, final int HEIGHT) {
			this.WIDTH = WIDTH;
			this.HEIGHT = HEIGHT;
		}

		public Config setCircleCount(final int CIRCLE_COUNT) {
			this.CIRCLE_COUNT = CIRCLE_COUNT;
			return this;
		}

		public Config setMinRadius(final int MIN_RADIUS) {
			this.MIN_RADIUS = MIN_RADIUS;
			return this;
		}

		public Config setMaxRadius(final int MAX_RADIUS) {
			this.MAX_RADIUS = MAX_RADIUS;
			return this;
		}
		
		public Config setMinAlpha(final float MIN_ALPHA) {
			this.MIN_ALPHA = MIN_ALPHA;
			return this;
		}

		public Config setBackgroundColor(final Color BACKGROUND_COLOR) {
			this.BACKGROUND_COLOR = BACKGROUND_COLOR;
			return this;
		}
	}
}
