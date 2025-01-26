package ch.stockmanager.client.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HTTPHelper {

	/**
	 * parse a server answer body in json to a class
	 * @param json the json to parse
	 * @return a class corresponding to the json
	 * @param <T> the class to which we parse the input
	 * @throws JsonProcessingException if the json doesn't match the class or is invalid
	 */
	public static <T> T parse(String json, Class<T> type) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, type);
	}

	public static <T> List<T> parseList(String json, Class<T> type) throws JsonProcessingException {
		var objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, type));
	}

	public static <T> T get(String url, Class<T> type) {
		try {
			String response = getResourceAt(url);

			return parse(response, type);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> List<T> getList(String url, Class<T> type) {
		try {
			String response = getResourceAt(url);

			return parseList(response, type);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void post(String url, T object) {
		modifyingRequest(url, object, "POST", HttpURLConnection.HTTP_CREATED);
	}

	public static <T> void put(String url, T object) {
		modifyingRequest(url, object, "PUT", HttpURLConnection.HTTP_OK);
	}

	public static void delete(String url) {
		try {
			HttpURLConnection connection = createConnexion(url, "DELETE");
			sendRequest(connection, HttpURLConnection.HTTP_OK);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> void modifyingRequest(String url, T object, String method, int expectedCode) {
		try {
			HttpURLConnection connection = createConnexion(url, method);
			serialize(connection, object);
			sendRequest(connection, expectedCode);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void serialize(HttpURLConnection connexion, Object o) throws JsonProcessingException {
		connexion.setRequestProperty("Content-Type", "application/json");
		connexion.setDoOutput(true);

		ObjectMapper objectMapper = new ObjectMapper();
		String jsonInputString = objectMapper.writeValueAsString(o);

		try (OutputStream os = connexion.getOutputStream()) {
			byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
			os.write(input, 0, input.length);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 *
	 * @param stringUrl the url of the api method
	 * @return the server answer's as a String
	 * @throws RuntimeException if the answer code isn't the success code
	 */
	private static HttpURLConnection createConnexion(String stringUrl, String method) throws IOException {
		URL url;
		try {
			url = new URI(stringUrl).toURL();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		HttpURLConnection connexion = (HttpURLConnection) url.openConnection();
		connexion.setRequestMethod(method);

		return connexion;
	}

	private static void sendRequest(HttpURLConnection connexion, int successCode) throws RuntimeException {
		try {
			connexion.connect();
			int responseCode = connexion.getResponseCode();
			if (responseCode != successCode) {
				throw new RuntimeException("Failed : HTTP error code : " + responseCode);
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to connect to the server: " + e.getMessage(), e);
		}
	}

	private static String getAnswer(HttpURLConnection connexion) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(connexion.getInputStream()))) {
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}

			return response.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static String getResourceAt(String url) {
		try {
			HttpURLConnection connexion = createConnexion(url, "GET");
			sendRequest(connexion, HttpURLConnection.HTTP_OK);
			return getAnswer(connexion);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
