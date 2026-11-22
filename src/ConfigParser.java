/**
 * Разбирает аргументы командной строки и проверяет корректность всех параметров.
 */
public class ConfigParser {
    /**
     * Минимально допустимая глубина анализа зависимостей.
     */
    private static final int MIN_DEPTH = 1;

    /**
     * Разбирает аргументы командной строки, проверяет их
     * и возвращает готовую конфигурацию. Для незаданных
     * параметров используются значения по умолчанию.
     *
     * @param args аргументы командной строки
     * @return проверенная конфигурация приложения
     * @throws ConfigException если параметр неизвестен, не имеет значения или задан неверно
     */
    public static Config parse(String[] args) throws ConfigException {
        String packageName = null;
        String repo = "https://index.crates.io";
        String output = "graph.png";
        String maxDepth = "3";
        boolean testMode = false;
        boolean asciiTree = false;
        boolean reverse = false;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--package")) {
                packageName = getValue(args, i);
                i++;
            } else if (args[i].equals("--repo")) {
                repo = getValue(args, i);
                i++;
            } else if (args[i].equals("--output")) {
                output = getValue(args, i);
                i++;
            } else if (args[i].equals("--max-depth")) {
                maxDepth = getValue(args, i);
                i++;
            } else if (args[i].equals("--test-mode")) {
                testMode = true;
            } else if (args[i].equals("--ascii-tree")) {
                asciiTree = true;
            } else if (args[i].equals("--reverse")) {
                reverse = true;
            } else {
                throw new ConfigException("Неизвестный параметр: " + args[i]);
            }
        }
        checkPackage(packageName);
        int depth = parseDepth(maxDepth);
        checkOutput(output);
        checkRepo(repo, testMode);
        checkReverse(reverse, testMode);
        return new Config(packageName, repo, output, depth, testMode, asciiTree, reverse);
    }

    /**
     * Возвращает значение опции - следующий за ней аргумент.
     *
     * @param args аргументы командной строки
     * @param i    индекс опции в массиве аргументов
     * @return значение опции
     * @throws ConfigException если значение отсутствует или начинается с "--"
     */
    private static String getValue(String[] args, int i) throws ConfigException {
        if (i == args.length - 1) {
            throw new ConfigException("Не задан аргумент параметра: " + args[i]);
        }
        if (args[i + 1].startsWith("--")) {
            throw new ConfigException("Неправильно задан аргумент параметру: " + args[i]);
        }
        return args[i + 1];
    }

    /**
     * Преобразует глубину анализа в число и проверяет, что она не меньше минимально допустимой.
     *
     * @param maxDepth глубина анализа в виде строки
     * @return глубина анализа в виде числа
     * @throws ConfigException если значение не целое число или меньше {@link #MIN_DEPTH}
     */
    private static int parseDepth(String maxDepth) throws ConfigException {
        int depth;
        try {
            depth = Integer.parseInt(maxDepth);
            if (depth < MIN_DEPTH) {
                throw new ConfigException("Глубина графа меньше " + MIN_DEPTH + ": " + depth);
            }
        } catch (NumberFormatException e) {
            throw new ConfigException("Глубина должна быть числом: " + maxDepth);
        }
        return depth;
    }

    /**
     * Проверяет, что файл изображения имеет расширение .png.
     *
     * @param output имя файла с изображением графа
     * @throws ConfigException если расширение не .png
     */
    private static void checkOutput(String output) throws ConfigException {
        if (!output.endsWith(".png")) {
            throw new ConfigException("Неверный формат изображения: " + output);
        }
    }

    /**
     * Проверяет репозиторий. В тестовом режиме это должен быть
     * существующий файл, иначе - адрес, начинающийся с http:// или https://.
     *
     * @param repo     URL репозитория или путь к тестовому файлу
     * @param testMode включён ли режим тестового репозитория
     * @throws ConfigException если файл не найден или адрес некорректен
     */
    private static void checkRepo(String repo, boolean testMode) throws ConfigException {
        if (testMode) {
            if (!new java.io.File(repo).isFile()) {
                throw new ConfigException("Файл не существует: " + repo);
            }
        } else {
            if (!(repo.startsWith("http://") || repo.startsWith("https://"))) {
                throw new ConfigException("Адрес сайта задан неправильно: " + repo);
            }
        }
    }

    /**
     * Проверяет, что имя пакета задано и не пустое.
     *
     * @param packageName имя пакета
     * @throws ConfigException если имя не задано или пустое
     */
    private static void checkPackage(String packageName) throws ConfigException {
        if (packageName == null || packageName.isBlank()) {
            throw new ConfigException("Название пакета не задано");
        }
    }

    /**
     * Проверяет, что режим обратных зависимостей используется
     * только вместе с тестовым репозиторием.
     *
     * @param reverse  включён ли режим обратных зависимостей
     * @param testMode включён ли режим тестового репозитория
     * @throws ConfigException если обратные зависимости запрошены без тестового режима
     */
    private static void checkReverse(boolean reverse, boolean testMode) throws ConfigException {
        if (reverse && !testMode) {
            throw new ConfigException("Обратные зависимости выводятся только в тестовом режиме");
        }
    }
}
