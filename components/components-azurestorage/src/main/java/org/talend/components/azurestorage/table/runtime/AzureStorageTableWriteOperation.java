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
package org.talend.components.azurestorage.table.runtime;

import java.util.Map;

import org.apache.avro.generic.IndexedRecord;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.Sink;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.Writer;
import org.talend.components.api.container.RuntimeContainer;

public class AzureStorageTableWriteOperation implements WriteOperation<Result> {

    private static final long serialVersionUID = 7869580178743633180L;

    protected RuntimeContainer runtime;

    protected AzureStorageTableSink sink;

    public AzureStorageTableWriteOperation(Sink sink) {
        this.sink = (AzureStorageTableSink) sink;
    }

    @Override
    public void initialize(RuntimeContainer adaptor) {
        this.runtime = adaptor;
    }

    @Override
    public Map<String, Object> finalize(Iterable<Result> writerResults, RuntimeContainer adaptor) {
        return Result.accumulateAndReturnMap(writerResults);
    }

    @Override
    public Writer<IndexedRecord, Result> createWriter(RuntimeContainer adaptor) {
        return new AzureStorageTableWriter(this, adaptor);
    }

    @Override
    public AzureStorageTableSink getSink() {
        return this.sink;
    }

}
