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
package org.talend.components.table.ttableinput;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumSet;
import java.util.Set;

import org.talend.components.api.component.AbstractComponentDefinition;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.DependenciesReader;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.component.runtime.JarRuntimeInfo;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.table.TableInputFamilyDefinition;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.runtime.RuntimeInfo;

/**
 * The TableInputDefinition acts as an entry point for all of services that 
 * a component provides to integrate with the Runtime Platform (at design-time) and other 
 * components (at run-time).
 */
public class TableInputDefinition extends AbstractComponentDefinition {
    
    public static final String COMPONENT_NAME = "TableInput"; //$NON-NLS-1$
    
    public static final String RUNTIME_CLASS_NAME = "org.talend.components.table.runtime.reader.TableInputSource"; //$NON-NLS-1$

    public TableInputDefinition() {
        super(COMPONENT_NAME, ExecutionEngine.DI, ExecutionEngine.BEAM);
    }

    @Override
    public String[] getFamilies() {
        return new String[] { "File/Input" }; //$NON-NLS-1$
    }

    /**
     * Defines a list of Return Properties (a.k.a After Properties). 
     * These properties collect different metrics and information during component execution.
     * Values of these properties are returned after component finished his work.
     * Runtime Platform may use this method to retrieve a this list and show in UI
     * Here, it is defined 2 properties: <br>
     * 1) Error message
     * 2) Number of records processed
     * For Error message property no efforts are required from component developer to set its value. 
     * Runtime Platform will set its value by itself in case of Exception in runtime.
     * As for Number of records property see Reader implementation in runtime part
     */
	@Override
	public Property[] getReturnProperties() {
		return new Property[] { RETURN_TOTAL_RECORD_COUNT_PROP, RETURN_ERROR_MESSAGE_PROP };
	}
    
    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return TableInputProperties.class;
    }
    
    @Override
    public RuntimeInfo getRuntimeInfo(ExecutionEngine engine, ComponentProperties properties, ConnectorTopology connectorTopology) {
        assertEngineCompatibility(engine);
        assertConnectorTopologyCompatibility(connectorTopology);
        try {
            return new JarRuntimeInfo(new URL(TableInputFamilyDefinition.MAVEN_RUNTIME_URI),
                    DependenciesReader.computeDependenciesFilePath(TableInputFamilyDefinition.MAVEN_GROUP_ID,
                    		TableInputFamilyDefinition.MAVEN_RUNTIME_ARTIFACT_ID), RUNTIME_CLASS_NAME);
        } catch (MalformedURLException e) {
            throw new ComponentException(e);
        }
    }

    @Override
    public Set<ConnectorTopology> getSupportedConnectorTopologies() {
        return EnumSet.of(ConnectorTopology.OUTGOING);
    } 
    
}
