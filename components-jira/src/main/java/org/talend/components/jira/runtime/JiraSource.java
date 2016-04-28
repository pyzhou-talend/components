// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.jira.runtime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field.Order;
import org.talend.components.api.component.runtime.Reader;
import org.talend.components.api.component.runtime.Source;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.jira.tjirainput.TJiraInputProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.ValidationResult;

/**
 * Jira source implementation
 */
public class JiraSource implements Source {

    private static final long serialVersionUID = 1L;

    /**
     * Jira REST API version. It is a part of REST URL
     */
    private static final String REST_VERSION = "rest/api/2/";

    /**
     * Jira component properties
     */
    private TJiraInputProperties properties;

    /**
     * Stores component properties in this object
     * 
     * @param container runtime container
     * @param properties
     */
    @Override
    public void initialize(RuntimeContainer container, ComponentProperties properties) {
        // FIXME could it throw cast exception?
        this.properties = (TJiraInputProperties) properties;
    }

    /**
     * What should I validate here? validate connection to Jira here TODO implement it
     * 
     */
    @Override
    public ValidationResult validate(RuntimeContainer container) {
        return ValidationResult.OK;
    }

    /**
     * TODO implement it
     */
    @Override
    public List<NamedThing> getSchemaNames(RuntimeContainer container) throws IOException {
        List<NamedThing> schemaNames = new ArrayList<>();
        return schemaNames;
    }

    /**
     * TODO implement it
     */
    @Override
    public Schema getSchema(RuntimeContainer container, String schemaName) throws IOException {
        Schema.Field jsonField = new Schema.Field("json", Schema.create(Schema.Type.STRING), null, null, Order.ASCENDING);
        return Schema.createRecord("issue", null, null, false, Collections.singletonList(jsonField));
    }

    @Override
    public Reader createReader(RuntimeContainer container) {
        String url = properties.hostUrl.getStringValue();
        String resourceType = properties.resource.getStringValue();
        String userId = properties.userPassword.userId.getStringValue();
        String password = properties.userPassword.password.getStringValue();
        String jqlQuery = properties.jql.getStringValue();
        int batchSize = properties.batchSize.getIntValue();

        // builds resource URL and parameters
        StringBuilder resourceBuilder = new StringBuilder(REST_VERSION);

        switch (resourceType) {
        case TJiraInputProperties.ISSUE: {
            resourceBuilder.append("search");
            resourceBuilder.append("?");
            resourceBuilder.append("jql=");
            resourceBuilder.append(jqlQuery);
            break;
        }
        case TJiraInputProperties.PROJECT: {
            resourceBuilder.append("project");
            // TODO Add project id /{projectIdOrKey}
            break;
        }
        }
        String resource = resourceBuilder.toString();

        return new JiraReader(this, url, resource, userId, password, batchSize);
    }

}
