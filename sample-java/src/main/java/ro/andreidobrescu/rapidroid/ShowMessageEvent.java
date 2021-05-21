package ro.andreidobrescu.rapidroid;

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
