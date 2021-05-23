# rapidroid = rapid + android

Some concurrency design patterns I use in my Android projects.

#### Import

```
repositories {
    maven { url "https://maven.andob.info/repository/open_source" }
}
```

```
dependencies {
    implementation 'ro.andob.rapidroid:rapidroid-api-java:1.1.6'
    implementation 'ro.andob.rapidroid:rapidroid-api-kotlin:1.1.6'
    implementation 'ro.andob.rapidroid:rapidroid-core:1.1.6'
    implementation 'ro.andob.rapidroid:rapidroid-futures-java:1.1.6'
    implementation 'ro.andob.rapidroid:rapidroid-futures-kotlin:1.1.6'
    implementation 'ro.andob.rapidroid:rapidroid-workflow:1.1.6'
    implementation 'ro.andob.rapidroid:rapidroid-actor:1.1.6'
}
`````

#### Core

To run a thread (example with the Java API):

```java
import ro.andob.rapidroid.Run;

Run.thread(() -> System.out.println("Hello!"));
```

To run a thread only once (example with Kotlin API):

```java
import ro.andob.rapidroid.kotlin.Run

object SyncService
{
    private val isRunning = ThreadIsRunningFlag()
    fun isRunning() = isRunning.get()

    fun start()
    {
        Run.threadIfNotAlreadyRunning(
            threadIsRunningFlag = isRunning,
            task = { println("do something") })
    }
}
```

#### Futures

A future is simply a task that does something / returns something on a background thread, then calls onSuccess/onError/onAny on the UI thread. Java API:

```java
import ro.andob.rapidroid.Run;

Run.async(() ->
{
    System.out.println("Hello!");
    return 4;
})
.onAny(() -> System.out.println("Any was called!"))
.onError(ex -> System.out.println("Error!"))
.onSuccess(x -> System.out.println("Success! "+x));
```

Kotlin API:

```kotlin
import ro.andob.rapidroid.kotlin.Run;

Run.async { 4 }
   .onAny { println("Any was called!") }
   .onError { ex -> println("Error!") }
   .onSuccess { x -> println("Success! "+x) }
```

#### Workflows

The workflow API lets you easily define and change how concurrent tasks gets executed. Based on the concept of composable lambdas (similar to Jetpack Compose), one can easily compose sequential / parallel / task blocks to describe the execution. Available only as a Kotlin API.

```kotlin
import ro.andob.rapidroid.kotlin.Run;

object SyncService
{
    private val isRunning = ThreadIsRunningFlag()
    fun isRunning() = isRunning.get()

    fun start()
    {
        Run.threadIfNotAlreadyRunning(
            threadIsRunningFlag = isRunning,
            task = {
                Run.workflow {
                    sequential {
                        task { println("1") }
                        parallel {
                            task { println("2") }
                            task { println("3") }
                            task { println("4") }
                        }
                        task { println("5") }
                    }
                }
            })
    }
}
```

![workflow](https://raw.githubusercontent.com/andob/rapidroid/master/docs/workflow.png)

NOTE: the code inside the ``task {}`` block must run synchronously / blocking. Do NOT create new threads inside ``task {}``, but if you do, please join them into the parent thread. For instance, if you're using RxJava or other concurrency framework, you must call ``blockingGet()`` or similar.

NOTE: The workflow spawns threads and then joins them into the parent thread. This means that, by calling ``Run.workflow``, you will block the current running thread. Do not call ``Run.workflow`` on UI thread. Wrap it inside a thread, like this: ``Run.thread { Run.workflow {} }``

#### Actors

Single-threaded actor model / event queue implementation. Using an actor, you can process events sequentially from an event queue. Actors must be singleton objects - there must be only one object instance per actor class. Example with the Kotlin API:

```java
class ShowMessageEvent(val message : String)
```

```java
object ShowMessageActor : Actor<ShowMessageEvent>
{
    override fun handleEvent(event : ShowMessageEvent)
    {
        println(event.message)
    }
}
```

```java
ShowMessageActor.enqueueEvent(ShowMessageEvent("Hello"))
```

#### License

```
Copyright 2020-2021 Andrei Dobrescu

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
