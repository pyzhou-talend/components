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

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.PrintWriter;

import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.talend.components.api.component.runtime.BoundedSource;
import org.talend.components.api.component.runtime.Reader;
import org.talend.components.api.component.runtime.Source;
import org.talend.components.api.service.ComponentService;
import org.talend.components.api.service.common.ComponentServiceImpl;
import org.talend.components.api.service.common.DefinitionRegistry;
import ${package}.${componentName}FamilyDefinition;
import ${package}.${componentPackage}.${componentName}Properties;

@SuppressWarnings("nls")
public class ${componentName}Test {

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    private ComponentService componentService;

    @Before
    public void initializeComponentRegistryAndService() {
        // reset the component service
        componentService = null;
    }

    // default implementation for pure java test. 
    public ComponentService getComponentService() {
        if (componentService == null) {
            DefinitionRegistry testComponentRegistry = new DefinitionRegistry();
            testComponentRegistry.registerComponentFamilyDefinition(new ${componentName}FamilyDefinition());
            componentService = new ComponentServiceImpl(testComponentRegistry);
        }
        return componentService;
    }

    @Test
    public void test${componentName}Runtime() throws Exception {
        ${componentName}Properties props = (${componentName}Properties) getComponentService().getComponentProperties("${componentName}");

        File tempFile = File.createTempFile("${componentName}TestFile", ".txt");
        try {
            PrintWriter writer = new PrintWriter(tempFile.getAbsolutePath(), "UTF-8");
            writer.println("The first line");
            writer.println("The second line");
            writer.close();

            props.filename.setValue(tempFile.getAbsolutePath());
            ${componentName}Source source = new ${componentName}Source();
            source.initialize(null, props);

            ${componentName}Reader reader = source.createReader(null);
            assertThat(reader.start(), is(true));
            IndexedRecord current = reader.getCurrent();
            assertThat(current.get(0), is((Object) "The first line"));
            // No auto advance when calling getCurrent more than once.
            current = reader.getCurrent();
            assertThat(current.get(0), is((Object) "The first line"));
            assertThat(reader.advance(), is(true));
            current = reader.getCurrent();
            assertThat(current.get(0), is((Object) "The second line"));
            assertThat(reader.advance(), is(false));
        } finally {
            tempFile.delete();
        }
    }

}
