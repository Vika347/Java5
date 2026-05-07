import java.io.*;
import java.time.ZonedDateTime;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Set;

/**
 * Менеджер коллекции: загрузка, сохранение, базовые операции.
 */
public class CollectionManager {

    private Hashtable<Long, LabWork> collection;
    private String fileName;
    private ZonedDateTime initializationDate;
    private long nextId = 1;
    private Set<String> passportIDs = new HashSet<>();

    // Проверка уникальности passportID
    private boolean isPassportUnique(String passportID) {
        if (passportID == null) return true; // null может повторяться
        return !passportIDs.contains(passportID);
    }

    private void addPassport(String passportID) {
        if (passportID != null) passportIDs.add(passportID);
    }

    private void removePassport(String passportID) {
        if (passportID != null) passportIDs.remove(passportID);
    }

    // Обновить passportID при изменении элемента
    private void updatePassport(String oldPassport, String newPassport) {
        removePassport(oldPassport);
        addPassport(newPassport);
    }

    public CollectionManager() {
        this.collection = new Hashtable<>();
        this.fileName = System.getenv("LABWORK_FILE");
        if (fileName == null) {
            System.err.println("Ошибка: переменная окружения LABWORK_FILE не установлена!");
            System.exit(1);
        }
        this.initializationDate = ZonedDateTime.now();
        loadFromFile();
        updateNextId();
    }

