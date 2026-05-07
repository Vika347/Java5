/**
 * Класс для хранения координат лабораторной работы.
 */
public class Coordinates {

    // ПОЛЯ:
    private int x;  // Координата по оси X
    private int y;  // Координата по оси Y

    // КОНСТРУКТОР:
    /**
     * Создаёт новые координаты.
     * @param x координата X
     * @param y координата Y
     */
    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // ГЕТТЕРЫ :
    /** @return координата X */
    public int getX() { return x; }

    /** @return координата Y */
    public int getY() { return y; }



    @Override
    public String toString() {
        return "Coordinates{x=" + x + ", y=" + y + "}";
    }
}