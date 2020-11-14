package pers.clare.lock;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 依 ID 建立鎖
 */
public abstract class IdLock<T> {
    // ID 鎖
    private Map<Object, T> locks = new HashMap<>();

    private Class<T> clazz;

    {
        Type type = this.getClass().getGenericSuperclass();
        if(type instanceof ParameterizedType){
            clazz = (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0];
        }else{
            clazz = (Class<T>) Object.class;
        }
    }

    /**
     * get lock object by id
     *
     * @param id
     * @return
     */
    public T getLock(Object id) {
        T lock = locks.get(id);
        if (lock != null) {
            return lock;
        }
        synchronized (locks) {
            lock = locks.get(id);
            if (lock != null) {
                return lock;
            }
            locks.put(id, lock = newInstance());
        }
        return lock;
    }

    public T remove(Object id) {
        synchronized (locks) {
            return locks.remove(id);
        }
    }

    protected T newInstance() {
        try{
            return clazz.getDeclaredConstructor().newInstance();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
