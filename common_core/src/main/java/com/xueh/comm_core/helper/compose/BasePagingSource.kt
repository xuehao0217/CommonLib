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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update


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
            LogUtils.e("BasePagingSource", "${e}")
//            ToastUtils.showLong("${e}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>) = null


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
        if (!it) {
            ToastUtils.showShort("网络异常，请检查网络设置")
        }
    }
}.cachedIn(viewModelScope)


//https://juejin.cn/post/7416848881407180838
fun <T : Any> Flow<PagingData<T>>.modifier(
    onEach: PagingDataModifier<T>.(PagingData<T>) -> Unit = {
        clearRemove()
        clearUpdate()
    },
    getID: (T) -> Any,
): PagingDataModifier<T> {
    return PagingDataModifier(
        flow = this,
        onEach = onEach,
        getID = getID,
    )
}

class PagingDataModifier<T : Any> internal constructor(
    flow: Flow<PagingData<T>>,
    private val onEach: PagingDataModifier<T>.(PagingData<T>) -> Unit,
    private val getID: (T) -> Any,
) {
    private val _removeFlow = MutableStateFlow<Set<Any>>(emptySet())
    private val _updateFlow = MutableStateFlow<Map<Any, T>>(emptyMap())
    private val _addHeaderFlow = MutableStateFlow<List<T>>(emptyList())
    private val _addFooterFlow = MutableStateFlow<List<T>>(emptyList())

    /** 数据流 */
    val flow = flow
        .onEach { onEach(it) }
        .modify()

    /**
     * 移除ID为[id]的项
     */
    fun remove(id: Any) {
        _removeFlow.update { value ->
            if (value.contains(id)) {
                value
            } else {
                value + id
            }
        }
    }

    /**
     * 清空所有删除的项
     */
    fun clearRemove() {
        _removeFlow.update { emptySet() }
    }

    /**
     * 更新项
     */
    fun update(item: T) {
        _updateFlow.update { value ->
            val id = getID(item)
            if (value[id] == item) {
                value
            } else {
                value + (id to item)
            }
        }
    }

    /**
     * 清空所有更新的项
     */
    fun clearUpdate() {
        _updateFlow.update { emptyMap() }
    }

    /**
     * 添加addHeader
     */
    fun addHeader(item: T) {
        _addHeaderFlow.update { value ->
            if (!value.contains(item)) {
                value + item
            } else {
                value
            }
        }
    }


    /**
     * 添加addFooter
     */
    fun addFooter(item: T) {
        _addFooterFlow.update { value ->
            if (!value.contains(item)) {
                value + item
            } else {
                value
            }
        }
    }

    private fun Flow<PagingData<T>>.modify(): Flow<PagingData<T>> {
        return combine(_removeFlow) { data, holder ->
            data.takeIf { holder.isEmpty() }
                ?: data.filter { item ->
                    !holder.contains(getID(item))
                }
        }.combine(_updateFlow) { data, holder ->
            data.takeIf { holder.isEmpty() }
                ?: data.map { item ->
                    holder[getID(item)] ?: item
                }
        }.combine(_addHeaderFlow) { data, holder ->
            holder.fold(data) { paging, t ->
                paging.insertHeaderItem(
                    item = t
                )
            }
        }.combine(_addFooterFlow) { pagingData, addFooter ->
            addFooter.fold(pagingData) { paging, t ->
                paging.insertFooterItem(
                    item = t
                )
            }
        }
    }
}


fun <Key : Any, Value : Any> PagerFlow(
    pageSize: Int = 20,
    prefetchDistance: Int = pageSize,
    enablePlaceholders: Boolean = false,
    initialLoadSize: Int = pageSize,
    maxSize: Int = PagingConfig.MAX_SIZE_UNBOUNDED,
    jumpThreshold: Int = PagingSource.LoadResult.Page.COUNT_UNDEFINED,
    initialKey: Key? = null,
    pagingSourceFactory: () -> PagingSource<Key, Value>,
): Flow<PagingData<Value>> {
    return Pager(
        config = PagingConfig(
            pageSize = pageSize,
            prefetchDistance = prefetchDistance,
            enablePlaceholders = enablePlaceholders,
            initialLoadSize = initialLoadSize,
            maxSize = maxSize,
            jumpThreshold = jumpThreshold,
        ),
        initialKey = initialKey,
        pagingSourceFactory = pagingSourceFactory,
    ).flow
}