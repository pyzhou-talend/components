#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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
package ${package}.runtime.reader;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.avro.Schema;

import org.talend.components.api.component.runtime.BoundedSource;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import ${package}.${componentPackage}.${componentName}Properties;

import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.ValidationResult;

/**
 * The ${componentName}Source provides the mechanism to supply data to other
 * components at run-time.
 *
 * Based on the Apache Beam project, the Source mechanism is appropriate to
 * describe distributed and non-distributed data sources and can be adapted
 * to scalable big data execution engines on a cluster, or run locally.
 *
 * This example component describes an input source that is guaranteed to be
 * run in a single JVM (whether on a cluster or locally)
 */
public class ${componentName}Source implements BoundedSource {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    
    private String filePath;
    
    private Schema schema;

    @Override
    public ValidationResult initialize(RuntimeContainer container, ComponentProperties properties) {
        ${componentName}Properties componentProperties = (${componentName}Properties) properties;
        schema = componentProperties.schema.schema.getValue();
        filePath = componentProperties.filename.getValue();
        return ValidationResult.OK;
    }

    /**
     * Validates that component can connect to Data Store
     * Here, method checks that file exist
     */
    @Override
    public ValidationResult validate(RuntimeContainer container) {
        File file = new File(filePath);
        if (file.exists()) {
            return ValidationResult.OK;
        } else {
            ValidationResult vr = new ValidationResult();
            vr.setMessage("The file '" + file.getPath() + "' does not exist."); //$NON-NLS-1$//$NON-NLS-2$
            vr.setStatus(ValidationResult.Result.ERROR);
            return vr;
        }
    }

    @Override
    public Schema getEndpointSchema(RuntimeContainer container, String schemaName) throws IOException {
        return null;
    }

    @Override
    public List<NamedThing> getSchemaNames(RuntimeContainer container) throws IOException {
        return null;
    }

    @Override
    public List<? extends BoundedSource> splitIntoBundles(long desiredBundleSizeBytes, RuntimeContainer adaptor) throws Exception {
       // There can be only one.
       return Arrays.asList(this);
    }

    @Override
    public long getEstimatedSizeBytes(RuntimeContainer adaptor) {
       // This will be ignored since the source will never be split.
       return 0;
    }

    @Override
    public boolean producesSortedKeys(RuntimeContainer adaptor) {
       return false;
    }
    
    @Override
    public ${componentName}Reader createReader(RuntimeContainer container) {
        return new ${componentName}Reader(this);
    }

    Schema getDesignSchema() {
        return this.schema;
    }
    
    String getFilePath() {
        return this.filePath;
    }
}
