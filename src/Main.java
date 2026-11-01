/**
 * Точка входа в приложение - инструмент визуализации графа зависимостей пакета Rust (cargo).
 */
public class Main {
    /**
     * Запускает приложение: разбирает параметры командной строки
     * и выводит их значения. При ошибке в параметрах выводит
     * сообщение об ошибке и завершает работу с кодом 1.
     * @param args аргументы командной строки.
     */
    public static void main(String[] args) {
        try {
            Config config = ConfigParser.parse(args);
            System.out.println("package: " + config.packageName());
            System.out.println("repo: " + config.repo());
            System.out.println("output: " + config.output());
            System.out.println("max-depth: " + config.maxDepth());
            System.out.println("test-mode: " + config.testMode());
            System.out.println("ascii-tree: " + config.asciiTree());
        } catch (ConfigException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}