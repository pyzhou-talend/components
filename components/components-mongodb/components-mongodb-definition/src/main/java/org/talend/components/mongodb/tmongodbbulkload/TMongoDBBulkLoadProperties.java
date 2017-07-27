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
package org.talend.components.mongodb.tmongodbbulkload;

import static org.talend.daikon.properties.property.PropertyFactory.newString;
import static org.talend.components.mongodb.MongoDBDefinition.SOURCE_OR_SINK_CLASS;
import static org.talend.components.mongodb.MongoDBDefinition.USE_CURRENT_JVM_PROPS;
import static org.talend.components.mongodb.MongoDBDefinition.getSandboxedInstance;

import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.mongodb.common.MongoDBRuntimeSourceOrSink;
import org.talend.components.mongodb.module.MongoDBConnectionModule;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.PresentationItem;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResult.Result;
import org.talend.daikon.properties.ValidationResultMutable;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;
import org.talend.daikon.properties.service.Repository;
import org.talend.daikon.sandbox.SandboxedInstance;

/**
 * Class TAzureStorageConnectionProperties.
 */
public class TMongoDBBulkLoadProperties extends ComponentPropertiesImpl {

	// Only for the wizard use
	public Property<String> name = newString("name").setRequired();

	public static final String FORM_WIZARD = "Wizard";

	private String repositoryLocation;

	private static final I18nMessages i18nMessages = GlobalI18N.getI18nMessageProvider()
			.getI18nMessages(TMongoDBBulkLoadProperties.class);
	//

    public MongoDBConnectionModule connection = new MongoDBConnectionModule("connection");
    
    public Property<Boolean> noQueryTimeout = PropertyFactory.newBoolean("noQueryTimeout");

	public PresentationItem testConnection = new PresentationItem("testConnection", "Test connection");

	public TMongoDBBulkLoadProperties(String name) {
		super(name);
	}



	@Override
	public void setupLayout() {
		super.setupLayout();
		Form mainForm = new Form(this, Form.MAIN);
		mainForm.addRow(connection.getForm(Form.MAIN));
		
		Form refForm = Form.create(this, Form.REFERENCE);
		refForm.addRow(noQueryTimeout);
		refForm.addRow(mainForm);


	}

	public ValidationResult validateTestConnection() throws Exception {
        try (SandboxedInstance sandboxedInstance = getSandboxedInstance(SOURCE_OR_SINK_CLASS, USE_CURRENT_JVM_PROPS)) {
        	MongoDBRuntimeSourceOrSink ss = (MongoDBRuntimeSourceOrSink) sandboxedInstance.getInstance();
            ss.initialize(null, TMongoDBBulkLoadProperties.this);
            ValidationResultMutable vr = new ValidationResultMutable(ss.validate(null));
            if (vr.getStatus() == ValidationResult.Result.OK) {
                vr.setMessage(i18nMessages.getMessage("connection.success"));
                getForm(FORM_WIZARD).setAllowForward(true);
            } else {
                getForm(FORM_WIZARD).setAllowForward(false);
            }
            return vr;
        }
	}

	public ValidationResult afterFormFinishWizard(Repository<Properties> repo) throws Exception {
		
         return ValidationResult.OK;
	}

	public TMongoDBBulkLoadProperties getConnectionProperties() {
		return this;
	}


	public TMongoDBBulkLoadProperties setRepositoryLocation(String repositoryLocation) {
		this.repositoryLocation = repositoryLocation;
		return this;
	}
}
