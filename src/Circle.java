import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class Circle {

	private int x = 0;
	private int y = 0;
	private float radius = 0;
	private float r = 0;
	private float g = 0;
	private float b = 0;
	private float a = 0;
	private Ellipse2D circle = new Ellipse2D.Float();
	public float[] normalValues = new float[7];
	/*
	 * [0] = r, [1] = g, [2] = b, [3] = a, [4] * (MAX_RADIUS - MIN_RADIUS) +
	 * MIN_RADIUS = radius, [5] * MAX_X = x, [6] * MAX_Y = y
	 */

	protected Circle() {
	}

	public void render(Graphics2D g2d) {
		g2d.setColor(new Color(r, g, b, a));
		int diameter = (int) (radius * 2);
		circle.setFrame(x - radius, y - radius, diameter, diameter);
		g2d.fill(circle);
	}

	public static Circle random(CirclePicture.Config config) {
		Circle random = new Circle();
		for (int i = 0; i < random.normalValues.length; i++) {
			random.normalValues[i] = (float) Math.random();
		}
		random.updateValues(config);
		return random;
	}

	public void updateValues(CirclePicture.Config config) {
		r = normalValues[0];
		g = normalValues[1];
		b = normalValues[2];
		a = config.MIN_ALPHA + (normalValues[3] * (1 - config.MIN_ALPHA));
		radius = normalValues[4] * (config.MAX_RADIUS - config.MIN_RADIUS) + config.MIN_RADIUS;
		x = (int) (normalValues[5] * config.WIDTH);
		y = (int) (normalValues[6] * config.HEIGHT);
	}

	public void mutate(float MUTATION_AMOUNT) {
		for (int i = 0; i < normalValues.length; i++) {
			normalValues[i] += Math.random() * MUTATION_AMOUNT * 2f - MUTATION_AMOUNT;
			if (normalValues[i] > 1) {
				normalValues[i] = 1;
			}
			if (normalValues[i] < 0) {
				normalValues[i] = 0;
			}
		}
	}

	public void set(Circle other) {
		x = other.x;
		y = other.y;
		radius = other.radius;
		r = other.r;
		g = other.g;
		b = other.b;
		a = other.a;
		System.arraycopy(other.normalValues, 0, normalValues, 0, other.normalValues.length);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getRadius() {
		return radius;
	}

	public float getRed () {
		return r;
	}
	
	public float getGreen () {
		return g;
	}
	
	public float getBlue () {
		return b;
	}
	
	public float getAlpha () {
		return a;
	}
}
