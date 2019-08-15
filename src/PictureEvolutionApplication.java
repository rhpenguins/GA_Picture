import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.json.JSONException;
import org.json.JSONObject;

public class PictureEvolutionApplication {

	public static void main(String[] args) {
		JSONObject config;
		Path path;

		if (args.length == 0) {
			path = getConfigPathFromDialog();
			if (path == null) {
				exitDialog("No file choosen");
			}
		} else {
			path = Paths.get(args[0]);
		}

		config = pathToJSON(path);
		if (config == null) {
			exitDialog("Error reading config file");
		}
		String error;
		if ((error = validateConfigFile(config)) != null) {
			exitDialog(error);
		}

		ImageLoader loader = new ImageLoader();
		BufferedImage image = loader.loadImage(config.getString("imagepath"));
		if (image == null) {
			exitDialog("Could not read image");
		}
		BufferedImage bufferedTarget = ImageLoader.convert(image, BufferedImage.TYPE_INT_RGB);
		World.Config worldConfig = new World.Config(bufferedTarget, config);

		World world = new World(worldConfig);

		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

		startServer(world);

		world.run();
	}

	private static void startServer(World world) {
		new Server(world).run();
	}

	private static String validateConfigFile(JSONObject config) {
		String validated = null;
		if (!config.has("imagepath")) {
			validated = "Config file must include imagepath";
		}
		return validated;
	}

	private static JSONObject pathToJSON(Path path) {
		try {
			return new JSONObject(new String(Files.readAllBytes(path)));
		} catch (JSONException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	private static Path getConfigPathFromDialog() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));
		int result = fileChooser.showOpenDialog(null);

		if (result == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile().toPath();
		} else {
			return null;
		}
	}

	private static class ImageLoader {
		public BufferedImage loadImage(String filename) {
			BufferedImage image;
			try {
				image = ImageIO.read(new File(filename));
			} catch (IOException e) {
				return null;
			}
			return image;
		}

		public static BufferedImage convert(BufferedImage source, int model) {
			BufferedImage image = new BufferedImage(source.getWidth(), source.getHeight(), model);
			Graphics2D g = image.createGraphics();
			g.drawImage(source, 0, 0, null);
			g.dispose();
			return image;
		}
	}

	public static void exitDialog(String message) {
		System.out.println(message);
		JOptionPane.showMessageDialog(null, message);
		System.exit(0);
	}
}
