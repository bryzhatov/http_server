package httpserver.anton.utils.controllers;

import java.net.URISyntaxException;
import java.nio.file.Paths;

/**
 * @author Dmitry Bryzhatov
 * @since 2019-02-27
 */
public class StaticControllerFactory {

    public static StaticResourceController classpath(String path) throws URISyntaxException {
        return new StaticResourceController(Paths.get(path.getClass().getResource(path).toURI()));
    }
}
