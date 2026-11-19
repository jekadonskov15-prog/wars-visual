# Wars Visual (Fabric 1.21.11, client-only)

Меню: Right Shift (повторно - закрыть). Клавишу меню можно сменить: .bind add gui <клавиша>

В меню: выбор функции, ON/OFF, кнопка бинда (нажми кнопку, потом клавишу; Backspace - очистить),
слайдеры для View model и Swing, смена темы.

Команды в чате (не уходят на сервер):
  .bind add <модуль> <клавиша>   .bind add keystrokes R
  .bind del <модуль>             .bind list
  .toggle <модуль>   .theme <имя>   .modules
Клавиши: A-Z, 0-9, F1-F12, KP0-KP9, SPACE, TAB, LSHIFT/RSHIFT, LCTRL/RCTRL, LALT/RALT, стрелки, GRAVE и т.д.
Модули: watermark info list keystrokes armor effects viewmodel swing ambience optimizer
ambience: цвет неба (Galaxy, Sunset, Aqua, Crimson, Toxic), время суток, погода. Только визуал.
optimizer: меньше дальность отрисовки/симуляции, минимум частиц, без теней. При выключении всё возвращается.

Сборка: JDK 21, `gradle wrapper` (или шаблон fabricmc.net/develop), `./gradlew build`.
Конфиг: config/warsvisual.json

## Получить .jar без установки Gradle
Залей папку в пустой репозиторий на GitHub: Actions -> build -> Artifacts -> wars-visual-jar.
Берёшь jar без `-sources` и кладёшь в mods (нужен Fabric Loader + Fabric API для 1.21.11).
