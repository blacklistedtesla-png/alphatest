# Appium Tests — Alfa-Test Android App

## Предварительные требования

- **JDK 17** — `java -version`
- **Maven 3.x** — `mvn -version`
- **Appium Server** — `appium -v`
- **UiAutomator2 драйвер** — `appium driver install uiautomator2`
- **Android Emulator** или реальное устройство с установленным APK

## Запуск на эмуляторе

Список доступных эмуляторов:
```bash
emulator -list-avds
```

Запуск (подставить имя из списка выше):
```bash
emulator -avd <ИМЯ_ИЗ_СПИСКА>
```

Запуск без окна (для CI/автоматизации):
```bash
emulator -avd <ИМЯ_ИЗ_СПИСКА> -no-window -no-audio
```

Дождаться загрузки:
```bash
adb wait-for-device
```

## Запуск на реальном устройстве

1. Включить режим разработчика и отладку по USB на устройстве.
2. Подключить устройство по USB, подтвердить разрешение на отладку.
3. Проверить подключение: `adb devices` — устройство должно быть `device`.
4. Если подключено несколько устройств, указать нужное через параметр:
   ```bash
   mvn test -f tests/pom.xml -Dudid=СЕРИЙНЫЙ_НОМЕР
   ```

## Запуск Appium Server

```bash
appium
```

Сервер запустится по умолчанию на `http://127.0.0.1:4723`

## Установка APK

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Запуск тестов

**Все тесты:**
```bash
mvn test -f tests/pom.xml
```

**Конкретный тестовый класс:**
```bash
mvn test -f tests/pom.xml -Dtest=LoginPositiveTest
mvn test -f tests/pom.xml -Dtest=LoginNegativeTest
mvn test -f tests/pom.xml -Dtest=LoginFieldValidationTest
mvn test -f tests/pom.xml -Dtest=LoginUITest
```

**Конкретный тестовый метод:**
```bash
mvn test -f tests/pom.xml -Dtest=LoginPositiveTest#testSuccessfulLogin
```

## Структура тестов

```
tests/src/test/java/com/alfabank/qapp/
├── base/
│   ├── BaseTest.java              — setup/teardown Appium драйвера
│   └── AppiumDriverManager.java   — управление драйвером, capabilites
├── pages/
│   ├── LoginPage.java             — Page Object экрана авторизации
│   └── MainPage.java              — Page Object главного экрана
├── tests/
│   ├── LoginPositiveTest.java     — позитивные тесты авторизации
│   ├── LoginNegativeTest.java     — негативные тесты авторизации
│   ├── LoginFieldValidationTest.java — валидация полей ввода
│   └── LoginUITest.java           — проверка UI элементов
└── utils/
    └── RegexHelper.java           — утилиты с регулярными выражениями
```
