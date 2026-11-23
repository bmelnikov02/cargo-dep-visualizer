import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Визуализация графа зависимостей: Graphviz (DOT, PNG) и ASCII-дерево.
 */
public class GraphRenderer {
    /**
     * Расширение файла изображения.
     */
    private static final String PNG_EXTENSION = ".png";

    /**
     * Формирует описание графа на языке Graphviz (DOT).
     *
     * @param graph граф: пакет -> список его прямых зависимостей
     * @return текст графа на языке DOT
     */
    public static String toDot(Map<String, List<String>> graph) {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph dependencies {\n");
        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
            dot.append("    \"").append(entry.getKey()).append("\";\n");
            for (String dep : entry.getValue()) {
                dot.append("    \"").append(entry.getKey()).append("\" -> \"")
                        .append(dep).append("\";\n");
            }
        }
        dot.append("}\n");
        return dot.toString();
    }

    /**
     * Сохраняет описание графа в файл .dot рядом с изображением
     * и строит по нему изображение PNG с помощью Graphviz.
     *
     * @param dot    описание графа на языке DOT
     * @param output имя файла изображения (.png)
     * @throws IOException          если файл не записан, Graphviz не найден
     *                              или завершился с ошибкой
     * @throws InterruptedException если ожидание Graphviz было прервано
     */
    public static void savePng(String dot, String output)
            throws IOException, InterruptedException {
        String dotFile = output.substring(0, output.length() - PNG_EXTENSION.length()) + ".dot";
        Files.writeString(Path.of(dotFile), dot);
        Process process = new ProcessBuilder("dot", "-Tpng", dotFile, "-o", output)
                .redirectErrorStream(true)
                .start();
        String messages = new String(process.getInputStream().readAllBytes());
        if (process.waitFor() != 0) {
            throw new IOException("Graphviz завершился с ошибкой: " + messages);
        }
    }

    /**
     * Элемент стека при выводе ASCII-дерева.
     *
     * @param name   имя пакета
     * @param prefix отступ с линиями родительских веток
     * @param last   является ли пакет последним ребёнком своего родителя
     */
    private record TreeItem(String name, String prefix, boolean last) {
    }

    /**
     * Выводит граф в виде ASCII-дерева. Пакет, который уже был показан,
     * повторно не раскрывается и помечается «(*)».
     *
     * @param graph граф: пакет -> список его прямых зависимостей
     * @param root  корень дерева
     */
    public static void printAsciiTree(Map<String, List<String>> graph, String root) {
        Set<String> printed = new HashSet<>();
        Deque<TreeItem> stack = new ArrayDeque<>();
        System.out.println(root);
        printed.add(root);
        pushChildren(stack, graph.getOrDefault(root, List.of()), "");
        while (!stack.isEmpty()) {
            TreeItem item = stack.pop();
            String line = item.prefix() + (item.last() ? "└── " : "├── ") + item.name();
            if (printed.contains(item.name())) {
                System.out.println(line + " (*)");
                continue;
            }
            System.out.println(line);
            printed.add(item.name());
            String childPrefix = item.prefix() + (item.last() ? "    " : "│   ");
            pushChildren(stack, graph.getOrDefault(item.name(), List.of()), childPrefix);
        }
    }

    /**
     * Кладёт детей пакета в стек в обратном порядке, чтобы первый
     * ребёнок был выведен первым.
     *
     * @param stack    стек вывода
     * @param children дети пакета
     * @param prefix   отступ для детей
     */
    private static void pushChildren(Deque<TreeItem> stack, List<String> children, String prefix) {
        for (int i = children.size() - 1; i >= 0; i--) {
            boolean last = i == children.size() - 1;
            stack.push(new TreeItem(children.get(i), prefix, last));
        }
    }
}
