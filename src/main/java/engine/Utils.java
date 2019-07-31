package engine;

import java.net.URL;

public class Utils {
    public Utils() {

    }

    public static String loadResource(String res) {

        ClassLoader classLoader = Utils.class.getClassLoader();

        URL resource = classLoader.getResource(res);
        if (resource == null)
            throw new IllegalArgumentException("Resource not found");
        else
            return resource.getFile();
    }
}
