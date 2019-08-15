public class Image {

	public RGBColor[] pixels;
	public final int width;
	public final int height;

	public Image(int width, int height, RGBColor[] pixels) {
		this.pixels = pixels;
		this.width = width;
		this.height = height;
	}
}