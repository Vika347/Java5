/**
 * Класс для хранения информации об авторе лабораторной работы.
 */
public class Person {

    // ПОЛЯ :
    private String name;        // Имя (не может быть null или пустым)
    private String passportID;  // Паспорт (может быть null)
    private Color eyeColor;     // Цвет глаз (может быть null)
    private Color hairColor;    // Цвет волос (может быть null)
    private Location location;  // Местоположение (может быть null)

    // КОНСТРУКТОР :
    /**
     * Создаёт автора.
     * @param name имя автора (обязательное)
     * @param passportID паспорт (можно null)
     * @param eyeColor цвет глаз (можно null)
     * @param hairColor цвет волос (можно null)
     * @param location местоположение (можно null)
     */
    public Person(String name, String passportID, Color eyeColor,
                  Color hairColor, Location location) {
        this.name = name;
        this.passportID = passportID;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.location = location;
    }

    // ГЕТТЕРЫ :

    /** @return имя автора */
    public String getName() { return name; }

    /** @return паспорт ID (может быть null) */
    public String getPassportID() { return passportID; }

    /** @return цвет глаз (может быть null) */
    public Color getEyeColor() { return eyeColor; }

    /** @return цвет волос (может быть null) */
    public Color getHairColor() { return hairColor; }

    /** @return местоположение (может быть null) */
    public Location getLocation() { return location; }


    // toString() для вывода:
    @Override
    public String toString() {
        // Для возможных null-значений
        String passport = (passportID == null) ? "нет паспорта" : passportID;
        String eyes = (eyeColor == null) ? "не указан" : eyeColor.name();
        String hair = (hairColor == null) ? "не указан" : hairColor.name();
        String loc = (location == null) ? "не указано" : location.toString();

        // Собираем итоговую строку
        return "Person{name='" + name + "', passport='" + passport +
                "', eyes=" + eyes + ", hair=" + hair + ", location=" + loc + "}";
    }
}
