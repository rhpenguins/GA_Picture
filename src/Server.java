import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class Server implements Runnable {
	private HttpServer server;
	private World world;
	private String html;
	private String script;
	private String config;
	private BufferedImage target;

	public Server(World world) {
		this.world = world;
		config = world.getConfig().getConfig().toString();
		target = world.getConfig().BUFFERED_TARGET;
	}

	@Override
	public void run() {
		InputStream in = getClass().getResourceAsStream("/index.html");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		html = reader.lines().collect(Collectors.joining("\n"));
		
		in = getClass().getResourceAsStream("/script.js");
		reader = new BufferedReader(new InputStreamReader(in));
		script = reader.lines().collect(Collectors.joining("\n"));

		try {
			server = HttpServer.create(new InetSocketAddress(8000), 0);
			server.createContext("/yuki/data", new DataHandler(world));
			server.createContext("/yuki/config", new FileHandler(config,"application/json"));
			server.createContext("/yuki/target", new TargetHandler(target));
			server.createContext("/yuki", new FileHandler(html, "text/html"));
			server.createContext("/yuki/script.js", new FileHandler(script, "application/javascript"));
			server.setExecutor(null);
			server.start();
		} catch (IOException e) {
			System.out.println("Could not create server");
			return;
		}
	}

	public static class DataHandler implements HttpHandler {

		World world;

		public DataHandler(World world) {
			this.world = world;
		}

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			JSONObject json = new JSONObject();
			JSONArray circlesArray = new JSONArray();
			Circle[] circles = world.getCircles();
			for (int i = 0; i < circles.length; i++) {
				JSONObject circle = new JSONObject();
				circle.put("x", circles[i].getX());
				circle.put("y", circles[i].getY());
				circle.put("radius", circles[i].getRadius());
				JSONObject color = new JSONObject();
				color.put("r", circles[i].getRed());
				color.put("g", circles[i].getGreen());
				color.put("b", circles[i].getBlue());
				color.put("a", (circles[i].getAlpha()));
				circle.put("color", color);
				circlesArray.put(circle);
			}

			json.put("generation", world.getGeneration());
			json.put("fitness", world.getPictureFitness());
			json.put("circles", circlesArray);
			String response = json.toString();
			exchange.getResponseHeaders().set("Content-Type", "application/json");
			exchange.sendResponseHeaders(200, response.length());

			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}

	public static class TargetHandler implements HttpHandler {
		BufferedImage target;

		public TargetHandler(BufferedImage target) {
			this.target = target;
		}

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			ByteArrayOutputStream byteos = new ByteArrayOutputStream();
			ImageIO.write(target, "png", byteos);

			byte[] bytes = byteos.toByteArray();

			exchange.getResponseHeaders().set("Content-Type", "image/png");
			exchange.sendResponseHeaders(200, bytes.length);

			OutputStream os = exchange.getResponseBody();
			os.write(bytes);
			os.close();
		}
	}

	public static class FileHandler implements HttpHandler {
		private String file;
		final private String MIME_TYPE;

		public FileHandler(String file, String MIME_TYPE) {
			this.file = file;
			this.MIME_TYPE = MIME_TYPE;
		}

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			exchange.getResponseHeaders().set("Content-Type", MIME_TYPE);
			exchange.sendResponseHeaders(200, file.length());

			OutputStream os = exchange.getResponseBody();
			os.write(file.getBytes());
			os.close();
		}
	}
}
