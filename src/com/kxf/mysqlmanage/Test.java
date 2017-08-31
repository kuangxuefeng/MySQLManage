package com.kxf.mysqlmanage;

import java.util.List;

import javax.swing.JComboBox.KeySelectionManager;

import com.kxf.mysqlmanage.LogUtils.LogListener;

public class Test {
	public static void main(String[] args) {
		LogUtils.setListener(new LogListener() {
			
			@Override
			public void i(String i) {
				System.out.println(i);
			}
			
			@Override
			public void e(String e) {
				System.err.println(e);
			}
		});
		DBManager db = new SimpleDBManage();
		TestObject to = new TestObject();
//		to.setId(100);
//		to.setName("lili3");
//		to.setShenggao(165.55);
//		System.out.println(to.getClass().getSimpleName());
//		db.save(to);
		
		List<TestObject> ls = db.findAll(TestObject.class);
		System.out.println("ls=" + ls);
		
		DBWhereBuilder dbw = new DBWhereBuilder("id", ">", "103");
		dbw.or("shenggao", "=", "175.55");
		List<TestObject> ls1 = db.find(TestObject.class, dbw, false, "shenggao", "id");
		System.out.println("ls1=" + ls1);
		
		String[] keys = ((SimpleDBManage) db).getPrimaryKey(TestObject.class);
		for (int i = 0; i < keys.length; i++) {
			System.out.println(keys[i]);
		}
	}
}
