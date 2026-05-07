/**
 * Перечисление уровней сложности.
 */
public enum Difficulty {
    VERY_EASY, EASY, HARD, VERY_HARD, IMPOSSIBLE;

    /**
     * Вывод доступных значений для пользователя.
     */
    public static void printAvailable() {
        System.out.print("Доступные значения: ");
        for (Difficulty d : values()) {
            System.out.print(d.name() + " ");
        }
        System.out.println();
    }
}