package ro.andob.rapidroid.future;

@FunctionalInterface
public interface Consumer<T>
{
    void accept(T object) throws Exception;
}
