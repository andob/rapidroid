package ro.andob.rapidroid;

@FunctionalInterface
public interface Supplier<R>
{
    R get() throws Exception;
}
