package httpserver.anton.api;


/**
 * @author Dmitry Bryzhatov
 * @since 2019-02-12
 */
@FunctionalInterface
public interface Reaction {
    void react(HttpRequest request, HttpResponse response);
}
