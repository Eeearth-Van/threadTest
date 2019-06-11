package CodisTest.Codis_Test;


import io.codis.jodis.JedisResourcePool;
import io.codis.jodis.RoundRobinJedisPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

public class CodisTest extends Thread
{
	public static JedisResourcePool jedisPool = null;
	private int optNum = 0;
	int num = 0;
	StringBuffer keyBuffer = new StringBuffer();
	StringBuffer valueBuffer = new StringBuffer();
	int testNum = 0;
	long sleepTime = 0;
	
	CodisTest(int optNum, int keySize, int valueSize, long sleepTime)
	{
		this.sleepTime = sleepTime;
		this.optNum = optNum;
		for (keySize = 1; keySize <= 32; keySize++)
		{
			keyBuffer.append("A");
		}
		
		for (valueSize = 1; valueSize <= 1024; valueSize++)
		{
			 valueBuffer.append("A");
		}
	}
	
	void set(int num) throws InterruptedException
	{
		try (Jedis jedis = jedisPool.getResource())
		{
			jedis.set(Integer.toString(num) + keyBuffer.toString(), valueBuffer.toString());
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			if (testNum < 2)
			{
				set(num);
				testNum++;
				sleep(3000);
			}
		}
	}
//
//	public test()
//	{
//		try(Jedis jedis = jedisPool.getResource ())
//		{
//
//		}
//		catch (Exception e)
//		{
//
//		}
//	}

	public void run()
	{
		System.out.println("Thread-" + this.getId() + ": start!");
		while(true)
		{
			try 
			{
				set(num % optNum);
				num++;
				sleep(sleepTime);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
//	public void run()
//	{
//		System.out.println("Thread-" + this.getId() + ": start!");
//		try (Jedis jedis = jedisPool.getResource())
//		{
//			while (true)
//			{
//				jedis.set(Integer.toString(num) + keyBuffer.toString(), valueBuffer.toString());
//				System.out.println("set key = " + Integer.toString(num) + keyBuffer.toString());
//				sleep(sleepTime);
//			}
//		} 
//		catch (InterruptedException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public static void main(final String[] args) throws InterruptedException 
	{
		if (args.length < 11)
		{
			System.out.println("******************************************************************************************************");
			System.out.println("*                                              参数错误                                                                                                  *");
			System.out.println("*  [threadNum] [Idle] [Total] [optNum] [keySize] [valueSize] [url] [pwd] [name] [sleepTime] [runtime] *");
			System.out.println("******************************************************************************************************");
			return;
		}
		

		Runnable runnable = new Runnable()
		{
			long maxRuntime = (long) (3600 * 1000 * Float.parseFloat(args[10].toString()));
			@SuppressWarnings("deprecation")
			@Override
			public void run()
			{

				System.out.println("test is begin");
				
				int threadNum = Integer.parseInt(args[0]);
				int idle = Integer.parseInt(args[1]);
				int total = Integer.parseInt(args[2]);
				int optNum = Integer.parseInt(args[3]);
				int keySize = Integer.parseInt(args[4]);
				int valueSize = Integer.parseInt(args[5]);
				int num = 0;
				String url = args[6];
				String pwd = args[7];
				String name = args[8];
				long sleepTime = Integer.parseInt(args[9]);
				
				System.out.println("threadNum----:" + threadNum);
				System.out.println("Idle--------:" + idle);
				System.out.println("Total--------:" + total);
				System.out.println("optNum-------:" + optNum);
				System.out.println("keySize------:" + keySize);
				System.out.println("valueSize----:" + valueSize);
				System.out.println("url----------:" + url);
				System.out.println("pwd----------:" + pwd);
				System.out.println("name---------:" + name);
				System.out.println("sleepTime----:" + sleepTime);
				System.out.println("maxRuntime---:" + maxRuntime);
				try
				{
					sleep (1000);
				} catch (InterruptedException e)
				{
					e.printStackTrace ();
				}
				JedisPoolConfig config = new JedisPoolConfig();
				config.setTestOnCreate(true);
				config.setTestOnBorrow(true);
				config.setTestOnReturn(true);
				config.setTestWhileIdle(true);
				config.setMaxIdle(idle);
				config.setMaxTotal(total);
				config.setMaxWaitMillis(100000);
				config.setMinEvictableIdleTimeMillis(100000);
				
				jedisPool = RoundRobinJedisPool.create().curatorClient(url, 30000).poolConfig(config).password(pwd).zkProxyDir("/jodis/" + name).build();
				//jedisPool = RoundRobinJedisPool.create().curatorClient(url, 30000).poolConfig(config).zkProxyDir("/jodis/codis-demo").build();
				//jedisPool = RoundRobinJedisPool.create().curatorClient("192.168.100.60:2181", 30000).password("tsd_test").zkProxyDir("/jodis/codis-tsd").build();
				List<CodisTest> objs = new ArrayList();
				
				for (int i = 0; i < threadNum; i++)
				{
					CodisTest obj = new CodisTest(optNum, keySize, valueSize, sleepTime);
					objs.add(obj);
				}

				long t1 = System.currentTimeMillis();
				for(CodisTest a:objs)
				{
					a.start();
					try
					{
						sleep(1);
					} 
					catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try 
				{
					sleep(maxRuntime);
					for(CodisTest a:objs)
					{
						a.stop();
					}

				} 
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				for(CodisTest a:objs)
//				{
//					try 
//					{
//						a.join();
//					} 
//					catch (InterruptedException e)
//					{
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					//System.out.println("Thread["+a+"] : stop!");
//				}
				long t2 = System.currentTimeMillis();
				for(CodisTest a:objs)
				{
					num += a.num;
					//System.out.println("Thread["+a+"] : stop!");
				}
				double qps = num / ((t2 - t1) / 1000.00);
				System.out.println("成功数：" + num);
				System.out.println("耗时：" + ((t2 - t1) / 1000.00) + "秒");
				System.out.println("QPS : " + qps);
			}
		};
		runnable.run();
		System.out.println("test is over");
	}
}