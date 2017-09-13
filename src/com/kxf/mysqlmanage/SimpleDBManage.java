package com.kxf.mysqlmanage;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class SimpleDBManage extends BaseDBManage {
	@Override
	public Connection openConnection() {
		Properties prop = new Properties();
		String driver = null;
		String url = null;
		String username = null;
		String password = null;
		try {
//			System.out.println("===========");
//			InputStream in = this.getClass().getClassLoader()
//					.getResourceAsStream("/DBConfig.properties");
//			File file = new File("." + File.separator + "DBConfig.properties");
//			System.out.println(file.getPath());
			InputStream in = new FileInputStream("." + File.separator + "DBConfig.properties");
//			System.out.println("===========" + in.toString());
			prop.load(in);
			driver = prop.getProperty("driver");
			url = prop.getProperty("url");
			username = prop.getProperty("username");
			password = prop.getProperty("password");
			Class.forName(driver);
			return DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			LogUtils.e(e.toString());
		}
		return null;
	}
}
