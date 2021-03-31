package com.example.module_video.videoDetail

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.common_base.base.mvvm.BaseMvvmActivity
import com.example.common_base.constants.AConstance
import com.example.common_base.constants.Constants
import com.example.common_base.util.StatusBarUtil
import com.example.module_video.BR
import com.example.module_video.R
import com.example.module_video.VideoViewModelFactory
import com.example.module_video.databinding.VideoActivityPlayBinding
import com.example.module_video.databinding.VideoItemHeaderViewBinding
import com.example.module_video.recommend.bean.VideoInfoBean
import com.example.module_video.videoDetail.adapter.VideoPlayMultiAdapter

/**
 * 视频详情页
 */
@Route(path = AConstance.ACTIVITY_URL_VIDEO_PLAY)
class VideoPlayActivity : BaseMvvmActivity<VideoActivityPlayBinding, VideoPlayViewModel>() {
    private lateinit var mAdapter: VideoPlayMultiAdapter

    @JvmField
    @Autowired(name = Constants.VIDEO_PLAY_VIDEO_INFO)
    var videoInfo: VideoInfoBean? = null

    override fun initView() {
        ARouter.getInstance().inject(this)
        StatusBarUtil.setDarkMode(this)
        StatusBarUtil.setColor(this, Color.BLACK)

        initRecycler()
        initRefresh()
    }

    override fun initData() {
        Glide.with(viewDataBinding?.videoPlayBg!!.context)
            .asBitmap()
            .load(videoInfo?.blurredUrl)
            .into(object : SimpleTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    viewDataBinding?.videoPlayBg?.background = BitmapDrawable(resource)
                }
            })
        viewModel.videoId = videoInfo!!.videoId
        viewModel.loadData()

        viewModel.relatedData.observe(this) {
            mAdapter.setNewInstance(it)
        }
    }

    private fun initRefresh() {
        viewDataBinding!!.srlRefresh.apply {
            setOnRefreshListener {}
            setOnLoadMoreListener {
                viewModel.getRelatedReplies(isLoadMore = true)
            }
            setEnableLoadMore(true)
            setEnableRefresh(true)
        }
    }

    private fun initRecycler() {
        mAdapter =
            VideoPlayMultiAdapter()
        viewDataBinding!!.rvContent.apply {
            layoutManager = LinearLayoutManager(this@VideoPlayActivity)
            adapter = mAdapter
        }
        mAdapter.setHeaderView(generateHeaderView())
    }

    private fun generateHeaderView(): View {
        val binding = DataBindingUtil.inflate<VideoItemHeaderViewBinding>(
            layoutInflater,
            R.layout.video_item_header_view,
            viewDataBinding!!.rvContent,
            false
        )
        binding.videoInfo = videoInfo
        binding.executePendingBindings()
        return binding.root
    }

    override fun getLayoutResId(): Int = R.layout.video_activity_play

    override fun initVariableId(): Int = BR.viewmodel

    override fun createViewModel(): VideoPlayViewModel {
        return ViewModelProvider(this, VideoViewModelFactory()).get(VideoPlayViewModel::class.java)
    }
}