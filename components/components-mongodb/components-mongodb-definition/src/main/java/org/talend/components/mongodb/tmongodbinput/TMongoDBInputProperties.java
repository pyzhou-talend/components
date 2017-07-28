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
package org.talend.components.mongodb.tmongodbinput;

import static org.talend.components.mongodb.MongoDBDefinition.SOURCE_OR_SINK_CLASS;
import static org.talend.components.mongodb.MongoDBDefinition.USE_CURRENT_JVM_PROPS;
import static org.talend.components.mongodb.MongoDBDefinition.getSandboxedInstance;
import static org.talend.daikon.properties.property.PropertyFactory.newString;

import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.common.SchemaProperties;
import org.talend.components.mongodb.MongoDBProperties;
import org.talend.components.mongodb.common.MongoDBRuntimeSourceOrSink;
import org.talend.components.mongodb.tmongodbconnection.TMongoDBConnectionProperties;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResultMutable;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;
import org.talend.daikon.properties.service.Repository;
import org.talend.daikon.sandbox.SandboxedInstance;

/**
 * Class TAzureStorageConnectionProperties.
 */
public class TMongoDBInputProperties extends MongoDBProperties {

	private static final I18nMessages i18nMessages = GlobalI18N.getI18nMessageProvider()
			.getI18nMessages(TMongoDBInputProperties.class);

    public TMongoDBConnectionProperties connection = new TMongoDBConnectionProperties("connection");
    
    public Property<String> collectionName = PropertyFactory.newString("collectionName").setRequired();


	public TMongoDBInputProperties(String name) {
		super(name);
	}



	@Override
	public void setupLayout() {
		
		super.setupLayout();
		Form mainForm = getForm(Form.MAIN);
		mainForm.addRow(connection.getForm(Form.REFERENCE));
		mainForm.addRow(collectionName);
		

	}

	public ValidationResult validateTestConnection() throws Exception {
        try (SandboxedInstance sandboxedInstance = getSandboxedInstance(SOURCE_OR_SINK_CLASS, USE_CURRENT_JVM_PROPS)) {
        	MongoDBRuntimeSourceOrSink ss = (MongoDBRuntimeSourceOrSink) sandboxedInstance.getInstance();
            ss.initialize(null, TMongoDBInputProperties.this);
            ValidationResultMutable vr = new ValidationResultMutable(ss.validate(null));
            return vr;
        }
	}

	public ValidationResult afterFormFinishWizard(Repository<Properties> repo) throws Exception {
		
         return ValidationResult.OK;
	}

	public TMongoDBInputProperties getConnectionProperties() {
		return this;
	}


	public TMongoDBInputProperties setRepositoryLocation(String repositoryLocation) {
		return this;
	}
}
