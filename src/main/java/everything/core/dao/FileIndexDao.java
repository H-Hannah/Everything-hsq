package everything.core.dao;

import everything.core.model.Condition;
import everything.core.model.Thing;

import java.util.List;

//DAO：数据访问对象
//数据库访问的对象
//file-->thing-->database-->table-->record
//关于业务层访问数据的CRUD（增删改查）
public interface FileIndexDao {
    //插入数据thing
    void insert(Thing thing);

    //检索查询
    List<Thing> query(Condition condition);

    //删除
    void delete(Thing thing);

}

