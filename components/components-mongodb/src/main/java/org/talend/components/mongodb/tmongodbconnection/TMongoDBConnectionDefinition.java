
package org.talend.components.mongodb.tmongodbconnection;

import java.util.EnumSet;
import java.util.Set;

import org.talend.components.api.component.ComponentImageType;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.mongodb.MongoDBDefinition;
import org.talend.components.mongodb.runtime.MongoDBSource;
import org.talend.daikon.runtime.RuntimeInfo;


public class TMongoDBConnectionDefinition extends MongoDBDefinition{

    public static final String COMPONENT_NAME = "TMongoDBConnection"; //$NON-NLS-1$
    

    public TMongoDBConnectionDefinition() {
        super(COMPONENT_NAME);
    }


    @Override
    public String getPngImagePath(ComponentImageType imageType) {
        switch (imageType) {
        case PALLETE_ICON_32X32:
            return "tMongoDBConnection_icon32.png"; //$NON-NLS-1$
        default:
            return "tMongoDBConnection_icon32.png"; //$NON-NLS-1$
        }
    }


    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return TMongoDBConnectionProperties.class;
    }
    
    
    @Override
    public Set<ConnectorTopology> getSupportedConnectorTopologies() {
        return EnumSet.of(ConnectorTopology.NONE);
    }
	
    @Override
    public boolean isSchemaAutoPropagate() {
        return true;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends ComponentProperties>[] getNestedCompatibleComponentPropertiesClass() {
        return new Class[]{TMongoDBConnectionProperties.class};
    }
    
    @Override
    public RuntimeInfo getRuntimeInfo(ExecutionEngine engine, ComponentProperties properties,
            ConnectorTopology connectorTopology) {
        if (connectorTopology == ConnectorTopology.NONE) {
            return getCommonRuntimeInfo(this.getClass().getClassLoader(), MongoDBSource.class);
        } else {
            return null;
        }
    }
    
}
