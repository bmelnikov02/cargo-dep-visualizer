import java.io.IOException;
import java.util.List;

/**
 * Источник зависимостей на основе индекса репозитория crates.io.
 */
public class CratesSource implements DependencySource {

    /**
     * Адрес индекса репозитория.
     */
    private final String repo;
    /**
     * Создаёт источник для заданного репозитория.
     *
     * @param repo адрес индекса, например https://index.crates.io
     */
    CratesSource(String repo) {
        this.repo = repo;
    }
    /**
     * Загружает описание пакета из индекса и возвращает прямые зависимости вида normal его последней неотозванной версии.
     * @param packageName имя пакета
     * @return список имён прямых зависимостей
     * @throws IOException          если пакет не найден или произошла ошибка сети
     * @throws InterruptedException если загрузка была прервана
     */
    @Override
    public List<String> dependencies(String packageName) throws IOException, InterruptedException {
        String indexFile = CratesIndex.download(repo, packageName);
        String version = CratesIndex.latestVersion(indexFile);
        return CratesIndex.dependencies(version);
    }
}
