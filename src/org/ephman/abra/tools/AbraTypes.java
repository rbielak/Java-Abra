package org.ephman.abra.tools;

/** listing of the abra types so that different layers can describe their 
 * mappings to those types
 * @author Paul Bethe
*/

public interface AbraTypes {

	public static final String INTEGER_OBJ = "Integer";
	public static final String DOUBLE_OBJ = "Double";
	public static final String BOOLEAN_OBJ = "Boolean";

	public static final String INTEGER = "integer";
	public static final String LONG = "long";
	public static final String STRING = "string";
	public static final String DOUBLE = "double";
	public static final String FLOAT = "float";
	public static final String CLOB = "longstring";
	public static final String BLOB = "blob";
	public static final String TIMESTAMP = "date";
	public static final String BOOLEAN = "boolean";
	public static final String CHARACTER = "char";

	// # new Types for 'Big' calculations
	public static final String BIG_DECIMAL = "BigDecimal";
	public static final String BIG_INTEGER = "BigInteger";
}
