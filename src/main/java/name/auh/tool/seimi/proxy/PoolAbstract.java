package name.auh.tool.seimi.proxy;

import java.util.List;

public abstract class PoolAbstract<T> implements Pool<T> {

    @Override
    public T get() {
        return null;
    }

    @Override
    public List<T> get(int count) {
        return null;
    }

    @Override
    public List<T> getALl() {
        return null;
    }

    @Override
    public void put(T t) {

    }

    @Override
    public void putAll(List<T> t) {

    }

    @Override
    public void remove(T t) {

    }

    @Override
    public void removeAll(List<T> t) {

    }
}
