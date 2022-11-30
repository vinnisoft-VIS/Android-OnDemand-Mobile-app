package com.vrsidekick.fragments.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.facebook.*
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.vrsidekick.NavGraphAuthDirections
import com.vrsidekick.R
import com.vrsidekick.activities.AuthActivity
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.databinding.FragmentLoginBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.network.ApiFactory
import com.vrsidekick.repository.AuthRepository
import com.vrsidekick.utils.AccountType
import com.vrsidekick.utils.Global
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.AuthViewModel
import kotlin.math.log

private const val TAG = "LoginFragment"
private const val RC_SIGN_IN_GOOGLE = 101
private const val RC_SIGN_IN_FACEBOOK = 102
private const val EMAIL = "email"



class LoginFragment : Fragment() {

    /*   private val signInRequest: GetSignInIntentRequest by lazy {
        GetSignInIntentRequest.builder()
            .setServerClientId(getString(R.string.server_client_id)).build()
    }

    private val googleLoginResultHandler: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            /* val task: Task<GoogleSignInAccount> =

                 GoogleSignIn.getSignedInAccountFromIntent(result.data)*/




            val credential: SignInCredential = Identity.getSignInClient(requireActivity())
                .getSignInCredentialFromIntent(result.data)
            Log.d(TAG, ": ${credential.displayName} ")
            Log.d(TAG, ": ${credential.familyName} ")
            Log.d(TAG, ": ${credential.givenName} ")
            Log.d(TAG, ": ${credential.googleIdToken} ")
            Log.d(TAG, ": ${credential.id} ")
            Log.d(TAG, ": ${credential.profilePictureUri} ")







            authViewModel.socialLogin(
                credential.displayName?:"",
                credential.id?:"",  //email
                credential.googleIdToken?:"",
                credential.profilePictureUri.toString()?:"",
                "Google",
                "ANDROID",
            )

