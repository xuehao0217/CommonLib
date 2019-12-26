/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xueh.comm_core.utils.rx.rxjava.impl;

import java.util.concurrent.ExecutionException;

/**
 * 在IO线程中执行的任务
 */
public interface IRxIOTask<T, R> {

    /**
     * 在IO线程中执行
     * @param t 任务执行的入参
     * @return  R 任务执行的出参
     */
    R doInIOThread(T t) throws ExecutionException, InterruptedException;
}
