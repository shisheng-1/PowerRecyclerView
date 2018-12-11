package com.lovejjfg.swiperefreshrecycleview.activity

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView.LayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.lovejjfg.powerrecycle.PowerAdapter
import com.lovejjfg.powerrecycle.SelectPowerAdapter
import com.lovejjfg.powerrecycle.holder.PowerHolder
import com.lovejjfg.powerrecycle.model.ISelect
import com.lovejjfg.swiperefreshrecycleview.R
import com.lovejjfg.swiperefreshrecycleview.model.Cat
import kotlinx.android.synthetic.main.activity_list.recycleView
import kotlinx.android.synthetic.main.activity_list.refresh
import kotlinx.android.synthetic.main.holder_cat.view.catCheckState
import kotlinx.android.synthetic.main.holder_cat.view.catImage
import kotlinx.android.synthetic.main.holder_cat.view.catName

/**
 * Created by joe on 2018/11/20.
 * Email: lovejjfg@gmail.com
 */
class CatsActivity : BaseSelectActivity<Cat>() {
    override fun startRefresh() {
        recycleView.postDelayed(
            {
                initData(false)
                refresh.isRefreshing = false
            }, 1000
        )
    }

    override fun initManager(): LayoutManager {
        return GridLayoutManager(this, 2)
    }

    override fun initAdapter(): PowerAdapter<Cat> {
        return CatsAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectAdapter.totalCount = 100
        selectAdapter.setLoadMoreListener {
            recycleView.postDelayed(
                {
                    appendData()
                    refresh.isRefreshing = false
                }, 1000
            )
        }
        initToolbar("猫猫图", R.menu.menu_select) {
            when (it.itemId) {
                R.id.select_single -> {
                    selectAdapter.setSelectedMode(ISelect.SINGLE_MODE)
                    selectAdapter.setCurrentPositions(0, 1, 2, 3)
                }
                R.id.select_mul -> {
                    selectAdapter.setSelectedMode(ISelect.MULTIPLE_MODE)
                    selectAdapter.setCurrentPositions(0, 1, 2, 3)
                }
                R.id.delete_select -> selectAdapter.deleteSelectedItems()
                R.id.revert_select -> selectAdapter.revertAllSelected()
                R.id.selet_all -> selectAdapter.selectAll()
                R.id.unselect_all -> selectAdapter.resetAll()
                R.id.default_select -> selectAdapter.isCancelAble = !selectAdapter.isCancelAble
            }
            return@initToolbar true
        }

        initData()
    }

    private fun initData(full: Boolean = true) {
        val cats = arrayOf(R.mipmap.cat1, R.mipmap.cat2, R.mipmap.cat3, R.mipmap.cat4, R.mipmap.cat5, R.mipmap.cat6)
        val list = ArrayList<Cat>(30)
        if (full) {
            for (i in 0..29) {
                list.add(Cat(cats[i % cats.size], "黑猫$i"))
            }
        } else {
            for (i in 4..29) {
                list.add(Cat(cats[i % cats.size], "黑猫$i"))
            }
        }

        selectAdapter.list = list
    }

    private fun appendData() {
        val cats = arrayOf(R.mipmap.cat1, R.mipmap.cat2, R.mipmap.cat3, R.mipmap.cat4, R.mipmap.cat5, R.mipmap.cat6)
        val list = ArrayList<Cat>(30)
        for (i in 0..9) {
            list.add(Cat(cats[i % cats.size], "黑猫$i"))
        }
        selectAdapter.appendList(list)
    }

    class CatsAdapter : SelectPowerAdapter<Cat>(ISelect.MULTIPLE_MODE, true, true) {
        override fun onViewHolderCreate(parent: ViewGroup, viewType: Int): PowerHolder<Cat> {
            return CatHolder(LayoutInflater.from(parent.context).inflate(R.layout.holder_cat, parent, false))
        }

        override fun onViewHolderBind(holder: PowerHolder<Cat>, position: Int) {
            holder.onBind(list[position], isSelectMode)
        }

        override fun onViewHolderBind(holder: PowerHolder<Cat>, position: Int, payloads: MutableList<Any>) {
            println("onViewHolderBind:$payloads")
            holder.onPartBind(list[position], isSelectMode, payloads)
        }

        override fun getMaxSelectCount(): Int {
            return 3
        }

        override fun onReceivedMaxSelectCount(count: Int) {
            Log.e("CatsAdapter", "onReceivedMaxSelectCount:$count")
        }

        override fun areContentsTheSame(oldItem: Cat, newItem: Cat): Boolean {
            return oldItem.src == newItem.src
        }

        override fun areItemsTheSame(oldItem: Cat, newItem: Cat): Boolean {
            return oldItem.name == newItem.name
        }
    }

    class CatHolder(view: View) : PowerHolder<Cat>(view) {
        private val catSrc = view.catImage
        private val catState = view.catCheckState
        private val catName = view.catName
        override fun onBind(t: Cat, isSelectMode: Boolean) {
            println("onBind:${t.name}")
            catSrc.setImageResource(t.src)
            catState.isChecked = t.isSelected
            catState.isVisible = isSelectMode
            catName.text = t.name
        }

        override fun onPartBind(t: Cat, isSelectMode: Boolean, payloads: MutableList<Any>) {
            println("onPartBind:${t.name}")
            catState.isVisible = isSelectMode
            catState.isChecked = t.isSelected
        }
    }
}
