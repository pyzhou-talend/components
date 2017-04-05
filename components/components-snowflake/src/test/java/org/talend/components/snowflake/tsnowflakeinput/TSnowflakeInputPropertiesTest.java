package org.talend.components.snowflake.tsnowflakeinput;

import java.util.Collections;
import java.util.Set;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.api.test.ComponentTestUtils;
import org.talend.daikon.properties.presentation.Form;


public class TSnowflakeInputPropertiesTest {

	TSnowflakeInputProperties inputProperties;
	
	@Rule
    public ErrorCollector errorCollector = new ErrorCollector();
	
	@Before
	public void reset() {
		inputProperties = new TSnowflakeInputProperties("input");
		inputProperties.init();
	}
	
	
	@Test
	public void testI18N() {
        ComponentTestUtils.checkAllI18N(inputProperties, errorCollector);
    }
	

	
	@Test
	public void testGetAllSchemaPropertiesConnectors() {
		Set<PropertyPathConnector> allSchemaPropertiesConnectorsForOutputConnection;
		Set<PropertyPathConnector> allSchemaPropertiesConnectorsForInputConnection;
		
		allSchemaPropertiesConnectorsForOutputConnection = inputProperties.getAllSchemaPropertiesConnectors(true);
		allSchemaPropertiesConnectorsForInputConnection = inputProperties.getAllSchemaPropertiesConnectors(false);
		
		assertEquals(allSchemaPropertiesConnectorsForInputConnection, Collections.EMPTY_SET);
		assertEquals(allSchemaPropertiesConnectorsForOutputConnection, 
				Collections.singleton(new PropertyPathConnector(Connector.MAIN_NAME, "table.main")));
	}
	
	@Test
	public void testDefaultProperties() {
		Form main; 
		boolean defaultManualQueryValue;
		boolean isQueryPropertyHidden;
		boolean isConditionPropertyHidden;
		
		main = inputProperties.getForm(Form.MAIN);
		defaultManualQueryValue = inputProperties.manualQuery.getValue();
		isQueryPropertyHidden = main.getWidget(inputProperties.query.getName()).isHidden();
		isConditionPropertyHidden = main.getWidget(inputProperties.condition.getName()).isHidden();
		
		assertFalse(defaultManualQueryValue);
		assertTrue(isQueryPropertyHidden);
		assertFalse(isConditionPropertyHidden);
		
	}
	
}
