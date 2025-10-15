# PlanetsNASA (Kotlin, Jetpack Compose)

Небольшое приложение для просмотра карточек планет/космических объектов из NASA Images API.

## Стек
- **Kotlin 1.9.25**, Coroutines/Flow
- **Jetpack Compose** + **Material 3**
- **Navigation-Compose**
- **Coil** (загрузка изображений)
- **Retrofit2 + OkHttp** (сеть) + Gson
- **Hilt** (DI)
- **Room** (кэш страниц списка)
- **DataStore** (под настройки, если потребуется)
- Pull-to-Refresh (**Material pullRefresh**)
- Min SDK 24, Target/Compile SDK 36

## Архитектура
Чистая слоистая структура:
- **data** — источники данных (Remote: `NasaImageApi`, Local: Room), имплементация репозитория.
- **domain** — модели и use-case'ы (`GetPlanetsPageUseCase`, `GetPlanetByIdUseCase`), интерфейс `PlanetRepository`.
- **ui (presentation)** — экраны Compose, `ViewModel` (MVVM), преобразование domain → UI-моделей.
- **di** — Hilt-модули (`NetworkModule`, `RepositoryModule`, `UseCaseModule`, `DatabaseModule`).

Основная схема: UI → ViewModel → UseCase → Repository → (Local/Remote) → back.  
UI наблюдает `StateFlow` и реагирует на состояния: `Loading / Content / Empty / Error`.

## Фичи (по ТЗ)
- Список карточек с изображением и названием.
- Детальный экран: полноэкранная картинка, заголовок, дата, описание.
- Навигация назад, **Share** и **Save** (через `DownloadManager`).
- Обработки состояний (loading/empty/error).
- Материальный Pull-to-Refresh.

## Дополнительно докручено (сверх базового ТЗ)
- **Пагинация (infinite scroll)**: догрузка страниц при прокрутке вниз.
- **Кэш списка в Room** (offline-first): при открытии берём страницу из БД, затем обновляем; порядок сохраняется благодаря полю `indexInPage`.
- **Hilt** интеграция для DI по всем слоям.
- **UX-полиш**:
    - “Мягкий” рефреш — индикатор виден не менее 400 мс, чтобы не «мигал» на быстром кэше.
    - Единые строки в `strings.xml`, контент-описания для доступности.
    - Карточный лэйаут, аккуратная детальная «простыня» с градиентом на фото.

## Как запустить
1. Android Studio Koala+; JDK 17 (в проекте включён десугаринг для java.time).
2. `gradle.properties` / `settings.gradle` — по умолчанию.
3. Запустить `app` на эмуляторе API 24+.
4. Интернет-разрешение уже добавлено в `AndroidManifest.xml`.

NASA Images API публичный, ключ не требуется.

## Что внутри по файлам (основное)
- `data/remote/NasaImageApi.kt` — Retrofit API.
- `data/PlanetRepositoryImpl.kt` — работа с сетью и Room, приведение DTO → domain.
- `data/local/*` — Room (`AppDatabase`, `PlanetDao`, `PlanetEntity`).
- `domain/model/*` — `Planet`, `PlanetDetail`.
- `domain/usecase/*` — `GetPlanetsPageUseCase`, `GetPlanetByIdUseCase`.
- `ui/list/*` — список: экран, карточка, ViewModel.
- `ui/detail/*` — детальный экран и действия `share/save`.
- `di/*` — Hilt-модули.

## Известные допущения
- В кэше хранится только список (страницы). Детальная карточка берётся из сети (можно расширить Room).
- Схема Room пересоздаётся через `fallbackToDestructiveMigration()` (для ТЗ ок).
- Нет локализаций, кроме ru (легко добавить `values-en`).

## Дальнейшие улучшения (roadmap)
- Room-кэш для деталей + prefetch при клике.
- Error-Snackbar для догрузки, лейбл «Конец списка».
- Скролл-to-top FAB.
- Paging 3 вместо ручной пагинации.
- Мульти-язычные ресурсы, иконка и скриншоты в README.
