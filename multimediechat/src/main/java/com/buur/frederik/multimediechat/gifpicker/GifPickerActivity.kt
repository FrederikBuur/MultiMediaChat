package com.buur.frederik.multimediechat.gifpicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.models.gif.GifData
import com.jakewharton.rxbinding3.widget.editorActions
import com.jakewharton.rxbinding3.widget.textChanges
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import com.trello.rxlifecycle3.kotlin.bindToLifecycle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_gif_picker.*
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

    private var canFetchMore: Boolean = false
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
        fetchTrending()
    }

    private fun setup() {

        gifSearchField.editorActions()
                .bindToLifecycle(this)
                .subscribe({ action ->
                    if (action == EditorInfo.IME_ACTION_SEARCH) {
                        // close keyboard
                        val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                        inputManager?.hideSoftInputFromWindow(
                                this.currentFocus.windowToken,
                                InputMethodManager.HIDE_NOT_ALWAYS)
                        true
                    }
                    false
                }, {})

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
                .bindToLifecycle(this)
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

                        if (lastVisible > totalCount.minus(5) && !isFetching.get() && canFetchMore) {
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
                }, {})
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
                }, {})
    }

    override fun gifOnclick(gifUrl: String) {
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

    fun isConnected(context: Context?): Boolean {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetwork = cm?.activeNetworkInfo
        return activeNetwork?.isConnected == true
    }

    companion object {
        const val GIF_KEY = "gifMessage"
    }

}