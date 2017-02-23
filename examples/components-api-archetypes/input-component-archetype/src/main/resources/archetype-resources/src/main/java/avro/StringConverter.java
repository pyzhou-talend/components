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

import org.talend.daikon.avro.converter.AvroConverter;

/**
 * Common abstract field converter for delimited string Inheritors should have
 * package access. They are not supposed to be used outside of this package
 */
abstract class StringConverter<AvroT> implements AvroConverter<String, AvroT> {

	/**
	 * Returns datum class, which is String
	 * 
	 * @return String.class
	 */
	@Override
	public Class<String> getDatumClass() {
		return String.class;
	}

}
