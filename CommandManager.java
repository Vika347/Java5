import java.io.*;
import java.util.Scanner;
import java.util.HashSet;
import java.util.Set;

/**
 * Менеджер команд: парсинг и выполнение.
 */
public class CommandManager {
    private CollectionManager cm;
    private boolean running = true;
    private Set<String> activeScripts = new HashSet<>(); // Хранит пути к выполняющимся скриптам
    private static final int MAX_SCRIPT_COMMANDS = 500;  // Оставим лимит команд для безопасности


    public CommandManager(CollectionManager cm) {
        this.cm = cm;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Приложение запущено! Введите 'help' для списка команд.");

        while (running) {
            System.out.print("\n> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+", 2);
            String command = parts[0].toLowerCase();
            String args = parts.length > 1 ? parts[1] : "";

            try {
                execute(command, args, scanner);
            } catch (Exception e) {
                System.err.println("Ошибка выполнения: " + e.getMessage());
            }
        }
    }

    private void execute(String cmd, String args, Scanner scanner) {
        switch (cmd) {
            case "help" -> printHelp();
            case "info" -> cm.info();
            case "show" -> cm.show();
            case "insert" ->{// Команда: insert null {element}
                 // args должен быть "null", дальше идёт элемент
                if (!args.equals("null")) {
                   System.out.println("Формат: insert null {element}");
                   break;
                }
                handleInsert(scanner);
            }
            case "update" -> handleUpdate(args, scanner);
            case "remove_key" -> {
                if (!args.equals("null")) {
                    System.out.println("Формат: remove_key null");
                    break;
                }
                handleRemoveKey();  // переделал, без args
            }
            case "clear" -> cm.clear();
            case "save" -> cm.saveToFile();
            case "execute_script" -> executeScript(args, scanner);
            case "exit" -> {
                System.out.println(" До свидания! (данные не сохранены)");
                running = false;
            }
            case "remove_greater" -> handleRemoveGreater(scanner);
            case "replace_if_lower" ->  {
                // Формат: replace_if_lower id {element}
                if (args.isEmpty()) {
                    System.out.println("Укажите id: replace_if_lower id");
                    break;
                }
                handleReplaceIfLower(args, scanner);
            }
            case "remove_greater_key" -> handleRemoveGreaterKey(args);
            case "count_by_author" -> handleCountByAuthor(args);
            case "filter_by_difficulty" -> handleFilterByDifficulty(args);
            case "filter_less_than_author" -> handleFilterLessThanAuthor(args);
            default -> System.out.println("Неизвестная команда. Введите 'help'.");
        }
    }

    private void printHelp() {
        System.out.println("""
             Доступные команды:
            help                           : вывести справку по доступным командам
            info                           : вывести в стандартный поток вывода информацию о коллекции 
            show                           : вывести все элементы коллекции в строковом представлении
            insert null {element} .        : добавить новый элемент с заданным ключом
            update id {element}            : обновить значение элемента коллекции, id которого равен заданному
            remove_key null                : удалить элемент из коллекции по его ключу
            clear                          : очистить коллекцию
            save                           : сохранить коллекцию в файл
            execute_script file_name       : выполнить скрипт
            exit                           : выйти
            remove_greater {element}       : удалить элементы > заданного
            replace_if_lower id {element}  : заменить, если новое < старого
            remove_greater_key key         : удалить элементы с ключом > key
            count_by_author author         : посчитать по автору
            filter_by_difficulty diff      : вывести элементы, значение поля difficulty которых равно заданному
            filter_less_than_author author : вывести элементы, значение поля author которых меньше заданного
            """);
    }

    // ОБРАБОТЧИКИ КОМАНД:

    private void handleInsert(Scanner scanner) {
        System.out.println("Добавление элемента:");
        cm.insert(InputReader.readLabWork());
    }

    private void handleUpdate(String args, Scanner scanner) {
        if (args.isEmpty()) { System.out.println("Укажите id"); return; }
        try {
            long id = Long.parseLong(args.split("\\s+")[0]);
            System.out.println("Обновление элемента #" + id + ":");
            cm.update(id, InputReader.readLabWork());
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат id!");
        }
    }

    private void handleRemoveKey() {
        System.out.print("Введите ключ (id) для удаления: ");
        try {
            Scanner sc = new Scanner(System.in);
            long key = Long.parseLong(sc.nextLine().trim());
            cm.removeKey(key);
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат ключа!");
        }
    }

    private void handleRemoveGreater(Scanner scanner) {
        System.out.println("Введите элемент для сравнения:");
        cm.removeGreater(InputReader.readLabWork());
    }

    private void handleReplaceIfLower(String args, Scanner scanner) {
        if (args.isEmpty()) { System.out.println("Укажите id"); return; }
        try {
            long id = Long.parseLong(args.split("\\s+")[0]);
            System.out.println("🔄 Ввод нового значения:");
            cm.replaceIfLower(id, InputReader.readLabWork());
        } catch (NumberFormatException e) { System.out.println("Ошибка парсинга ключа"); }
    }

    private void handleRemoveGreaterKey(String args) {
        try { cm.removeGreaterKey(Long.parseLong(args)); }
        catch (NumberFormatException e) { System.out.println("Неверный формат ключа"); }
    }

    private void handleCountByAuthor(String args) {
        if (args.isEmpty()) { System.out.println("Укажите имя автора"); return; }
        cm.countByAuthor(args.trim());
    }

