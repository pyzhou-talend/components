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
package org.talend.components.salesforce.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.IndexedRecord;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.BoundedReader;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.Writer;
import org.talend.components.api.test.ComponentTestUtils;
import org.talend.components.salesforce.SalesforceBulkProperties.Concurrency;
import org.talend.components.salesforce.SalesforceConnectionModuleProperties;
import org.talend.components.salesforce.SalesforceOutputProperties;
import org.talend.components.salesforce.integration.SalesforceTestBase;
import org.talend.components.salesforce.tsalesforcebulkexec.TSalesforceBulkExecDefinition;
import org.talend.components.salesforce.tsalesforcebulkexec.TSalesforceBulkExecProperties;
import org.talend.components.salesforce.tsalesforceinput.TSalesforceInputProperties;
import org.talend.components.salesforce.tsalesforceoutputbulk.TSalesforceOutputBulkProperties;
import org.talend.components.salesforce.tsalesforceoutputbulkexec.TSalesforceOutputBulkExecProperties;
import org.talend.daikon.runtime.RuntimeInfo;
import org.talend.daikon.runtime.RuntimeUtil;
import org.talend.daikon.sandbox.SandboxedInstance;

/**
 * Created by jzhao on 2016-03-09.
 */
public class SalesforceBulkExecReaderTestIT extends SalesforceTestBase {

    @Test
    @Ignore("because of 5M data storage limitation")
    public void testBulkLimitation() throws Throwable {
        testOutputBulkExec(5055);
    }

    @Test
    public void testOutputBulkExec() throws Throwable {
        testOutputBulkExec(10);
    }

    /**
     *
     * Test when bulk file is empty
     *
     */
    @Test
    public void testOutputBulkExecWithEmptyFile() throws Throwable {
        testOutputBulkExec(0);
    }

    /**
     * This test for tSalesforceOutputBulk and tSalesforceBulkExec The runtime of tSalesforceOutputBulkExec should be
     * work like this.
     *
     */
    private void testOutputBulkExec(int count) throws Throwable {

        String random = createNewRandom();

        List<IndexedRecord> rows = makeRows(random, count, false);

        TSalesforceOutputBulkExecProperties outputBulkExecProperties = createAccountSalesforceOutputBulkExecProperties();

        // Prepare the bulk file
        TSalesforceOutputBulkProperties outputBulkProperties = (TSalesforceOutputBulkProperties) outputBulkExecProperties
                .getInputComponentProperties();
        generateBulkFile(outputBulkProperties, rows);

        // Test append
        outputBulkProperties.append.setValue(true);
        generateBulkFile(outputBulkProperties, rows);

        // Execute the bulk action
        TSalesforceBulkExecProperties bulkExecProperties = (TSalesforceBulkExecProperties) outputBulkExecProperties
                .getOutputComponentProperties();

        try {
            executeBulkInsert(bulkExecProperties, random, count * 2);
        } finally {
            // Delete the generated bulk file
            delete(outputBulkProperties);

            List<IndexedRecord> inputRows = readRows(bulkExecProperties);
            List<IndexedRecord> allReadTestRows = filterAllTestRows(random, inputRows);
            deleteRows(allReadTestRows, bulkExecProperties);
            inputRows = readRows(bulkExecProperties);
            assertEquals(0, filterAllTestRows(random, inputRows).size());
        }
    }

    /**
     * Test runtime of tSalesforceOutputBulk
     */
    private void executeBulkInsert(TSalesforceBulkExecProperties bulkExecProperties, String random, int count)
            throws Throwable {

        TSalesforceBulkExecDefinition definition = (TSalesforceBulkExecDefinition) getComponentService()
                .getComponentDefinition(TSalesforceBulkExecDefinition.COMPONENT_NAME);
        RuntimeInfo runtimeInfo = definition.getRuntimeInfo(ExecutionEngine.DI, bulkExecProperties, ConnectorTopology.OUTGOING);
        try (SandboxedInstance sandboxedInstance = RuntimeUtil.createRuntimeClass(runtimeInfo,
                definition.getClass().getClassLoader())) {
            SalesforceSource boundedSource = (SalesforceSource) sandboxedInstance.getInstance();
            boundedSource.initialize(null, bulkExecProperties);
            BoundedReader<IndexedRecord> boundedReader = boundedSource.createReader(null);

            try {
                boolean hasRecord = boundedReader.start();
                List<IndexedRecord> rows = new ArrayList<>();
                while (hasRecord) {
                    rows.add((IndexedRecord) boundedReader.getCurrent());
                    hasRecord = boundedReader.advance();
                }
                checkRows(random, rows, count);
            } finally {
                boundedReader.close();
            }
        }
    }

