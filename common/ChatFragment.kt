package com.vrsidekick.fragments.common

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.vrsidekick.R
import com.vrsidekick.adapter.common.ChatAdapter
import com.vrsidekick.databinding.FragmentChatBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.utils.setSafeOnClickListener

private const val TAG ="ChatFragment"
class ChatFragment : Fragment() {

  private lateinit var binding : FragmentChatBinding
  private var mIBaseActivity : IBaseActivity? =null
    private var mChatAdapter : ChatAdapter? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mChatAdapter = ChatAdapter(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvChat.adapter = mChatAdapter

        setupClickListener()
    }

    private fun setupClickListener() {
        binding.ivBack.setSafeOnClickListener {
            it.findNavController().navigateUp()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIBaseActivity = context as IBaseActivity
    }

    override fun onDetach() {
        super.onDetach()
        mIBaseActivity = null
    }
}