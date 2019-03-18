package httpserver.anton.utils.controllers;

import httpserver.anton.api.HttpRequest;
import httpserver.anton.api.HttpResponse;
import httpserver.anton.api.Reaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Dmitry Bryzhatov
 * @since 2019-02-21
 */
@Log4j
public class StaticResourceController implements Reaction {
    private final Map<String, File> cache = new ConcurrentHashMap<>();
    private final Path staticResDir;

    public StaticResourceController(Path staticResDir) {
        this.staticResDir = staticResDir;
    }

    @Override
    public void react(HttpRequest request, HttpResponse response) {
        try {
            File file = cache.get(request.getPathInfo());

            if (file != null) {
                response.write(file.getFile());
            } else {
                Files.walk(staticResDir)
                        .filter(Files::isRegularFile)
                        .forEach((path) ->
                        {
                            if (path.toString().endsWith(request.getPathInfo())) {
                                try {
                                    File newFile = new File(MimeType.get(path.getFileName().toString()), Files.readAllBytes(path));

                                    cacheFile(request.getPathInfo(), newFile);

                                    response.addHeader("Content-Type", newFile.getMimeType());
                                    response.write(newFile.getFile());

                                } catch (IOException e) {
                                    log.error("Can't read bytes from resource", e);
                                }
                            }
                        });
            }
        } catch (IOException e) {
            log.error("Can't watch static resource directory", e);
        }
    }

    private void cacheFile(String file, File bytesFile) {
        cache.put(file, bytesFile);
    }

    private static class MimeType {
        private static final Map<String, String> MIME_TYPES = new HashMap<>();

        static {
            MIME_TYPES.put("html", "text/html");
            MIME_TYPES.put("css", "text/css");
            MIME_TYPES.put("js", "text/javascript");
            MIME_TYPES.put("jpeg", "image/jpeg");
            MIME_TYPES.put("jpg", "image/jpg");
            MIME_TYPES.put("png", "image/png");
        }

        public static String get(String fileName) {
            String[] splitFileName = StringUtils.split(fileName, ".");
            String mimeType = MIME_TYPES.get(splitFileName[splitFileName.length - 1]);

            if (mimeType != null) {
                return mimeType;
            }
            return "text/plain";
        }
    }

    @Data
    @AllArgsConstructor
    private static class File {
        private final String mimeType;
        private final byte[] file;
    }
}
