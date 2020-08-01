package ro.andreidobrescu.slcd4aj;

import ro.andreidobrescu.slcd4a.Run;
import ro.andreidobrescu.slcd4a.SLCD4A;

public class Main
{
    public static void main(String[] args)
    {
        SLCD4A.setExceptionLogger(Throwable::printStackTrace);

        Run.thread(() -> System.out.println("Hello!"));

        Run.async(() ->
        {
            System.out.println("Hello procedure!");
            Thread.sleep(5000);
            System.out.println("Stop procedure!");
            return 4;
        })
        .onAny(() -> System.out.println("Any was called!"))
        .onError(ex -> System.out.println("It was error!"))
        .onSuccess(x -> System.out.println("It was successful! "+x));

        for(int i=1; i<=100; i++)
            ShowMessageActor.Instance.enqueueEvent(new ShowMessageEvent("count: "+i));
    }
}
