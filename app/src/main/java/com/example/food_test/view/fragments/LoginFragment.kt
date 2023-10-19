package com.example.food_test.view.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.food_test.R
import com.example.food_test.databinding.FragmentLoginBinding
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private lateinit var loginButton: LoginButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        fbUpdateUi(currentUser)
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (account != null) {
            updateUI(account)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        loginButton = binding.fbLoginButton
        callbackManager = CallbackManager.Factory.create()
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
            fbSignIn()
        }

        binding.submitBtnIn.setOnClickListener {
            val email = binding.emailEtIn.text.toString()
            val password = binding.passwordEtIn.text.toString()
            val validEmail =
                Pattern.compile("(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
                    .matcher(email).matches()
            if (email.isNotEmpty() && password.isNotEmpty() && validEmail && binding.checkBoxIn.isChecked && password.length >= 8) {
                binding.submitBtnCardIn.isVisible = false
                binding.dotsLoadingIn.isVisible = true
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { authResult ->
                        if (authResult.isSuccessful) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                binding.dotsLoadingIn.isVisible = false
                                Handler(Looper.getMainLooper()).postDelayed({
                                    binding.successIn.isVisible = true
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        binding.successIn.isVisible = false
                                        findNavController().navigate(R.id.homeFragment)
                                    }, 1800)
                                }, 200)
                            }, 2000)
                        } else {
                            binding.dotsLoadingIn.isVisible = false
                            binding.submitBtnCardIn.isVisible = true
                            binding.submitBtnIn.isVisible = true
                            Toast.makeText(
                                requireContext(),
                                "invalid email or password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Fields are required", Toast.LENGTH_SHORT).show()
            }

            if (email.isEmpty() && !validEmail) {
                binding.emailEtIn.error = "required or invalid"
                binding.emailEtIn.requestFocus()
            } else {
                binding.emailEtIn.error = null
            }

            if (password.isEmpty() && password.length < 8) {
                binding.passwordEtIn.error = "required or invalid"
                binding.passwordEtIn.requestFocus()
            } else {
                binding.passwordEtIn.error = null
            }

        }

        binding.emailEtIn.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    val validEmailListener =
                        Patterns.EMAIL_ADDRESS.matcher(binding.emailEtIn.text.toString()).matches()
                    if (validEmailListener) {
                        binding.emailEtIn.error = null
                    }
                    if (!validEmailListener) {
                        binding.emailEtIn.error = "invalid email"
                    }
                }

            }
        )

        binding.passwordEtIn.addTextChangedListener(
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
                    val passwordLength = binding.passwordEtIn.text.toString().length
                    if (passwordLength >= 8) {
                        binding.passValidSuccess.isVisible = true
                        binding.passwordEtIn.error = null
                    }
                    if (passwordLength < 8) {
                        binding.passValidSuccess.isVisible = false
                        binding.passwordEtIn.error = "at least 8 chars"
                    }
                }

            }
        )

        Handler(Looper.getMainLooper()).postDelayed({
            binding.signUpBtn.setOnClickListener {
                findNavController().navigate(R.id.signUpFragment)
            }
        }, 500)

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
            updateUI(null)
        }
    }

    @SuppressLint("CommitPrefEdits")
    fun updateUI(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { authResults ->
            if (authResults.isSuccessful) {
                val googleSharedPref =
                    context?.getSharedPreferences("googlePref", Context.MODE_PRIVATE)
                val editor = googleSharedPref?.edit()
                editor?.apply {
                    putString("googleEmail", account.email)
                    putString("googleName", account.displayName)
                    putString("googleImage", account.photoUrl.toString())
                    apply()
                }
                findNavController().navigate(R.id.homeFragment)

            } else {
                Toast.makeText(
                    requireContext(),
                    authResults.exception.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun fbSignIn() {
        loginButton.setPermissions("public_profile", "email","user_location")
        loginButton.registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {

                override fun onCancel() {
                    Log.d(TAG, "onCancel")
                }

                override fun onError(error: FacebookException) {
                    Log.d(TAG, "onError")
                }

                override fun onSuccess(result: LoginResult) {
                    handleFacebookAccessToken(result.accessToken)
                }

            })

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)

    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Success",
                        Toast.LENGTH_SHORT
                    ).show()
                    val user = firebaseAuth.currentUser
                    fbUpdateUi(user)
                } else {
                    Toast.makeText(
                        requireContext(), task.exception.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    fbUpdateUi(null)
                }
            }
    }

    @SuppressLint("CommitPrefEdits")
    fun fbUpdateUi(user: FirebaseUser?) {
        if (user != null) {
            val photo = "${user.photoUrl}?access_token=${AccessToken.getCurrentAccessToken()?.token}"
            val facebookSharedPref =
                context?.getSharedPreferences("facebookPref", Context.MODE_PRIVATE)
            val editor = facebookSharedPref?.edit()
            editor?.apply {
                putString("facebookEmail", user.email)
                putString("facebookName", user.displayName)
                putString("facebookImg", photo)
                apply()
            }
            findNavController().navigate(R.id.homeFragment)
        }
    }

//    private fun handleFacebookAccessToken(accessToken: AccessToken?) {
//
//        val credential = FacebookAuthProvider.getCredential(accessToken!!.token)
//        firebaseAuth.signInWithCredential(credential)
//            .addOnCompleteListener { authResult ->
//                if (authResult.isSuccessful) {
//
//                    val email = firebaseAuth.currentUser!!.email.toString()
//                    Toast.makeText(
//                        requireContext(),
//                        firebaseAuth.currentUser!!.displayName.toString(),
//                        Toast.LENGTH_LONG
//                    ).show()
//                    Toast.makeText(requireContext(), "You logged in as $email", Toast.LENGTH_LONG)
//                        .show()
//                } else {
//                    Toast.makeText(
//                        requireContext(),
//                        authResult.exception!!.message.toString(),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//
//            .addOnSuccessListener { authResult ->
//                Toast.makeText(requireContext(),authResult.user!!.displayName.toString(),Toast.LENGTH_SHORT).show()
//                val email = authResult.user!!.email
//                Toast.makeText(requireContext(), "You logged in with $email", Toast.LENGTH_LONG)
//                    .show()
//                findNavController().navigate(R.id.homeFragment)
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(requireContext(), exception.message.toString(), Toast.LENGTH_LONG)
//                    .show()
//            }
// }

}

