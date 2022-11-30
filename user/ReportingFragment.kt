package com.vrsidekick.fragments.user

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.vrsidekick.NavGraphDirections
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.UserActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.adapter.user.JobsAdapter
import com.vrsidekick.databinding.FragmentReportingBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.PropertyViewModel

private const val TAG= "ReportingFragment"
class ReportingFragment : Fragment(), JobsAdapter.IJobAdapter {
    private lateinit var binding : FragmentReportingBinding
    private val  propertyViewModel : PropertyViewModel by viewModels()
    private var mFutureJobAdapter : JobsAdapter? =null
    private var mPastJobAdapter : JobsAdapter? =null
    private var mIBaseActivity : IBaseActivity? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFutureJobAdapter = JobsAdapter()
        mFutureJobAdapter?.setOnJobClickListener(this)
        mPastJobAdapter = JobsAdapter()
        mPastJobAdapter?.setOnJobClickListener(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportingBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar()
        binding.rvPastJobs.adapter = mPastJobAdapter
        binding.rvFutureJobs.adapter = mFutureJobAdapter
        setupPropertyObserver()
        setClickListener()
        propertyViewModel.getJobs(
            "Bearer ${prefs.userToken}"
        )
    }

    private fun setupPropertyObserver() {
      propertyViewModel.getProgressObserver.observe(viewLifecycleOwner){
          mIBaseActivity?.showProgressDialog(it)
      }

      propertyViewModel.getMessageObserver.observe(viewLifecycleOwner){
          mIBaseActivity?.showMessage(it)
      }

        propertyViewModel.getJobsObserver.observe(viewLifecycleOwner){
            val futureJobs = it.futureJobs
            val pastJobs = it.pastJobs

            if(futureJobs.isEmpty()){
               binding.tvNoFutureJobsAvailable.visibility = View.VISIBLE
               binding.rvFutureJobs.visibility = View.GONE
            }else{
                binding.tvNoFutureJobsAvailable.visibility = View.GONE
                binding.rvFutureJobs.visibility = View.VISIBLE
                mFutureJobAdapter?.submitList(it.futureJobs)
            }
            if(pastJobs.isEmpty()){
                binding.tvNoPastJobsAvailable.visibility = View.VISIBLE
                binding.rvPastJobs.visibility = View.GONE
            }else{
                binding.tvNoPastJobsAvailable.visibility = View.GONE
                binding.rvPastJobs.visibility = View.VISIBLE
                mPastJobAdapter?.submitList(it.pastJobs)
            }




        }



    }

    private fun setClickListener() {

        binding.ivBack.setSafeOnClickListener(1000) {
            it.findNavController().navigateUp()
        }

    }

    override fun onStop() {
        super.onStop()
        mIBaseActivity?.showProgressDialog(false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIBaseActivity = context as IBaseActivity
    }

    override fun onDetach() {
        super.onDetach()
        mIBaseActivity = null
    }



    override fun onItemClick(jobId: Long, status: Int) {
        if(status == 0){
            val direction = NavGraphDirections.actionGlobalMySidekickDetailFragment(jobId)
            findNavController().navigate(direction)
        }

    }
}