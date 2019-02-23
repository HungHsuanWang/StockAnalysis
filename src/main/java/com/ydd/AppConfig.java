package com.ydd;


import javax.servlet.Filter;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.cz.framework.CORSFilter;

@Configuration
@EnableTransactionManagement
@PropertySource(value = { "classpath:application.properties" })
@ComponentScan(basePackages = {"com.ydd"})
public class AppConfig {
 
	//映射配置文件的参数
    @Value("${database.url}")
    private String dbUrl;//数据库连接
    @Value("${database.username}")
    private String dbUser;//数据库账号
    @Value("${database.password}")
    private String dbPassword;//数据库密码
    @Value("${database.driver}")
    private String dbDriver;//数据库驱动
    @Value("${database.initSize}")
    private int initSize;//初始化连接数
    @Value("${database.maxSize}")
    private int maxSize;//最大连接数
     
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer(){
        return new PropertySourcesPlaceholderConfigurer();
    }    
    
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource){
    	return new DataSourceTransactionManager(dataSource);
    }
    //创建数据源
    @Bean
    public DataSource dataSource(){
    	// 创建tomcat-jdbc连接池属性对象
		PoolProperties poolProps = new PoolProperties();
		poolProps.setUrl(dbUrl);
		poolProps.setDriverClassName(dbDriver);
		poolProps.setUsername(dbUser);
		poolProps.setPassword(dbPassword);
		poolProps.setInitialSize(initSize);
		poolProps.setMaxActive(maxSize);
		poolProps.setMaxIdle(maxSize);
		poolProps.setDefaultAutoCommit(false);
		// 创建连接池, 使用了 tomcat 提供的的实现，它实现了 javax.sql.DataSource 接口
		DataSource dataSource = new DataSource();
		// 为连接池设置属性
		dataSource.setPoolProperties(poolProps);
        return dataSource;
    }
 
    @Bean
    public FilterRegistrationBean<Filter> someFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(corsFilter());
        registration.addUrlPatterns("/*");
        registration.setName("corsFilter");
        return registration;
    }

    @Bean(name = "corsFilter")
    public Filter corsFilter() {
        return new CORSFilter();
    }
}
