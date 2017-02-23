#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package ${package}.avro;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes.TimestampMicros;
import org.apache.avro.LogicalTypes.TimestampMillis;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.avro.converter.AvroConverter;

/**
 * Converts delimited string to {@link IndexedRecord} and vice versa
 * 
 * Delimited string example (delimiter is ';'): "first name;last name; age"
 * 
 * Such converter could be used in {@link Reader} to convert data storage
 * specific object to {@link IndexedRecord} and in writer to convert
 * {@link IndexedRecord} to data storage specific object
 */
public class DelimitedStringConverter implements AvroConverter<String, IndexedRecord> {

	private static final String DEFAULT_DELIMITER = ";";

	/**
	 * Contains available {@link StringConverter}. Avro type is used as a key
	 * However datum class could be also used as key. It depends on what data
	 * mapping is required for particular component family. There might be
	 * situations when several datum classes are mapped to the same avro type.
	 * This is the case to use datum class as a key
	 */
	private static final Map<Type, StringConverter> converterRegistry;

	/**
	 * Fill in converter registry
	 */
	static {
		converterRegistry = new HashMap<>();
		converterRegistry.put(Type.BOOLEAN, new StringToBooleanConverter());
		converterRegistry.put(Type.DOUBLE, new StringToDoubleConverter());
		converterRegistry.put(Type.FLOAT, new StringToFloatConverter());
		converterRegistry.put(Type.INT, new StringToIntConverter());
		converterRegistry.put(Type.LONG, new StringToLongConverter());
		converterRegistry.put(Type.STRING, new StringToStringConverter());
	}

	private final String delimiter;

	/**
	 * Schema of Avro IndexedRecord
	 */
	private final Schema schema;

	/**
	 * Number of fields in schema
	 */
	private final int size;

	/**
	 * Stores converters. Index in array corresponds to index of field in
	 * schema(?)
	 */
	private StringConverter[] converters;

	/**
	 * Constructor sets schema and default delimiter, which will be used during
	 * conversion
	 * 
	 * @param schema
	 *            avro schema
	 */
	public DelimitedStringConverter(Schema schema) {
		this(schema, DEFAULT_DELIMITER);
	}

	/**
	 * Constructor sets schema and delimiter, which will be used during
	 * conversion
	 * 
	 * @param schema
	 *            avro schema
	 */
	public DelimitedStringConverter(Schema schema, String delimiter) {
		this.schema = schema;
		this.delimiter = delimiter;
		this.size = schema.getFields().size();
		initConverters(schema);
	}

	/**
	 * Initialize converters per each schema field
	 * 
	 * @param schema
	 *            design schema
	 */
	private void initConverters(Schema schema) {
		converters = new StringConverter[size];
		List<Field> fields = schema.getFields();
		for (int i = 0; i < size; i++) {
			Field field = fields.get(i);
			Schema fieldSchema = field.schema();
			fieldSchema = AvroUtils.unwrapIfNullable(fieldSchema);
			if (isLogicalTimestamp(fieldSchema)) {
				String datePattern = field.getProp(SchemaConstants.TALEND_COLUMN_PATTERN);
				converters[i] = new StringToTimestampConverter(datePattern);
			} else {
				Type type = fieldSchema.getType();
				converters[i] = converterRegistry.get(type);
			}
		}
	}

	/**
	 * Move it to daikon
	 * 
	 * @param schema
	 * @return
	 */
	private boolean isLogicalTimestamp(Schema schema) {
		Type type = schema.getType();
		if (type != Type.LONG) {
			return false;
		}
		LogicalType logicalType = schema.getLogicalType();
		return logicalType instanceof TimestampMicros || logicalType instanceof TimestampMillis;
	}

	@Override
	public IndexedRecord convertToAvro(String delimitedString) {
		String[] fields = delimitedString.split(delimiter);
		if (fields.length != size) {
			throw new IllegalArgumentException("Input string has wrong number of fields");
		}

		IndexedRecord record = new GenericData.Record(schema);
		for (int i = 0; i < size; i++) {
			Object value = converters[i].convertToAvro(fields[i]);
			record.put(i, value);
		}

		return record;
	}

	@Override
	public String convertToDatum(IndexedRecord record) {
		if (!schema.equals(record.getSchema())) {
			throw new IllegalArgumentException("Input record has different schema");
		}
		if (size == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			Object value = record.get(i);
			String field = (String) converters[i].convertToDatum(value);
			sb.append(field);
			sb.append(delimiter);
		}
		sb.deleteCharAt(sb.length() - 1);

		return sb.toString();
	}

	/**
	 * Returns datum class, which is String
	 * 
	 * @return String.class
	 */
	@Override
	public Class<String> getDatumClass() {
		return String.class;
	}

	/**
	 * Returns avro schema
	 * 
	 * @return avro schema
	 */
	@Override
	public Schema getSchema() {
		return schema;
	}

}
