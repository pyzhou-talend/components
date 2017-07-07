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
package org.talend.components.runtime;

import java.io.IOException;
import java.util.List;

import org.apache.avro.Schema;
import org.talend.components.FileOutput.FileOutputProperties;
import org.talend.components.api.component.runtime.Sink;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.ValidationResult;

public class FileIOSink implements Sink {

    private static final long serialVersionUID = -3926228821855368697L;
    
    FileOutputProperties fileOutputProperties;
    
    private static final I18nMessages i18nMessages = GlobalI18N.getI18nMessageProvider()
            .getI18nMessages(FileIOSink.class);

    @Override
    public WriteOperation<?> createWriteOperation() {
        return new FileIOWriterOpertion(this);
    }

    public FileOutputProperties getProperties() {
        return (FileOutputProperties) fileOutputProperties;
    }

	@Override
	public ValidationResult initialize(RuntimeContainer container, ComponentProperties properties) {
		this.fileOutputProperties = (FileOutputProperties) properties;
		return ValidationResult.OK;
	}
    
    
    @Override
    public ValidationResult validate(RuntimeContainer container) {
        if(fileOutputProperties.filename==null||fileOutputProperties.filename.getStringValue().isEmpty()) {
        	return new ValidationResult(ValidationResult.Result.ERROR,"Empty File Name");
        }
    
        return ValidationResult.OK;
    }

	@Override
	public List<NamedThing> getSchemaNames(RuntimeContainer container) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Schema getEndpointSchema(RuntimeContainer container, String schemaName) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}


    
    
    
    
}
