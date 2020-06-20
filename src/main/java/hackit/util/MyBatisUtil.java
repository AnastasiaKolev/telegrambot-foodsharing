package hackit.util;

import hackit.repository.IUserRepository;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MyBatisUtil {

    public static SqlSessionFactory getSqlSessionFactory() throws IOException {
        @NotNull final Properties props = new Properties();
        @NotNull final InputStream is
                = hackit.util.MyBatisUtil.class.getResourceAsStream("/auth/myBatis.properties");
        props.load(is);
        @Nullable final String user = props.getProperty("myBatis.username");
        @Nullable final String password = props.getProperty("myBatis.password");
        @Nullable final String url = props.getProperty("myBatis.url");
        @Nullable final String driver = props.getProperty("myBatis.driver");
        final DataSource dataSource =
                new PooledDataSource(driver, url, user, password);
        final TransactionFactory transactionFactory =
                new JdbcTransactionFactory();
        final Environment environment =
                new Environment("development", transactionFactory, dataSource);
        final Configuration configuration = new Configuration(environment);
        configuration.addMapper(IUserRepository.class);

        return new SqlSessionFactoryBuilder().build(configuration);
    }
}
