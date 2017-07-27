
package org.talend.components.mongodb;

import org.talend.components.api.component.AbstractComponentDefinition;
import org.talend.components.api.component.ComponentImageType;
import org.talend.components.api.component.runtime.DependenciesReader;
import org.talend.components.api.component.runtime.JarRuntimeInfo;
import org.talend.components.api.component.runtime.RuntimableRuntime;
import org.talend.components.api.component.runtime.SimpleRuntimeInfo;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.runtime.RuntimeInfo;
import org.talend.daikon.runtime.RuntimeUtil;
import org.talend.daikon.sandbox.SandboxedInstance;


/**
 * The FileInputDefinition acts as an entry point for all of services that 
 * a component provides to integrate with the Studio (at design-time) and other 
 * components (at run-time).
 */
public abstract class MongoDBDefinition extends AbstractComponentDefinition{

    
	public static final boolean USE_CURRENT_JVM_PROPS = true;
	
    private static final String MAVEN_ARTIFACT_ID = "components-mongodb";

    private static final String MAVEN_GROUP_ID = "org.talend.components";
    
    public static final String RUNTIME_MVN_URL = "mvn:org.talend.components/components-mongodb-runtime";
    
    public static final String RUNTIME_MVN_GROUP_ID = "org.talend.components";

    public static final String RUNTIME_MVN_ARTIFACT_ID = "components-mongodb-runtime";
    
    public static final String SOURCE_OR_SINK_CLASS = "org.talend.components.mongodb.runtime.MongoDBSourceOrSink";
    
    public static final String SOURCE_CLASS = "org.talend.components.mongodb.runtime.MongoDBSource";

    public static final String SINK_CLASS = "org.talend.components.mongodb.runtime.MongoDBSink";
    
    
    /** Provides {@link SandboxedInstance}s. */
    private static SandboxedInstanceProvider sandboxedInstanceProvider = SandboxedInstanceProvider.INSTANCE;

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
    
    public static RuntimeInfo getCommonRuntimeInfo(String clazzFullName) {
        return new JarRuntimeInfo(RUNTIME_MVN_URL,
                DependenciesReader.computeDependenciesFilePath(RUNTIME_MVN_GROUP_ID, RUNTIME_MVN_ARTIFACT_ID),
                clazzFullName);
    }
    
    /**
     * Set provider of {@link SandboxedInstance}s.
     *
     * <p>The method is intended for debug/test purposes only and should not be used in production.
     *
     * @param provider provider to be set, can't be {@code null}
     */
    public static void setSandboxedInstanceProvider(SandboxedInstanceProvider provider) {
        sandboxedInstanceProvider = provider;
    }

    /**
     * Get current provider of {@link SandboxedInstance}s.
     *
     * @return provider
     */
    public static SandboxedInstanceProvider getSandboxedInstanceProvider() {
        return sandboxedInstanceProvider;
    }

    /**
     * Get {@link SandboxedInstance} for given runtime object class and <b>not</b> using current JVM properties.
     *
     * @see #getSandboxedInstance(String, boolean)
     * @see SandboxedInstanceProvider
     *
     * @param runtimeClassName full name of runtime object class
     * @return sandboxed instance
     */
    public static SandboxedInstance getSandboxedInstance(String runtimeClassName) {
        return getSandboxedInstance(runtimeClassName, false);
    }

    /**
     * Get {@link SandboxedInstance} for given runtime object class.
     *
     * @see SandboxedInstanceProvider
     *
     * @param runtimeClassName full name of runtime object class
     * @param useCurrentJvmProperties whether to use current JVM properties
     * @return sandboxed instance
     */
    public static SandboxedInstance getSandboxedInstance(String runtimeClassName, boolean useCurrentJvmProperties) {
        return sandboxedInstanceProvider.getSandboxedInstance(runtimeClassName, useCurrentJvmProperties);
    }

    /**
     * Provides {@link SandboxedInstance} objects.
     */
    public static class SandboxedInstanceProvider {

        /** Shared instance of provider. */
        public static final SandboxedInstanceProvider INSTANCE = new SandboxedInstanceProvider();

        /**
         * Get {@link SandboxedInstance} for given runtime object class.
         *
         * @param runtimeClassName full name of runtime object class
         * @param useCurrentJvmProperties whether to use current JVM properties
         * @return sandboxed instance
         */
        public SandboxedInstance getSandboxedInstance(final String runtimeClassName, final boolean useCurrentJvmProperties) {
            ClassLoader classLoader = MongoDBDefinition.class.getClassLoader();
            RuntimeInfo runtimeInfo = MongoDBDefinition.getCommonRuntimeInfo(runtimeClassName);
            if (useCurrentJvmProperties) {
                return RuntimeUtil.createRuntimeClassWithCurrentJVMProperties(runtimeInfo, classLoader);
            } else {
                return RuntimeUtil.createRuntimeClass(runtimeInfo, classLoader);
            }
        }
    }
}
