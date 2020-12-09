package name.auh.tool.seimi.proxy;

import java.util.List;

public interface Pool<T> {

    T get();

    List<T> get(int count);

    List<T> getALl();

    void put(T t);

    void putAll(List<T> t);

    void remove(T t);

    void removeAll(List<T> t);

}
