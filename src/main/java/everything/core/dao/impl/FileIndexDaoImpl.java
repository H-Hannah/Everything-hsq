package everything.core.dao.impl;

import everything.core.dao.DataSourceFactory;
import everything.core.dao.FileIndexDao;
import everything.core.model.Condition;
import everything.core.model.FileType;
import everything.core.model.Thing;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FileIndexDaoImpl implements FileIndexDao {

    //DriverManager.getConnection
    //DataSource.getConnection  通过数据源工厂获取DataSource实例化对象

    private final DataSource dataSource;
    //数据源
    public FileIndexDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    //插入操作
    public void insert(Thing thing) {
        //准备连接  JDBC操作
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            //1、获取数据库连接
            connection = this.dataSource.getConnection();
            //2、准备SQL语句
            //JDBC编程
            String sql = " insert into file_index(name, path, deepth, file_type) VALUES (?,?,?,?) ";

            //3、准备命令，预编译命令
            statement = connection.prepareStatement(sql);

            //4、设置参数  1234
            //注意这里有报错，不能使用四个私有属性的get方法
            //原因是之前Thing类中的lombok包的@Data 注解没有起到作用
            //预编译命令中SQL的占位符赋值
            statement.setString(1, thing.getName());
            statement.setString(2, thing.getPath());
            statement.setInt(3, thing.getDepth());
            statement.setString(4, thing.getFileType().name());

            //5、执行命令，更新操作
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //释放资源
            releaseResource(null, statement, connection);
        }
    }

    @Override
    public void delete(Thing thing) {
        //与插入基本相同，复制过来
        //做个like path% 目录的删掉  可以吗？？？
        //thing -> path => D:\a\b\hello.java
        //thing -> path => D:\a\b
        //thing -> path => D:\a\ba
        //=最多只能删除一个，绝对匹配

        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            String sql = " delete from thing where path = ? ";
            statement = connection.prepareStatement(sql);
            statement.setString(1, thing.getPath());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseResource(null, statement, connection);
        }
    }


    @Override
    public List<Thing> query(Condition condition) {
        List<Thing> things = new ArrayList<>();
        //加个注释TODO意思是这里的代码还没有写，应用会提醒你
        {
            //准备连接
            Connection connection = null;
            PreparedStatement statement = null;
            //查询是有结果的
            ResultSet resultSet = null;
            try {
                //1、获取数据库连接
                connection = dataSource.getConnection();
                //2、准备SQL语句
                //String sql = "select name, path, deepth, file_type from file_index";

                //将condition条件拼接
                //模糊匹配  name:like  fileType:=
                //查询后的文件深度进行排序
                //limit  : limit offset
                //orderbyAsc  : order by
                StringBuilder sqlBuilder = new StringBuilder();
                //sql语句拼接就不用绝对的sql语句了
                //为什么不用StringBuffer？--线程安全
                //这里不会出现多线程访问  这个方法启动在虚拟机内存中是方法作用域里
                //name必选  fileType可选
                sqlBuilder.append(" select name, path, deepth, file_type from file_index ");
                //前模糊    后模糊（√）    前后模糊
                //模糊匹配原则
                //因为后模糊常用且可用到index索引
                sqlBuilder.append(" where ").append(" name like '% ").append(condition.getName()).append("%' ");
                //后模糊
                if (condition.getFileType()!=null){
                    FileType fileType = FileType.looupByName(condition.getName());
                    sqlBuilder.append(" and file_type = '").append(condition.getFileType().toUpperCase()).append("' ");
                }
                sqlBuilder.append("order bu depth").append(condition.getOrderByDepthAsc()?"asc":"desc");
                sqlBuilder.append("limit").append(condition.getLimit());
                //limit  orderby
//                statement = connection.prepareStatement(sqlBuilder.toString());
//                sqlBuilder.append(" order by depth ").append(condition.getOrderByAsc()? "asc":"desc");
//                sqlBuilder.append(" limit ").append(condition.getLimit()).append(" offset 0 ");
                System.out.println(sqlBuilder);

                //3、准备命令
                statement = connection.prepareStatement(sqlBuilder.toString());
                //4、设置参数  1234
                //插入，更新，删除属于“执行更新”；查询属于“执行查询”。
                //5、执行命令  结果集
                resultSet = statement.executeQuery();
                //6、处理结果
                while (resultSet.next()) {
                    //把数据库的行记录编程java中的对象 变成了thing 加到集合中去 可以返回了
                    Thing thing = new Thing();
                    thing.setName(resultSet.getString("name"));
                    thing.setPath(resultSet.getString("path"));
                    thing.setDepth(resultSet.getInt("depth"));
                    String fileType = resultSet.getString("file_type");
                    thing.setFileType(FileType.looupByName(resultSet.getString("file_type")));
                    things.add(thing);
                }
            } catch (SQLException e) {

            } finally {
                releaseResource(resultSet,statement,connection);
            }
        }
        return things;
    }



    //解决内部代码大量重复问题：重构
    //在不改变程序功能和业务的前提下对代码进行优化，使得代码更易阅读和扩展
    //释放资源的地方写成一个方法
    private void releaseResource(ResultSet resultSet,PreparedStatement statement,Connection connection){
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    //测试代码
//    public static void main(String[] args) {
//        DataSource dataSource = DataSourceFactory.getInstance();
//        FileIndexDao fileIndexDao = new FileIndexDaoImpl(dataSource);
//
//        //插入测试
//        Thing thing =new Thing();
//        thing.setName("Java编程.pdf");
//        thing.setPath("D:\\a\\Java编程.pdf");
//        thing.setDepth(3);
//        thing.setFileType(FileType.DOC);
////
////        //删除
////        fileIndexDao.delete(thing);
//
//        //新建对象thing后将其插入
//        fileIndexDao.insert(thing);
//
////        //查询测试
////        Condition condition = new Condition();
////        condition.setName("简历.pdf");
//////        condition.setLimit(2);
//////        condition.setOrderByAsc(true);
//////        condition.setFileType("IMG");
////        //查询thing并进行接收
////        List<Thing> things = fileIndexDao.query(condition);
////        for (Thing t:things){
////            System.out.println(t);
////        }
//
//        //数据库报错~
//    }
}
