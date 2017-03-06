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
package org.talend.components.table;

import org.talend.components.api.AbstractComponentFamilyDefinition;
import org.talend.components.api.ComponentInstaller;
import org.talend.components.api.Constants;
import org.talend.components.table.ttableinput.TableInputDefinition;

import aQute.bnd.annotation.component.Component;

import com.google.auto.service.AutoService;

/**
 * Install all of the definitions provided for the TableInput family of components.
 */
@AutoService(ComponentInstaller.class)
@Component(name = Constants.COMPONENT_INSTALLER_PREFIX + TableInputFamilyDefinition.NAME, provide = ComponentInstaller.class)
public class TableInputFamilyDefinition extends AbstractComponentFamilyDefinition implements ComponentInstaller {

    public static final String NAME = "TableInput";
    
    public static final String MAVEN_GROUP_ID = "org.talend.components";

    public static final String MAVEN_RUNTIME_ARTIFACT_ID = "components-table-runtime";

    public static final String MAVEN_RUNTIME_URI = "mvn:" + MAVEN_GROUP_ID + "/" + MAVEN_RUNTIME_ARTIFACT_ID;

    public TableInputFamilyDefinition() {
        super(NAME, new TableInputDefinition());

    }

    @Override
    public void install(ComponentFrameworkContext ctx) {
        ctx.registerComponentFamilyDefinition(this);
    }
}
