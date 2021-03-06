/*
 * Copyright (c) "Neo4j"
 * Neo4j Sweden AB [http://neo4j.com]
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
package org.neo4j.kernel.impl.transaction.state.storeview;

import java.util.function.IntPredicate;
import javax.annotation.Nullable;

import org.neo4j.internal.helpers.collection.Visitor;
import org.neo4j.internal.index.label.RelationshipTypeScanStore;
import org.neo4j.io.pagecache.tracing.cursor.PageCursorTracer;
import org.neo4j.lock.LockService;
import org.neo4j.memory.MemoryTracker;
import org.neo4j.storageengine.api.EntityTokenUpdate;
import org.neo4j.storageengine.api.EntityUpdates;
import org.neo4j.storageengine.api.StorageReader;

public class RelationshipTypeViewRelationshipStoreScan<FAILURE extends Exception> extends RelationshipStoreScan<FAILURE>
{
    private final RelationshipTypeScanStore relationshipTypeScanStore;
    private final PageCursorTracer cursorTracer;

    public RelationshipTypeViewRelationshipStoreScan( StorageReader storageReader, LockService locks,
            RelationshipTypeScanStore relationshipTypeScanStore,
            @Nullable Visitor<EntityTokenUpdate,FAILURE> relationshipTypeUpdateVisitor,
            @Nullable Visitor<EntityUpdates,FAILURE> propertyUpdatesVisitor,
            int[] relationshipTypeIds, IntPredicate propertyKeyIdFilter, PageCursorTracer cursorTracer, MemoryTracker memoryTracker )
    {
        super( storageReader, locks, relationshipTypeUpdateVisitor, propertyUpdatesVisitor, relationshipTypeIds, propertyKeyIdFilter, cursorTracer,
                memoryTracker );
        this.relationshipTypeScanStore = relationshipTypeScanStore;
        this.cursorTracer = cursorTracer;
    }

    @Override
    protected EntityIdIterator getEntityIdIterator()
    {
        return new TokenScanViewIdIterator<>( relationshipTypeScanStore.newReader(), relationshipTypeIds, entityCursor, cursorTracer );
    }
}
