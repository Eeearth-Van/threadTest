package com.ledongtech.test.druid;

import com.ledongtech.test.druid.MySQLPool;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;


class DBOoperator extends Thread
{
	private String PreSql = null;
	private Connection connect = null;
	private MySQLPool pool = null;
	private PreparedStatement stmt = null;
//	private int opnum = Integer.parseInt(SysProperties.getConfigByName("opnum"));
//	public  int threadNum = Integer.parseInt(SysProperties.getConfigByName("threadNum"));
//	private int operator = Integer.parseInt(SysProperties.getConfigByName("operator"));
	private int opnum;
	public  int threadNum;
	private int operator;
	private int mynum = 0;
	private static Properties prop = new Properties();
	public void loadInfo() throws IOException
	{
		prop.load(MySQLPool.class.getClassLoader().getResourceAsStream("db-opt.properties"));
	}
	//private int batchNum = threadNum;
	public DBOoperator(int t_num, int t_opnum, int t_threadNum, int t_operator)
	{
		mynum = t_num;
		opnum = t_opnum;
		threadNum = t_threadNum;
		operator = t_operator;
	}
	
	
	public void run()
	{
		pool = MySQLPool.getInstance();
		connect = pool.getConnection();
		if (operator == 1)
		{
			PreSql = prop.getProperty("isql");
			try 
			{
				stmt = connect.prepareStatement(PreSql);
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
			int tmp = mynum * opnum + opnum;
            for (mynum = mynum * opnum; mynum < tmp; mynum++)
			{
				try
				{
					//stmt.setString(1, UUID.randomUUID().toString());
					stmt.setString(1, Integer.toString(mynum));
					connect.commit();
				} 
				catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
		else
		{
			PreSql = prop.getProperty("ssql");
			int tmp = mynum * opnum + opnum;
			for (mynum = mynum * opnum; mynum < tmp; mynum++)
			{
				try
				{
					stmt = connect.prepareStatement(PreSql);
					stmt.setInt(1, mynum);
					ResultSet set = stmt.executeQuery();
					//stmt.executeQuery();
					set.next();
					System.out.println(stmt);
				} 
				catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
		try 
		{
			stmt.close();
			//connect.close();
			pool.releaseConnection(connect);
		} 
		catch (SQLException e1) 
		{
			e1.printStackTrace();
		}
		//super.run();
	}
}

public class Druid_test
{
//	public static int threadNum = Integer.parseInt(SysProperties.getConfigByName("threadNum"));
//	private static int opnum = Integer.parseInt(SysProperties.getConfigByName("opnum"));
	
	public static int threadNum = 0;
	private static int opnum = 0;
	private static int operator = 0;
	
	public static void delete_all() throws SQLException
	{
		MySQLPool pool = MySQLPool.getInstance();
		Connection connect = pool.getConnection();
		PreparedStatement stmt = connect.prepareStatement("delete from test;");
		stmt.executeUpdate();
		connect.commit();
		stmt.close();
//		connect.close();
		pool.releaseConnection(connect);
	}
	
	
	public static void main(String[] args) throws SQLException, InterruptedException, IOException 
	{
		List<DBOoperator> threads = new ArrayList<DBOoperator>();
		threadNum = Integer.parseInt(args[0]);
		opnum = Integer.parseInt(args[1]);
		operator = Integer.parseInt(args[2]);
		
//		if (operator == 1)
//		{
//			delete_all();
//		}
//		
		for (int i = 0; i < threadNum; i++)
		{
			DBOoperator a = new DBOoperator(i, opnum, threadNum, operator);
			a.loadInfo();
			threads.add(a);
		}
		if (operator == 1)
		{
			System.out.print("insert beging\n");
		}
		else
		{
			System.out.print("select beging\n");
		}
		long t1 = System.currentTimeMillis();
		for (DBOoperator entry : threads)
    	{
    		entry.start();
    	}
		for (DBOoperator entry : threads)
    	{
			entry.join();
    	}
		double t2 = (System.currentTimeMillis() - t1) / 1000.0000;
		System.out.println("insert end");
		System.out.print("QPS:" + (threadNum * opnum) / t2 + "     Time:" + t2);
	}
}
