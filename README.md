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

[workflow](data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAUwAAAC/CAYAAACVH+VcAAAFqHRFWHRteGZpbGUAJTNDbXhmaWxlJTIwaG9zdCUzRCUyMmFwcC5kaWFncmFtcy5uZXQlMjIlMjBtb2RpZmllZCUzRCUyMjIwMjAtMDgtMDFUMTElM0EzMSUzQTMyLjU4NFolMjIlMjBhZ2VudCUzRCUyMjUuMCUyMChYMTEpJTIyJTIwZXRhZyUzRCUyMlBDbFd2THg5STd4VE5CUTlqS0JTJTIyJTIwdmVyc2lvbiUzRCUyMjEzLjUuOCUyMiUyMHR5cGUlM0QlMjJnb29nbGUlMjIlM0UlM0NkaWFncmFtJTIwaWQlM0QlMjJZbnVIQ1NKYmEwVnFoLXpIMlQ2YyUyMiUyMG5hbWUlM0QlMjJQYWdlLTElMjIlM0U3VmxOYzlvd0VQMDFITXZJTnNia0dDQk5PMDJubVhJSTdVMUZHMXVOYlRGQ2ZEaSUyRnZoS1dQJTJCVVFweVNZek9TRTlsbGFTJTJGdmVhaGZvT1pOb2Q4M3hNdmpPQ0lROUc1RmR6NW4yYk51eWtDMCUyRkZKS2tpSHN4VEFHZlU2SW5GY0NNUG9JR2tVYlhsTUNxTWxFd0ZncTZySUlMRnNld0VCVU1jODYyMVduM0xLeSUyQmRZbDlNSURaQW9jbWVrZUpDRkowNUtJQyUyRndMVUQwUiUyQllQMGt3dGxrRGF3Q1ROaTJCRGxYUFdmQ0dSUHBLTnBOSUZUQnklMkJLU3J2djh4Tk44WXh4aTBXWUIyZmlqMzklMkJROTNCelBYYkozZGZSUExJJTJCYVM4YkhLNzFnZlZtUlpKRkFHSnlxUUlwclpqRkVod0hJZ3FsWmNraDdLaVl5ekhxSTNTaDdWJTJCbFoxTWxCSlFaU2NtNEJVNGpFTUExbHI0WGlFRkhjVDROcmRpYUwlMkJEQW9ldzh1bEtXd09SYmVDTFhiUXYlMkJNdnFDRW5VWnhpSEVnbTZxMjhCYVJuN3VMbiUyRkRMYU55Z3piS0pEJTJGUWZwS2FuYmxJdDY5WGxmbXFPUnBhenpnU21Qc2dERWR5VURwMkFlM2w4QUpwMklZMGZuRHEweGlybFNMZ2dJbWhGUUU3VVJVSURxa2Z5JTJGRkNzcWk0SG0lMkJBQ3lyejYxSSUyRmlDZ2hhdm1ZdzRvJTJCNGo5N1Ywb1FTM1d3JTJGVkhkY2MlMkJkS2w5cndWYnBEV0hsaWxIJTJCWUhkWU02WVc5QUo3Vkl0eGxxd2xyVmhXZzFoczlMUXVLa1M4Tk9yT2tRblpObzI2U2clMkZIcmtWOCUyQkolMkZwTVVEUE9Icmo5QmdZUlAxY3glMkYzV2ViRVNuRDNBaElXTUZ6emUwekNzUWUzelp4dFFBYk1sM2wlMkJOVzFtTlZVNnhkVXlBVks3WW94TEdxVWZkTXhObWNNcDhjVCUyRnlwVjIlMkJHSTVPWFU2UVNZeU0lMkJreWJqSXVBJTJCVXhXbDZzQ3JlbTNtSFBEMkZJVCUyQkJlRVNIVGZxQXBFYzM5aWxYb1QxSGNQZGlkdEpkR2lFN0cwUE5QWUhwbzRiQlpaYSUyRlVjbFVUNWVjNmVHOGtBVCUyQlpsbzdSS21jV3l2ZFVwcDZOT09XMjRDRDg0UFpwVHIwdE9MYlBhbVpsYjViQ3BLekRLMzFFdGdkR0l1V1pMNERRVXlIcjllVDNsdSUyQjlGJTJCYSUyQnA0R0ZiQlY5MHF1Q2hvV0R6bmpxNWdyMXpVN0R6WGhSOERuZTM5ejZVN3huS0g1eWQ4dTJHcjNPblZiNFprdyUyRmxIMjR3ejElMkY1STBQNTV2M1d1Zkt0anBXZmNsU0prdG5IbkRwS2J2M0g3TGVyak5JcyUyRmd0SmYzUW8lMkZsRnlydjRCJTNDJTJGZGlhZ3JhbSUzRSUzQyUyRm14ZmlsZSUzRW4vyhYAABY0SURBVHic7d1/TJT1AwfwJ0nvFI1wKvNnjtKaiKIk6EQFVBAuPfTK8xemlk6XmU6dpc25WVmBZVEZZpGZudSZzRZKzdw058R0sx+mW5ZlmbYszxQBvff3D8bzhXi47nnA5z6fz/N+bffHcdznx/vxeffcHYQGIiIKixbpBRARyYKFSUQUJhYmEVGYWJhERGEyVZgffPABRo0aBa/Xi9GjR2PBggW4fPnyfz7v1Vdfxbx588Kao7q6GvHx8Thz5oyZpTVw7tw5ZGdnw+Vyhf2cjRs3olevXli8eHGT5raioqICY8eOhaZpuHLliu3zE9F/C7swX3nlFYwaNQr//POP/rV169YhJSUFVVVVIZ97+vRpHD58OOxFffjhh6isrAz7+xvzyy+/hCxMj8eDkpKS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)

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
