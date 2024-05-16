package com.example.h

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.h.data.User
import com.example.h.data.Profile
import com.example.h.saveSharedPreference.SaveSharedPreference
import com.example.h.viewModel.UserViewModel
import com.example.h.viewModel.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar

class EditProfile : Fragment() {
    private lateinit var txtNameEditProfile: EditText
    private lateinit var txtLanguageEditProfile: EditText
    private lateinit var txtPhoneNoEditProfile: EditText
    private lateinit var txtBirthdayEditProfile: EditText
    private lateinit var txtDescriptionEditProfile: EditText
    private lateinit var spinnerCourseEditProfile: Spinner
    private lateinit var btnSaveEditProfile: Button
    private lateinit var imgProfile: ImageView
    private lateinit var selectedImageUri: Uri
    private lateinit var courseArray: Array<String>
    private lateinit var userViewModel: UserViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var currentUser: User
    private lateinit var currentProfile: Profile

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        txtNameEditProfile = view.findViewById(R.id.txtNameEditProfile)
        txtLanguageEditProfile = view.findViewById(R.id.txtlanguageEditProfile)
        txtPhoneNoEditProfile = view.findViewById(R.id.txtPhoneNoEditProfile)
        txtBirthdayEditProfile = view.findViewById(R.id.txtBirthdayEditProfile)
        txtDescriptionEditProfile = view.findViewById(R.id.txtDescriptionEditProfile)
        spinnerCourseEditProfile = view.findViewById(R.id.spinnerCourseEditProfile)
        btnSaveEditProfile = view.findViewById(R.id.btnSaveEditProfile)
        imgProfile = view.findViewById(R.id.imgProfileEditProfile)

        courseArray = resources.getStringArray(R.array.course)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, courseArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCourseEditProfile.adapter = adapter

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        val userID = SaveSharedPreference.getUserID(requireContext())
        GlobalScope.launch(Dispatchers.Main) {
            currentUser = userViewModel.getUserByID(userID) ?: User()
            currentProfile = profileViewModel.getProfile(currentUser.userID) ?: Profile()
            populateFields(currentUser, currentProfile)
        }

        btnSaveEditProfile.setOnClickListener {
            updateUser()
        }

        txtBirthdayEditProfile.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener {
                showDatePicker()
            }
        }

        return view
    }

    private fun populateFields(user: User, profile: Profile) {
        txtNameEditProfile.setText(user.username)
        txtLanguageEditProfile.setText(profile.userChosenLanguage)
        txtPhoneNoEditProfile.setText(user.userMobileNo)
        txtBirthdayEditProfile.setText(user.userDOB)
        txtDescriptionEditProfile.setText(profile.userBio)

        val courseIndex = courseArray.indexOf(profile.userCourse)
        spinnerCourseEditProfile.setSelection(courseIndex)

        // Set profile image if available
        if (profile.userImage.isNotEmpty()) {
            imgProfile.setImageURI(Uri.parse(profile.userImage))
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data!!
            imgProfile.setImageURI(selectedImageUri)
        }
    }

    private fun updateUser() {
        // Update user fields
        val updatedUser = currentUser.copy(
            username = txtNameEditProfile.text.toString(),
            userMobileNo = txtPhoneNoEditProfile.text.toString(),
            userDOB = txtBirthdayEditProfile.text.toString()
        )

        // Update profile fields
        val updatedProfile = Profile(
            userID = currentUser.userID,
            userCourse = spinnerCourseEditProfile.selectedItem.toString(),
            userBio = txtDescriptionEditProfile.text.toString(),
            userImage = selectedImageUri.toString(),
            userChosenLanguage = txtLanguageEditProfile.text.toString()
        )


        lifecycleScope.launch {
            // Update user and profile
            userViewModel.addUser(updatedUser)
            profileViewModel.addProfile(updatedProfile)
            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            txtBirthdayEditProfile.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 100
    }
}