package com.buur.frederik.multimediechat.gifpicker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.models.gif.GifData
import com.jakewharton.rxbinding2.widget.textChanges
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_gif_picker.*
import retrofit2.HttpException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class GifPickerActivity : RxAppCompatActivity(), IGifOnClick {

    private val searchInputDebounce = 1000L
    private val tag = " GifPickerActivity"

    private var adapter: GifAdapter? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var gifList: ArrayList<GifData>? = null
    private var controller: GifPickerController? = null

    private var isFetching = AtomicBoolean(false)
    private var gifTotalCount: Int = 0
    private var lastSearch: String? = null

    private var currentDisposable: Disposable? = null
    private var searchListenerDisposable: Disposable? = null

    private var shouldFetchMore: Boolean = false
        get() {
            return gifTotalCount.minus(GifPickerController.gifFetchAmount) > gifList?.count() ?: 0
        }

    init {
        if (controller == null) {
            this.controller = GifPickerController()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gif_picker)

        setup()

        savedInstanceState?.let { state ->
            state.getParcelableArrayList<GifData>(GIF_LIST_KEY)?.let {
                updateGifList(it)
            }
            this.gifTotalCount = state.getInt("gifTotalCount")
            this.lastSearch = state.getString("lastSearch")
        } ?: kotlin.run {
            fetchTrending()
        }

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelableArrayList(GIF_LIST_KEY, this.gifList)
        outState?.putInt("gifTotalCount", gifTotalCount)
        outState?.putString("lastSearch", lastSearch)
        super.onSaveInstanceState(outState)
    }

    private fun setup() {
        setupRecyclerView()
        setupSearchListener()
    }

    private fun setupRecyclerView() {
        gridLayoutManager = GridLayoutManager(this, 2)
        this.gifList = ArrayList()
        if (adapter == null) {
            adapter = GifAdapter(this, this.gifList, this)
        }
        gifRecyclerView.layoutManager = gridLayoutManager
        gifRecyclerView.adapter = adapter
        setupScrollListener()
    }

    private fun setupSearchListener() {
        searchListenerDisposable = gifSearchField.textChanges()
                .compose(bindToLifecycle())
                .debounce(searchInputDebounce, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ search ->
                    if (search.isNotEmpty()) {
                        searchGifs(search.toString(), true)
                    }
                }, { error ->
                    Log.d(tag, error.message)
                })
    }

    private fun setupScrollListener() {
        gifRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (this@GifPickerActivity.gifList?.isEmpty() == true) return
                gifRecyclerView?.post {
                    gridLayoutManager?.let { layoutManager ->
                        val totalCount = layoutManager.itemCount
                        val lastVisible = layoutManager.findLastVisibleItemPosition()

                        if (lastVisible > totalCount.minus(5) && !isFetching.get() && shouldFetchMore) {
                            lastSearch?.let {
                                searchGifs(it, false, gifList?.count() ?: 0)
                            } ?: kotlin.run {
                                fetchTrending(gifList?.count() ?: 0)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun fetchTrending(offset: Int = 0) {
        disposeCurrentDisposable()
        showGifProgress(true)
        currentDisposable = controller?.getTrendingGifs(offset)
                ?.compose(bindToLifecycle())
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doOnSubscribe {
                    isFetching.set(true)
                }
                ?.doOnComplete {
                    isFetching.set(false)
                }
                ?.subscribe({ gifResponse ->
                    updateGifList(gifResponse.data)
                    gifTotalCount = gifResponse.pagination.total_count
                    showGifProgress(false)
                }, {
                    it
                })
    }

    private fun searchGifs(search: String, newSearch: Boolean, offset: Int = 0) {
        disposeCurrentDisposable()
        lastSearch = search
        showGifProgress(true)
        currentDisposable = controller?.getSearchGifs(search, offset)
                ?.compose(bindToLifecycle())
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doOnSubscribe {
                    isFetching.set(true)
                }
                ?.doOnComplete {
                    isFetching.set(false)
                }
                ?.subscribe({ gifResponse ->
                    if (newSearch) this.gifList?.clear()
                    gifTotalCount = gifResponse.pagination.total_count
                    updateGifList(gifResponse.data)
                    showGifProgress(false)
                }, {
                    it
                })
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

    private fun showGifProgress(shouldShow: Boolean) {
        if (shouldShow) {
            gifProgressBar.visibility = View.VISIBLE
        } else {
            gifProgressBar.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onPause() {
        super.onPause()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
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
        const val GIF_LIST_KEY = "gifList"
    }

}