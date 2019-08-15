public class RGBColor {
	public int r;
	public int g;
	public int b;
	public int a;

	public RGBColor(int r, int g, int b, int a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public RGBColor(int r, int g, int b) {
		this(r, g, b, 255);
	}

	public RGBColor copy() {
		return new RGBColor(r, g, b, a);
	}
	
	public void set(RGBColor other) {
		r = other.r;
		g = other.g;
		b = other.b;
		a = other.a;
	}
}