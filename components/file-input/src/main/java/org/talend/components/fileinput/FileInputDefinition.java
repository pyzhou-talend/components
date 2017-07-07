
package org.talend.components.fileinput;

import java.io.InputStream;
import java.util.EnumSet;
import java.util.Set;

import org.talend.components.api.Constants;
import org.talend.components.api.component.AbstractComponentDefinition;
import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.component.ComponentImageType;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.DependenciesReader;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.component.runtime.RuntimableRuntime;
import org.talend.components.api.component.runtime.SimpleRuntimeInfo;
import org.talend.components.api.component.runtime.Source;
import org.talend.components.api.properties.ComponentProperties;

import org.talend.daikon.properties.property.Property;
import org.talend.daikon.runtime.RuntimeInfo;

import aQute.bnd.annotation.component.Component;

/**
 * The FileInputDefinition acts as an entry point for all of services that 
 * a component provides to integrate with the Studio (at design-time) and other 
 * components (at run-time).
 */
@Component(name = Constants.COMPONENT_INSTALLER_PREFIX + FileInputDefinition.COMPONENT_NAME, provide = ComponentDefinition.class)
public class FileInputDefinition extends AbstractComponentDefinition{

    public static final String COMPONENT_NAME = "FileInput"; //$NON-NLS-1$
    
    private static final String MAVEN_ARTIFACT_ID = "components-fileinput";

    private static final String MAVEN_GROUP_ID = "org.talend.components";

    public FileInputDefinition() {
        super(COMPONENT_NAME,ExecutionEngine.DI, ExecutionEngine.BEAM);
    }

    @Override
    public String[] getFamilies() {
        return new String[] { "File/Input" }; //$NON-NLS-1$
    }

    @Override
    public Property[] getReturnProperties() {
        return new Property[] { RETURN_TOTAL_RECORD_COUNT_PROP, RETURN_ERROR_MESSAGE_PROP };
    }

    @Override
    public String getPngImagePath(ComponentImageType imageType) {
        switch (imageType) {
        case PALLETE_ICON_32X32:
            return "FileInput_icon32.png"; //$NON-NLS-1$
        default:
            return "FileInput_icon32.png"; //$NON-NLS-1$
        }
    }

    public String getMavenGroupId() {
        return "org.talend.components";
    }

    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return FileInputProperties.class;
    }
    
    

	@Override
	public RuntimeInfo getRuntimeInfo(ExecutionEngine arg0, ComponentProperties arg1, ConnectorTopology arg2) {
		return getCommonRuntimeInfo(this.getClass().getClassLoader(), FileInputSource.class);
	}

	@Override
	public Set<ConnectorTopology> getSupportedConnectorTopologies() {
		return EnumSet.of(ConnectorTopology.OUTGOING);
	}
	
    @Override
    public boolean isSchemaAutoPropagate() {
        return true;
    }
    

    public static RuntimeInfo getCommonRuntimeInfo(ClassLoader classLoader, Class<? extends RuntimableRuntime<?>> clazz) {
        return new SimpleRuntimeInfo(classLoader,
                DependenciesReader.computeDependenciesFilePath(MAVEN_GROUP_ID, MAVEN_ARTIFACT_ID), clazz.getCanonicalName());
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends ComponentProperties>[] getNestedCompatibleComponentPropertiesClass() {
        return new Class[]{FileInputProperties.class};
    }
    
    @Override
    public boolean isStartable() {
        return true;
    }
}
