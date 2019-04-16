package everything.core.interceptor;

import everything.core.model.Thing;

//检索结果Thing的拦截器
//检索结果thing的拦截器
public interface ThingInterceptor {
    void apply(Thing thing);
}
