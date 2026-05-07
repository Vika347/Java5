import java.time.ZonedDateTime;

/**
 * Класс лабораторной работы.
 * Реализует Comparable для сортировки по умолчанию (по id).
 */
public class LabWork implements Comparable<LabWork> {

    // ПОЛЯ:

    // Уникальный идентификатор  (генерируется автоматически)
    private long id;

    // Название работы (не может быть пустым)
    private String name;

    // Координаты (не могут быть null)
    private Coordinates coordinates;

    // Дата создания (генерируется автоматически, не может быть null)
    private ZonedDateTime creationDate;

    // Минимальный балл (может быть null, но если есть — то > 0)
    private Double minimalPoint;

    // Минимум личных качеств (обязательно > 0)
    private double personalQualitiesMinimum;

    // Сложность (не может быть null)
    private Difficulty difficulty;

    // Автор (не может быть null)
    private Person author;

    // КОНСТРУКТОР:

    /**
     * Создаёт новую лабораторную работу.
     * Дата создания устанавливается автоматически.
     *
     * @param id уникальный идентификатор (> 0)
     * @param name название работы
     * @param coordinates координаты
     * @param minimalPoint минимальный балл (можно null)
     * @param personalQualitiesMinimum минимум личных качеств (> 0)
     * @param difficulty уровень сложности
     * @param author автор работы
     */
    public LabWork(long id, String name, Coordinates coordinates,
                   Double minimalPoint, double personalQualitiesMinimum,
                   Difficulty difficulty, Person author) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = ZonedDateTime.now();
        this.minimalPoint = minimalPoint;
        this.personalQualitiesMinimum = personalQualitiesMinimum;
        this.difficulty = difficulty;
        this.author = author;
    }

    // ГЕТТЕРЫ:

    public long getId() { return id; }
    public String getName() { return name; }
    public Coordinates getCoordinates() { return coordinates; }
    public ZonedDateTime getCreationDate() { return creationDate; }
    public Double getMinimalPoint() { return minimalPoint; }
    public double getPersonalQualitiesMinimum() { return personalQualitiesMinimum; }
    public Difficulty getDifficulty() { return difficulty; }
    public Person getAuthor() { return author; }

    //СЕТТЕРЫ :

    public void setName(String name) { this.name = name; }
    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }
    public void setMinimalPoint(Double minimalPoint) { this.minimalPoint = minimalPoint; }
    public void setPersonalQualitiesMinimum(double personalQualitiesMinimum) {
        this.personalQualitiesMinimum = personalQualitiesMinimum;
    }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    public void setAuthor(Person author) { this.author = author; }



    // Comparable: сортировка по умолчанию

    /**
     * Сравнивает две лабораторные работы по id.
     * Нужно для сортировки коллекции.
     */
    @Override
    public int compareTo(LabWork other) {
        return Long.compare(this.id, other.id);
    }

    // toString() для вывода

    @Override
    public String toString() {
        String minPoint = (minimalPoint == null) ? "не задан" : minimalPoint.toString();

        return "LabWork{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", minimalPoint=" + minPoint +
                ", personalQualitiesMinimum=" + personalQualitiesMinimum +
                ", difficulty=" + difficulty +
                ", author=" + author +
                '}';
    }

    // === Методы для работы с файлом (CSV) ===

    public String toCsv() {
        // Превращаем возможные null в пустые строки
        String minPointStr = (minimalPoint == null) ? "" : minimalPoint.toString();
        String passportStr = (author.getPassportID() == null) ? "" : author.getPassportID();
        String eyeStr = (author.getEyeColor() == null) ? "" : author.getEyeColor().name();
        String hairStr = (author.getHairColor() == null) ? "" : author.getHairColor().name();

        // Обрабатываем местоположение
        String locX = "", locY = "", locZ = "";
        if (author.getLocation() != null) {
            locX = String.valueOf(author.getLocation().getX());
            locY = String.valueOf(author.getLocation().getY());
            locZ = (author.getLocation().getZ() == null) ? "" : author.getLocation().getZ().toString();
        }

        // Собираем строку через +
        return id + ";" +
                name + ";" +
                coordinates.getX() + ";" + coordinates.getY() + ";" +
                creationDate.toString() + ";" +
                minPointStr + ";" +
                personalQualitiesMinimum + ";" +
                difficulty.name() + ";" +
                author.getName() + ";" +
                passportStr + ";" +
                eyeStr + ";" +
                hairStr + ";" +
                locX + ";" + locY + ";" + locZ;
    }

    public static LabWork fromCsv(String csv) {
        String[] parts = csv.split(";", -1);  // -1 сохраняет пустые поля в конце

        // Простые поля
        long id = Long.parseLong(parts[0]);
        String name = parts[1];

        // Координаты
        Coordinates coords = new Coordinates(
                Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3])
        );

        // Минимальный балл (может быть пустым)
        Double minPoint = null;
        if (!parts[5].isEmpty()) {
            minPoint = Double.parseDouble(parts[5]);
        }

        // Местоположение (если есть данные)
        Location loc = null;
        if (!parts[12].isEmpty() && !parts[13].isEmpty()) {
            long x = Long.parseLong(parts[12]);
            double y = Double.parseDouble(parts[13]);
            Integer z = parts[14].isEmpty() ? null : Integer.parseInt(parts[14]);
            loc = new Location(x, y, z);
        }

        // Автор
        Person author = new Person(
                parts[8],
                parts[9].isEmpty() ? null : parts[9],
                parts[10].isEmpty() ? null : Color.valueOf(parts[10]),
                parts[11].isEmpty() ? null : Color.valueOf(parts[11]),
                loc
        );

        // Создаём и возвращаем LabWork
        return new LabWork(
                id, name, coords, minPoint,
                Double.parseDouble(parts[6]),
                Difficulty.valueOf(parts[7]),
                author
        );
    }


    /**
     * Метод для восстановления даты при обновлении элемента.
     * (Для команды update)
     */
    public void setCreationDate(java.time.ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }
}