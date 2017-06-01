/*
 * Copyright (c) 2002-2017 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.impl.index.schema;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.neo4j.collection.primitive.PrimitiveLongIterator;
import org.neo4j.cursor.RawCursor;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.helpers.collection.BoundedIterable;
import org.neo4j.index.internal.gbptree.Hit;
import org.neo4j.index.internal.gbptree.Layout;
import org.neo4j.index.internal.gbptree.RecoveryCleanupWorkCollector;
import org.neo4j.io.pagecache.IOLimiter;
import org.neo4j.io.pagecache.PageCache;
import org.neo4j.kernel.api.exceptions.index.IndexEntryConflictException;
import org.neo4j.kernel.api.exceptions.index.IndexNotApplicableKernelException;
import org.neo4j.kernel.api.index.IndexAccessor;
import org.neo4j.kernel.api.index.IndexUpdater;
import org.neo4j.kernel.api.index.PropertyAccessor;
import org.neo4j.kernel.api.schema.IndexQuery;
import org.neo4j.kernel.impl.api.index.IndexUpdateMode;
import org.neo4j.kernel.impl.util.Cursors;
import org.neo4j.storageengine.api.schema.IndexReader;
import org.neo4j.storageengine.api.schema.IndexSampler;

public class NativeSchemaNumberIndexAccessor<KEY extends NumberKey, VALUE extends NumberValue>
        extends NativeSchemaNumberIndex<KEY,VALUE> implements IndexAccessor
{
    private final NativeSchemaNumberIndexUpdater<KEY,VALUE> singleUpdater;

    NativeSchemaNumberIndexAccessor( PageCache pageCache, File storeFile,
            Layout<KEY,VALUE> layout, RecoveryCleanupWorkCollector recoveryCleanupWorkCollector ) throws IOException
    {
        super( pageCache, storeFile, layout );
        singleUpdater = new NativeSchemaNumberIndexUpdater<>( layout.newKey(), layout.newValue() );
        instantiateTree( recoveryCleanupWorkCollector );
    }

    @Override
    public void drop() throws IOException
    {
        throw new UnsupportedOperationException( "Implement me" );
    }

    @Override
    public IndexUpdater newUpdater( IndexUpdateMode mode )
    {
        try
        {
            return singleUpdater.initialize( tree.writer(), true );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    @Override
    public void flush() throws IOException
    {
        // todo remove this from interface completely
        throw new UnsupportedOperationException( "Implement me" );
    }

    @Override
    public void force() throws IOException
    {
        // TODO add IOLimiter arg
        tree.checkpoint( IOLimiter.unlimited() );
    }

    @Override
    public void close() throws IOException
    {
        closeTree();
    }

    @Override
    public IndexReader newReader()
    {
        return new NativeSchemaNumberIndexReader();
    }

    @Override
    public BoundedIterable<Long> newAllEntriesReader()
    {
        throw new UnsupportedOperationException( "Implement me" );
    }

    @Override
    public ResourceIterator<File> snapshotFiles() throws IOException
    {
        throw new UnsupportedOperationException( "Implement me" );
    }

    @Override
    public void verifyDeferredConstraints( PropertyAccessor propertyAccessor )
            throws IndexEntryConflictException, IOException
    {
        throw new UnsupportedOperationException( "Implement me" );
    }

    private class NativeSchemaNumberIndexReader implements IndexReader
    {
        private final KEY treeKeyFrom = layout.newKey();
        private final KEY treeKeyTo = layout.newKey();

        @Override
        public void close()
        {
            throw new UnsupportedOperationException( "Implement me" );
        }

        @Override
        public long countIndexedNodes( long nodeId, Object... propertyValues )
        {
            treeKeyFrom.from( nodeId, propertyValues );
            treeKeyTo.from( nodeId, propertyValues );
            try ( RawCursor<Hit<KEY,VALUE>,IOException> seeker = tree.seek( treeKeyFrom, treeKeyTo ) )
            {
                return Cursors.count( seeker );
            }
            catch ( IOException e )
            {
                throw new UncheckedIOException( e );
            }
        }

        @Override
        public IndexSampler createSampler()
        {
            throw new UnsupportedOperationException( "Implement me" );
        }

        @Override
        public PrimitiveLongIterator query( IndexQuery... predicates ) throws IndexNotApplicableKernelException
        {
            throw new UnsupportedOperationException( "Implement me" );
        }
    }
}
