package engine;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utils {

    public static String loadResource(String res) throws IOException {

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        File file = new File(Objects.requireNonNull(classLoader.getResource(res)).getFile());

        return new String(Files.readAllBytes(file.toPath()));
    }

    public static List<String> readAllLines(String fileName) throws Exception {
        List<String> list = new ArrayList<>();

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(classLoader.getResourceAsStream(fileName))))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        }
        return list;
    }

    public static float[] listToArray(List<Float> list) {
        int size = list != null ? list.size() : 0;
        float[] floatArr = new float[size];
        for (int i = 0; i < size; i++) {
            floatArr[i] = list.get(i);
        }
        return floatArr;
    }
}
