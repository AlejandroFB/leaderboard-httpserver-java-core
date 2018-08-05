package com.alejandro.game.handler;

import com.alejandro.game.leaderboard.LeaderboardService;
import com.alejandro.game.util.RequestParser;
import com.alejandro.game.leaderboard.session.SessionService;
import com.alejandro.game.util.HttpCode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * HttpHandler to process incoming requests.
 *
 * @author afernandez
 */
public class MainHandler implements HttpHandler {
    public static final String LOGIN_REQUEST       = "login";
    public static final String SCORE_REQUEST       = "score";
    public static final String LEADERBOARD_REQUEST = "highscorelist";

    public static final String REQUEST             = "request";
    public static final String SCORE_ATTR          = "score_attr";

    private LeaderboardService leaderboardService;
    private RequestParser requestParser;
    private SessionService sessionService;

    public MainHandler(LeaderboardService leaderboardService, RequestParser requestParser, SessionService sessionService) {
        this.leaderboardService = leaderboardService;
        this.requestParser = requestParser;
        this.sessionService = sessionService;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        executeAction(httpExchange);
        httpExchange.getResponseBody().close();
    }

    private void executeAction(HttpExchange httpExchange) throws IOException {
        final String uri = httpExchange.getRequestURI().getPath();
        final String request = httpExchange.getAttribute(REQUEST).toString();
        int id = requestParser.getUserOrLevelId(uri);

        if (LOGIN_REQUEST.equals(request)) {
            RequestParser.sendResponse(HttpCode.OK.getCode(), leaderboardService.getSessionKey(id), httpExchange);
        } else if (SCORE_REQUEST.equals(request)) {
            addScore(id, httpExchange);
        } else if (LEADERBOARD_REQUEST.equals(request)) {
            RequestParser.sendResponse(HttpCode.OK.getCode(), leaderboardService.getHighScores(id), httpExchange);
        }
    }

    /**
     * Prepares and validates the data needed to add a score to the leaderboard.
     *
     * @param levelId The level id
     * @param httpExchange The http exchange
     * @throws IOException
     */
    private void addScore(int levelId, HttpExchange httpExchange) throws IOException {
        final String query = httpExchange.getRequestURI().getQuery();
        final String sessionKey = requestParser.getQueryValue(query);

        if (sessionKey != null && validateSession(sessionKey)) {
            leaderboardService.addScore(levelId, (int) httpExchange.getAttribute(SCORE_ATTR), sessionKey);
            RequestParser.sendResponse(HttpCode.OK.getCode(), "", httpExchange);
        } else {
            RequestParser.sendResponse(HttpCode.UNAUTHORIZED.getCode(), "Session key not valid.", httpExchange);
        }
    }

    /**
     * Validates a session key.
     *
     * @param sessionKey The session key string
     * @return True if valid, false otherwise
     */
    private boolean validateSession(String sessionKey) {
        Integer userId = sessionService.getUserIdBySessionKey(sessionKey);
        return userId == null ? false : sessionService.isSessionValid(userId, sessionKey);
    }
}