/**
 * Класс для хранения местоположения.
 */
public class Location {

    // ПОЛЯ:
    private long x;
    private double y;
    private Integer z; // Может быть null

    // КОНСТРУКТОР:
    /**
     * Создаёт новое местоположение с заданными координатами.
     * @param x координата по оси X
     * @param y координата по оси Y
     * @param z координата по оси Z (можно null)
     */
    public Location(long x, double y, Integer z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // ГЕТТЕРЫ:
    /** @return координата X */
    public long getX() { return x; }

    /** @return координата Y */
    public double getY() { return y; }

    /** @return координата Z (может быть null) */
    public Integer getZ() { return z; }

    // toString() для вывода:
    @Override
    public String toString() {
        String zText = (z == null) ? "нет" : z.toString();
        return "Location{x=" + x + ", y=" + y + ", z=" + zText + "}";
    }

}