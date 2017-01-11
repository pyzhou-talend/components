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
package org.talend.components.simplefileio.runtime;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.talend.components.simplefileio.runtime.SimpleFileIoInputRuntimeTest.createInputComponentProperties;
import static org.talend.components.simplefileio.runtime.SimpleFileIoOutputRuntimeTest.createOutputComponentProperties;
import static org.talend.components.test.RecordSetUtil.getSimpleTestData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.runners.direct.DirectRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.values.PCollection;
import org.junit.Rule;
import org.junit.Test;
import org.talend.components.adapter.beam.transform.ConvertToIndexedRecord;
import org.talend.components.adapter.beam.transform.DirectCollector;
import org.talend.components.simplefileio.SimpleFileIoDatasetProperties.FieldDelimiterType;
import org.talend.components.simplefileio.SimpleFileIoDatasetProperties.RecordDelimiterType;
import org.talend.components.simplefileio.SimpleFileIoFormat;
import org.talend.components.simplefileio.input.SimpleFileIoInputProperties;
import org.talend.components.simplefileio.output.SimpleFileIoOutputProperties;
import org.talend.components.test.BeamDirectTestResource;
import org.talend.components.test.MiniDfsResource;
import org.talend.components.test.RecordSet;

/**
 * Unit tests for {@link SimpleFileIoInputRuntime} and {@link SimpleFileIoOutputRuntime}, focusing on the use cases
 * where data is written by an output component, and then read by an input component.
 *
 * The input component should be able to read all files generated by the output component.
 */
public class SimpleFileIoRoundTripRuntimeTest {

    @Rule
    public MiniDfsResource mini = new MiniDfsResource();

    @Rule
    public BeamDirectTestResource beam = BeamDirectTestResource.of();

    /**
     * Tests a round-trip on the data when writing to the data source using the given output properties, then
     * subsequently reading using the given input properties. This is the equivalent of two pipeline jobs.
     *
     * @param initialData The initial data set to write, then read.
     * @param outputProps The properties used to create the output runtime.
     * @param inputProps The properties used to create the input runtime.
     * @return The data returned from the round-trip.
     */
    protected static List<IndexedRecord> runRoundTripPipelines(BeamDirectTestResource beam, List<IndexedRecord> initialData,
            SimpleFileIoOutputProperties outputProps, SimpleFileIoInputProperties inputProps) {
        // Create the runtimes.
        SimpleFileIoOutputRuntime outputRuntime = new SimpleFileIoOutputRuntime();
        outputRuntime.initialize(null, outputProps);
        SimpleFileIoInputRuntime inputRuntime = new SimpleFileIoInputRuntime();
        inputRuntime.initialize(null, inputProps);

        // Use the runtime in a direct pipeline to test.
        PipelineOptions options = PipelineOptionsFactory.create();
        options.setRunner(DirectRunner.class);

        // Create a pipeline to write the records to the output.
        {
            final Pipeline p = beam.createPipeline();
            PCollection<IndexedRecord> input = p.apply(Create.<IndexedRecord> of(initialData));
            input.apply(outputRuntime);
            p.run().waitUntilFinish();
        }

        // Read the records that were written.
        try (DirectCollector<IndexedRecord> collector = DirectCollector.of()) {
            final Pipeline p = beam.createPipeline();
            PCollection<IndexedRecord> input = p.apply(inputRuntime);
            input.apply(collector);
            p.run().waitUntilFinish();

            // Return the list of records from the round trip.
            return collector.getRecords();
        }
    }

    protected static List<IndexedRecord> rewriteRecordsWithCsvSchema(List<IndexedRecord> in) {
        List<IndexedRecord> out = new ArrayList<>();
        for (IndexedRecord record : in) {
            String[] strings = new String[record.getSchema().getFields().size()];
            for (int i = 0; i < strings.length; i++)
                strings[i] = record.get(i) == null ? null : String.valueOf(record.get(i));
            out.add(ConvertToIndexedRecord.convertToAvro(strings));
        }
        return out;
    }

    protected static String[] rewriteRecordsAsCsvLines(List<IndexedRecord> in, String recordDelimiter, String fieldDelimiter) {
        String[] out = new String[in.size()];
        StringBuilder sb = new StringBuilder();
        int columns = (in.size() > 0) ? in.get(0).getSchema().getFields().size() : -1;
        for (int i = 0; i < out.length; i++) {
            sb.setLength(0);
            IndexedRecord record = in.get(i);
            if (columns > 0)
                sb.append(record.get(0));
            for (int col = 1; col < columns; col++) {
                sb.append(fieldDelimiter);
                sb.append(record.get(col));
            }
            // sb.append(recordDelimiter);
            out[i] = sb.toString();
        }
        return out;
    }

