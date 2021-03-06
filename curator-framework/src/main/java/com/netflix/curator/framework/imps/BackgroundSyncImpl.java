/*
 *
 *  Copyright 2011 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.curator.framework.imps;

import com.netflix.curator.TimeTrace;
import com.netflix.curator.framework.api.CuratorEventType;
import org.apache.zookeeper.AsyncCallback;

class BackgroundSyncImpl implements BackgroundOperation<String>
{
    private final CuratorFrameworkImpl client;
    private final Object context;

    BackgroundSyncImpl(CuratorFrameworkImpl client, Object context)
    {
        this.client = client;
        this.context = context;
    }

    @Override
    public void performBackgroundOperation(final OperationAndData<String> operationAndData) throws Exception
    {
        final TimeTrace trace = client.getZookeeperClient().startTracer("BackgroundSyncImpl");
        client.getZooKeeper().sync
        (
            operationAndData.getData(),
            new AsyncCallback.VoidCallback()
            {
                @Override
                public void processResult(int rc, String path, Object ctx)
                {
                    trace.commit();
                    CuratorEventImpl event = new CuratorEventImpl(client, CuratorEventType.SYNC, rc, path, null, ctx, null, null, null, null, null);
                    client.processBackgroundOperation(operationAndData, event);
                }
            },
            context
        );
    }
}
