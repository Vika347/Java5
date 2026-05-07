import java.util.Scanner;

/**
 * Класс для ввода данных с клавиатуры.
 */
public class InputReader {
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Ввод строки (не пустой).
     */
    public static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Строка не может быть пустой!");
        }
    }

    /**
     * Ввод строки (может быть пустой - возвращает null).
     */
    public static String readStringNullable(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? null : input;
    }

    /**
     * Ввод целого числа.
     */
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное целое число!");
            }
        }
    }

    /**
     * Ввод положительного double.
     */
    public static double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double val = Double.parseDouble(scanner.nextLine().trim());
                if (val > 0) return val;
                System.out.println("Значение должно быть больше 0!");
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное число!");
            }
        }
    }

    /**
     * Ввод положительного Double (может быть null).
     */
    public static Double readPositiveDoubleNullable(String prompt) {
        System.out.print(prompt + " (Enter для пропуска): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return null;
        while (true) {
            try {
                double val = Double.parseDouble(input);
                if (val > 0) return val;
                System.out.println("Значение должно быть больше 0!");
                return readPositiveDoubleNullable(prompt);
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное число!");
                return readPositiveDoubleNullable(prompt);
            }
        }
    }

    /**
     * Ввод enum-значения с подсказкой.
     */
    public static <T extends Enum<T>> T readEnum(String prompt, Class<T> enumClass) {
        System.out.print(prompt);
        // Вывод доступных значений
        System.out.print("[");
        for (T constant : enumClass.getEnumConstants()) {
            System.out.print(constant.name() + " ");
        }
        System.out.println("]");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.isEmpty()) return null;
            try {
                return Enum.valueOf(enumClass, input);
            } catch (IllegalArgumentException e) {
                System.out.println("Некорректное значение! Выберите из списка выше.");
            }
        }
    }

    /**
     * Ввод координат.
     */
    public static Coordinates readCoordinates() {
        System.out.println("Ввод координат:");
        int x = readInt("  x: ");
        int y = readInt("  y: ");
        return new Coordinates(x, y);
    }

    /**
     * Ввод местоположения.
     */
    public static Location readLocation() {
        System.out.println("Ввод местоположения:");
        long x = Long.parseLong(readString("  x (целое): "));
        double y = Double.parseDouble(readString("  y (дробное): "));
        Integer z = null;
        String zStr = readStringNullable("  z (Enter для null): ");
        if (zStr != null) z = Integer.parseInt(zStr);
        return new Location(x, y, z);
    }

    /**
     * Ввод автора.
     */
    public static Person readPerson() {
        System.out.println("Ввод автора:");
        String name = readString("  Имя: ");
        String passportID = readStringNullable("  Passport ID (Enter для null): ");
        Color eyeColor = readEnum("  Цвет глаз: ", Color.class);
        Color hairColor = readEnum("  Цвет волос: ", Color.class);

        //  Валидация ввода: только y или n
        System.out.print("  Добавить местоположение? (y/n): ");
        Location location = null;
        while (true) {
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y")) {
                location = readLocation();
                break;
            } else if (input.equals("n")) {
                location = null;
                break;
            } else {
                System.out.print("Введите 'y' или 'n': ");
            }
        }
        return new Person(name, passportID, eyeColor, hairColor, location);
    }

    /**
     * Ввод LabWork (без id и creationDate - они автогенерируются).
     */
    public static LabWork readLabWork() {
        System.out.println("Ввод лабораторной работы:");
        String name = readString("  Название: ");
        Coordinates coords = readCoordinates();
        Double minimalPoint = readPositiveDoubleNullable("  Минимальный балл");
        double pqMin = readPositiveDouble("  Минимум личных качеств: ");
        Difficulty difficulty = readEnum("  Сложность: ", Difficulty.class);
        Person author = readPerson();

        // Создаём с id=0, настоящий id сгенерирует CollectionManager
        return new LabWork(0, name, coords, minimalPoint, pqMin, difficulty, author);
    }
}