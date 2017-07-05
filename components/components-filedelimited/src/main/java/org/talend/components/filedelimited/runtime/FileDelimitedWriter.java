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
package org.talend.components.filedelimited.runtime;

import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.Writer;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.filedelimited.tfileoutputdelimited.TFileOutputDelimitedProperties;
import org.talend.daikon.avro.AvroUtils;

import com.talend.csv.CSVWriter;

public class FileDelimitedWriter implements Writer<IndexedRecord, Result> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileDelimitedWriter.class);

    private final FileDelimitedSink sink;

    private WriteOperation<Result> writeOperation;

    FileOutputDelimitedRuntime outputRuntime;

    TFileOutputDelimitedProperties props;

    private transient Schema recordSchema;

    private Result result;

    private CSVWriter csvWriter;

    private java.io.Writer writer;

    private int currentRowNo = 0;

    FileDelimitedWriter(FileDelimitedWriteOperation writeOperation, RuntimeContainer container) {
        this.writeOperation = writeOperation;
        sink = writeOperation.getSink();
        props = sink.getOutputProperties();
        outputRuntime = new FileOutputDelimitedRuntime(props);
    }

    @Override
    public void open(String uId) throws IOException {
        this.result = new Result(uId);
        if (recordSchema == null) {
            recordSchema = props.main.schema.getValue();
            if (AvroUtils.isIncludeAllFields(recordSchema)) {
                // if design schema include dynamic,need to get schema from record
                recordSchema = null;
            }
        }
        if (props.csvOptions.getValue()) {
            csvWriter = outputRuntime.getCsvWriter();

        } else {
            writer = outputRuntime.getWriter();
        }
        if (recordSchema != null) {
            if (csvWriter != null) {
                outputRuntime.writeHeader(csvWriter, recordSchema);
            } else {
                outputRuntime.writeHeader(writer, recordSchema);
            }
        }
    }

    @Override
    public void write(IndexedRecord record) throws IOException {
        if (record == null) {
            return;
        }
        // This for dynamic which would get schema from the first record
        if (recordSchema == null) {
            recordSchema = record.getSchema();
            if (csvWriter != null) {
                outputRuntime.writeHeader(csvWriter, recordSchema);
            } else {
                outputRuntime.writeHeader(writer, recordSchema);
            }
        }

        result.totalCount++;
        if (props.csvOptions.getValue()) {
            if (!props.targetIsStream.getValue() && props.split.getValue()) {
                if (currentRowNo != 0 && currentRowNo % props.splitEvery.getValue() == 0) {
                    csvWriter.close();
                    // close original outputStream
                    csvWriter = outputRuntime.getCsvWriter();
                    outputRuntime.writeHeader(csvWriter, recordSchema);
                }
                currentRowNo++;
            }
            csvWriter.writeNext(getValues(record));
            if (props.rowMode.getValue()) {
                outputRuntime.writer.write(outputRuntime.strWriter.getBuffer().toString());
                outputRuntime.strWriter.getBuffer().delete(0, outputRuntime.strWriter.getBuffer().length());
            }
            if (props.flushOnRow.getValue()) {

                if (result.getTotalCount() % props.flushOnRowNum.getValue() == 0) {
                    if (props.rowMode.getValue()) {
                        outputRuntime.writer.flush();
                    } else {
                        csvWriter.flush();
                    }
                }
            }

        } else {
            if (!props.targetIsStream.getValue() && props.split.getValue()) {
                if (!props.targetIsStream.getValue() && props.split.getValue()) {
                    writer.close();
                    // close original outputStream

                    writer = outputRuntime.getWriter();
                    outputRuntime.writeHeader(writer, recordSchema);
                }
                currentRowNo++;
            }
            writer.write(getRowString(record));
            if (props.flushOnRow.getValue()) {
                if (result.getTotalCount() % props.flushOnRowNum.getValue() == 0) {
                    writer.flush();
                }
            }
        }
        LOGGER.debug("Writing the record: " + result.totalCount);

    }

    @Override
    public Result close() throws IOException {
        if (props.csvOptions.getValue()) {
            if (props.targetIsStream.getValue()) {
                if (props.rowMode.getValue()) {
                    if (csvWriter != null) {
                        csvWriter.close();
                    }
                } else {
                    if (csvWriter != null) {
                        csvWriter.flush();
                    }
                }
                if (outputRuntime.writer != null) {
                    outputRuntime.writer.flush();
                }
                if (outputRuntime.streamWriter != null) {
                    outputRuntime.streamWriter.flush();
                }
            } else {
                if (csvWriter != null) {
                    csvWriter.close();
                }
                if (props.rowMode.getValue()) {
                    if (outputRuntime.writer != null) {
                        outputRuntime.writer.flush();
                        outputRuntime.writer.close();
                    }
                }
            }

        } else {
            if (props.targetIsStream.getValue()) {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            } else {
                if (writer != null) {
                    writer.flush();
                    outputRuntime.streamWriter.flush();
                    writer.close();
                }
            }
        }
        if (!props.targetIsStream.getValue() && props.deleteEmptyFile.getValue() && outputRuntime.isFileGenerated
                && result.totalCount == 0) {
            // Delete empty file, if no record is written
            if (props.compress.getValue() && !props.append.getValue() && !props.split.getValue()) {
                outputRuntime.zipFile.delete();
            } else {
                outputRuntime.file.delete();
            }

        }
        return result;
    }

    @Override
    public WriteOperation<Result> getWriteOperation() {
        return writeOperation;
    }

    private String[] getValues(IndexedRecord record) {
        String[] values = new String[recordSchema.getFields().size()];
        for (Schema.Field field : recordSchema.getFields()) {
            Object value = record.get(field.pos());
            values[field.pos()] = value == null ? null : String.valueOf(value);
        }
        return values;
    }

    private String getRowString(IndexedRecord record) {
        StringBuilder sb = new StringBuilder();
        for (Schema.Field field : recordSchema.getFields()) {
            Object value = record.get(field.pos());
            if (value != null) {
                sb.append(value);
            }
            if (field.pos() != (recordSchema.getFields().size() - 1)) {
                sb.append(outputRuntime.fieldSeparator);
            }
        }
        sb.append(outputRuntime.rowSeparator);
        return sb.toString();
    }

}