            // handleSignInResult(task)

        }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        Log.d(TAG, "handleSignInResult: ")
        val account = task.getResult(ApiException::class.java)
        Log.d(TAG, "handleSignInResult: $account")


    }*/



    private lateinit var binding: FragmentLoginBinding
    private var mIBaseActivity : IBaseActivity? =null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mGoogleSignInAccount: GoogleSignInAccount? = null
    private var mCallbackManager: CallbackManager? = null
    private var isPasswordVisible : Boolean = false

    private lateinit var callbackManager: CallbackManager
    private val RC_GOOGLE_SIGN_IN = 155

    private val authViewModel: AuthViewModel by viewModels()





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callbackManager = CallbackManager.Factory.create()
        if (prefs.isFirstVisit) prefs.isFirstVisit = false


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setupSignUpSpan()
        setupObserver()
        setupListener()
    }




    private fun init() {
        mCallbackManager = CallbackManager.Factory.create()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(getString(R.string.server_client_id))
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .requestProfile().requestScopes(Scope(Scopes.PROFILE))
            .requestProfile().requestScopes(Scope(Scopes.PLUS_ME))
            .requestProfile().requestScopes(Scope(Scopes.EMAIL))
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        mGoogleSignInClient?.signOut()

    }




    private fun setupSignUpSpan() {
        val ss = SpannableString(getString(R.string.dontHaveAccount))
        ss.setSpan(StyleSpan(Typeface.BOLD), 23, ss.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        // ss.setSpan(ForegroundColorSpan(Color.WHITE),23,ss.length,Spanned.)

        ss.setSpan(object : ClickableSpan() {

            override fun onClick(p0: View) {
                val directions = LoginFragmentDirections.actionLoginFragmentToSignupFragment()
                findNavController().navigate(directions)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.WHITE
                ds.isUnderlineText = false
            }
        }, 23, ss.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

        binding.tvDontHaveAccount.text = ss
        binding.tvDontHaveAccount.movementMethod = LinkMovementMethod.getInstance()
        binding.tvDontHaveAccount.highlightColor = Color.TRANSPARENT

    }



    private fun setupObserver() {
        authViewModel.getProgressObserver.observe(viewLifecycleOwner){
            mIBaseActivity?.showProgressDialog(it)

        }

        authViewModel.getMessageObserver.observe(viewLifecycleOwner){
            mIBaseActivity?.showMessage(it)

        }

        authViewModel.getLoginObserver.observe(viewLifecycleOwner){
            prefs.isLogin = true
            prefs.currentUser =  it.user
            prefs.userToken =  it.user.token
            val direction = NavGraphAuthDirections.actionGlobalSelectTypeFragment()
            findNavController().navigate(direction)
        }

    }


    private fun setupListener() {
        binding.btForgotPassword.setSafeOnClickListener(1000) {
            val direction = LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
            findNavController().navigate(direction)
        }

        binding.btLogin.setSafeOnClickListener {
           validateLoginForm()
        }

        binding.ivGoogle.setSafeOnClickListener { signInGoogle() }
        binding.ivFacebook.setSafeOnClickListener { initFacebookLogin() }

        binding.ivTogglePasswordVisibility.setOnClickListener {
            togglePasswordVisibility()
        }
    }

    private fun togglePasswordVisibility() {
        if(isPasswordVisible){
            isPasswordVisible = false
            binding.ivTogglePasswordVisibility.setImageResource(R.drawable.ic_eye_hide)
            binding.etPassword.transformationMethod =  PasswordTransformationMethod()
        }else{
            isPasswordVisible = true
            binding.ivTogglePasswordVisibility.setImageResource(R.drawable.ic_eye)
            binding.etPassword.transformationMethod =null
        }
    }


    private fun validateLoginForm() {

        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty()) {
            mIBaseActivity?.showMessage(getString(R.string.messageEmailRequired))
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mIBaseActivity?.showMessage(getString(R.string.messageInvalidEmail))
            return
        }
        if (password.isEmpty()) {
            mIBaseActivity?.showMessage(getString(R.string.messagePasswordRequired))
            return
        }

        authViewModel.loginUser(email, password)


    }



    private fun initFacebookLogin() {

        val loginManager: LoginManager = LoginManager.getInstance()
        disconnectFromFacebook()
        loginManager.setLoginBehavior(LoginBehavior.WEB_ONLY)

        loginManager.logInWithReadPermissions(
            this,
            listOf("public_profile","email")
        )

        loginManager.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.d(TAG, "onSuccess: $result")
                result.let {
                    val request = GraphRequest.newMeRequest(
                        it.accessToken
                    ) { jsonObject, response ->
                        jsonObject?.let { it ->
                            try {
                                val fbId = it.getString("id")
                                val name =
                                    "${it.getString("first_name")} ${it.optString("last_name")}"
                                var email = it.optString("email")
                                if (email.isNullOrEmpty()) email = "$fbId@facebook.com"
                                val url = "http://graph.facebook.com/$fbId/picture?type=large"

                                Log.d(TAG, "onSuccess: $jsonObject")
                                disconnectFromFacebook()


                                authViewModel.socialLogin(
                                    name,
                                    email,  //email
                                    fbId,
                                    url,
                                    "Google",
                                    "ANDROID",
                                )



                            } catch (e: Exception) {
                                Log.d(TAG, "onSuccess: ")
                                Global.showMessage(binding.root, e.localizedMessage)
                            }

                        }

                    }

                    val params = Bundle()
                    params.putString(
                        "fields",
                        "id, first_name, last_name, email,gender, birthday, location"
                    )


                    request.parameters = params
                    request.executeAsync()

                }


                /*  helper?.saveBoolean(PrefKeys.KEY_IS_USER_LOG_IN, true)
                  Toast.makeText(this@LoginActivity, "Success", Toast.LENGTH_LONG).show()
                  moveTo(MainActivity())*/

            }

            override fun onCancel() {
                Toast.makeText(requireContext(), "cancel", Toast.LENGTH_LONG).show()

            }

            override fun onError(error: FacebookException) {
                Toast.makeText(requireContext(), error.toString() , Toast.LENGTH_LONG).show()
            }

        })
    }




    private fun signInGoogle() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE)
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: $requestCode   $resultCode")
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == RC_SIGN_IN_GOOGLE) {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                googleSignIn(task)
            } else {
                mCallbackManager?.onActivityResult(requestCode, resultCode, data)
            }

       } else {
            Log.e(TAG, "error while signing In")
        }
    }

    private fun googleSignIn(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            Log.w(TAG, "signInResult:success=$account")
Log.w(TAG, "signInResult:token" + account.serverAuthCode)



            authViewModel.socialLogin(
                account.displayName?:"",
                account.email?:"",  //email
                account.id?:"",
                account.photoUrl.toString()?:"",
                "Google",
                "ANDROID",
            )

        }catch (e: java.lang.Exception){
            Log.w(TAG, "signInResult:failed code= ${e.localizedMessage}" )
        }

    }


    fun disconnectFromFacebook() {
        Log.d(TAG, "disconnectFromFacebook: ${AccessToken.getCurrentAccessToken()}")
        if (AccessToken.getCurrentAccessToken() == null) {
            return  // already logged out
        }
        GraphRequest(
            AccessToken.getCurrentAccessToken(),
            "/me/permissions/",
            null,
            HttpMethod.DELETE,
            { LoginManager.getInstance().logOut() })
            .executeAsync()
    }



/*    private fun signIn() {
        Log.d(TAG, "signIn: ")
        Identity.getSignInClient(requireActivity())
            .getSignInIntent(signInRequest)
            .addOnSuccessListener { result ->

                val intentSenderRequest = IntentSenderRequest.Builder(result.intentSender).build()
                googleLoginResultHandler.launch(intentSenderRequest)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Google Sign-in failed", e);
            }
    }*/

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIBaseActivity = context as IBaseActivity
    }

    override fun onDetach() {
        super.onDetach()
        mIBaseActivity = null
    }
}

//end of file



