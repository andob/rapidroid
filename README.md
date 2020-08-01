# slcd4a

Simple Lightweight Concurrency Design Patterns For Android

Some design patterns I use in my Android projects.

todo documentation.. work in progress

Licenced under Apache licence.

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
