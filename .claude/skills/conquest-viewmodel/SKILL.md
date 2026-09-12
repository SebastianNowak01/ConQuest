---
name: conquest-viewmodel
description: How ConQuest ViewModels are written, registered and tested — AndroidViewModel with injected DAOs, StateFlow exposure, viewModelScope writes, AppViewModelProvider registration, and the Robolectric + in-memory Room test template. Use when adding or changing anything in viewmodel/, moving logic out of a screen, or writing a ViewModel test.
---

# ViewModels in ConQuest

Package: `app/src/main/java/com/maeldev/conquest/viewmodel/`.
`TaskViewModel.kt` + `app/src/test/java/com/maeldev/conquest/viewmodel/TaskViewModelTest.kt` are
the reference pair.

## Shape

```kotlin
class TaskViewModel(
    application: Application,
    private val taskDao: CosplayTaskDao,
    private val cosplayDao: CosplayDao,
) : AndroidViewModel(application)
```

`AndroidViewModel` because reminders and content resolvers need the `Application`. DAOs arrive
through the constructor — never reach for the database inside the ViewModel.

## Exposing state

Cold Room `Flow`s become hot `StateFlow`s:

```kotlin
val allTasks: StateFlow<List<CosplayTask>> =
    taskDao.getAllTasks().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
```

For a query parameterised by an id, hold the id in a `MutableStateFlow` and `flatMapLatest` so
the query re-runs when it changes:

```kotlin
private val _taskCosplayId = MutableStateFlow<Int?>(0)

@OptIn(ExperimentalCoroutinesApi::class)
val tasks: StateFlow<List<CosplayTask>> =
    _taskCosplayId.filterNotNull()
        .flatMapLatest { id -> taskDao.getTasksForCosplay(id) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
```

Single-row reads may return the raw `Flow` (`getTaskById`); the screen collects it.

## Writes

Every mutation runs in `viewModelScope.launch` — Room suspend functions keep it off the main
thread. Two project-specific obligations inside a write:

- **Stats.** After inserting, updating or deleting child rows (tasks, elements), refresh the
  parent's denormalised counters: `cosplayDao.refreshStatsFor(cosplayId)` — it takes a single id
  or a `Set<Int>` (`data/dao/CosplayDaos.kt`). Delete paths must collect the affected cosplay ids
  *before* deleting (`taskDao.getCosplayIdsForTaskIdsOnce(ids)`).
- **Reminders.** Anything with an `alarm`/`date` goes through
  `ReminderScheduler.syncReminder(context, ReminderTarget(ReminderEntityType.TASK, id), alarm, date, message)`
  on insert/update, and `ReminderScheduler.cancelReminder(...)` on delete. A deleted row whose
  alarm was never cancelled still fires.

## Registration — do not skip

Add a branch to `AppViewModelProvider.kt`:

```kotlin
modelClass.isAssignableFrom(TaskViewModel::class.java) -> {
    TaskViewModel(application = application, taskDao = db.cosplayTaskDao(), cosplayDao = db.cosplayDao()) as T
}
```

Unregistered, `viewModel(factory = AppViewModelProvider.Factory)` throws
`IllegalArgumentException: Unknown ViewModel class` at runtime, not at compile time.

## Test template

Unit tests, not instrumented — Robolectric plus a real in-memory Room database:

```kotlin
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = ConQuestApplication::class)
class TaskViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Before fun setup() {
        application = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(application, CosplayDatabase::class.java)
            .allowMainThreadQueries()
            .setQueryExecutor { it.run() }
            .setTransactionExecutor { it.run() }
            .build()
        viewModel = TaskViewModel(application, db.cosplayTaskDao(), db.cosplayDao())
    }

    @After fun tearDown() = db.close()
}
```

`MainDispatcherRule` (`app/src/test/.../MainDispatcherRule.kt`) swaps in an
`UnconfinedTestDispatcher` so a `viewModelScope.launch` has already completed by the time the
call returns — that plus the synchronous executors is what makes assertions deterministic without
sleeping. Bodies use `runTest { }` and read results with `.first()`.

Cover at minimum: insert lands in the DB, update changes it, delete removes it, and any stats or
reminder side effect the method is responsible for.

## Then

Run the `verify-changes` gate.
