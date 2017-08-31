package com.kxf.mysqlmanage;

import java.sql.Connection;
import java.util.List;

public interface DBManager {
	abstract Connection openConnection();
	public abstract void closeConn(Connection conn);
	
	long save(Object o);
	long saveOrUpdate(Object o);

	long update(Object o);
	
	<T> List<T> find(Class<T> cls, DBWhereBuilder dbw, String... orders);
	<T> List<T> find(Class<T> cls, DBWhereBuilder dbw, Boolean isAsc, String... orders);
	<T> List<T> findAll(Class<T> cls);
	
	List<String> getTableNameByCon();
}
