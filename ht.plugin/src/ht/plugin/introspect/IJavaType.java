package ht.plugin.introspect;

public class IJavaType {
	private String packageTarget;
	private String typeName;
	
	private static IJavaType ByteInstance;
	private static IJavaType ShortInstance;
	private static IJavaType IntegerInstance;
	private static IJavaType LongInstance;
	private static IJavaType BooleanInstance;
	private static IJavaType CharInstance;
	private static IJavaType DoubleInstance;
	private static IJavaType FloatInstance;
	
	private static IJavaType StringInstance;
	private static IJavaType MapInstance;
	private static IJavaType ListInstance;
	private static IJavaType BigDecimalInstance;
	private static IJavaType DateInstance;
	private static IJavaType voidType;
	
	public IJavaType(String packageTarget,String typeName){
		this.packageTarget=packageTarget;
		this.typeName=typeName;
	}

	public String getPackageTarget() {
		return packageTarget;
	}

	public String getTypeName() {
		return typeName;
	}	
	
	public static IJavaType getByteInstance(){
		if(ByteInstance==null)
			ByteInstance=new IJavaType("Byte","java.lang");
		return ByteInstance;
	}
	
	public static IJavaType getShortInstance(){
		if(ShortInstance==null)
			ShortInstance=new IJavaType("Short","java.lang");
		return ShortInstance;
	}
	
	public static IJavaType getIntegerInstance(){
		if(IntegerInstance==null)
			IntegerInstance=new IJavaType("Integer","java.lang");
		return IntegerInstance;
	}

	public static IJavaType getLongInstance() {
		if(LongInstance==null)
			LongInstance=new IJavaType("Long","java.lang");
		return LongInstance;
	}

	public static IJavaType getBooleanInstance() {
		if(BooleanInstance==null)
			BooleanInstance=new IJavaType("Boolean","java.lang");
		return BooleanInstance;
	}

	public static IJavaType getCharInstance() {
		if(CharInstance==null)
			CharInstance=new IJavaType("Character","java.lang");
		return CharInstance;
	}

	public static IJavaType getDoubleInstance() {
		if(DoubleInstance==null)
			CharInstance=new IJavaType("Double","java.lang");
		return DoubleInstance;
	}

	public static IJavaType getFloatInstance() {
		if(FloatInstance==null)
			FloatInstance=new IJavaType("Float","java.lang");
		return FloatInstance;
	}

	public static IJavaType getStringInstance() {
		if(StringInstance==null)
			StringInstance=new IJavaType("String","java.lang");
		return StringInstance;
	}

	public static IJavaType getMapInstance() {
		if(MapInstance==null)
			MapInstance=new IJavaType("Map","java.util");
		return MapInstance;
	}

	public static IJavaType getListInstance() {
		if(ListInstance==null)
			ListInstance=new IJavaType("List","java.util");
		return ListInstance;
	}

	public static IJavaType getBigDecimalInstance() {
		if(BigDecimalInstance==null)
			BigDecimalInstance=new IJavaType("BigDecimal","java.math");
		return BigDecimalInstance;
	}

	public static IJavaType getDateInstance() {
		if(DateInstance==null)
			DateInstance=new IJavaType("Date","java.math");
		return DateInstance;
	}
	
	public static IJavaType getVoidType() {
		if(voidType==null)
			voidType=new IJavaType("void",null);
		return DateInstance;
	}
}
