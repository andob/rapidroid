package ro.andreidobrescu.slcd4aj;

import ro.andreidobrescu.slcd4a.actor.Actor;

public class ShowMessageActor extends Actor<ShowMessageEvent>
{
    public static final ShowMessageActor Instance = new ShowMessageActor();

    private ShowMessageActor() {}

    @Override
    public void handleEvent(ShowMessageEvent event)
    {
        System.out.println(event.toString());

        try { Thread.sleep(100); }
        catch (Exception ex) {}
    }
}
