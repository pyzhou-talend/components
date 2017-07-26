package org.talend.components.mongodb.module;

import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newString;

import java.util.EnumSet;
import java.util.List;

import org.talend.components.api.properties.ComponentReferenceProperties;
import org.talend.components.mongodb.tmongodbconnection.TMongoDBConnectionDefinition;
import org.talend.components.mongodb.tmongodbconnection.TMongoDBConnectionProperties;

//============================================================================
//
//Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
//This source code is available under agreement available at
//%InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
//You should have received a copy of the agreement
//along with this program; if not, write to Talend SA
//9 rue Pages 92150 Suresnes, France
//
//============================================================================


import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;


/**
* common JDBC connection information properties
*
*/
public class MongoDBConnection extends PropertiesImpl {
	


	public Property<List<String>> DBVersion = PropertyFactory.newStringList("DBVersion");

	public Property<Boolean> useReplicaSetAddress = PropertyFactory.newBoolean("useReplicaSetAddress");

	public Property<String> server = PropertyFactory.newString("server").setRequired();

	public Property<Integer> port = PropertyFactory.newInteger("port", 27017);

	public Property<String> database = PropertyFactory.newString("database").setRequired();

	public Property<Boolean> useSSLconnection = PropertyFactory.newBoolean("useSSLconnection");

	public Property<Boolean> requiredAuthentication = PropertyFactory.newBoolean("requiredAuthentication");

	public Property<List<String>> authenticationMechanism = PropertyFactory.newStringList("authenticationMechanism");

	public Property<Boolean> setAuthenticationDatabase = PropertyFactory.newBoolean("setAuthenticationDatabase");

	public Property<String> authenticationDatabase = PropertyFactory.newString("authenticationDatabase");

	public Property<String> username = PropertyFactory.newString("username");

	public Property<String> password = newString("password").setRequired()
			.setFlags(EnumSet.of(Property.Flags.ENCRYPT, Property.Flags.SUPPRESS_LOGGING));
	
	public ComponentReferenceProperties<TMongoDBConnectionProperties> referencedComponent = new ComponentReferenceProperties<>(
			"referencedComponent", TMongoDBConnectionDefinition.COMPONENT_NAME);
	
	public MongoDBConnection(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void setupProperties() {
		super.setupProperties();
		DBVersion.setPossibleValues("MongoDB 3.2.X", "MongoDB 3.0.X", "MongoDB 2.6.X", "MongoDB 2.5.X (Deprecated)");
		authenticationMechanism.setPossibleValues("NEGOTIATE (Recommended for non Kerberized environments)",
				"PLAIN SASL", "SCRAM-SHA-1 SASL", "GSSAPI SASL (KERBEROS)");
		server.setValue("");
		// database.setValue("");
		// useReplicaSetAddress.setValue(false);
		// useSSLconnection.setValue(false);
		// requiredAuthentication.setValue(false);
		// setAuthenticationDatabase.setValue(false);
		// noQueryTimeout.setValue(false);
	}

	@Override
	public void setupLayout() {
		super.setupLayout();
		Form mainForm = new Form(this, Form.MAIN);
		mainForm.addRow(Widget.widget(DBVersion).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
		mainForm.addRow(useReplicaSetAddress);
		mainForm.addRow(server);
		mainForm.addColumn(port);
		mainForm.addRow(database);
		mainForm.addRow(useSSLconnection);
		
		mainForm.addRow(requiredAuthentication);
		mainForm.addRow(authenticationMechanism);
		mainForm.addRow(setAuthenticationDatabase);
		mainForm.addRow(authenticationDatabase);
		mainForm.addRow(username);
		mainForm.addColumn(password);
		

		Form refForm = Form.create(this, Form.REFERENCE);
		Widget compListWidget = widget(referencedComponent).setWidgetType(Widget.COMPONENT_REFERENCE_WIDGET_TYPE);
		refForm.addRow(mainForm);

//		Form wizardForm = Form.create(this, FORM_WIZARD);
//		wizardForm.addRow(name);
//		wizardForm.addRow(accountName);

	}

	@Override
	public void refreshLayout(Form form) {
		super.refreshLayout(form);

		String refComponentIdValue = getReferencedComponentId();
		boolean useAuthentication = !requiredAuthentication.getValue();
		boolean useAuthenticationDatabase = !setAuthenticationDatabase.getValue();
		if (form.getName().equals(Form.MAIN) || form.getName().equals(TMongoDBConnectionProperties.FORM_WIZARD)) {
			form.getWidget(authenticationMechanism).setHidden(useAuthentication);
			form.getWidget(setAuthenticationDatabase).setHidden(useAuthentication);
			form.getWidget(authenticationDatabase).setHidden(useAuthentication||useAuthenticationDatabase);
			form.getWidget(username).setHidden(useAuthentication);
			form.getWidget(password).setHidden(useAuthentication);
	
		}
	}

	public void afterUseReplicaSetAddress() {
		refreshLayout(getForm(Form.MAIN));
		refreshLayout(getForm(Form.REFERENCE));
	}

	public void afterRequiredAuthentication() {
		refreshLayout(getForm(Form.MAIN));
		refreshLayout(getForm(Form.REFERENCE));
//		refreshLayout(getForm(FORM_WIZARD));
	}
	
	public void afterSetAuthenticationDatabase() {
		refreshLayout(getForm(Form.MAIN));
		refreshLayout(getForm(Form.REFERENCE));
	}
	
	public String getReferencedComponentId() {
		return referencedComponent.componentInstanceId.getValue();
	}
	
	public TMongoDBConnectionProperties getReferencedConnectionProperties() {
		TMongoDBConnectionProperties refProps = referencedComponent.getReference();
		if (refProps != null) {
			return refProps;
		}
		return null;
	}
	
}

