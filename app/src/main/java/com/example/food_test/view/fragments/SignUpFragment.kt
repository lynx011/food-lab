package com.example.food_test.view.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.food_test.R
import com.example.food_test.databinding.FragmentSignUpBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class SignUpFragment : Fragment() {
    lateinit var binding: FragmentSignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var loginButton: LoginButton
    private lateinit var callBackManager : CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onStart() {
        super.onStart()
        val user = firebaseAuth.currentUser
        fbUpdateUi(user)
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if(account != null){
            updateUI(account)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        callBackManager = CallbackManager.Factory.create()
        loginButton = binding.fbLoginButton
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions)

        binding.googleBtn.setOnClickListener {
            binding.googleCard.isVisible = false
            binding.googleDots.isVisible = true
            Handler(Looper.getMainLooper()).postDelayed({
                binding.googleDots.isVisible = false
                binding.googleCard.isVisible = true
                Handler(Looper.getMainLooper()).postDelayed({
                    signInGoogle()
                }, 100)
            }, 3000)
        }

        loginButton.setOnClickListener {
            signInFb()
        }

        binding.submitBtnUp.setOnClickListener {
            val email = binding.emailEtUp.text.toString()
            val password = binding.passwordEtUp.text.toString()
            val confirmPassword = binding.confirmEtUp.text.toString()
            val validEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches()

            if (email.isNotEmpty() && password.isNotEmpty() &&
                confirmPassword.isNotEmpty() && validEmail &&
                binding.checkBoxUp.isChecked && password.length >= 8 &&
                confirmPassword.length >= 8
            ) {

                if (password == confirmPassword) {
                    binding.submitBtnCardUp.isVisible = false
                    binding.dotsLoadingUp.isVisible = true
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { authResult ->
                            if (authResult.isSuccessful) {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    binding.dotsLoadingUp.isVisible = false
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        binding.successUp.isVisible = true
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            binding.successUp.isVisible = false
                                            findNavController().navigate(R.id.loginFragment)
                                        }, 1800)
                                    }, 200)
                                }, 2000)
                            } else {
                                binding.dotsLoadingUp.isVisible = false
                                binding.submitBtnCardUp.isVisible = true
                                binding.submitBtnUp.isVisible = true
                                Toast.makeText(
                                    requireContext(),
                                    authResult.exception.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(requireContext(), "password isn't matched", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(requireContext(), "Fields are required", Toast.LENGTH_SHORT).show()
            }
        }

        binding.emailEtUp.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    val validEmailListener =
                        Patterns.EMAIL_ADDRESS.matcher(binding.emailEtUp.text.toString()).matches()
                    if (validEmailListener) {
                        binding.emailEtUp.error = null
                    }
                    if (!validEmailListener) {
                        binding.emailEtUp.error = "invalid email"
                    }
                }

            }
        )

        binding.passwordEtUp.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int
                ) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    val passwordLength = binding.passwordEtUp.text.toString().length
                    val passwordUp = binding.passwordEtUp.text.toString()
                    val confirmPasswordUp = binding.confirmEtUp.text.toString()
                    if (passwordLength >= 8) {
                        binding.passwordEtUp.error = null
                    }
                    if (passwordLength < 8) {
                        binding.passwordEtUp.error = "at least 8 chars"
                    }

                    if (passwordUp == confirmPasswordUp && passwordLength >= 8) {
                        binding.passMatchTv.isVisible = true
                        binding.passValidSuccess.isVisible = true
                        binding.totalCharsTv.isVisible = false
                    } else {
                        binding.passMatchTv.isVisible = false
                        binding.passValidSuccess.isVisible = false
                        binding.totalCharsTv.isVisible = true
                    }
                }

            }
        )

        binding.confirmEtUp.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    val confirmPasswordLength = binding.confirmEtUp.text.toString().length
                    val confirmPasswordUp = binding.confirmEtUp.text.toString()
                    val passwordUp = binding.passwordEtUp.text.toString()
                    if (confirmPasswordLength >= 8) {
                        binding.confirmEtUp.error = null
                    }
                    if (confirmPasswordLength < 8) {
                        binding.confirmEtUp.error = "at least 8 chars"
                    }

                    if (passwordUp == confirmPasswordUp && confirmPasswordLength >= 8) {
                        binding.passMatchTv.isVisible = true
                        binding.passValidSuccess.isVisible = true
                        binding.totalCharsTv.isVisible = false
                    } else {
                        binding.passMatchTv.isVisible = false
                        binding.passValidSuccess.isVisible = false
                        binding.totalCharsTv.isVisible = true
                    }
                }

            }
        )

        Handler(Looper.getMainLooper()).postDelayed({
            binding.loginBtn.setOnClickListener {
                findNavController().navigate(R.id.loginFragment)
            }
        }, 500)
    }

    private fun signInFb() {
        loginButton.setPermissions("public_profile","email","user_location")
        loginButton.registerCallback(callBackManager,object : FacebookCallback<LoginResult>{

            override fun onCancel() {
                Log.d(TAG,"onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG,"onError")
            }

            override fun onSuccess(result: LoginResult) {
                handleFacebookAccessToken(result.accessToken)
            }
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callBackManager.onActivityResult(requestCode, resultCode, data)

    }

    private fun handleFacebookAccessToken(accessToken: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if(task.isSuccessful){
                val user = firebaseAuth.currentUser
                fbUpdateUi(user)
            }
            else {
                Toast.makeText(requireContext(),task.exception.toString(),Toast.LENGTH_SHORT).show()
                fbUpdateUi(null)
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    fun fbUpdateUi(user: FirebaseUser?) {
        if(user != null){
            val signupFacebookSharedPref = requireContext().getSharedPreferences("signupFacebookSharedPref",Context.MODE_PRIVATE)
            val fbEditor = signupFacebookSharedPref?.edit()
            fbEditor?.apply {
               val photo = "${user.photoUrl}?access_token=${AccessToken.getCurrentAccessToken()?.token}"
                putString("suFacebookName",user.displayName)
                putString("suFacebookEmail",user.email)
                putString("suFacebookImage",photo)
                apply()
            }
            findNavController().navigate(R.id.homeFragment)
        }
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { results ->
            if (results.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(results.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(requireContext(), task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun updateUI(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { authResult ->
            if (authResult.isSuccessful) {
                val signupGoogleSharedPref = requireContext().getSharedPreferences("SignupGoogleSharedPref",Context.MODE_PRIVATE)
                val signupEditor = signupGoogleSharedPref.edit()
                signupEditor.apply {
                    putString("suGoogleName",account.displayName)
                    putString("suGoogleEmail",account.email)
                    putString("suGoogleImage",account.photoUrl.toString())
                }
                signupEditor.apply()
                findNavController().navigate(R.id.homeFragment)
            } else {
                Toast.makeText(
                    requireContext(),
                    authResult.exception.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}