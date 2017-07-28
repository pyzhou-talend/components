
package org.talend.components.mongodb.tmongodbconnection;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumSet;
import java.util.Set;

import org.talend.components.api.component.ComponentImageType;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.DependenciesReader;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.component.runtime.JarRuntimeInfo;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.mongodb.MongoDBDefinition;
import org.talend.components.mongodb.MongoDBFamilyDefinition;
import org.talend.daikon.runtime.RuntimeInfo;


public class TMongoDBConnectionDefinition extends MongoDBDefinition{

    public static final String COMPONENT_NAME = "tMongoDBConnection"; //$NON-NLS-1$
    
    public static final String RUNTIME_CLASS_NAME = "org.talend.components.mongodb.runtime.MongoDBSource";
    

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
    
    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends ComponentProperties>[] getNestedCompatibleComponentPropertiesClass() {
        return new Class[]{TMongoDBConnectionProperties.class};
    }
    
    @Override
    public RuntimeInfo getRuntimeInfo(ExecutionEngine engine, ComponentProperties properties, ConnectorTopology connectorTopology) {
        assertEngineCompatibility(engine);
        assertConnectorTopologyCompatibility(connectorTopology);
        try {
            return new JarRuntimeInfo(new URL(MongoDBFamilyDefinition.MAVEN_RUNTIME_URI),
                    DependenciesReader.computeDependenciesFilePath(MongoDBFamilyDefinition.MAVEN_GROUP_ID,
                    		MongoDBFamilyDefinition.MAVEN_RUNTIME_ARTIFACT_ID), RUNTIME_CLASS_NAME);
        } catch (MalformedURLException e) {
            throw new ComponentException(e);
        }
    }
    
}
