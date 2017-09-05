package com.kxf.mysqlmanage;

public class TbColumnInfo {
	private String Field;
	private String Type;
	private String Null;
	private String Key;
	private String Default;
	private String Extra;

	public String getField() {
		return Field;
	}

	public void setField(String field) {
		Field = field;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public String getNull() {
		return Null;
	}

	public void setNull(String null1) {
		Null = null1;
	}

	public String getKey() {
		return Key;
	}

	public void setKey(String key) {
		Key = key;
	}

	public String getDefault() {
		return Default;
	}

	public void setDefault(String default1) {
		Default = default1;
	}

	public String getExtra() {
		return Extra;
	}

	public void setExtra(String extra) {
		Extra = extra;
	}

	@Override
	public String toString() {
		return "TbColumnInfo [Field=" + Field + ", Type=" + Type + ", Null="
				+ Null + ", Key=" + Key + ", Default=" + Default + ", Extra="
				+ Extra + "]";
	}

	private String removeKH(String str) {
		if (BaseDBManage.isEmpty(str)) {
			return "";
		}
		int index = str.indexOf("(");
		if (index >= 0) {
			str = str.substring(0, index);
		}
		return str;
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj || !(obj instanceof TbColumnInfo)) {
			return false;
		}

		TbColumnInfo tb = (TbColumnInfo) obj;

		if (!BaseDBManage.isEmpty(removeKH(Field))) {
			if (!removeKH(Field).equals(removeKH(tb.Field))) {
				return false;
			}
		}

		if (!BaseDBManage.isEmpty(removeKH(Type))) {
			if (!removeKH(Type).equals(removeKH(tb.Type))) {
				return false;
			}
		}

		return true;
	}
}
