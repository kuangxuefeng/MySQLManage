package com.kxf.mysqlmanage;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kxf.mysqlmanage.DBWhereBuilder.DBWhere;

public class SimpleDBManage implements DBManager {
	private final String default_Primary_Key = "id";
	private final String orderSql = " ORDER BY ";// 排序语句

	@Override
	public Connection openConnection() {
		Properties prop = new Properties();
		String driver = null;
		String url = null;
		String username = null;
		String password = null;

		try {
			prop.load(this.getClass().getClassLoader()
					.getResourceAsStream("DBConfig.properties"));

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

	@Override
	public void closeConn(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			LogUtils.e(e.toString());
		}
	}

	@Override
	public long save(Object o) {
		long raw = -1;
		Connection conn = openConnection();
		Map<String, Object> map = getContentValues(o);
		String tName = o.getClass().getSimpleName();
		creatTable(o);
		String sql = "insert into ";
		String columns = "";
		String values = "";
		if (map != null && map.size() > 0) {
			Set<String> keys = map.keySet();
			for (String key : keys) {
				columns = columns + key + ",";
				values = values + map.get(key) + ",";
			}
			columns = columns.substring(0, columns.length() - 1);
			values = values.substring(0, values.length() - 1);
		} else {
			return raw;
		}
		sql = sql + tName + "(" + columns + ") values(" + values + ")";
		LogUtils.i("sql=" + sql);
		try {
			Statement stat = conn.createStatement();
			raw = stat.executeUpdate(sql);
		} catch (SQLException e) {
			LogUtils.e(e.toString());
		}
		closeConn(conn);
		return raw;
	}

	@Override
	public long saveOrUpdate(Object o) {
		return 0;
	}

	@Override
	public long update(Object o, DBWhereBuilder dbw) {
		String tName = o.getClass().getSimpleName();
		String sql = "update " + tName + " set ";
		Map<String, Object> setVal = getContentValues(o);
		List<DBWhere> dbws = dbw.value();
		Set<String> keys = setVal.keySet();
		String set = "";
		for (String s : keys) {
			boolean isWhere = false;
			for (DBWhere d : dbws) {
				if (s.equals(d.column)) {
					isWhere = true;
					break;
				}
			}
			if (!isWhere) {
				set = set + s + "=" + setVal.get(s) + ",";
			}
		}
		set = set.substring(0, set.length() - 1);
		sql = sql + set + dbw.getWhereSql();
		LogUtils.i("sql=" + sql);
		Connection conn = openConnection();
		long raw = -1;
		try {
			Statement stat = conn.createStatement();
			raw = stat.executeUpdate(sql);
		} catch (SQLException e) {
			LogUtils.e(e.toString());
		}
		closeConn(conn);
		return raw;
	}

	@Override
	public <T> List<T> findAll(Class<T> cls) {
		return find(cls, null);
	}

	private Map<String, Object> getContentValues(Object o) {
		Map<String, Object> values = new HashMap<String, Object>();
		Class cls = o.getClass();
		Field[] fs = cls.getDeclaredFields();
		for (int i = 0; i < fs.length; i++) {
			fs[i].setAccessible(true);
			try {
				String type = fs[i].getType().getSimpleName();
				if ("String".equals(type)) {
					values.put(fs[i].getName(), "\'" + fs[i].get(o) + "\'");
				} else {
					values.put(fs[i].getName(), fs[i].get(o));
				}
			} catch (Exception e) {
				LogUtils.e(e.toString());
			}
		}
		LogUtils.i("values=" + values);
		return values;
	}

	@Override
	public List<String> getTableNameByCon() {
		Connection conn = openConnection();
		List<String> ls = new ArrayList<String>();
		try {
			DatabaseMetaData meta = conn.getMetaData();
			ResultSet rs = meta.getTables(null, null, null,
					new String[] { "TABLE" });
			while (rs.next()) {
				ls.add(rs.getString(3));
			}
		} catch (SQLException e) {
			LogUtils.e(e.toString());
		} finally {
			closeConn(conn);
		}
		return ls;
	}

	public boolean isExitsTableName(Object o) {
		String tName = o.getClass().getSimpleName();
		LogUtils.i("tName=" + tName);
		if (null == tName || tName.length() < 1) {
			return false;
		}
		List<String> names = getTableNameByCon();
		if (names != null && names.size() > 0) {
			for (String s : names) {
				if (tName.equalsIgnoreCase(s)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean creatTable(Object o) {
		Connection conn = openConnection();
		String tName = o.getClass().getSimpleName();
		if (!isExitsTableName(o)) {
			String types = getColumType(o);
			String sql = "create table " + tName + "(" + types + ")";
			LogUtils.i("sql=" + sql);
			try {
				Statement stat = conn.createStatement();
				// 创建表
				stat.executeUpdate(sql);
				closeConn(conn);
				return true;
			} catch (SQLException e) {
				LogUtils.e(e.toString());
			}
		} else {
			return checkTableColumn(o);
		}
		return false;
	}

	private boolean checkTableColumn(Object o) {

		return false;
	}

	public String getColumType(Object o) {
		String types = "";
		Class cls = o.getClass();
		Field[] fs = cls.getDeclaredFields();
		boolean hasPrimaryKey = false;
		for (int i = 0; i < fs.length; i++) {
			fs[i].setAccessible(true);
			Class<?> type = fs[i].getType();
			if ("java.lang.String".equals(type.getSimpleName())) {
				types = types + fs[i].getName() + " " + "varchar(20), ";
			} else if ("int".equals(type.getSimpleName())) {
				types = types + fs[i].getName() + " " + "int, ";
			} else if ("double".equals(type.getSimpleName())) {
				types = types + fs[i].getName() + " " + "double, ";
			} else {
				types = types + fs[i].getName() + " " + "varchar(20), ";
			}
			if (default_Primary_Key.equals(fs[i].getName())) {
				hasPrimaryKey = true;
				types = types.substring(0, types.length() - ", ".length());
				types = types + " NOT NULL AUTO_INCREMENT" + ", ";
			}
		}
		if (hasPrimaryKey) {
			types = types + "PRIMARY KEY (`id`)";
		} else {
			types = types.substring(0, types.length() - ", ".length());
		}
		return types;
	}

	@Override
	public <T> List<T> find(Class<T> cls, DBWhereBuilder dbw, String... orders) {
		return find(cls, dbw, true, orders);
	}

	@Override
	public <T> List<T> find(Class<T> cls, DBWhereBuilder dbw, Boolean isAsc,
			String... orders) {
		String tName = cls.getSimpleName();
		String orderStr = " ";
		if (orders != null && orders.length > 0) {
			orderStr = orderSql;
			for (String s : orders) {
				orderStr = orderStr + s + ",";
			}
			orderStr = orderStr.substring(0, orderStr.length() - 1);
		}
		// 查询SQL语句
		String sql = "select * from " + tName;
		if (null != dbw) {
			sql = sql + dbw.getWhereSql();
		}
		sql = sql + orderStr;
		if (!isAsc) {
			sql = sql + " desc";
		}
		LogUtils.i("sql=" + sql);
		// 获得连接
		Connection conn = openConnection();
		List<T> list = new ArrayList<T>();

		// 执行查询
		ResultSet rs = null;
		try {
			// 获得预定义语句
			PreparedStatement pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
		} catch (SQLException e) {
			LogUtils.e(e.toString());
		}
		list = toObject(rs, cls);
		closeConn(conn);
		return list;
	}

	public <T> List<T> toObject(ResultSet rs, Class<T> cls) {
		List<T> list = new ArrayList<T>();
		try {
			// 得到对象的属性和方法
			Field[] fs = cls.getDeclaredFields();
			T t;
			while (rs.next()) {
				t = (T) Class.forName(cls.getName()).newInstance();
				for (int i = 0; i < fs.length; i++) {
					fs[i].setAccessible(true);
					// 属性的名称 是getName()得到， 得到属性的值用fi[i].get(u)
					// 这个里面可以得到对应的属性值，也就是getName 的Value
					Class<?> type = fs[i].getType();
					if ("String".equals(type.getSimpleName())) {
						fs[i].set(t,
								rs.getString(rs.findColumn(fs[i].getName())));
					} else if ("int".equals(type.getSimpleName())) {
						fs[i].set(t, rs.getInt(rs.findColumn(fs[i].getName())));
					} else if ("double".equals(type.getSimpleName())) {
						fs[i].set(t,
								rs.getDouble(rs.findColumn(fs[i].getName())));
					} else {
						// 按需要添加
					}
				}
				list.add(t);
			}
		} catch (SecurityException e) {
			LogUtils.e(e.toString());
		} catch (IllegalArgumentException e) {
			LogUtils.e(e.toString());
		} catch (SQLException e) {
			LogUtils.e(e.toString());
		} catch (InstantiationException e) {
			LogUtils.e(e.toString());
		} catch (IllegalAccessException e) {
			LogUtils.e(e.toString());
		} catch (ClassNotFoundException e) {
			LogUtils.e(e.toString());
		}
		return list;
	}

	/**
	 * 根据数据库连接和表明获取主键名
	 * 
	 * @param con
	 *            传进来一个数据库连接对象
	 * @param table
	 *            数据库中的表名
	 * @return 执行成功返回一个主键名的字符数组，否则返回null或抛出一个异常
	 * @exception 抛出sql执行异常
	 */
	public List<String> getPrimaryKey(Class cls) {
		List<TbColumnInfo> infos = getAllColumnDB(cls);
		List<String> list = null;
		if (infos != null && infos.size() > 0) {
			list = new ArrayList<String>();
			for (TbColumnInfo info : infos) {
				if (null != info.getKey() && "PRI".equals(info.getKey())) {
					list.add(info.getField());
				}
			}
		}
		return list;
	}

	public List<TbColumnInfo> getAllColumnDB(Class cls) {
		Connection con = openConnection();
		;
		String tName = cls.getSimpleName();
		String sql = "SHOW COLUMNS FROM " + tName;
		ResultSet rs = null;
		try {
			PreparedStatement pre = con.prepareStatement(sql);
			rs = pre.executeQuery();
		} catch (Exception e) {
			LogUtils.e(e.toString());
		}
		List<TbColumnInfo> info = toObject(rs, TbColumnInfo.class);
		LogUtils.d("info=" + info);
		closeConn(con);
		return info;
	}
}
