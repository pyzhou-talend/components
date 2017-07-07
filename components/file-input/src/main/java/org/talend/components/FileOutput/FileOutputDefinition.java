
package org.talend.components.FileOutput;

import java.util.EnumSet;
import java.util.Set;

import org.talend.components.FileIODefinition;
import org.talend.components.api.component.ComponentImageType;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.runtime.FileIOSink;
import org.talend.daikon.runtime.RuntimeInfo;

/**
 * The FileInputDefinition acts as an entry point for all of services that 
 * a component provides to integrate with the Studio (at design-time) and other 
 * components (at run-time).
 */
public class FileOutputDefinition extends FileIODefinition{

    public static final String COMPONENT_NAME = "FileOutput"; //$NON-NLS-1$
    

    public FileOutputDefinition() {
        super(COMPONENT_NAME);
    }

    @Override
    public String[] getFamilies() {
        return new String[] { "File/Input" }; //$NON-NLS-1$
    }


    @Override
    public String getPngImagePath(ComponentImageType imageType) {
        switch (imageType) {
        case PALLETE_ICON_32X32:
            return "FileOutput_icon32.png"; //$NON-NLS-1$
        default:
            return "FileOutput_icon32.png"; //$NON-NLS-1$
        }
    }


    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return FileOutputProperties.class;
    }
    
    

	@Override
	public RuntimeInfo getRuntimeInfo(ExecutionEngine arg0, ComponentProperties arg1, ConnectorTopology arg2) {
		return getCommonRuntimeInfo(this.getClass().getClassLoader(), FileIOSink.class);
	}

    @Override
    public Set<ConnectorTopology> getSupportedConnectorTopologies() {
        return EnumSet.of(ConnectorTopology.INCOMING, ConnectorTopology.INCOMING_AND_OUTGOING);
    }
	
    @Override
    public boolean isSchemaAutoPropagate() {
        return true;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends ComponentProperties>[] getNestedCompatibleComponentPropertiesClass() {
        return new Class[]{FileOutputProperties.class};
    }
    
    @Override
    public boolean isStartable() {
        return false;
    }
}
