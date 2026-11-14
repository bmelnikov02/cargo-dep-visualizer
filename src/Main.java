import java.io.IOException;
import java.util.List;

/**
 * Точка входа в приложение - инструмент визуализации графа зависимостей пакета Rust (cargo).
 */
public class Main {
    /**
     * Запускает приложение: разбирает параметры командной строки
     * и выводит их значения. При ошибке в параметрах выводит
     * сообщение об ошибке и завершает работу с кодом 1.
     *
     * @param args аргументы командной строки.
     */
    public static void main(String[] args) {
        try {
            Config config = ConfigParser.parse(args);
            if (config.testMode()) {
                System.out.println("Тестовый режим будет реализован на этапе 3");
                return;
            }
            printDependencies(config);
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
}