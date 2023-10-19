package com.example.food_test.view.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.food_test.R
import com.example.food_test.api_service.PostApiService
import com.example.food_test.databinding.FragmentProfileBinding
import com.example.food_test.model.PostModelItem
import com.example.food_test.repository.PostRepository
import com.example.food_test.view_model.PostViewModel
import com.example.food_test.view_model_factory.PostViewModelFactory
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    lateinit var binding : FragmentProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var loginManager: LoginManager
    private lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var postViewModel: PostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val fm = parentFragmentManager
        val loginFragment : LoginFragment? = fm.findFragmentById(R.id.loginFragment) as LoginFragment?
        val  signUpFragment : SignUpFragment? = fm.findFragmentById(R.id.signUpFragment) as SignUpFragment?
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null){
            loginFragment?.fbUpdateUi(currentUser)
            signUpFragment?.fbUpdateUi(currentUser)
        }
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (account != null) {
            loginFragment?.updateUI(account)
            signUpFragment?.updateUI(account)
        }
    }

    @SuppressLint("CommitPrefEdits")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val retrofitService = PostApiService.retrofitInstance()
        val mainRepository = PostRepository(retrofitService)
        postViewModel = ViewModelProvider(this,PostViewModelFactory(mainRepository))[PostViewModel::class.java]

        binding.postBtn.setOnClickListener {
            val idTv = binding.postId.text.toString()
            val userIdTv = binding.postUserId.text.toString()
            val titleTv = binding.postTitle.text.toString()
            val bodyTv = binding.postBody.text.toString()
            val myPost = PostModelItem(id = idTv, userId = userIdTv, title = titleTv, body = bodyTv)
            postViewModel.pushPost(myPost)
            postViewModel.postLiveData.observe(viewLifecycleOwner){
                Log.d("post",it.toString())
            }
        }
        // for login
        val googleSharedPref = context?.getSharedPreferences("googlePref",Context.MODE_PRIVATE)
        val facebookSharedPref = context?.getSharedPreferences("facebookPref",Context.MODE_PRIVATE)
        // for signup
        val signupGoogleSharedPref = requireContext().getSharedPreferences("SignupGoogleSharedPref",Context.MODE_PRIVATE)
        val signupFacebookSharedPref = context?.getSharedPreferences("signupFacebookSharedPref",Context.MODE_PRIVATE)

        firebaseAuth = FirebaseAuth.getInstance()
        loginManager = LoginManager.getInstance()
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions)
        binding.logoutBtn.setOnClickListener {
            if(AccessToken.isCurrentAccessTokenActive()){
                loginManager.logOut()
                firebaseAuth.signOut()
                    if(facebookSharedPref != null){
                        facebookSharedPref.edit()?.clear()?.apply()
                    }
                if(signupFacebookSharedPref != null){
                    signupFacebookSharedPref.edit()?.clear()?.apply()
                }
            }
//            if(AccessToken.isCurrentAccessTokenActive()){
//                loginManager.logOut()
//                firebaseAuth.signOut()
//                fbSharedPref?.edit()?.clear()?.apply()
//            }
            if(firebaseAuth.currentUser != null){
                firebaseAuth.signOut()
                googleSharedPref?.edit()?.clear()?.apply()
                signupGoogleSharedPref.edit().clear().apply()
                googleSignInClient.signOut().addOnCompleteListener {
                    val fm = parentFragmentManager
                    val loginFragment : LoginFragment? = fm.findFragmentById(R.id.loginFragment) as LoginFragment?
                    loginFragment?.updateUI(null)
                }
                googleSignInClient.signOut().addOnCompleteListener {
                    val pfm = parentFragmentManager
                    val signupFragment : SignUpFragment? = pfm.findFragmentById(R.id.signUpFragment) as SignUpFragment?
                    signupFragment?.updateUI(null)
                }
            }

            Toast.makeText(requireContext(), "Signed Out", Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().navigate(R.id.loginFragment)
            }, 2000)
//            if (AccessToken.getCurrentAccessToken() != null && com.facebook.Profile.getCurrentProfile() != null){
//                LoginManager.getInstance().logOut();
//            }
        }

        val googleEmail = googleSharedPref?.getString("googleEmail",null)
        val googleName = googleSharedPref?.getString("googleName",null)
        val googleImage = googleSharedPref?.getString("googleImage",null)

                if(googleEmail != null && googleName != null && googleImage != null){
                    binding.apply {
                        pfName.text = googleName.toString()
                        pfUserName.text = googleEmail.toString()
                        pfUserName.isVisible = true
                        Glide.with(this@ProfileFragment)
                            .asBitmap()
                            .load(googleImage)
                            .into(pfImg)
                    }
                }

        val facebookEmail = facebookSharedPref?.getString("facebookEmail",null)
        val facebookName = facebookSharedPref?.getString("facebookName",null)
        val facebookImg = facebookSharedPref?.getString("facebookImg",null)

        if(facebookName != null && facebookImg != null && facebookEmail == null){
            binding.apply {
                pfName.text = facebookName.toString()
                pfUserName.isVisible = false
                Glide.with(this@ProfileFragment)
                    .asBitmap()
                    .load(facebookImg)
                    .into(pfImg)
            }
        }
        else{
            binding.pfUserName.isVisible = true
        }

        val suGoogleName = signupGoogleSharedPref.getString("suGoogleName",null)
        val suGoogleEmail = signupGoogleSharedPref.getString("suGoogleEmail",null)
        val suImage = signupGoogleSharedPref.getString("suGoogleImage",null)

        if(suGoogleName != null && suGoogleEmail != null && suImage != null){
            binding.apply {
                pfName.text = suGoogleName.toString()
                pfUserName.text = suGoogleEmail.toString()
                Glide.with(this@ProfileFragment)
                    .load(suImage)
                    .into(pfImg)
            }

        }

        val suFacebookName = signupFacebookSharedPref?.getString("suFacebookName",null)
        val suFacebookEmail = signupFacebookSharedPref?.getString("suFacebookEmail",null)
        val suFacebookImage = signupFacebookSharedPref?.getString("suFacebookImage",null)

        if(suFacebookName != null && suFacebookEmail == null && suFacebookImage != null){
            binding.apply {
                pfName.text = suFacebookName.toString()
                pfUserName.isVisible = false
                Glide.with(this@ProfileFragment)
                    .load(suFacebookImage)
                    .into(pfImg)
            }
        }
    //        val fbName = fbSharedPref?.getString("fbName",null)
//        val fbEmail = fbSharedPref?.getString("fbUsername",null)
//        val fbProfile = fbSharedPref?.getString("fbProfile",null)
//
//            binding.apply {
//                pfName.text = fbName.toString()
//                pfUserName.text = fbEmail.toString()
//                Glide.with(this@ProfileFragment)
//                    .asBitmap()
//                    .load(fbProfile)
//                    .into(pfImg)
//
//        }



    }

}