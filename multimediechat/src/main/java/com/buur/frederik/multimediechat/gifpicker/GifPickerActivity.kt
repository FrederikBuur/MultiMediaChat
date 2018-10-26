package com.buur.frederik.multimediechat.gifpicker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.models.gif.GifData
import com.jakewharton.rxbinding2.widget.textChanges
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_gif_picker.*
import java.util.concurrent.TimeUnit

class GifPickerActivity: RxAppCompatActivity(), IGifOnClick {

    private val searchInputDebounce = 1000L

    private var adapter: GifAdapter? = null
    private var gifList: ArrayList<GifData>? = null
    private var controller: GifPickerController? = null

    private var currentDisposable: Disposable? = null
    private var searchListenerDisposable: Disposable? = null

    init {
        if (controller == null) {
            this.controller = GifPickerController()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gif_picker)
        setup()
    }

    private fun setup() {
        setupRecyclerView()
        fetchTrending()
        setupSearchListener()
    }

    private fun setupRecyclerView() {
//        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val gridLayoutManager =  GridLayoutManager(this, 2)
//        val staggeredGridLayoutManager =  StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
//        val flexBox = FlexboxLayoutManager(this)
//        flexBox.flexDirection = FlexDirection.COLUMN
//        flexBox.flexWrap = FlexWrap.WRAP

        this.gifList = ArrayList()
        if (adapter == null) {
            adapter = GifAdapter(this, this.gifList, this)
        }
        gifRecyclerView.layoutManager = gridLayoutManager
        gifRecyclerView.adapter = adapter
    }

    private fun setupSearchListener() {
        searchListenerDisposable = gifSearchField.textChanges()
                .compose(bindToLifecycle())
                .debounce(searchInputDebounce, TimeUnit.MILLISECONDS)
                .subscribe({ search ->
                    if (search.isNotEmpty()) {
                        searchGifs(search.toString())
                    }
                }, { error ->
                    Log.d(this.toString(), error.message)
                })
    }

    private fun fetchTrending() {
        disposeCurrentDisposable()
        currentDisposable = controller?.getTrendingGifs()
                ?.compose(bindToLifecycle())
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ gifResponse ->
                    updateGifList(gifResponse.data)
                }, {})
    }

    private fun searchGifs(search: String) {
        disposeCurrentDisposable()
        currentDisposable = controller?.getSearchGifs(search)
                ?.compose(bindToLifecycle())
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ gifResponse ->
                    this.gifList?.clear()
                    updateGifList(gifResponse.data)
                }, {})
    }

    override fun GifOnclick(gifUrl: String) {
        val returnIntent = Intent()
        returnIntent.putExtra(GIF_KEY, gifUrl)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    private fun updateGifList(gifList: ArrayList<GifData>) {
        this.gifList?.addAll(gifList)
        adapter?.notifyDataSetChanged()
    }

    private fun disposeCurrentDisposable() {
        if (currentDisposable?.isDisposed == false) {
            currentDisposable?.dispose()
        }
    }

    override fun onDestroy() {
        disposeCurrentDisposable()
        if (searchListenerDisposable?.isDisposed == false) {
            searchListenerDisposable?.dispose()
        }
        super.onDestroy()
    }

    companion object {
        const val GIF_KEY = "gifMessage"
    }

}