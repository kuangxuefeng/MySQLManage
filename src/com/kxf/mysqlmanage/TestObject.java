package com.kxf.mysqlmanage;

import com.kxf.mysqlmanage.annotations.Column;

public class TestObject {
	@Column()
	private int id;
	
	@Column()
	private String name;
	
	@Column()
	private double shenggao;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getShenggao() {
		return shenggao;
	}
	public void setShenggao(double shenggao) {
		this.shenggao = shenggao;
	}
	@Override
	public String toString() {
		return "TestObject [id=" + id + ", name=" + name + ", shenggao="
				+ shenggao + "]";
	}
}
