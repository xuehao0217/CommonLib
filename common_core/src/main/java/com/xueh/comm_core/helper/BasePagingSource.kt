/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xueh.comm_core.helper

import androidx.paging.PagingSource
import androidx.paging.PagingState


abstract class BasePagingSource<T : Any> : PagingSource<Int, T>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            //页码未定义置为1
            val nextPage = params.key ?: 1
            val datas = getDataList(nextPage)
            LoadResult.Page(
                data = datas,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (!datas.isNullOrEmpty()) nextPage + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? = null

    abstract suspend fun getDataList(page: Int): List<T>
}