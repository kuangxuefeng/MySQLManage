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

import com.kxf.mysqlmanage.DBWhereBuilder.DBWhere;
import com.kxf.mysqlmanage.annotations.DBAnnotation;

public abstract class BaseDBManage implements DBManager {
	protected static String primaryKey = "id";
	protected final String orderSql = " ORDER BY ";// 排序语句

	@Override
	public abstract Connection openConnection();

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
	public int save(Object o) {
		int raw = -1;
		creatOrUpdateTable(o);
		Connection conn = openConnection();
		Map<String, Object> map = getContentValues(o);
		String tName = o.getClass().getSimpleName();
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
	public int saveOrUpdate(Object o) {
		int raw = -1;
		creatOrUpdateTable(o);
		List<String> keys = getPrimaryKey(o.getClass());
		if (null == keys || keys.size() < 1 || isEmpty(keys.get(0))) {
			raw = save(o);
		}else {
			Map<String, Object> values = getContentValues(o);
			DBWhereBuilder dbw = new DBWhereBuilder(keys.get(0), "=", values.get(keys.get(0)) + "");
			List<? extends Object> ls = find(o.getClass(), dbw);
			if (null == ls || ls.size() < 1) {
				raw = save(o);
			}else {
				raw = update(o, dbw);
			}
		}
		return raw;
	}

	@Override
	public int update(Object o, DBWhereBuilder dbw) {
		creatOrUpdateTable(o);
		int raw = -1;
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

	public boolean creatOrUpdateTable(Object o) {
		if (!isExitsTableName(o)) {
			Connection conn = openConnection();
			String tName = o.getClass().getSimpleName();
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
		LogUtils.d("checkTableColumn(Object o) o=" + o);
		List<TbColumnInfo> dbColumns = getAllColumnDB(o.getClass());
		List<TbColumnInfo> objColumns = getAllColumnObj(o);
		if (null == objColumns || objColumns.size() <1) {
			return false;
		}
		if (null != dbColumns && dbColumns.size() > 0) {
			for (TbColumnInfo tb : dbColumns) {
				if (!isExits(objColumns, tb)) {
					LogUtils.d("需要删除的字段：" + tb);
					deleteColumn(o.getClass(), tb.getField());
				}
			}
		}
		
		for (TbColumnInfo tb : objColumns) {
			if (!isExits(dbColumns, tb)) {
				LogUtils.d("需要添加的字段：" + tb);
				addColumn(o.getClass(), tb.getField(), tb.getType());
			}
		}
		return true;
	}
	
	public int addColumn(Class c, String colu, String type) {
		int raw = -1;
		if (isEmpty(colu) || isEmpty(type)) {
			return raw;
		}
		String sql = "ALTER TABLE ";
		String tName = c.getSimpleName();
		sql = sql + tName + " ADD " + colu + " " + type;
		Connection conn = openConnection();
		LogUtils.i("sql=" + sql);
		try {
			Statement stat = conn.createStatement();
			raw = stat.executeUpdate(sql);
			closeConn(conn);
		} catch (SQLException e) {
			LogUtils.e(e.toString());
		}
		return raw;
	}
	
	public int deleteColumn(Class c, String colu) {
		int raw = -1;
		if (isEmpty(colu)) {
			return raw;
		}
		String sql = "alter table ";
		String tName = c.getSimpleName();
		sql = sql + tName + " drop column " + colu;
		Connection conn = openConnection();
		LogUtils.i("sql=" + sql);
		try {
			Statement stat = conn.createStatement();
			raw = stat.executeUpdate(sql);
			closeConn(conn);
		} catch (SQLException e) {
			LogUtils.e(e.toString());
		}
		return raw;
	}
	
	private <T> boolean isExits(List<T> ls, T t){
		if (null == ls || ls.size() < 1 || null == t) {
			return false;
		}
		for (T t1 : ls) {
			if (t.equals(t1)) {
				return true;
			}
		}
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
			if ("String".equals(type.getSimpleName())) {
				if (fs[i].isAnnotationPresent(DBAnnotation.class)) {
					DBAnnotation dba = fs[i].getAnnotation(DBAnnotation.class);
					types = types + fs[i].getName() + " " + "varchar(" + dba.length() + "), ";
				}else {
					types = types + fs[i].getName() + " " + "varchar(255), ";
				}
			} else if ("int".equals(type.getSimpleName())) {
				types = types + fs[i].getName() + " " + "int, ";
			} else if ("double".equals(type.getSimpleName())) {
				types = types + fs[i].getName() + " " + "double, ";
			} else {
				if (fs[i].isAnnotationPresent(DBAnnotation.class)) {
					DBAnnotation dba = fs[i].getAnnotation(DBAnnotation.class);
					types = types + fs[i].getName() + " " + "varchar(" + dba.length() + "), ";
				}else {
					types = types + fs[i].getName() + " " + "varchar(255), ";
				}
			}
			if (fs[i].isAnnotationPresent(DBAnnotation.class)) {
				DBAnnotation dba = fs[i].getAnnotation(DBAnnotation.class);
				if (dba.isKey()) {
					hasPrimaryKey = true;
					primaryKey = fs[i].getName();
					types = types.substring(0, types.length() - ", ".length());
					types = types + " NOT NULL AUTO_INCREMENT" + ", ";
				}
			}
		}
		if (hasPrimaryKey) {
			types = types + "PRIMARY KEY (`" + primaryKey + "`)";
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
		Object o = null;
		try {
			o = Class.forName(cls.getName()).newInstance();
		} catch (InstantiationException e) {
			LogUtils.e(e.toString());
		} catch (IllegalAccessException e) {
			LogUtils.e(e.toString());
		} catch (ClassNotFoundException e) {
			LogUtils.e(e.toString());
		}
		creatOrUpdateTable(o);
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

	public List<TbColumnInfo> getAllColumnObj(Object o) {
		List<TbColumnInfo> info = new ArrayList<TbColumnInfo>();
		Class cls = o.getClass();
		Field[] fs = cls.getDeclaredFields();
		for (int i = 0; i < fs.length; i++) {
			TbColumnInfo tb = new TbColumnInfo();
			tb.setField(fs[i].getName());
			fs[i].setAccessible(true);
			Class<?> type = fs[i].getType();
			if ("String".equals(type.getSimpleName())) {
				if (fs[i].isAnnotationPresent(DBAnnotation.class)) {
					DBAnnotation dba = fs[i].getAnnotation(DBAnnotation.class);
					tb.setType("varchar(" + dba.length() + ")");
				}else {
					tb.setType("varchar(255)");
				}
			} else if ("int".equals(type.getSimpleName())) {
				tb.setType("int");
			} else if ("double".equals(type.getSimpleName())) {
				tb.setType("double");
			} else {
				if (fs[i].isAnnotationPresent(DBAnnotation.class)) {
					DBAnnotation dba = fs[i].getAnnotation(DBAnnotation.class);
					tb.setType("varchar(" + dba.length() + ")");
				}else {
					tb.setType("varchar(255)");
				}
			}
			if (fs[i].isAnnotationPresent(DBAnnotation.class)) {
				DBAnnotation dba = fs[i].getAnnotation(DBAnnotation.class);
				if (dba.isKey()) {
					tb.setKey("PRI");
				}
			}
			info.add(tb);
		}
		LogUtils.d("info=" + info);
		return info;
	}
	
	public static boolean isEmpty(String str) {
		if (null == str || str.length() < 1 || "null".equalsIgnoreCase(str)) {
			return true;
		}
		return false;
	}

	@Override
	public int delete(Class cls, DBWhereBuilder dbw) {
		// delete from MyClass where id=1
		int raw = -1;
		String sql = "delete from ";
		String tName = cls.getSimpleName();
		if (null == dbw) {
			sql = sql + tName;
		}else {
			sql = sql + tName + dbw.getWhereSql();
		}
		Connection conn = openConnection();
		LogUtils.i("sql=" + sql);
		try {
			Statement stat = conn.createStatement();
			raw = stat.executeUpdate(sql);
			closeConn(conn);
		} catch (SQLException e) {
			LogUtils.e(e.toString());
		}
		return raw;
	}

	@Override
	public int dropTable(Class cls) {
		int raw = -1;
		String sql = "DROP TABLE IF EXISTS ";
		String tName = cls.getSimpleName();
		sql = sql + tName;
		Connection conn = openConnection();
		LogUtils.i("sql=" + sql);
		try {
			Statement stat = conn.createStatement();
			raw = stat.executeUpdate(sql);
			closeConn(conn);
		} catch (SQLException e) {
			LogUtils.e(e.toString());
		}
		return raw;
	}
}
