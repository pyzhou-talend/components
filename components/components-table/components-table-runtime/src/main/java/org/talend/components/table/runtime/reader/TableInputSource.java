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
package org.talend.components.table.runtime.reader;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.talend.components.api.component.runtime.BoundedSource;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.common.avro.RootSchemaUtils;
import org.talend.components.table.avro.DelimitedStringConverter;
import org.talend.components.table.avro.DelimitedStringSchemaInferrer;
import org.talend.components.table.ttableinput.TableInputProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.avro.converter.AvroConverter;
import org.talend.daikon.properties.ValidationResult;

/**
 * The TableInputSource provides the mechanism to supply data to other
 * components at run-time.
 *
 * Based on the Apache Beam project, the Source mechanism is appropriate to
 * describe distributed and non-distributed data sources and can be adapted
 * to scalable big data execution engines on a cluster, or run locally.
 *
 * This example component describes an input source that is guaranteed to be
 * run in a single JVM (whether on a cluster or locally)
 */
public class TableInputSource implements BoundedSource {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    
    private String filePath;
    
    private Schema designSchema;
    
    private Schema runtimeSchema;
    
    private String delimiter;

    @Override
    public ValidationResult initialize(RuntimeContainer container, ComponentProperties properties) {
        TableInputProperties componentProperties = (TableInputProperties) properties;
        designSchema = componentProperties.schema.schema.getValue();
        filePath = componentProperties.filename.getValue();
        if (componentProperties.useCustomDelimiter.getValue()) {
            delimiter = componentProperties.customDelimiter.getValue();
        } else {
            delimiter = componentProperties.delimiter.getValue().getDelimiter();
        }
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
    public TableInputReader createReader(RuntimeContainer container) {
        return new TableInputReader(this);
    }
    
    /**
     * Creates converter, which converts delimited string to
     * {@link IndexedRecord} and vice versa. <code>delimitedString</code> is
     * used to infer Runtime schema in case Design schema contains dynamic field
     * 
     * @param delimitedString
     *            a line, which was read from file source
     * @return {@link AvroConverter} from delimited string to
     *         {@link IndexedRecord}
     */
    AvroConverter<String, IndexedRecord> createConverter(String delimitedString) {
        Schema runtimeSchema = getRuntimeSchema(delimitedString);
        AvroConverter<String, IndexedRecord> converter = new DelimitedStringConverter(runtimeSchema, delimiter);
        return converter;
    }
    
	/**
	 * Creates Root schema, which is used during IndexedRecord creation
	 * 
	 * @param delimitedString
	 *            a line, which was read from file source
	 * @return avro Root schema
	 */
	Schema createRootSchema(String delimitedString) {
		Schema runtimeSchema = getRuntimeSchema(delimitedString);
		Schema rootSchema = RootSchemaUtils.createRootSchema(runtimeSchema, TableInputProperties.outOfBandSchema);
		return rootSchema;
	}

    Schema getDesignSchema() {
        return this.designSchema;
    }
    
    String getFilePath() {
        return this.filePath;
    }
    
    String getDelimiter() {
        return this.delimiter;
    }
    
    /**
	 * Creates Runtime schema from data line, if it is not exist yet and returns
	 * it
	 * 
	 * @param delimitedString
	 *            data line
	 * @return avro Runtime schema
	 */
	private Schema getRuntimeSchema(String delimitedString) {
		if (runtimeSchema == null) {
			runtimeSchema = new DelimitedStringSchemaInferrer(delimiter).inferSchema(designSchema, delimitedString);
		}
		return runtimeSchema;
	}    
}
