
package org.talend.components.mongodb;

import org.talend.components.api.component.AbstractComponentDefinition;
import org.talend.components.api.component.ComponentImageType;
import org.talend.components.api.component.runtime.DependenciesReader;
import org.talend.components.api.component.runtime.RuntimableRuntime;
import org.talend.components.api.component.runtime.SimpleRuntimeInfo;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.runtime.RuntimeInfo;


/**
 * The FileInputDefinition acts as an entry point for all of services that 
 * a component provides to integrate with the Studio (at design-time) and other 
 * components (at run-time).
 */
public abstract class MongoDBDefinition extends AbstractComponentDefinition{

    
    private static final String MAVEN_ARTIFACT_ID = "components-mongodb";

    private static final String MAVEN_GROUP_ID = "org.talend.components";

    public MongoDBDefinition(String ComponentName) {
        super(ComponentName,true);
    }

    @Override
    public String[] getFamilies() {
        return new String[] { "Database/MongoDB" }; //$NON-NLS-1$
    }

    @Override
    public Property[] getReturnProperties() {
        return new Property[] { RETURN_TOTAL_RECORD_COUNT_PROP, RETURN_ERROR_MESSAGE_PROP };
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

    public String getMavenGroupId() {
        return MAVEN_GROUP_ID;
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
        return new Class[]{MongoDBProperties.class};
    }
}
