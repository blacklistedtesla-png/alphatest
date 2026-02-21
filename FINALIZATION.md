# Финализация проекта

## Что было сделано

1. **Клонирование проекта** — исходный код Android-приложения `qa-mobile` (Alfa-Test) был склонирован и настроен для сборки на Windows.

2. **Настройка окружения сборки** — обновлены пути к Android SDK в `local.properties`, добавлен Gradle Wrapper (7.4.2) для совместимости с AGP 7.2.1, исправлена синтаксическая ошибка в `LoginUseCase.kt`.

3. **Сборка APK** — приложение успешно собрано командой `gradlew assembleDebug`.

4. **Проект автотестов** — создан Maven-проект с Appium-тестами в директории `tests/`.

5. **Прогон тестов** — все 19 тестов успешно пройдены на эмуляторе Android (API 36.1).

## Структура проекта

```
alphatest/
├── app/                          — исходный код Android-приложения
│   ├── src/main/                 — код приложения (Kotlin)
│   └── build.gradle              — конфигурация сборки модуля
├── tests/                        — проект автотестов (Maven + Java)
│   ├── pom.xml                   — зависимости (Appium, TestNG, Allure)
│   ├── README.md                 — инструкции по запуску тестов
│   └── src/test/java/com/alfabank/qapp/
│       ├── base/                 — базовые классы (драйвер, setup/teardown)
│       ├── pages/                — Page Object модели
│       ├── tests/                — тестовые классы
│       └── utils/                — утилиты (RegexHelper)
├── build.gradle                  — корневая конфигурация Gradle
├── gradlew / gradlew.bat         — Gradle Wrapper
├── README.md                     — описание задания
└── FINALIZATION.md               — данный документ
```

## Как запустить тесты

### Предварительные требования
- JDK 17
- Maven 3.x
- Appium Server (`npm install -g appium`) + UiAutomator2 драйвер (`appium driver install uiautomator2`)
- Android SDK с platform-tools и emulator
- Android-эмулятор или реальное устройство

### Запуск на эмуляторе

1. **Посмотреть доступные эмуляторы:**
   ```bash
   emulator -list-avds
   ```

2. **Запустить эмулятор** (подставить имя из списка выше):
   ```bash
   emulator -avd <ИМЯ_ИЗ_СПИСКА>
   ```
   Или без окна (headless, для CI/автоматизации):
   ```bash
   emulator -avd <ИМЯ_ИЗ_СПИСКА> -no-window -no-audio
   ```

3. **Дождаться загрузки и установить APK:**
   ```bash
   adb wait-for-device
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

4. **Запустить Appium-сервер:**
   ```bash
   appium
   ```

5. **Запустить тесты:**
   ```bash
   mvn test -f tests/pom.xml
   ```

### Запуск на реальном устройстве

1. **Включить режим разработчика** на устройстве (Настройки → О телефоне → 7 раз нажать на «Номер сборки»).

2. **Включить отладку по USB** (Настройки → Для разработчиков → Отладка по USB).

3. **Подключить устройство** по USB и подтвердить разрешение на отладку.

4. **Проверить подключение:**
   ```bash
   adb devices
   ```
   Устройство должно быть в состоянии `device` (не `unauthorized`).

5. **Установить APK:**
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

6. **Если подключено несколько устройств**, указать нужное через параметр:
   ```bash
   mvn test -f tests/pom.xml -Dudid=СЕРИЙНЫЙ_НОМЕР
   ```
   Серийный номер можно получить из `adb devices`. Если подключено одно устройство, параметр можно не указывать.

7. **Запустить Appium и тесты:**
   ```bash
   appium
   mvn test -f tests/pom.xml
   ```

### Запуск отдельных тестов

```bash
# Все тесты
mvn test -f tests/pom.xml

# Конкретный класс
mvn test -f tests/pom.xml -Dtest=LoginPositiveTest

