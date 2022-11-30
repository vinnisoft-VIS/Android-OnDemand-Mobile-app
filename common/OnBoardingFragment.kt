package com.vrsidekick.fragments.common

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.vrsidekick.NavGraphAuthDirections
import com.vrsidekick.R
import com.vrsidekick.activities.AuthActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.adapter.common.OnBoardingAdapter
import com.vrsidekick.databinding.FragmentOnBoardingBinding
import com.vrsidekick.models.OnBoardingTab
import com.vrsidekick.utils.AccountType
import com.vrsidekick.utils.setSafeOnClickListener

private const val TAG = "OnBoardingFragment"

class OnBoardingFragment : Fragment() {
    private lateinit var binding: FragmentOnBoardingBinding
    private var mOnBoardingAdapter: OnBoardingAdapter? = null
    private var mCurrentTab = 0


    private val tabs = arrayOf(
        OnBoardingTab(
            R.drawable.img_on_boarding_1,
            "Home Service",
            "We are a home delivery platform and marketplace, powered by the people for the people"
        ),
        OnBoardingTab(
            R.drawable.img_on_boarding_2,
            "Easy access",
            "To function as a means for people who require service direct to their door"
        ),
        OnBoardingTab(
            R.drawable.img_on_boarding_3,
            "Time to relax",
            "Find an easy way to handle your daily needs in one place. "
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "isLogin: ${prefs.isLogin}")
        Log.d(TAG, "isFirstVisit: ${prefs.isFirstVisit}")
        Log.d(TAG, "current user: ${prefs.currentUser}")
        if (!prefs.isFirstVisit) {
            if(prefs.isLogin){

                when(prefs.selectedAccountType){
                    AccountType.SIDEKICK.name ->  (activity as AuthActivity).switchAccount(AccountType.SIDEKICK)
                    AccountType.SIDEKICK_NEEDED.name -> (activity as AuthActivity).switchAccount(AccountType.SIDEKICK_NEEDED)
                    else -> {
                        val direction = NavGraphAuthDirections.actionGlobalSelectTypeFragment()
                        findNavController().navigate(direction)
                    }

                }


            }else{
                navigateToLogin()
            }

            return
        }

        (activity as AuthActivity?)?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)



    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupViewPager()
        setupListener()
    }

    private fun setupViewPager() {
        binding.pagerOnBoarding.setCurrentItem(mCurrentTab,true)
        mOnBoardingAdapter = OnBoardingAdapter(tabs)
        binding.pagerOnBoarding.adapter = mOnBoardingAdapter
        TabLayoutMediator(binding.tabLayoutOnBoarding,binding.pagerOnBoarding){_,position ->
            Log.d(TAG, "setupViewPager: position = $position")
            
        }.attach()
        binding.pagerOnBoarding.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setupButtonVisibility(position)
            }
        })
    }

    private fun setupButtonVisibility(position:Int) {

        binding.btPrevious.visibility = if(position == 0) View.GONE else View.VISIBLE
        binding.btSkip.visibility = if(position == 2) View.INVISIBLE else View.VISIBLE
        binding.btSkip.isEnabled = position !=3
        binding.btNext.text = if(position == 2) getString(R.string.login) else getString(R.string.next)

    }

    private fun setupListener() {
        binding.btSkip.setSafeOnClickListener(800) {
           navigateToLogin()
        }
        binding.btNext.setSafeOnClickListener(800) {
          if(mCurrentTab ==2){
              navigateToLogin()

          }else{
              mCurrentTab++
              binding.pagerOnBoarding.post {
                  binding.pagerOnBoarding.setCurrentItem(mCurrentTab, true)
              }
          }
        }
        binding.btPrevious.setSafeOnClickListener(800) {

            mCurrentTab--
            binding.pagerOnBoarding.post {
                binding.pagerOnBoarding.setCurrentItem(mCurrentTab, true)
            }
        }
    }


    private fun navigateToLogin() {

            val directions = NavGraphAuthDirections.actionGlobalLoginFragment()
            findNavController().navigate(directions)



    }
}