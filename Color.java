/**
 * Перечисление возможных цветов.
 */
public enum Color {
    GREEN,
    RED,
    ORANGE,
    WHITE,
    BLACK,
    YELLOW;

    /**
     * Вывод доступных значений для пользователя.
     */
    public static void printAvailable() {
        System.out.print("Доступные значения: ");
        for (Color c : values()) {
            System.out.print(c.name() + " ");
        }
        System.out.println();
    }
}