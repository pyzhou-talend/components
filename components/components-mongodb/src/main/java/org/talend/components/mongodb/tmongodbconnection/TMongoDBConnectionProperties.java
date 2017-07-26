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
package org.talend.components.mongodb.tmongodbconnection;

import static org.talend.daikon.properties.property.PropertyFactory.newString;


import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.mongodb.module.MongoDBConnection;
import org.talend.components.mongodb.runtime.MongoDBSourceOrSink;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.PresentationItem;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResult.Result;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;
import org.talend.daikon.properties.service.Repository;

/**
 * Class TAzureStorageConnectionProperties.
 */
public class TMongoDBConnectionProperties extends ComponentPropertiesImpl {

	// Only for the wizard use
	public Property<String> name = newString("name").setRequired();

	public static final String FORM_WIZARD = "Wizard";

	private String repositoryLocation;

	private static final I18nMessages i18nMessages = GlobalI18N.getI18nMessageProvider()
			.getI18nMessages(TMongoDBConnectionProperties.class);
	//

    public MongoDBConnection connection = new MongoDBConnection("connection");
    
    public Property<Boolean> noQueryTimeout = PropertyFactory.newBoolean("noQueryTimeout");

	public PresentationItem testConnection = new PresentationItem("testConnection", "Test connection");

	public TMongoDBConnectionProperties(String name) {
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

		ValidationResult vr = MongoDBSourceOrSink.validateConnection(this);
		if (ValidationResult.Result.OK != vr.getStatus()) {
			return vr;
		}

		return new ValidationResult(Result.OK, i18nMessages.getMessage("message.success"));
	}

	public ValidationResult afterFormFinishWizard(Repository<Properties> repo) throws Exception {
		ValidationResult vr = MongoDBSourceOrSink.validateConnection(this);
		if (vr.getStatus() != ValidationResult.Result.OK) {
			return vr;
		}

		return ValidationResult.OK;
	}

	public TMongoDBConnectionProperties getConnectionProperties() {
		return this;
	}


	public TMongoDBConnectionProperties setRepositoryLocation(String repositoryLocation) {
		this.repositoryLocation = repositoryLocation;
		return this;
	}
}