    /**
     * Basic unit test using all default values (except for the path) on an in-memory DFS cluster.
     */
    @Test
    public void testBasicDefaults() throws IOException {
        // The file that we will be creating.
        RecordSet rs = getSimpleTestData(0);
        String fileSpec = mini.getLocalFsNewFolder() + "output/";

        // Configure the components.
        SimpleFileIoOutputProperties outputProps = createOutputComponentProperties();
        outputProps.getDatasetProperties().path.setValue(fileSpec);
        SimpleFileIoInputProperties inputProps = createInputComponentProperties();
        inputProps.setDatasetProperties(outputProps.getDatasetProperties());

        List<IndexedRecord> actual = runRoundTripPipelines(beam, rs.getAllData(), outputProps, inputProps);

        // Generate the set of expected records. By default, CSV turns all columns into String and loses the original
        // column name.
        List<IndexedRecord> expected = rewriteRecordsWithCsvSchema(rs.getAllData());
        assertThat(expected, containsInAnyOrder(actual.toArray()));

        // Verify that the file on the filesystem was correctly written.
        mini.assertReadFile(
                mini.getLocalFs(),
                fileSpec,
                rewriteRecordsAsCsvLines(expected, inputProps.getDatasetProperties().getRecordDelimiter(),
                        inputProps.getDatasetProperties().getFieldDelimiter()));
    }

    /**
     * Test CSV with custom delimiters.
     */
    @Test
    public void testCsvWithDelimiters() throws IOException {
        // The file that we will be creating.
        RecordSet rs = getSimpleTestData(0);
        String fileSpec = mini.getLocalFsNewFolder() + "output/";

        // Configure the components.
        SimpleFileIoOutputProperties outputProps = createOutputComponentProperties();
        outputProps.getDatasetProperties().format.setValue(SimpleFileIoFormat.CSV);
        outputProps.getDatasetProperties().path.setValue(fileSpec);
        outputProps.getDatasetProperties().recordDelimiter.setValue(RecordDelimiterType.OTHER);
        outputProps.getDatasetProperties().specificRecordDelimiter.setValue("---");
        outputProps.getDatasetProperties().fieldDelimiter.setValue(FieldDelimiterType.OTHER);
        outputProps.getDatasetProperties().specificFieldDelimiter.setValue("|");
        SimpleFileIoInputProperties inputProps = createInputComponentProperties();
        inputProps.setDatasetProperties(outputProps.getDatasetProperties());

        List<IndexedRecord> actual = runRoundTripPipelines(beam, rs.getAllData(), outputProps, inputProps);

        // Generate the set of expected records. By default, CSV turns all columns into String and loses the original
        // column name.
        List<IndexedRecord> expected = rewriteRecordsWithCsvSchema(rs.getAllData());
        assertThat(expected, containsInAnyOrder(actual.toArray()));

        // Verify that the file on the filesystem was correctly written.
        mini.assertReadFile(
                "---",
                mini.getLocalFs(),
                fileSpec,
                rewriteRecordsAsCsvLines(expected, inputProps.getDatasetProperties().getRecordDelimiter(),
                        inputProps.getDatasetProperties().getFieldDelimiter()));
    }

    /**
     * Basic Avro test.
     */
    @Test
    public void testAvro() throws IOException {
        // The file that we will be creating.
        RecordSet rs = getSimpleTestData(0);
        String fileSpec = mini.getLocalFsNewFolder() + "output/";

        // Configure the components.
        SimpleFileIoOutputProperties outputProps = createOutputComponentProperties();
        outputProps.getDatasetProperties().format.setValue(SimpleFileIoFormat.AVRO);
        outputProps.getDatasetProperties().path.setValue(fileSpec);
        SimpleFileIoInputProperties inputProps = createInputComponentProperties();
        inputProps.setDatasetProperties(outputProps.getDatasetProperties());

        List<IndexedRecord> actual = runRoundTripPipelines(beam, rs.getAllData(), outputProps, inputProps);

        // Generate the set of expected records. By default, CSV turns all columns into String and loses the original
        // column name.
        List<IndexedRecord> expected = rs.getAllData();
        assertThat(actual, containsInAnyOrder(expected.toArray()));

        // Verify that the file on the filesystem was correctly written.
        // TODO(rskraba): verify independently
        // mini.assertReadFile(
        // mini.getLocalFs(),
        // fileSpec,
        // rewriteRecordsAsCsvLines(expected, inputProps.getDatasetProperties().recordDelimiter.getValue(),
        // inputProps.getDatasetProperties().fieldDelimiter.getValue()));
    }

    /**
     * Basic Parquet test.
     */
    @Test
    public void testParquet() throws IOException {
        // The file that we will be creating.
        RecordSet rs = getSimpleTestData(0);
        String fileSpec = mini.getLocalFsNewFolder() + "output/";

        // Configure the components.
        SimpleFileIoOutputProperties outputProps = createOutputComponentProperties();
        outputProps.getDatasetProperties().format.setValue(SimpleFileIoFormat.PARQUET);
        outputProps.getDatasetProperties().path.setValue(fileSpec);
        SimpleFileIoInputProperties inputProps = createInputComponentProperties();
        inputProps.setDatasetProperties(outputProps.getDatasetProperties());

        List<IndexedRecord> actual = runRoundTripPipelines(beam, rs.getAllData(), outputProps, inputProps);

        // Generate the set of expected records. By default, CSV turns all columns into String and loses the original
        // column name.
        List<IndexedRecord> expected = rs.getAllData();
        assertThat(actual, containsInAnyOrder(expected.toArray()));

        // Verify that the file on the filesystem was correctly written.
        // TODO(rskraba): verify independently from
        // mini.assertReadFile(
        // mini.getLocalFs(),
        // fileSpec,
        // rewriteRecordsAsCsvLines(expected, inputProps.getDatasetProperties().recordDelimiter.getValue(),
        // inputProps.getDatasetProperties().fieldDelimiter.getValue()));
    }
}