# Конкретный метод
mvn test -f tests/pom.xml -Dtest=LoginPositiveTest#testSuccessfulLogin
```

## Покрытие тестами

### Обязательные тесты (5 шт.)
| # | Тест | Класс | Метод |
|---|------|-------|-------|
| 1 | Успешная авторизация (Login/Password) | `LoginPositiveTest` | `testSuccessfulLogin` |
| 2 | Неверный пароль | `LoginNegativeTest` | `testLoginWithWrongPassword` |
| 3 | Неверный логин | `LoginNegativeTest` | `testLoginWithWrongUsername` |
| 4 | Пустые поля | `LoginFieldValidationTest` | `testLoginWithEmptyFields` |
| 5 | Максимальная длина поля (50 символов) | `LoginFieldValidationTest` | `testLoginWithMaxLengthUsername` |

### Дополнительные тесты
| # | Тест | Класс | Метод |
|---|------|-------|-------|
| 6 | Авторизация через XPath-локаторы | `LoginPositiveTest` | `testSuccessfulLoginViaXPath` |
| 7 | Оба поля неверные | `LoginNegativeTest` | `testLoginWithBothWrongCredentials` |
| 8 | Спецсимволы в полях | `LoginNegativeTest` | `testLoginWithSpecialCharacters` |
| 9 | Только пробелы | `LoginNegativeTest` | `testLoginWithWhitespaceOnly` |
| 10 | Превышение длины пароля | `LoginFieldValidationTest` | `testLoginWithMaxLengthPassword` |
| 11 | Пустой логин | `LoginFieldValidationTest` | `testLoginWithEmptyUsername` |
| 12 | Пустой пароль | `LoginFieldValidationTest` | `testLoginWithEmptyPassword` |
| 13 | Граничное значение (ровно 50 символов) | `LoginFieldValidationTest` | `testLoginWithExactMaxLength` |
| 14 | Наличие UI-элементов | `LoginUITest` | `testLoginScreenElementsPresence` |
| 15 | Текст заголовка | `LoginUITest` | `testLoginScreenTitle` |
| 16 | Поиск элемента через XPath | `LoginUITest` | `testTitleFoundByXPath` |
| 17 | Маскировка пароля | `LoginUITest` | `testPasswordFieldIsMasked` |
| 18 | Кнопка входа активна | `LoginUITest` | `testLoginButtonEnabled` |

## Технические решения

- **Page Object Pattern** — разделение логики работы с UI и тестовой логики
- **XPath и CSS-style локаторы** — оба типа локаторов продемонстрированы в `LoginPage`
- **Explicit Waits** — для обработки асинхронных операций авторизации (ожидание ошибки, ожидание перехода на главный экран)
- **Регулярные выражения** — класс `RegexHelper` для валидации форматов полей и текста ошибок
- **Allure** — зависимость для отчётов подключена в `pom.xml`
- **TestNG** — фреймворк для управления тестами
- **Appium Java Client 8.6.0** — клиент для взаимодействия с Android-приложением через UiAutomator2

## Рекомендации по приложению

По результатам тестирования выявлены следующие замечания и рекомендации:

1. **Синтаксическая ошибка в `LoginUseCase.kt`** (строка 25) — лишний символ `-` после `LoginResult.Error()`. Исправлено в рамках настройки сборки. В продакшен-коде такие ошибки должны выявляться линтером на этапе CI.

2. **Отсутствие валидации на стороне клиента** — приложение отправляет запрос на авторизацию даже с пустыми полями. Рекомендуется добавить проверку заполненности полей перед отправкой (например, деактивировать кнопку «Вход» при пустых полях).

3. **Асинхронная обработка ошибок** — сообщение об ошибке «Введены неверные данные» появляется с задержкой после нажатия кнопки «Вход». Для улучшения UX рекомендуется показывать индикатор загрузки на время проверки, что уже частично реализовано (элемент `loader`), но его поведение стоит проверить.

4. **Отсутствие Gradle Wrapper** в репозитории — wrapper не был включён в исходный репозиторий, что затрудняет первичную сборку проекта. Рекомендуется всегда включать `gradle-wrapper.jar` и `gradle-wrapper.properties` в систему контроля версий.

5. **Чувствительные данные** — файлы `local.properties` с путями к SDK включены в репозиторий. Рекомендуется добавить их в `.gitignore`.

6. **Совместимость SDK** — проект использует `compileSdk 32` и `targetSdk 32`. Для публикации в Google Play потребуется обновление до актуальной версии API.

## Примечание

Приложение является тренировочным и может содержать намеренные дефекты (см. README.md). Тесты написаны с учётом этого — они автоматизированы до момента возникновения блокирующей ошибки, чтобы после исправления дефекта тесты можно было быстро доработать.
