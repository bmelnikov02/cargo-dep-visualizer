/**
 * Параметры приложения, заданные пользователем в командной строке.
 * @param packageName имя пакета.
 * @param repo URL репозитория или путь к файлу тестового репозитория.
 * @param output имя файла с изображением графа.
 * @param maxDepth максимальная глубина анализа зависимостей.
 * @param testMode режим работы с тестовым репозиторием.
 * @param asciiTree режим вывода зависимостей в виде ASCII-дерева.
 * @param reverse режим вывода обратных зависимостей
 */
public record Config(
        String packageName,
        String repo,
        String output,
        int maxDepth,
        boolean testMode,
        boolean asciiTree,
        boolean reverse
) {
}
