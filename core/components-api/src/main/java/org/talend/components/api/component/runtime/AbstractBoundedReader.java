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
package org.talend.components.api.component.runtime;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.apache.avro.generic.IndexedRecord;
import org.joda.time.Instant;

/**
 * Basic implementation of {@link BoundedReader}, useful for those readers that don't require sharding.
 */
public abstract class AbstractBoundedReader<T extends IndexedRecord> implements BoundedReader<T> {

    private final BoundedSource source;

    protected AbstractBoundedReader(BoundedSource source) {
        this.source = source;
    }

    @Override
    public BoundedSource getCurrentSource() {
        // This is guaranteed not to change since an unsharded input will never support dynamic load rebalancing.
        return source;
    }

    @Override
    public Double getFractionConsumed() {
        // Not supported
        return null;
    }

    @Override
    public BoundedSource splitAtFraction(double fraction) {
        // Not supported
        return null;
    }

    @Override
    public Instant getCurrentTimestamp() throws NoSuchElementException {
        // NB. Should return BoundedWindow.TIMESTAMP_MIN_VALUE
        return null;
    }

    @Override
    public void close() throws IOException {
        // TODO:
    }
}
