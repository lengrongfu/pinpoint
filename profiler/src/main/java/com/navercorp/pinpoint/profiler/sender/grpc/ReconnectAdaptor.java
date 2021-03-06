/*
 * Copyright 2019 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.profiler.sender.grpc;

import com.navercorp.pinpoint.common.util.Assert;

import java.util.concurrent.Executor;

/**
 * @author Woonduk Kang(emeroad)
 */
public class ReconnectAdaptor implements Reconnector {
    private final Executor executor;
    private final ExponentialBackoffReconnectJob reconnectJobWrap;

    public ReconnectAdaptor(Executor executor, Runnable reconnectJob) {
        this.executor = Assert.requireNonNull(executor, "executor must not be null");
        Assert.requireNonNull(reconnectJob, "reconnectJob must not be null");
        reconnectJobWrap = wrapExponentialBackoffReconnectJob(reconnectJob);
    }

    private ExponentialBackoffReconnectJob wrapExponentialBackoffReconnectJob(Runnable runnable) {
        return new ExponentialBackoffReconnectJob(runnable);
    }

    @Override
    public void reset() {
        reconnectJobWrap.resetBackoffNanos();
    }

    @Override
    public void reconnect() {
        executor.execute(reconnectJobWrap);
    }
}
