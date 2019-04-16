package everything.core.dao;

import com.alibaba.druid.pool.DruidDataSource;
import everything.core.model.Thing;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//数据源工厂
public class DataSourceFactory {
    //单例
    //数据源改成druid的数据源
    private static volatile DruidDataSource instance;

    private DataSourceFactory(){

    }
    //单例的数据源
    public static DataSource getInstance() throws IOException, SQLException {
        if (instance == null){
            synchronized (DataSource.class){
                if (instance == null){
                    instance = new DruidDataSource();
                    //连接数据库 url  userName  password
                    //这是连接MySQL的配置
//                    instance.setUrl("jdbc:mysql://127.0.0.1:3306/everything_hsq");
//                    instance.setUsername("root");
//                    instance.setPassword("root");
//                    instance.setDriverClassName("com.mysql.jdbc.Driver");

                    //这是连接H2数据库的配置,官网上查到的
                    instance.setTestWhileIdle(false);   //解决编译后警告严重的问题
                    instance.setDriverClassName("org.h2.driver");
                    String path = System.getProperty("user.dir")+File.separator+"everything-hsq";
                    instance.setUrl("jdbc:h2:"+path);
                    //数据库完成之后初始化表结构
                    databaseInit();
                }
            }
        }
        return instance;
    }
    public static void databaseInit() throws SQLException, IOException {
        Connection connection = getInstance().getConnection();
        StringBuilder sb = new StringBuilder();
        InputStream in = DataSourceFactory.class.getClassLoader().getResourceAsStream("database.sql");
        if (in!=null){
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine())!=null){
                sb.append(line);
            }
        }else {
            throw new RuntimeException("database.sql script can`t load please check it!");
        }
        String sql = sb.toString();
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
    }

    public static void main(String[] args) throws IOException, SQLException {
        DataSourceFactory.databaseInit();
    }
//    public static void main(String[] args) {
//        Thing thing = new Thing();
//        DataSource dataSource = DataSourceFactory.getInstance();
//        try (Connection connection = dataSource.getConnection();
//             PreparedStatement statement = connection
//                     .prepareStatement("insert into thing(name,path,depth,file_type) values (?,?,?,?)")
//        ){
//            statement.setString(1,"简历.pdf");
//            statement.setString(1,"D:\\abc\\简历.pdf");
//            statement.setInt(1,2);
//            statement.setString(1,"DOC");
//            statement.executeUpdate();
//        } catch (SQLException e) {
//
//        }
//    }
}
