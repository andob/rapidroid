package ro.andob.rapidroid.future;

@FunctionalInterface
public interface Supplier<R>
{
    R get() throws Exception;
}
