package com.example.sacrenachat.views.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.sacrenachat.R
import com.example.sacrenachat.databinding.FragmentMessagesBinding
import com.example.sacrenachat.viewmodels.BaseViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.models.Filters
import io.getstream.chat.android.models.User
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModel
import io.getstream.chat.android.ui.viewmodel.channels.ChannelListViewModelFactory
import io.getstream.chat.android.ui.viewmodel.channels.bindView

class MessagesFragment : BaseFragment<FragmentMessagesBinding>() {

    companion object {
        const val TAG = "MessagesFragment"
    }

    override fun setupViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMessagesBinding {
        return FragmentMessagesBinding.inflate(inflater, container, false)
    }

    override val viewModel: BaseViewModel?
        get() = null

    override fun init() {
        setupViews()
    }

    private fun setupViews() {
        binding.apply {
            setupChannels()
        }
    }

    private fun setupChannels() {
        // Step 1 - Set up the OfflinePlugin for offline storage
        val offlinePluginFactory = StreamOfflinePluginFactory(requireActivity())
        val statePluginFactory = StreamStatePluginFactory(
            config = StatePluginConfig(
                backgroundSyncEnabled = true,
                userPresence = true,
            ),
            appContext = requireActivity(),
        )

        // Step 2 - Set up the client for API calls with the plugin for offline storage
        val client = ChatClient.Builder(getString(R.string.api_key_chat), requireActivity())
            .withPlugins(offlinePluginFactory, statePluginFactory)
            .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
            .build()

        // Step 3 - Authenticate and connect the user
        val user = User(
            id = "alice",
            name = "Alice",
            image = "https://bit.ly/2TIt8NR"
        )
        val token = client.devToken(user.id)
        client.connectUser(
            user = user,
            token = token,
//            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoidHV0b3JpYWwtZHJvaWQifQ.WwfBzU1GZr0brt_fXnqKdKhz3oj0rbDUm2DqJO_SS5U"
        ).enqueue {
            if (it.isSuccess) {
                // Step 4 - Set the channel list filter and order
                // This can be read as requiring only channels whose "type" is "messaging" AND
                // whose "members" include our "user.id"
                val filter = Filters.and(
                    Filters.eq("type", "messaging"),
                    Filters.`in`("members", listOf(user.id))
                )
                val viewModelFactory =
                    ChannelListViewModelFactory(filter, ChannelListViewModel.DEFAULT_SORT)
                val viewModel: ChannelListViewModel by viewModels { viewModelFactory }

                // Step 5 - Connect the ChannelListViewModel to the ChannelListView, loose
                //          coupling makes it easy to customize
                viewModel.bindView(binding.channelListView, viewLifecycleOwner)
                binding.channelListView.setChannelItemClickListener { channel ->
                    performTransaction(ChatFragment.newInstance(channel), ChatFragment.TAG)
                }
            } else {
                Toast.makeText(requireContext(), "something went wrong!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.finish()
    }
}