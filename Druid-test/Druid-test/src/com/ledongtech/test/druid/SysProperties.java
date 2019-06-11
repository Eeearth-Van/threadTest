package com.ledongtech.test.druid;

import java.util.ResourceBundle;

public class SysProperties {
	
	private static final ResourceBundle bundle = java.util.ResourceBundle.getBundle("db-opt");

	/**
	 * 获取配置文件参数
	 * 
	 * @param name
	 * @return
	 */
	public static final String getConfigByName(String name) 
	{
		return bundle.getString(name);
	}
}
