package com.alejandro.game.util;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.util.logging.Logger;

/**
 * Utility class to parse requests and retrieve its values.
 *
 * @author afernandez
 */
public class RequestParser {
    private static final Logger LOGGER = Logger.getLogger(RequestParser.class.getName());

    /**
     * Static utility method to send a generic response to the client.
     *
     * @param httpCode The http code
     * @param content The content body
     * @param httpExchange The http exchange
     * @throws IOException
     */
    public static void sendResponse(int httpCode, String content, HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(httpCode, content.length());

        OutputStream os = httpExchange.getResponseBody();
        os.write(content.getBytes());
        os.close();
    }

    /**
     * Given an URL path returns its user id or level id, depending on the request.
     *
     * @param path The url path in string format
     * @return The user id or level id
     */
    public int getUserOrLevelId(String path) {
        final String number = path.substring(1).split("/")[0];
        return validateAndConvertNumber(number);
    }

    /**
     * Parses the query parameter's value to get the session key.
     *
     * @param query The query string part of the URL
     * @return The query parameter's value containing the session key
     */
    public String getQueryValue(String query) {
        return query == null ? query : query.split("=")[1];
    }

    /**
     * Retrieves the score from the request body.
     *
     * @param inputStream InputStream to read the request body
     * @return The int representation of the score
     */
    public int getScore(InputStream inputStream) {
        String score = null;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            score = reader.readLine();
        } catch (IOException ex) {
            handleArgumentError("Cannot read the request body 'Score' value", ex);
        }
        return validateAndConvertNumber(score);
    }

    /**
     * Converts a string number to its integer representation. Additionally validates if the number is an unsigned
     * 31 bit number.
     *
     * @param id The number in String format
     * @return The int representation of the number
     */
    private int validateAndConvertNumber(String id) {
        int number = 0;

        try {
            number = Integer.parseInt(id);
            // Integer.MAX_VALUE holds 2^31 - 1.
            if (number < 0 || number > Integer.MAX_VALUE) {
                throw new IllegalArgumentException(String.format("ID is not a valid unsigned 31 bit number", id));
            }
        } catch (NumberFormatException ex) {
            handleArgumentError(String.format("ID [%s] is not a valid number.", id), ex);
        }
        return number;
    }

    private void handleArgumentError(String errorMessage, Exception ex) {
        LOGGER.severe(errorMessage);
        throw new IllegalArgumentException(errorMessage, ex);
    }
}