    private void handleFilterByDifficulty(String args) {
        try {
            Difficulty diff = Difficulty.valueOf(args.trim().toUpperCase());
            cm.filterByDifficulty(diff);
        } catch (IllegalArgumentException e) {
            System.out.println("Неверное значение. Доступно: VERY_EASY, EASY, HARD, VERY_HARD, IMPOSSIBLE");
        }
    }

    private void handleFilterLessThanAuthor(String args) {
        if (args.isEmpty()) { System.out.println("Укажите имя автора"); return; }
        cm.filterLessThanAuthor(args.trim());
    }

    /**
     * !!!!Выполнение СКРИПТА из файла.
     */

    private void executeScript(String fileName, Scanner mainScanner) {
        if (fileName == null || fileName.trim().isEmpty()) {
            System.out.println("Укажите имя файла скрипта");
            return;
        }

        // Проверка на рекурсию через множество
        if (activeScripts.contains(fileName)) {
            System.err.println("Ошибка: рекурсивный вызов скрипта '" + fileName + "'!");
            return;
        }

        activeScripts.add(fileName);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileName), "UTF-8"))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                //!!! Для insert в скрипте
                if (line.startsWith("insert")) {
                    String[] tokens = line.split("\\s+");
                    if (tokens.length >= 15) {
                        try {
                            cm.insert(parseLabWorkFromTokens(tokens));
                            continue;  // ← переходим к следующей строке, не вызывая execute()
                        } catch (Exception e) {
                            System.err.println("Ошибка парсинга insert: " + e.getMessage());
                            continue;
                        }
                    } else {
                        System.err.println("insert в скрипте требует 15 параметров. Получено: " + tokens.length);
                        continue;
                    }
                }
                // Все остальные команды:
                String[] parts = line.split("\\s+", 2);
                String cmd = parts[0].toLowerCase();
                String args = parts.length > 1 ? parts[1] : "";
                execute(cmd, args, mainScanner);
            }
            System.out.println("Обработка скрипта '" + fileName + "' завершена");

        } catch (FileNotFoundException e) {
            System.err.println("Файл скрипта не найден: " + fileName);
        } catch (IOException e) {
            System.err.println("Ошибка чтения скрипта: " + e.getMessage());
        } finally {
            //  Обязательно удаляем файл из множества, даже если была ошибка
            activeScripts.remove(fileName);
        }
    }


    /**
     * Создаёт объект LabWork из параметров скрипта.
     * Вызывается только при выполнении скрипта
     *
     * Пример:
     * insert null Лаба1 100 200 4.5 3.0 EASY Мария 123456 GREEN RED 55 37.6 150
     */
    private LabWork parseLabWorkFromTokens(String[] tokens) {

        // Название и координаты:

        // Имя лабораторной работы (индекс 2, потому что 0=insert, 1=null)
        String labName = tokens[2];

        // Координаты работы (индексы 3 и 4)
        int coordX = Integer.parseInt(tokens[3]);
        int coordY = Integer.parseInt(tokens[4]);
        Coordinates coords = new Coordinates(coordX, coordY);

        //Числовые поля с возможными null ===

        // Минимальный балл: может быть "null" или пустым → тогда null
        Double minPoint = null;
        if (!tokens[5].equals("null") && !tokens[5].isEmpty()) {
            minPoint = Double.parseDouble(tokens[5]);
        }

        // Минимум личных качеств: всегда есть, не может быть null
        double personalQualities = Double.parseDouble(tokens[6]);

        // Сложность: переводим строку в enum
        Difficulty difficulty = Difficulty.valueOf(tokens[7].toUpperCase());

        //Создаём автора

        // Имя автора (обязательное поле)
        String authorName = tokens[8];

        // Паспорт: может быть "null" или пустым
        String passport = null;
        if (!tokens[9].equals("null") && !tokens[9].isEmpty()) {
            passport = tokens[9];
        }

        // Цвет глаз: может быть "null" или пустым
        Color eyeColor = null;
        if (!tokens[10].equals("null") && !tokens[10].isEmpty()) {
            eyeColor = Color.valueOf(tokens[10].toUpperCase());
        }

        // Цвет волос: может быть "null" или пустым
        Color hairColor = null;
        if (!tokens[11].equals("null") && !tokens[11].isEmpty()) {
            hairColor = Color.valueOf(tokens[11].toUpperCase());
        }

        // Местоположение автора (может отсутствовать)

        Location location = null;

        // Проверяем, есть ли данные для местоположения (индексы 12, 13, 14)
        boolean hasLocation = !tokens[12].equals("null") && !tokens[13].equals("null");

        if (hasLocation) {
            long locX = Long.parseLong(tokens[12]);
            double locY = Double.parseDouble(tokens[13]);

            // Z-координата может быть "null" или пустой
            Integer locZ = null;
            if (!tokens[14].equals("null") && !tokens[14].isEmpty()) {
                locZ = Integer.parseInt(tokens[14]);
            }

            location = new Location(locX, locY, locZ);
        }

        // Собираем автора из всех полей
        Person author = new Person(authorName, passport, eyeColor, hairColor, location);

        // Создаём и возвращаем LabWork
        // id=0, потому что CollectionManager сам сгенерирует уникальный id
        return new LabWork(0, labName, coords, minPoint, personalQualities, difficulty, author);
    }
}