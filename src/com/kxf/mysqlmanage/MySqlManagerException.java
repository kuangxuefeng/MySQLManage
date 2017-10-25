package com.kxf.mysqlmanage;
/**
 * 
 * @author kuangxf
 *
 */
public class MySqlManagerException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4512975639681232159L;

	public MySqlManagerException(String msg){
		super(msg);
	}
	
	public MySqlManagerException(String msg, Throwable cause){
		super(msg, cause);
	}
}