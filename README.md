# SLCD4A - Simple Lightweight Concurrency Design Patterns For Android

Some concurrency design patterns I use in my Android projects.

#### Import

```
repositories {
    maven { url "https://maven.andob.info/repository/open_source" }
}
```

```
dependencies {
    implementation 'ro.andob.slcd4a:slcd4a:1.0.5'
}
`````

#### Basics

To run a thread,

```java
Run.thread(() -> System.out.println("Hello!"));
```

To run a thread only once,

```java
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

#### Workflows

The workflow API lets you easily define and change how concurrent tasks gets executed. Based on the concept of composable lambdas (similar to Jetpack Compose), one can easily compose sequential / parallel / task blocks to describe the execution.

```kotlin
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

![workflow](https://raw.githubusercontent.com/andob/slcd4a/master/docs/workflow.png)

NOTE: the code inside the ``task {}`` block must run synchronously / blocking. Do NOT create new threads inside ``task {}``, but if you do, please join them into the parent thread. For instance, if you're using RxJava or other concurrency framework, you must call ``blockingGet()`` or similar.

NOTE: The workflow spawns threads and then joins them into the parent thread. This means that, by calling ``Run.workflow``, you will block the current running thread. Do not call ``Run.workflow`` on UI thread. Wrap it inside a thread, like this: ``Run.thread { Run.workflow {} }``

#### Futures

Naive future implementation. It's simply a thread that does something, then calls onSuccess/onError/onAny on the UI thread.

```java
Run.async(() ->
{
    System.out.println("Hello!");
    return 4;
})
.onAny(() -> System.out.println("Any was called!"))
.onError(ex -> System.out.println("Error!"))
.onSuccess(x -> System.out.println("Success! "+x));
```

#### Actors

Naive single-threaded actor model / event queue implementation. Using an actor, you can process events sequentially from an event queue. Actors must be singleton objects - there must be only one object instance per actor class.

```java
public class ShowMessageEvent
{
    private final String message;

    public ShowMessageEvent(String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return message;
    }
}
```

```java
public class ShowMessageActor extends Actor<ShowMessageEvent>
{
    public static final ShowMessageActor Instance = new ShowMessageActor();

    private ShowMessageActor() {}

    @Override
    public void handleEvent(ShowMessageEvent event)
    {
        System.out.println(event.toString());
        
        //heavy work here
    }
}
```

```java
for(int i=1; i<=100; i++)
    ShowMessageActor.Instance.enqueueEvent(new ShowMessageEvent("count: "+i));
```

#### Thread pools

One can specify a custom thread pool executor, for instance, this will run no more than 5 parallel threads at a given time:

```kotlin
@JvmStatic
val MY_THREAD_POOL_EXECUTOR by lazy {
    val executor=ThreadPoolExecutor(5, 5, 1, TimeUnit.MINUTES, LinkedBlockingQueue<>(Integer.MAX_VALUE))
    executor.allowCoreThreadTimeOut(true)
    return@lazy executor
}
```

```kotlin
Run.onThreadPoolExecutor(ThreadPoolExecutors.MY_THREAD_POOL_EXECUTOR)
   .thread { println("do stuff") }
```

```kotlin
Run.onThreadPoolExecutor(ThreadPoolExecutors.MY_THREAD_POOL_EXECUTOR)
   .workflow {
       parallel {
           task { println("do this") }
           task { println("do that") }
       }
   }
```

#### Some more synctactic sugar on workflows

```kotlin
val filesToUpload = listOf<File>()

Run.workflow {
    parallel {
        task { println("something else") }
        
        filesToUpload.map { fileToUpload ->
            task { uploadFileToServer(fileToUpload) }
        }
    }
}
```

This can be also written as:

```kotlin
val filesToUpload = listOf<File>()

Run.workflow {
    parallel {
        task { println("something else") }
        
        parallelList(
            itemsProvider = { filesToUpload },
            itemSubtask = { fileToUpload -> uploadFileToServer(fileToUpload) })
    }
}
```

#### License

```
Copyright 2020 Andrei Dobrescu

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
