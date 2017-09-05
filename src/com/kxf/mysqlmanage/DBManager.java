package com.kxf.mysqlmanage;

import java.sql.Connection;
import java.util.List;

public interface DBManager {
	abstract Connection openConnection();
	public abstract void closeConn(Connection conn);
	
	int save(Object o);
	int saveOrUpdate(Object o);

	int update(Object o, DBWhereBuilder dbw);
	
	<T> List<T> find(Class<T> cls, DBWhereBuilder dbw, String... orders);
	<T> List<T> find(Class<T> cls, DBWhereBuilder dbw, Boolean isAsc, String... orders);
	<T> List<T> findAll(Class<T> cls);
	
	List<String> getTableNameByCon();
	
	int delete(Class cls, DBWhereBuilder dbw);
	int dropTable(Class cls);
}
