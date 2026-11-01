/**
 * Исключение, сигнализирующее об ошибке в параметрах командной строки.
 */
public class ConfigException extends Exception {
    /**
     * Создаёт исключение с описанием ошибки.
     * @param message понятное пользователю описание ошибки
     */
    public ConfigException(String message) {
        super(message);
    }
}
