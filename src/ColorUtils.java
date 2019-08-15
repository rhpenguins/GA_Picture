import java.util.concurrent.ThreadLocalRandom;

public class ColorUtils {

	public static RGBColor random() {
		return new RGBColor(ThreadLocalRandom.current().nextInt(255), ThreadLocalRandom.current().nextInt(255),
				ThreadLocalRandom.current().nextInt(255), (int) Math.max((Math.random() * Math.random()) * 255, 50));
	}

}