    private void loadFromFile() {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
             BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                LabWork lw = LabWork.fromCsv(line);
                collection.put(lw.getId(), lw);
                // Добавляем passportID в множество для проверки уникальности
                addPassport(lw.getAuthor().getPassportID());
            }
            System.out.println("Загружено элементов: " + collection.size());
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден, создаётся новая коллекция");
        } catch (IOException e) {
            System.err.println("Ошибка чтения: " + e.getMessage());
        }
    }

    public boolean saveToFile() {
        try (FileWriter writer = new FileWriter(fileName)) {
            for (LabWork lw : collection.values()) {
                writer.write(lw.toCsv() + "\n");
            }
            System.out.println("Коллекция сохранена");
            return true;
        } catch (IOException e) {
            System.err.println("Ошибка записи: " + e.getMessage());
            return false;
        }
    }

    private void updateNextId() {
        for (Long id : collection.keySet()) {
            if (id >= nextId) nextId = id + 1;
        }
    }

    public long generateId() { return nextId++; }

    // БАЗОВЫЕ ОПЕРАЦИИ :

    public void insert(LabWork element) {
        // Проверка уникальности passportID
        String passportID = element.getAuthor().getPassportID();
        if (!isPassportUnique(passportID)) {
            System.out.println("Ошибка: Паспорт ID '" + passportID + "' уже существует!");
            return;
        }

        long id = generateId();
        LabWork newLw = new LabWork(id, element.getName(), element.getCoordinates(),
                element.getMinimalPoint(), element.getPersonalQualitiesMinimum(),
                element.getDifficulty(), element.getAuthor());
        collection.put(id, newLw);
        addPassport(passportID);
        System.out.println("Добавлен элемент с id=" + id);
    }

    public boolean update(long id, LabWork newElement) {
        if (collection.containsKey(id)) {
            LabWork old = collection.get(id);
            String oldPassport = old.getAuthor().getPassportID();
            String newPassport = newElement.getAuthor().getPassportID();

            // Проверка уникальности passportID (если изменился)
            if (newPassport != null && !newPassport.equals(oldPassport)) {
                if (!isPassportUnique(newPassport)) {
                    System.out.println("Ошибка: Паспорт ID '" + newPassport + "' уже существует!");
                    return false;
                }
            }

            LabWork updated = new LabWork(id, newElement.getName(), newElement.getCoordinates(),
                    newElement.getMinimalPoint(), newElement.getPersonalQualitiesMinimum(),
                    newElement.getDifficulty(), newElement.getAuthor());
            updated.setCreationDate(old.getCreationDate());
            collection.put(id, updated);

            // Обновляем множество passportID
            if (newPassport != null && !newPassport.equals(oldPassport)) {
                updatePassport(oldPassport, newPassport);
            }

            System.out.println("Элемент обновлён");
            return true;
        }
        System.out.println("Элемент с id=" + id + " не найден");
        return false;
    }

    public boolean removeKey(long key) {
        LabWork removed = collection.remove(key);
        if (removed != null) {
            removePassport(removed.getAuthor().getPassportID());
            System.out.println("✓ Элемент удалён");
            return true;
        }
        System.out.println("Элемент не найден");
        return false;
    }

    public void clear() {
        collection.clear();
        passportIDs.clear();
        System.out.println("Коллекция очищена");
    }

    public void show() {
        if (collection.isEmpty()) {
            System.out.println("Коллекция пуста");
            return;
        }
        for (LabWork lw : collection.values()) {
            System.out.println(lw);
        }
    }

    public void info() {
        System.out.println("Информация о коллекции:");
        System.out.println("  Тип: Hashtable<Long, LabWork>");
        System.out.println("  Дата инициализации: " + initializationDate);
        System.out.println("  Количество элементов: " + collection.size());
        System.out.println("  Файл данных: " + fileName);
    }

    // ДОПОЛНИТЕЛЬНЫЕ КОМАНДЫ :

    public void removeGreater(LabWork threshold) {
        // Собираем ключи элементов, которые нужно удалить
        Set<Long> keysToRemove = new HashSet<>();
        for (LabWork lw : collection.values()) {
            if (lw.compareTo(threshold) > 0) {
                keysToRemove.add(lw.getId());
            }
        }
        // Удаляем и очищаем passportID
        for (Long key : keysToRemove) {
            LabWork removed = collection.remove(key);
            if (removed != null) {
                removePassport(removed.getAuthor().getPassportID());
            }
        }
        System.out.println("Элементы, превышающие заданный, удалены (" + keysToRemove.size() + " шт.)");
    }

    public boolean replaceIfLower(long key, LabWork newElement) {
        if (collection.containsKey(key)) {
            LabWork old = collection.get(key);
            if (newElement.compareTo(old) < 0) {
                return update(key, newElement);
            }
            System.out.println("Новое значение не меньше старого, замена отменена");
        } else {
            System.out.println("Элемент не найден");
        }
        return false;
    }

    public void removeGreaterKey(long key) {
        Set<Long> keysToRemove = new HashSet<>();
        for (Long k : collection.keySet()) {
            if (k > key) {
                keysToRemove.add(k);
            }
        }
        for (Long k : keysToRemove) {
            LabWork removed = collection.remove(k);
            if (removed != null) {
                removePassport(removed.getAuthor().getPassportID());
            }
        }
        System.out.println("Элементы с ключом > " + key + " удалены (" + keysToRemove.size() + " шт.)");
    }

    public void countByAuthor(String authorName) {
        long count = collection.values().stream()
                .filter(lw -> lw.getAuthor().getName().equals(authorName))
                .count();
        System.out.println("Элементов с автором '" + authorName + "': " + count);
    }

    public void filterByDifficulty(Difficulty diff) {
        boolean found = false;
        for (LabWork lw : collection.values()) {
            if (lw.getDifficulty() == diff) {
                System.out.println(lw);
                found = true;
            }
        }
        if (!found) System.out.println("Нет элементов с такой сложностью");
    }

    public void filterLessThanAuthor(String authorName) {
        boolean found = false;
        for (LabWork lw : collection.values()) {
            if (lw.getAuthor().getName().compareTo(authorName) < 0) {
                System.out.println(lw);
                found = true;
            }
        }
        if (!found) System.out.println("Нет авторов, чьё имя меньше указанного");
    }

    public Hashtable<Long, LabWork> getCollection() {
        return collection;
    }
}