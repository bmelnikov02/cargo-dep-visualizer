import java.io.IOException;
import java.util.List;

/**
 * Источник сведений о зависимостях пакетов.
 */
public interface DependencySource {
    /**
     * Возвращает прямые зависимости пакета.
     * @param packageName имя пакета
     * @return список имён прямых зависимостей
     * @throws IOException если данные о пакете не удалось получить
     * @throws InterruptedException если загрузка была прервана
     */
    List<String> dependencies(String packageName) throws IOException, InterruptedException;
}