    /**
     * Test runtime of tSalesforceBulkExec
     */
    public void generateBulkFile(TSalesforceOutputBulkProperties outputBulkProperties, List<IndexedRecord> rows)
            throws Throwable {

        SalesforceBulkFileSink bfSink = new SalesforceBulkFileSink();
        bfSink.initialize(null, outputBulkProperties);

        SalesforceBulkFileWriteOperation writeOperation = (SalesforceBulkFileWriteOperation) bfSink.createWriteOperation();
        Writer<IndexedRecord, Result> saleforceWriter = writeOperation.createWriter(null);

        Result result = writeRows(saleforceWriter, rows);
        Map<String, Object> resultMap = getConsolidatedResults(result, saleforceWriter);
        Assert.assertEquals(rows.size(), resultMap.get(ComponentDefinition.RETURN_TOTAL_RECORD_COUNT));
    }

    /**
     * The configuration of tSalesforceOutputBulkExec
     */
    protected TSalesforceOutputBulkExecProperties createAccountSalesforceOutputBulkExecProperties() throws Throwable {
        TSalesforceOutputBulkExecProperties props = (TSalesforceOutputBulkExecProperties) new TSalesforceOutputBulkExecProperties(
                "foo").init();

        props.connection.timeout.setValue(1200000);
        props.connection.bulkConnection.setValue(true);
        props.outputAction.setValue(SalesforceOutputProperties.OutputAction.INSERT);
        String bulkFilePath = this.getClass().getResource("").getPath() + "/test_outputbulk_1.csv";
        System.out.println("Bulk file path: " + bulkFilePath);
        props.bulkFilePath.setValue(bulkFilePath);
        props.bulkProperties.bytesToCommit.setValue(10 * 1024 * 1024);
        props.bulkProperties.rowsToCommit.setValue(10000);
        props.bulkProperties.concurrencyMode.setValue(Concurrency.Parallel);
        props.bulkProperties.waitTimeCheckBatchState.setValue(10000);

        props.module.main.schema.setValue(getMakeRowSchema(false));
        props.schemaFlow.schema.setValue(getMakeRowSchema(false));

        setupProps(props.connection, !ADD_QUOTES);
        props.module.moduleName.setValue(EXISTING_MODULE_NAME);
        ComponentTestUtils.checkSerialize(props, errorCollector);
        return props;
    }

    protected static void delete(TSalesforceOutputBulkProperties outputBulkProperties) {
        File file = new File(outputBulkProperties.bulkFilePath.getStringValue());

        assertTrue(file.exists());
        assertTrue(file.delete());
        assertFalse(file.exists());
    }

    /**
     * Query all fields is not supported in Bulk Query
     */
    @Override
    protected List<IndexedRecord> readRows(SalesforceConnectionModuleProperties props) throws IOException {
        TSalesforceInputProperties inputProps = (TSalesforceInputProperties) new TSalesforceInputProperties("bar").init();
        inputProps.connection = props.connection;
        inputProps.module = props.module;
        inputProps.batchSize.setValue(200);
        inputProps.queryMode.setValue(TSalesforceInputProperties.QueryMode.Query);

        inputProps.manualQuery.setValue(true);
        inputProps.query.setValue(
                "select Id,Name,ShippingStreet,ShippingPostalCode,BillingStreet,BillingState,BillingPostalCode from Account");

        inputProps.module.moduleName.setValue(EXISTING_MODULE_NAME);
        inputProps.module.main.schema.setValue(getMakeRowSchema(false));

        List<IndexedRecord> inputRows = readRows(inputProps);
        return inputRows;
    }

    @Override
    public Schema getMakeRowSchema(boolean isDynamic) {
        SchemaBuilder.FieldAssembler<Schema> fa = SchemaBuilder.builder().record("MakeRowRecord").fields() //
                .name("Id").type().nullable().stringType().noDefault() //
                .name("Name").type().nullable().stringType().noDefault() //
                .name("ShippingStreet").type().nullable().stringType().noDefault() //
                .name("ShippingPostalCode").type().nullable().intType().noDefault() //
                .name("BillingStreet").type().nullable().stringType().noDefault() //
                .name("BillingState").type().nullable().stringType().noDefault() //
                .name("BillingPostalCode").type().nullable().stringType().noDefault() //
                .name("BillingCity").type().nullable().stringType().noDefault();
        if (isDynamic) {
            fa = fa.name("ShippingState").type().nullable().stringType().noDefault();
        }

        return fa.endRecord();
    }

}
