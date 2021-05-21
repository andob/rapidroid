package ro.andob.rapidroid.future;

public interface Consumer<T>
{
    void accept(T object) throws Exception;
}
