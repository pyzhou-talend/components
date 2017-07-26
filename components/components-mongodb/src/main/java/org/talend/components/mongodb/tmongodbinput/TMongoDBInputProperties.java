package org.talend.components.mongodb.tmongodbinput;

import java.util.Collections;
import java.util.Set;

import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.mongodb.MongoDBProperties;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;


public class TMongoDBInputProperties extends MongoDBProperties {

	
	public Property filename = PropertyFactory.newString("filename").setRequired(); //$NON-NLS-1$
 
    public TMongoDBInputProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        // Code for property initialization goes here
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
    }

    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputComponent) {
        if (isOutputComponent) {
            return Collections.singleton(MAIN_CONNECTOR);
        }
        return Collections.emptySet();
    }
    
    

}
