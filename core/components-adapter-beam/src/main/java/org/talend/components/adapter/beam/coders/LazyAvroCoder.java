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
package org.talend.components.adapter.beam.coders;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.AtomicCoder;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.CoderException;
import org.talend.components.adapter.beam.transform.ConvertToIndexedRecord;

public class LazyAvroCoder<T> extends AtomicCoder<Object> {

    private final String id;

    private transient AvroCoder internalAvroCoder;

    private final static HashMap<String, Schema> schemaRegistry = new HashMap<>();

    private final static AtomicInteger count = new AtomicInteger();

    protected LazyAvroCoder() {
        this("Lazy" + count.getAndIncrement());
    }

    protected LazyAvroCoder(String id) {
        this.id = id;
    }

    public static LazyAvroCoder of() {
        return new LazyAvroCoder();
    }

    public static LazyAvroCoder of(String id) {
        return new LazyAvroCoder(id);
    }

    public String getAvroSchemaId() {
        return id;
    }

    protected Schema getSchema() {
        Schema schema = schemaRegistry.get(id);
        if (schema == null) {
            // This should not occur, since the encode must be called before the schema is available. If it happens, it
            // is an unrecoverable error.
            throw new Pipeline.PipelineExecutionException(new NoSuchElementException("No schema found for " + id));
        }
        return schema;
    }

    @Override
    public void encode(Object value, OutputStream outputStream) throws CoderException, IOException {
        if (internalAvroCoder == null) {
            Schema s = (value instanceof IndexedRecord) ? ((IndexedRecord) value).getSchema() : ConvertToIndexedRecord
                    .convertToAvro(value).getSchema();
            schemaRegistry.put(id, s);
            internalAvroCoder = AvroCoder.of(s);
        }
        if (value instanceof IndexedRecord)
            internalAvroCoder.encode(value, outputStream);
        else
            internalAvroCoder.encode(ConvertToIndexedRecord.convertToAvro(value), outputStream);
    }

    @Override
    public T decode(InputStream inputStream) throws CoderException, IOException {
        if (internalAvroCoder == null) {
            internalAvroCoder = AvroCoder.of(getSchema());
        }
        return (T) internalAvroCoder.decode(inputStream);
    }

}
