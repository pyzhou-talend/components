package org.talend.components.adapter.beam.coders;

import java.util.List;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.coders.CoderProvider;
import org.apache.beam.sdk.coders.CoderProviderRegistrar;
import org.apache.beam.sdk.coders.CoderProviders;
import org.apache.beam.sdk.values.TypeDescriptor;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableList;

/**
 * A {@link CoderProviderRegistrar} for {@link LazyAvroCoder}.
 */
@AutoService(CoderProviderRegistrar.class)
public class LazyAvroCoderProviderRegistrar implements CoderProviderRegistrar {

    @Override
    public List<CoderProvider> getCoderProviders() {
        return ImmutableList.of(CoderProviders.forCoder(TypeDescriptor.of(IndexedRecord.class), LazyAvroCoder.of()));
    }
}
