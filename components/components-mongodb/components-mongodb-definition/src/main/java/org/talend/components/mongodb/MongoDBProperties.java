package org.talend.components.mongodb;

import java.util.Collections;
import java.util.Set;

import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.common.FixedConnectorsComponentProperties;
import org.talend.components.common.SchemaProperties;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;


public class MongoDBProperties extends FixedConnectorsComponentProperties {

    public SchemaProperties schema = new SchemaProperties("schema"); //$NON-NLS-1$
    
    protected transient PropertyPathConnector mainConnector = new PropertyPathConnector(Connector.MAIN_NAME, "schema");
    
    protected transient PropertyPathConnector MAIN_CONNECTOR = new PropertyPathConnector(Connector.MAIN_NAME, "schema");

    protected transient PropertyPathConnector FLOW_CONNECTOR = new PropertyPathConnector(Connector.MAIN_NAME, "schemaFlow");

    protected transient PropertyPathConnector REJECT_CONNECTOR = new PropertyPathConnector(Connector.REJECT_NAME, "schemaReject");
 
    public MongoDBProperties(String name) {
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
        Form main = new Form(this, Form.MAIN);
        main.addRow(schema.getForm(Form.REFERENCE));
    }

    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputComponent) {
        if (isOutputComponent) {
            return Collections.singleton(mainConnector);
        }
        return Collections.emptySet();
    }

}
