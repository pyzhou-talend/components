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
package org.talend.components.common.runtime;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.Writer;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.common.BulkFileProperties;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.converter.IndexedRecordConverter;

import com.csvreader.CsvWriter;

/**
 * Generate bulk file
 */
public class BulkFileWriter<T extends IndexedRecord> implements Writer<T, Result> {

    protected RuntimeContainer container;

    private WriteOperation<Result> writeOperation;

    private Result result;

    protected BulkFileProperties bulkProperties;

    private CsvWriter csvWriter;

    private char separator = ',';

    private String charset = "UTF-8";

    private boolean isAppend;

    private transient IndexedRecordConverter<IndexedRecord, IndexedRecord> factory;

    public BulkFileWriter(WriteOperation<Result> writeOperation, BulkFileProperties bulkProperties, RuntimeContainer container) {
        this.writeOperation = writeOperation;
        this.container = container;
        this.bulkProperties = bulkProperties;
        this.isAppend = bulkProperties.append.getValue();
    }

    @Override
    public void open(String uId) throws IOException {
        this.result = new Result(uId);
        String filepath = bulkProperties.bulkFilePath.getStringValue();
        if (filepath == null || filepath.isEmpty()) {
            throw new RuntimeException("Please set a valid value for \"Bulk File Path\" field.");
        }
        File file = new File(bulkProperties.bulkFilePath.getStringValue());
        file.getParentFile().mkdirs();
        csvWriter = new CsvWriter(new OutputStreamWriter(new java.io.FileOutputStream(file, isAppend), charset), separator);

        fileIsEmpty = (file.length() == 0);
    }

    private boolean headerIsReady = false;

    private boolean fileIsEmpty = false;

    @Override
    public void write(T record) throws IOException {
        if (null == record) {
            return;
        }

        if (!headerIsReady && (!isAppend || fileIsEmpty)) {
            Schema schema = new Schema.Parser().parse(bulkProperties.schema.schema.getStringValue());

            if (AvroUtils.isIncludeAllFields(schema)) {
                schema = record.getSchema();
            }

            csvWriter.writeRecord(getHeaders(schema));
            headerIsReady = true;
        }

        List<String> values = getValues(record);
        csvWriter.writeRecord(values.toArray(new String[values.size()]));
        result.totalCount++;
    }

    public void flush() throws IOException {
        csvWriter.flush();
    }

    @Override
    public Result close() throws IOException {
        flush();
        csvWriter.close();
        return result;
    }

    @Override
    public WriteOperation<Result> getWriteOperation() {
        return writeOperation;
    }

    public String[] getHeaders(Schema schema) {
        List<String> headers = new ArrayList<String>();
        for (Schema.Field f : schema.getFields()) {
            headers.add(f.name());
        }
        return headers.toArray(new String[headers.size()]);
    }

    public List<String> getValues(T record) {
        List<String> values = new ArrayList<String>();
        for (Schema.Field f : record.getSchema().getFields()) {
            if (record.get(f.pos()) != null) {
                values.add(String.valueOf(record.get(f.pos())));
            } else {
                values.add("");
            }
        }
        return values;
    }
    
}
