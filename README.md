# rapidroid = rapid + android

Some structured concurrency design patterns I use in my Android projects.

### Import

```
repositories {
    maven { url "https://andob.io/repository/open_source" }
}
```

```
dependencies {
    implementation 'ro.andob.rapidroid:rapidroid-api:1.4.0'
    implementation 'ro.andob.rapidroid:rapidroid-core:1.4.0'
    implementation 'ro.andob.rapidroid:rapidroid-futures:1.4.0'
    implementation 'ro.andob.rapidroid:rapidroid-workflow:1.4.0'
    implementation 'ro.andob.rapidroid:rapidroid-actor:1.4.0'
}
```

### Threads

To start a thread:

```kotlin
Run.thread { doSomething() }
```

To start and join a thread:

```kotlin
val thread = Run.thread { doSomething() }
thread.join()
```

### Futures (Promises)

A future is simply a task which runs on a background thread pool, returns a result, then calls the onSuccess(result) callback or the onError(exception) callback on the UI thread.

```kotlin
fun calculateTheMeaningOfLife() : Int
{
    println("Complicated computation...")
    return 42
}

fun calculateTheMeaningOfLifeAsync() : Future<Int>
{
    return Run.future { calculateTheMeaningOfLife() }
}
```

```kotlin
calculateTheMeaningOfLifeAsync()
    .onSuccess { result -> view.showMeaningOfLife(result) }
    .onError { exception -> view.showError(exception) }
```

Of course this could be chained:

```kotlin
view.showLoadingAnimation()

Run.future { calculateTheMeaningOfLife() }
    .onAny { view.hideLoadingAnimation() }
    .onError { ex -> view.showError(ex) }
    .onSuccess { result -> view.showMeaningOfLife(result) }
```

Loading animation boilerplate could be abstracted as follows:

```kotlin
abstract class BaseActivity : AppCompatActivity()
{
    fun getLoadingViewHandler() : LoadingViewHandler
    {
        return LoadingViewHandler(lifecycleOwner = this,
            showLoadingView = { /*show loading animation...*/ },
            hideLoadingView = { /*hide loading animation...*/ })
    }
}
```

```kotlin
fun calculate(view : MeaningOfLifeActivity)
{
    Run.future { calculateTheMeaningOfLife() }
        .withLoadingViewHandler(view.getLoadingViewHandler())
        .onSuccess { result -> view.showMeaningOfLife(result) }
        .onError { ex -> view.showError(ex) }
}
```

### Workflows

A workflow is a asynchronous task orchestrator. The workflow DSL API easily lets you define and change how such tasks gets executed. For instance, this will execute two tasks sequentially:

```kotlin
Run.workflow {
    sequential { 
        task { doSomething() }
        task { doAnotherThing() }
    }
}
```

To run tasks in parallel:

```kotlin
Run.workflow {
    parallel {
        task { doSomething() }
        task { doAnotherThing() }
    }
}
```

Parallel execution is limited by a given number of threads at any moment in time. The default value is 4 threads. That is, at any moment in time, only 4 tasks will run in parallel, the rest will be queued and will get executed when currently running tasks finish their execution. You can change this limit by passing the ``numberOfThreads`` argument. For instance, this will print 1, 2, then after 100 milliseconds 3, 4:

```kotlin
Run.workflow {
    parallel(numberOfThreads = 2) {
        task { println(1); Thread.sleep(100) }
        task { println(2); Thread.sleep(100) }
        task { println(3) }
        task { println(4) }
    }
}
```

``sequential {}`` and ``parallel {}`` blocks can be composed as much as you like. By composing, you will easily be able to describe the execution of tasks. For instance:

```kotlin
Run.thread {
    Run.workflow {
        sequential {
            task { f1() }
            parallel {
                task { f2() }
                task { f3() }
                task { f4() }
            }
            task { f5() }
        }
    }
}
```

Will execute as follows:

