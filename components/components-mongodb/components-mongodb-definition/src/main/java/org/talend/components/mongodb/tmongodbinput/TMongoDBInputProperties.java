package org.talend.components.mongodb.tmongodbinput;

import java.util.Collections;
import java.util.Set;

import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.mongodb.MongoDBProperties;
import org.talend.components.mongodb.module.MongoDBConnectionModule;
import org.talend.daikon.properties.presentation.Form;


public class TMongoDBInputProperties extends MongoDBProperties {
	
	public MongoDBConnectionModule connection = new MongoDBConnectionModule("connection");
	
 
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
		Form mainForm = new Form(this, Form.MAIN);
		mainForm.addRow(connection.getForm(Form.MAIN));
		
		Form refForm = Form.create(this, Form.REFERENCE);
		refForm.addRow(mainForm);
    }

    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputComponent) {
        if (isOutputComponent) {
            return Collections.singleton(MAIN_CONNECTOR);
        }
        return Collections.emptySet();
    }
    
    

}
