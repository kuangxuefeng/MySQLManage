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
		SimpleDBManage db = new SimpleDBManage();
		TestObject to = new TestObject();
//		to.setId(100);
//		to.setName("lili3");
//		to.setShenggao(165.55);
//		System.out.println(to.getClass().getSimpleName());
//		db.save(to);
		
		to.setId(119);
		to.setName("haha119");
		to.setShenggao(119.01);
		DBWhereBuilder dbw1 = new DBWhereBuilder("id", "=", "119");
		db.update(to, dbw1 );
//		
//		List<TestObject> ls = db.findAll(TestObject.class);
//		System.out.println("ls=" + ls);
		
		DBWhereBuilder dbw = new DBWhereBuilder("id", ">", "103");
		dbw.or("shenggao", "=", "175.55");
		List<TestObject> ls1 = db.find(TestObject.class, dbw, false, "shenggao", "id");
		System.out.println("ls1=" + ls1);
		
		String[] Keys = db.getPrimaryKey(TestObject.class);
		for (int i = 0; i < Keys.length; i++) {
			System.out.println(Keys[i]);
		}
	}
}
