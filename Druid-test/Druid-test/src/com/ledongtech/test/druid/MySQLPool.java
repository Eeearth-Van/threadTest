package com.ledongtech.test.druid;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.alibaba.druid.pool.DruidDataSource;

public class MySQLPool
{
	
	private static MySQLPool instance;		//????????
	private static String jdbcUrl;			//mysql????
	private static String username; 		//mysql?????
	private static String password;			//mysql????
	private static String driverClassName; 	//mysql????
	private static int initialSize;
	private static int maxActive;
	private static int minIdle;
	private static int maxWait;
	private static boolean poolPreparedStatements;
	private static int maxOpenPreparedStatements;
	private static boolean testWhileIdle;
	private static boolean defaultAutoCommit;
	
//	private DruidDataSource dataSource;
	public DruidDataSource dataSource;

	static 
	{
		Properties prop = new Properties();
		try 
		{
			prop.load(MySQLPool.class.getClassLoader().getResourceAsStream("jdbc.properties"));
			jdbcUrl = prop.getProperty("druid.jdbcUrl");
			username = prop.getProperty("druid.username");
			password = prop.getProperty("druid.password");
			driverClassName = prop.getProperty("druid.driverClassName");
			initialSize = Integer.parseInt(prop.getProperty("druid.initialSize"));
			maxActive = Integer.parseInt(prop.getProperty("druid.maxActive"));
			minIdle = Integer.parseInt(prop.getProperty("druid.minIdle"));
			maxWait = Integer.parseInt(prop.getProperty("druid.maxWait"));
			poolPreparedStatements = Boolean.parseBoolean(prop.getProperty("druid.poolPreparedStatements"));
			maxOpenPreparedStatements = Integer.parseInt(prop.getProperty("druid.maxOpenPreparedStatements"));
			testWhileIdle = Boolean.parseBoolean(prop.getProperty("druid.testWhileIdle"));
			defaultAutoCommit = Boolean.parseBoolean(prop.getProperty("druid.defaultAutoCommit"));
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
    
    
	//?????JDBC????
    MySQLPool()
    {
    	//System.out.print("MySQLPool!\n");
    	dataSource = new DruidDataSource();
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxWait(maxWait);
        dataSource.setPoolPreparedStatements(poolPreparedStatements);
        dataSource.setMaxOpenPreparedStatements(maxOpenPreparedStatements);
        dataSource.setTestWhileIdle(testWhileIdle);
        dataSource.setDefaultAutoCommit(defaultAutoCommit);
    }
    
    //??????
    public static MySQLPool getInstance() 
    {
    	//System.out.print("getInstance!\n");
        if (instance == null) 
        {
            synchronized (MySQLPool.class) 
            {
                if(instance == null) 
                {
                    instance = new MySQLPool();
                }
            }
        }
        return instance;
    }
     
    //???????
    public synchronized Connection getConnection()
    {
    	//System.out.print("getConnection!\n");
        try 
        {
			return dataSource.getConnection();
		}
        catch (SQLException e)
        {
			e.printStackTrace();
		}
        return null;
    }
    
    //???????
    public void releaseConnection(Connection conn)
    {
    	//System.out.print("releaseConnection!\n");
        try
        {
            conn.close();
        }
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
}