import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Тестовый репозиторий: граф пакетов, загруженный из текстового файла.
 * Формат строки: «ПАКЕТ: ЗАВИСИМОСТЬ1 ЗАВИСИМОСТЬ2 ...».
 */
public class TestRepository {
    /**
     * Значение, которое indexOf возвращает, если символ не найден.
     */
    private static final int NOT_FOUND = -1;

    /**
     * Загружает граф пакетов из файла.
     *
     * @param path путь к файлу тестового репозитория
     * @return отображение «пакет -> список его зависимостей»
     * @throws IOException если файл не читается или содержит ошибку
     */
    public static Map<String, List<String>> load(String path) throws IOException {
        Map<String, List<String>> graph = new LinkedHashMap<>();
        List<String> lines = Files.readAllLines(Path.of(path));
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            int colon = line.indexOf(':');
            if (colon == NOT_FOUND) {
                throw new IOException("Нет двоеточия в строке: " + line);
            }
            String header = line.substring(0, colon).trim();
            checkName(header, line);
            String rest = line.substring(colon + 1).trim();
            if (rest.isEmpty()) {
                graph.put(header, List.of());
                continue;
            }
            String[] deps = rest.split("\\s+");
            for (String dep : deps) {
                checkName(dep, line);
            }
            graph.put(header, List.of(deps));
        }
        return graph;
    }

    /**
     * Проверяет, что имя пакета состоит из заглавных латинских букв.
     *
     * @param name имя пакета
     * @param line строка файла, в которой встретилось имя
     * @throws IOException если имя некорректно
     */
    private static void checkName(String name, String line) throws IOException {
        if (!name.matches("[A-Z]+")) {
            throw new IOException("Некорректное имя пакета \"" + name + "\" в строке: " + line);
        }
    }
}
