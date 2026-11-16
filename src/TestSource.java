import java.util.List;
import java.util.Map;

/**
 * Источник зависимостей на основе тестового репозитория.
 */
public class TestSource implements DependencySource {
    /**
     * Граф пакетов: пакет -> список его зависимостей.
     */
    private final Map<String, List<String>> graph;
    /**
     * Создаёт источник из графа тестового репозитория.
     *
     * @param graph граф пакетов
     */
    public TestSource(Map<String, List<String>> graph) {
        this.graph = graph;
    }

    /**
     * Возвращает зависимости пакета из графа. Для пакета, которого нет в графе, возвращается пустой список.
     * @param packageName имя пакета
     * @return список имён прямых зависимостей
     */
    @Override
    public List<String> dependencies(String packageName) {
        return graph.getOrDefault(packageName, List.of());
    }
}
