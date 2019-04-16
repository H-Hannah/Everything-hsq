package everything.core.search;

//文件检索业务

import everything.core.model.Condition;
import everything.core.model.Thing;

import java.util.List;

public interface ThingSearch {

    //根据condition条件进行数据库检索
    //返回的是很多条记录，放在List中

    List<Thing> search(Condition condition);

}
