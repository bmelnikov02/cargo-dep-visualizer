import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Точка входа в приложение - инструмент визуализации графа зависимостей пакета Rust (cargo).
 */
public class Main {
    /**
     * Запускает приложение: разбирает параметры командной строки,
     * строит граф зависимостей пакета и выводит его. При ошибке
     * выводит сообщение и завершает работу с кодом 1.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        try {
            Config config = ConfigParser.parse(args);
            DependencySource source = createSource(config);
            Map<String, List<String>> graph =
                    GraphBuilder.build(source, config.packageName(), config.maxDepth());
            System.out.println("Граф зависимостей " + config.packageName()
                    + " (глубина " + config.maxDepth() + "):");
            printGraph(graph);
        } catch (ConfigException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Загружает описание пакета из репозитория и выводит его прямые зависимости.
     * @param config параметры приложения
     * @throws IOException если пакет не найден или произошла ошибка сети
     * @throws InterruptedException если загрузка была прервана
     */
    private static void printDependencies(Config config) throws IOException, InterruptedException {
        String indexFile = CratesIndex.download(config.repo(), config.packageName());
        String version = CratesIndex.latestVersion(indexFile);
        List<String> dependencies = CratesIndex.dependencies(version);
        System.out.println("Прямые зависимости " + config.packageName() + ":");
        if (dependencies.isEmpty()) {
            System.out.println("(нет зависимостей)");
        }
        for (String dependency : dependencies) {
            System.out.println(dependency);
        }
    }
    /**
     * Создаёт источник зависимостей в соответствии с режимом работы.
     *
     * @param config параметры приложения
     * @return тестовый источник в тестовом режиме, иначе источник crates.io
     * @throws IOException если файл тестового репозитория не удалось загрузить
     */
    private static DependencySource createSource(Config config) throws IOException {
        if (config.testMode()) {
            return new TestSource(TestRepository.load(config.repo()));
        }
        return new CratesSource(config.repo());
    }
    /**
     * Выводит граф зависимостей: по строке на каждый пакет.
     *
     * @param graph граф: пакет -> список его прямых зависимостей
     */
    private static void printGraph(Map<String, List<String>> graph) {
        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
            System.out.println(entry.getKey() + " -> " + String.join(", ", entry.getValue()));
        }
    }
}