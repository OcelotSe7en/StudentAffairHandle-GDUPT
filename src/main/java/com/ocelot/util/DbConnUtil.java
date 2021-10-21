package com.ocelot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConnUtil {
    private static final Logger logger = LoggerFactory.getLogger(DbConnUtil.class);
    private static InputStream in;
    private static DataSource dataSource;

    static {
        /**
         * 加载配置文件，用阿里的工厂创建数据源
         */

    }

    /**
     * 数据库连接的获取
     *
     * @return Connection
     */
    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("连接工具类getConnection方法异常", e);
            throw new RuntimeException("连接工具类异常");
        }
    }

    /**
     * 数据源获取
     *
     * @return
     */
    public static DataSource getDataSource() {
        return dataSource;
    }

    /**
     * 资源关闭
     *
     * @param conn
     * @param statement
     * @param rs
     */
    public static void close(Connection conn, Statement statement, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error("连接工具类关闭结果集异常", e);
                throw new RuntimeException("连接工具类异常");
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                logger.error("连接工具类关闭statement异常", e);
                throw new RuntimeException("连接工具类异常");
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("连接工具类关闭连接异常", e);
                throw new RuntimeException("连接工具类异常");
            }
        }
    }
}