![workflow](https://raw.githubusercontent.com/andob/rapidroid/master/workflow.png)

Note: Any exception will short-circuit the workflow. For instance, given the above example, let's assume ``f3`` will throw exception E. Execution will be canceled on all pending tasks (in this case, ``f5`` and any other remaining task in the parallel block). Then, ``Run.workflow`` method will throw E further. This is a fail-fast, transactional approach.

Note: ``Run.workflow`` will block the current thread. Do not call it directly on the UI thread, always wrap it inside ``Run.thread`` or ``Run.future`` if you are on the UI thread.

Another small note on synchronicity (maybe it's not obvious). The code inside either ``Run.thread {}``, ``Run.future {}`` or ``task {}`` must either run synchronously (must block the current thread) or if it runs asynchronously, the inner thread must be joined into the parent thread. For instance, if you use Retrofit to make HTTP calls, do not call ``enqueue`` as it is an asynchronous API. Use the ``execute`` as it is a blocking API:

```kotlin
interface APIClient
{
    @GET("/getItems")
    fun getItems() : Call<List<Item>>
}
```

```kotlin
Run.future { apiClient.getItems().execute().body()!! }
    .onSuccess { items -> view.showItems(items) }
```

### Actors

This implements a single-threaded actor model. By using an actor, you can process events sequentially. Actors must be singleton objects (there must be only one actor instance). Internally, an actor will run a background thread that will sequentially pop and process an internal queue.

```kotlin
class OnItemReadyEvent(val item : Item)
```

```kotlin
object ProcessItemsActor : Actor<OnItemReadyEvent>()
{
    override fun handleEvent(event : OnItemReadyEvent)
    {
        doSomethingWith(event.item)
    }
}
```

```kotlin
ProcessItemsActor.enqueueEvent(OnItemReadyEvent(item))
```

### Global exception logger

You can attach a global exception logger. This will be called on any exception detected by any of the library APIs. That is, will log every exception your code would ever throw (that is, as long as your code is wrapped inside ``task {}``, ``async {}``, ``thread {}`` or similar)!

```kotlin
Rapidroid.exceptionLogger = Rapidroid.ExceptionLogger { ex -> log(ex) }
```

This is particularly useful in order to log exception from any application thread, whatever it may be. Take a look at this simple yet powerful logger:

```kotlin
class App : Application()
{
    override fun onCreate()
    {
        super.onCreate()

        //log any exception on the UI thread (any application crash)
        Thread.setDefaultUncaughtExceptionHandler { _, ex -> ExceptionLogger.log(ex) }

        //log other exceptions from any other threads
        Rapidroid.exceptionLogger = Rapidroid.ExceptionLogger { ex -> ExceptionLogger.log(ex) }
    }
}

object ExceptionLogger
{
    fun log(ex : Throwable)
    {
        Run.thread {
            //we will log all errors on the backend. logging every error from every user!
            try { ApiClient.Instance.logException(ex).execute() }
            catch (ignored : Throwable) {} //prevent recalling log()
        }
    }
}
```

Note: this is how various APIs handle exceptions:

| API                                | Behavior                                                                                                                                                                                                                           |
|------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ``Run.thread {}``                  | If the input closure throws an exception, it will be caught and logged.                                                                                                                                    |
| ``Run.future {}``                  | If the input closure throws an exception, it will be caught and logged. Then, the ``onError(exception)`` callback will be called on the UI thread.                                                         |
| ``Run.workflow {}``                | If any inner closure (the input closure, all closures given as arguments to ``task``, ``sequential`` and ``parallel``) throws an exception, it will short-circuit the workflow. The exception will be logged and thrown further. |
| ``Actor.handleEvent``              | If the code inside ``handleEvent`` function throws an exception, it will be caught and logged. The actor will continue to await events.                                                                                            |

### License

```
Copyright 2020-2025 Andrei Dobrescu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
