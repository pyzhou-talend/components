
package org.talend.components.mongodb.tmongodbinput;

import java.util.EnumSet;
import java.util.Set;

import org.talend.components.api.Constants;
import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.component.ComponentImageType;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.DependenciesReader;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.component.runtime.RuntimableRuntime;
import org.talend.components.api.component.runtime.SimpleRuntimeInfo;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.mongodb.MongoDBDefinition;
import org.talend.components.mongodb.runtime.MongoDBSource;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.runtime.RuntimeInfo;

import aQute.bnd.annotation.component.Component;

/**
 * The FileInputDefinition acts as an entry point for all of services that 
 * a component provides to integrate with the Studio (at design-time) and other 
 * components (at run-time).
 */
@Component(name = Constants.COMPONENT_INSTALLER_PREFIX + TMongoDBInputDefinition.COMPONENT_NAME, provide = ComponentDefinition.class)
public class TMongoDBInputDefinition extends MongoDBDefinition{

    public static final String COMPONENT_NAME = "TMongoDBInput"; //$NON-NLS-1$
    
    public TMongoDBInputDefinition() {
        super(COMPONENT_NAME);
    }

    @Override
    public Property[] getReturnProperties() {
        return new Property[] { RETURN_TOTAL_RECORD_COUNT_PROP, RETURN_ERROR_MESSAGE_PROP };
    }

    @Override
    public String getPngImagePath(ComponentImageType imageType) {
        switch (imageType) {
        case PALLETE_ICON_32X32:
            return "tMongoDBInput_icon32.png"; //$NON-NLS-1$
        default:
            return "tMongoDBInput_icon32.png"; //$NON-NLS-1$
        }
    }



    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return TMongoDBInputProperties.class;
    }
    
    

	@Override
	public RuntimeInfo getRuntimeInfo(ExecutionEngine arg0, ComponentProperties arg1, ConnectorTopology arg2) {
		return getCommonRuntimeInfo(this.getClass().getClassLoader(), MongoDBSource.class);
	}

	@Override
	public Set<ConnectorTopology> getSupportedConnectorTopologies() {
		return EnumSet.of(ConnectorTopology.OUTGOING);
	}
	
    @Override
    public boolean isSchemaAutoPropagate() {
        return true;
    }
    

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends ComponentProperties>[] getNestedCompatibleComponentPropertiesClass() {
        return new Class[]{TMongoDBInputProperties.class};
    }
    
    @Override
    public boolean isStartable() {
        return true;
    }
}
