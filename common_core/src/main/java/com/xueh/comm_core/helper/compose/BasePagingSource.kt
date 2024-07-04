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

package com.xueh.comm_core.helper.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.helper.isNotEmpty
import kotlinx.coroutines.flow.filter


abstract class BasePagingSource<T : Any> : PagingSource<Int, T>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            //页码未定义置为1
            val nextPage = params.key ?: 1
            val datas = getDataList(nextPage)
            LoadResult.Page(
                data = datas,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (datas.isNotEmpty()) nextPage + 1 else null
            )
        } catch (e: Exception) {
            LogUtils.e("BasePagingSource","${e}")
//            ToastUtils.showLong("${e}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>)=null


    abstract suspend fun getDataList(page: Int): List<T>
}




//val datas = Pager(PagingConfig(pageSize = 20)) {
//    object : BasePagingSource<HomeEntity.Data>() {
//        override suspend fun getDataList(page: Int) = api.getHome(page).data.datas
//    }
//}.flow.cachedIn(viewModelScope)

fun <T : Any> ViewModel.pager(
    pagingConfig: ((PagingConfig) -> Unit) = { },
    getDatas: suspend (page: Int) -> List<T>
) = Pager(PagingConfig(pageSize = 20).apply {
    pagingConfig.invoke(this)
}) {
    object : BasePagingSource<T>() {
        override suspend fun getDataList(page: Int) = getDatas.invoke(page)
    }
}.flow.filter {
    NetworkUtils.isConnected().also {
        if (!it){
            ToastUtils.showShort("网络异常，请检查网络设置")
        }
    }
}.cachedIn(viewModelScope)