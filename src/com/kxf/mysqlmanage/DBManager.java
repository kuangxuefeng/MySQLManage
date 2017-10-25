package com.kxf.mysqlmanage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DBManager {
	abstract Connection openConnection();
	public abstract void closeConn(Connection conn) throws SQLException;
	
	int save(Object o) throws SQLException, MySqlManagerException;
	int saveOrUpdate(Object o) throws SQLException, MySqlManagerException;

	int update(Object o, DBWhereBuilder dbw) throws SQLException, MySqlManagerException;
	
	<T> List<T> find(Class<T> cls, DBWhereBuilder dbw, String... orders) throws SQLException, MySqlManagerException;
	<T> List<T> find(Class<T> cls, DBWhereBuilder dbw, Boolean isAsc, String... orders) throws SQLException, MySqlManagerException;
	<T> List<T> findAll(Class<T> cls) throws SQLException, MySqlManagerException;
	
	List<String> getTableNameByCon() throws SQLException;
	
	int delete(Class cls, DBWhereBuilder dbw) throws SQLException;
	int dropTable(Class cls) throws SQLException;
}
