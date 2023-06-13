package DataBusAndMicroservices;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        InMemoryCache inMemoryCache = new InMemoryCache();
        String menu = """
                      1. Добавить запись
                      2. Удалить запись
                      3. Изменить запись
                      4. Получить записи по полям
                      0. Выйти
                       """;
        try (Scanner scanner = new Scanner(System.in)) {
            String next = "";
            do {
                System.out.println(menu);
                next = scanner.next();
                switch (next) {
                    case "1" -> addRecord(inMemoryCache, scanner);
                    case "2" -> removeRecord(inMemoryCache, scanner);
                    case "3" -> updateRecord(inMemoryCache, scanner);
                    case "4" -> getRecords(inMemoryCache, scanner);
                }
            } while (!next.equals("0"));
        }
    }

    private static void getRecords(InMemoryCache inMemoryCache, Scanner scanner) {
        System.out.println("Введите название поля из перечисленных - account, name и value");
        String fieldName = scanner.next();
        System.out.println("Введите значение");
        switch (fieldName) {
            case "account" -> {
                long account = scanner.nextLong();
                UserData userData = inMemoryCache.getByAccount(account);
                if (userData != null) {
                    System.out.println(userData);
                } else {
                    System.out.println("Запись не найдена");
                }
            }
            case "name" -> {
                String name = scanner.next();
                List<UserData> allByName = inMemoryCache.getAllByName(name);
                if (allByName == null) {
                    System.out.println("Записи не найдены");
                } else {
                    allByName.forEach(System.out::println);
                }
            }
            case "value" -> {
                double value = scanner.nextDouble();
                List<UserData> allByValue = inMemoryCache.getAllByValue(value);
                if (allByValue == null) {
                    System.out.println("Записи не найдены");
                } else {
                    allByValue.forEach(System.out::println);
                }
            }
        }

    }

    private static void updateRecord(InMemoryCache inMemoryCache, Scanner scanner) {
        System.out.println("Введите account изменяемой записи");
        long account = scanner.nextLong();
        UserData userData = inMemoryCache.getByAccount(account);
        if (userData == null) {
            System.out.println("Не найдена информация по данному account");
            return;
        }
        UserDataWrapper userDataWrapper = new UserDataWrapper(userData);
        UserData userDataUpdated = new UserData(userData.getAccount(), userData.getNameBytes(), userData.getValue());
        System.out.println("Введите новое значение account или оставьте поле неизменным оставив ввод пустым");
        System.out.println(userData.getAccount());
        scanner.nextLine();
        String s = scanner.nextLine();
        if (!s.isEmpty()) {
            long account1 = Long.parseLong(s);
            userDataUpdated.setAccount(account1);
            if (inMemoryCache.getByAccount(account1) != null) {
                System.out.println("Невозможно добавить запись с уже существующим account");
                return;
            }
        }

        System.out.println("Введите новое значение name или оставьте поле неизменным оставив ввод пустым");
        System.out.println(userDataWrapper.getName());
        s = scanner.nextLine();
        if (!s.isEmpty()) {
            userDataUpdated.setNameBytes(s.getBytes(StandardCharsets.UTF_8));
        }
        System.out.println("Введите новое значение value или оставьте поле неизменным оставив ввод пустым");
        System.out.println(userData.getValue());
        String s1 = scanner.nextLine();
        if (!s1.isEmpty()) {
            userDataUpdated.setValue(Double.parseDouble(s1));
        }
        inMemoryCache.update(account, userDataUpdated);
    }

    private static void removeRecord(InMemoryCache inMemoryCache, Scanner scanner) {
        System.out.println("Введите account");
        long account = scanner.nextLong();
        inMemoryCache.delete(account);
    }

    private static void addRecord(InMemoryCache inMemoryCache, Scanner scanner) {
        System.out.println("Введите account");
        long account = scanner.nextLong();
        System.out.println("Введите name");
        String name = scanner.next();
        System.out.println("Введите value");
        double value = scanner.nextDouble();
        if (inMemoryCache.getByAccount(account) != null) {
            System.out.println("Невозможно добавить запись с уже существующим account");
            return;
        }
        inMemoryCache.add(new UserData(account, name.getBytes(StandardCharsets.UTF_8), value));
    }
}
