package ro.andob.rapidroid;

@FunctionalInterface
public interface Consumer<T>
{
    void accept(T object) throws Exception;
}
