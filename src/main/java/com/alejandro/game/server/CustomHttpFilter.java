package com.alejandro.game.server;

import com.alejandro.game.exception.HttpBadRequestException;
import com.alejandro.game.exception.HttpNotFoundException;
import com.alejandro.game.handler.MainHandler;
import com.alejandro.game.util.HttpCode;
import com.alejandro.game.util.HttpMethod;
import com.alejandro.game.util.RequestParser;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * Post processing of incoming requests.
 *
 * @author afernandez
 */
public class CustomHttpFilter extends Filter {
    private static final String FILTER_DESC = "HttpRequestFilter that validates correctness of requests.";

    private static final String LOGIN       = "[/][0-9]+[/login]{6}";
    private static final String SCORE       = "[/][0-9]+[/score]{6}";
    private static final String LEADERBOARD = "[/][0-9]+[/highscorelist]{14}";

    private RequestParser requestParser;

    public CustomHttpFilter(RequestParser requestParser) {
        this.requestParser = requestParser;
    }

    @Override
    public String description() {
        return FILTER_DESC;
    }

    @Override
    public void doFilter(HttpExchange httpExchange, Chain chain) throws IOException {
        final String uri = httpExchange.getRequestURI().getPath();

        try {
            if (uri.matches(LOGIN)) {
                validateGetRequest(httpExchange);
                httpExchange.setAttribute(MainHandler.REQUEST, MainHandler.LOGIN_REQUEST);
            } else if (uri.matches(SCORE)) {
                validateScoreRequest(httpExchange);
                httpExchange.setAttribute(MainHandler.REQUEST, MainHandler.SCORE_REQUEST);
            } else if (uri.matches(LEADERBOARD)) {
                validateGetRequest(httpExchange);
                httpExchange.setAttribute(MainHandler.REQUEST, MainHandler.LEADERBOARD_REQUEST);
            } else {
                throw new HttpNotFoundException("URL not found.");
            }

            chain.doFilter(httpExchange);
        } catch (HttpNotFoundException ex) {
            RequestParser.sendResponse(HttpCode.NOT_FOUND.getCode(), ex.getMessage(), httpExchange);
        } catch (HttpBadRequestException ex) {
            RequestParser.sendResponse(HttpCode.BAD_REQUEST.getCode(), ex.getMessage(), httpExchange);
        }
    }

    private void validateGetRequest(HttpExchange httpExchange) throws HttpBadRequestException {
        if (!httpExchange.getRequestMethod().equals(HttpMethod.GET.getMethod())) {
            throw new HttpBadRequestException(String.format("Incorrect HTTP Method: %s", httpExchange.getRequestMethod()));
        }
    }

    private void validateScoreRequest(HttpExchange httpExchange) throws HttpBadRequestException {
        if (!httpExchange.getRequestMethod().equals(HttpMethod.POST.getMethod())) {
            throw new HttpBadRequestException(String.format("Incorrect HTTP Method: %s", httpExchange.getRequestMethod()));
        }

        try {
            httpExchange.setAttribute(MainHandler.SCORE_ATTR, requestParser.getScore(httpExchange.getRequestBody()));
        } catch (IllegalArgumentException ex) {
            throw new HttpBadRequestException("The the score value to add was empty.");
        }
    }
}