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
package ${package};

/**
 * Enumeration of standard string delimiters
 * 
 * This shows possible values for dropdown list UI element i18n message for it
 * should be placed in ${componentName}Properties.properties file
 */
public enum StringDelimiter {
	SEMICOLON {

		@Override
		public String getDelimiter() {
			return ";";
		}

	},
	COLON {

		@Override
		public String getDelimiter() {
			return ":";
		}

	},
	COMMA {

		@Override
		public String getDelimiter() {
			return ",";
		}
	};

	public abstract String getDelimiter();
}
