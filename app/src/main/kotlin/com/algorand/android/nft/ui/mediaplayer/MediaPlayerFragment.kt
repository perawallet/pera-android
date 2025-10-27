/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.nft.ui.mediaplayer

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.algorand.android.MainActivity
import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.databinding.FragmentMediaPlayerBinding
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.utils.setScreenOrientationFullSensor
import com.algorand.android.utils.setScreenOrientationPortrait
import com.algorand.android.utils.viewbinding.viewBinding

@UnstableApi
abstract class MediaPlayerFragment : BaseFragment(R.layout.fragment_media_player) {

    override val fragmentConfiguration = FragmentConfiguration()

    protected val binding by viewBinding(FragmentMediaPlayerBinding::bind)

    abstract val mediaPlayerViewModel: MediaPlayerViewModel

    private var exoPlayer: ExoPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    protected open fun initUi() {
        binding.backButton.setOnClickListener { navBack() }
        loadMedia()
    }

    private fun loadMedia() {
        val url = mediaPlayerViewModel.collectibleMediaUrl
        val mediaSource = createMediaSource(url)
        setupPlayer(mediaSource)
    }

    override fun onResume() {
        super.onResume()
        activity?.setScreenOrientationFullSensor()
        resumePlayer()
    }

    override fun onPause() {
        super.onPause()
        activity?.setScreenOrientationPortrait()
        pausePlayer()
    }

    override fun onStop() {
        super.onStop()
        destroyPlayer()
    }

    private fun setupPlayer(mediaSource: ProgressiveMediaSource) {
        exoPlayer = ExoPlayer.Builder(requireContext()).build().apply {
            setMediaSource(mediaSource)
            prepare()
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_ONE
        }
        binding.playerView.player = exoPlayer
    }

    private fun createMediaSource(url: String): ProgressiveMediaSource {
        val dataSourceFactory = DefaultDataSource.Factory(requireContext())
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(url.toUri()))
    }

    private fun resumePlayer() {
        (activity as? MainActivity)?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        exoPlayer?.playWhenReady = true
    }

    private fun pausePlayer() {
        (activity as? MainActivity)?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        exoPlayer?.playWhenReady = false
    }

    private fun destroyPlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }
}
