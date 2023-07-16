/*
 * Copyright 2017 KG Soft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kgurgul.roy93group.utils.lifecycleawarelist

import android.annotation.SuppressLint
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView

/**
 * [android.arch.lifecycle.LiveData] observer for [ListLiveData] which will notify [adapter] about
 * list changes.
 *
 * @author kgurgul
 */
class ListLiveDataObserver(val adapter: RecyclerView.Adapter<*>) :
    Observer<ListLiveDataChangeEvent> {

    @SuppressLint("NotifyDataSetChanged")
    override fun onChanged(value: ListLiveDataChangeEvent) {
        when (value.listLiveDataState) {
            ListLiveDataState.CHANGED ->
                adapter.notifyDataSetChanged()

            ListLiveDataState.ITEM_RANGE_CHANGED ->
                adapter.notifyItemRangeChanged(value.startIndex, value.itemCount)

            ListLiveDataState.ITEM_RANGE_INSERTED ->
                adapter.notifyItemRangeInserted(value.startIndex, value.itemCount)

            ListLiveDataState.ITEM_RANGE_REMOVED ->
                adapter.notifyItemRangeRemoved(value.startIndex, value.itemCount)
        }
    }
}