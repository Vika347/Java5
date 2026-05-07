/**
 * Точка входа в приложение.
 */
public class Main {
    public static void main(String[] args) {
        // Создаём менеджер коллекции
        CollectionManager cm = new CollectionManager();

        // Запускаем обработчик команд
        CommandManager commandManager = new CommandManager(cm);
        commandManager.start();

        System.out.println("Программа завершена!");
    }
}