package com.ocelot.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;


@Configuration
public class DruidConfig {
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.druid.initial-size}")
    private Integer initialSize;
    @Value("${spring.datasource.druid.minIdle}")
    private Integer minIdle;
    @Value("${spring.datasource.druid.maxActive}")
    private Integer maxActive;
    @Value("${spring.datasource.druid.maxWait}")
    private Long maxWait;
    @Value("${spring.datasource.druid.time-between-eviction-runs-millis}")
    private Long timeBetweenEvictionRunsMillis;
    @Value("${spring.datasource.druid.min-evictable-idle-time-millis}")
    private Long minEvictableIdleTimeMillis;
    @Value("${spring.datasource.druid.validation-query}")
    private String validationQuery;
    @Value("${spring.datasource.druid.test-while-idle}")
    private boolean testWhileIdle;
    @Value("${spring.datasource.druid.test-on-borrow}")
    private boolean testOnBorrow;
    @Value("${spring.datasource.druid.test-on-return}")
    private boolean testOnReturn;
    @Value("${spring.datasource.druid.filters}")
    private String filters;


    @Bean
    public ServletRegistrationBean statViewServlet(){
        //创建Servlet注册实体
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(
                new StatViewServlet(), "/druid/*");
        servletRegistrationBean.addInitParameter("loginUsername","druid");
        servletRegistrationBean.addInitParameter("loginPassword","123456");
        return servletRegistrationBean;
    }
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

    @Override
    public String toString() {
        return "DruidConfig{" +
                "url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", initialSize=" + initialSize +
                ", minIdle=" + minIdle +
                ", maxActive=" + maxActive +
                ", maxWait=" + maxWait +
                ", timeBetweenEvictionRunsMillis=" + timeBetweenEvictionRunsMillis +
                ", minEvictableIdleTimeMillis=" + minEvictableIdleTimeMillis +
                ", validationQuery='" + validationQuery + '\'' +
                ", testWhileIdle=" + testWhileIdle +
                ", testOnBorrow=" + testOnBorrow +
                ", testOnReturn=" + testOnReturn +
                ", filters='" + filters + '\'' +
                '}';
    }

//    @Bean(name = "dataSource")
//    public DruidDataSource createDataSource() throws SQLException {
//        DruidDataSource druidDataSource = new DruidDataSource();
//        System.out.println(toString());
//        druidDataSource.setUrl(url);
//        druidDataSource.setUsername(username);
//        druidDataSource.setPassword(password);
//        druidDataSource.setInitialSize(initialSize);
//        druidDataSource.setMinIdle(minIdle);
//        druidDataSource.setMaxActive(maxActive);
//        druidDataSource.setMaxWait(maxWait);
//        druidDataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
//        druidDataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
//        druidDataSource.setValidationQuery(validationQuery);
//        druidDataSource.setTestWhileIdle(testWhileIdle);
//        druidDataSource.setTestOnBorrow(testOnBorrow);
//        druidDataSource.setTestOnReturn(testOnReturn);
//        druidDataSource.setPoolPreparedStatements(false);
//        druidDataSource.setFilters(filters);
//        return druidDataSource;
//    }
    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.druid.one")
    public DataSource dataSource(){
        return DruidDataSourceBuilder.create().build();
    }
}
