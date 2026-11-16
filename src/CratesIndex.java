import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.net.URI;
import java.net.HttpURLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Работа с индексом репозитория пакетов crates.io.
 */
public class CratesIndex {
    /**
     * Длина имени из одного символа.
     */
    private static final int ONE_CHAR = 1;
    /**
     * Длина имени из двух символов.
     */
    private static final int TWO_CHARS = 2;
    /**
     * Длина имени из трех символов.
     */
    private static final int THREE_CHARS = 3;
    /**
     * HTTP-клиент, общий для всех запросов к индексу.
     */
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    /**
     * Шаблон описания одной зависимости; группа 1 — имя пакета.
     */
    private static final Pattern DEPENDENCY = Pattern.compile("\\{\"name\":\"([^\"]+)\"[^{}]*\\}");
    /**
     * Шаблон настоящего имени переименованной зависимости; группа 1 — имя.
     */
    private static final Pattern PACKAGE = Pattern.compile("\"package\":\"([^\"]+)\"");
    /**
     * Вычисляет путь к файлу пакета в индексе crates.io.
     *
     * @param packageName имя пакета
     * @return путь к файлу пакета относительно корня индекса
     */
    public static String indexPath(String packageName) {
        String name = packageName.toLowerCase(Locale.ROOT);
        if (name.length() == ONE_CHAR) {
            return "1/" + name;
        } else if (name.length() == TWO_CHARS) {
            return "2/" + name;
        } else if (name.length() == THREE_CHARS) {
            return "3/" + name.substring(0, 1) + "/" + name;
        } else {
            return name.substring(0, 2) + "/" + name.substring(2, 4) + "/" + name;
        }
    }

    /**
     * Скачивает файл описания пакета из индекса.
     *
     * @param repo        адрес индекса
     * @param packageName имя пакета
     * @return содержимое файла: по одной строке JSON на каждую версию пакета
     * @throws IOException          если пакет не найден или произошла ошибка сети
     * @throws InterruptedException если загрузка была прервана
     */
    public static String download(String repo, String packageName) throws IOException, InterruptedException {
        String address = repo + "/" + indexPath(packageName);
        HttpRequest request = HttpRequest.newBuilder(URI.create(address)).build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new IOException("Пакет не найден: " + packageName);
        } else if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Ошибка сервера: " + response.statusCode());
        }
        return response.body();
    }

    /**
     * Находит описание последней неотозванной версии пакета.
     *
     * @param indexFile содержимое файла пакета из индекса
     * @return строка JSON с описанием последней неотозванной версии
     * @throws IOException если все версии пакета отозваны
     */
    public static String latestVersion(String indexFile) throws IOException {
        String[] lines = indexFile.split("\n");
        for (int i = lines.length - 1; i >= 0; i--) {
            if (lines[i].contains("\"yanked\":false")) {
                return lines[i];
            }
        }
        throw new IOException("Все версии пакета отозваны");
    }

    /**
     * Извлекает имена зависимостей вида normal из описания версии.
     *
     * @param versionLine строка JSON с описанием версии пакета
     * @return список имён зависимостей
     */
    public static List<String> dependencies(String versionLine) {
        List<String> result = new ArrayList<>();
        Matcher matcher = DEPENDENCY.matcher(versionLine);
        while (matcher.find()) {
            String dep = matcher.group();
            Matcher packageMatcher = PACKAGE.matcher(dep);
            String name = packageMatcher.find() ? packageMatcher.group(1) : matcher.group(1);
            if (dep.contains("\"kind\":\"normal\"") && !result.contains(name)) {
                result.add(name);
            }
        }
        return result;
    }
}
