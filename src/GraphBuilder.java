import java.io.IOException;
import java.util.*;

/**
 * Построение графа зависимостей обходом в глубину (DFS) без рекурсии.
 */
public class GraphBuilder {
    /**
     * Элемент стека обхода: пакет и его глубина относительно корня.
     *
     * @param name  имя пакета
     * @param depth глубина
     */
    private record Node(String name, int depth) {
    }

    /**
     * Строит граф зависимостей пакета с учётом максимальной глубины.
     *
     * @param source   источник сведений о зависимостях
     * @param root     имя анализируемого пакета
     * @param maxDepth максимальная глубина анализа
     * @return граф: пакет -> список его прямых зависимостей
     * @throws IOException          если данные о пакете не удалось получить
     * @throws InterruptedException если загрузка была прервана
     */
    public static Map<String, List<String>> build(DependencySource source, String root, int maxDepth)
            throws IOException, InterruptedException {
        Map<String, List<String>> graph = new LinkedHashMap<>();
        Map<String, Integer> visited = new HashMap<>();
        Deque<Node> stack = new ArrayDeque<>();
        stack.push(new Node(root, 0));
        while (!stack.isEmpty()) {
            Node node = stack.pop();
            if (node.depth() >= maxDepth) {
                continue;
            }
            Integer seenDepth = visited.get(node.name());
            if (seenDepth != null && seenDepth <= node.depth()) {
                continue;
            }
            visited.put(node.name(), node.depth());
            List<String> deps = graph.get(node.name());
            if (deps == null) {
                deps = source.dependencies(node.name());
                graph.put(node.name(), deps);
            }
            for (String dep : deps) {
                stack.push(new Node(dep, node.depth() + 1));
            }
        }
        return graph;
    }
}
