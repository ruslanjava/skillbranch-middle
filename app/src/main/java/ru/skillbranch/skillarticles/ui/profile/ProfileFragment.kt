package ru.skillbranch.skillarticles.ui.profile

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.ui.base.BaseFragment
import ru.skillbranch.skillarticles.ui.base.Binding
import ru.skillbranch.skillarticles.ui.delegates.RenderProp
import ru.skillbranch.skillarticles.ui.dialogs.AvatarActionsDialog
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand
import ru.skillbranch.skillarticles.viewmodels.profile.PendingAction
import ru.skillbranch.skillarticles.viewmodels.profile.ProfileState
import ru.skillbranch.skillarticles.viewmodels.profile.ProfileViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : BaseFragment<ProfileViewModel>() {

    override val viewModel: ProfileViewModel by viewModels()

    override val layout: Int = R.layout.fragment_profile
    override val binding: ProfileBinding by lazy { ProfileBinding() }

    private val permissionsResultCallback =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val permissionsResult = result.mapValues { (permission, isGranted) ->
                if (isGranted) true to true
                else false to ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    permission
                )
            }
            viewModel.handlePermission(permissionsResult)
        }

    private val galleryResultCallback =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            if (result != null) {
                val inputStream = requireContext().contentResolver.openInputStream(result)
                viewModel.handleUploadPhoto(inputStream)
            }
        }

    private val settingResultCallback =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // do something with result if need
        }

    private val cameraResultCallback =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            val (payload) = binding.pendingAction as PendingAction.CameraAction
            // if take photo from camera upload to server
            if (result) {
                val inputStream = requireContext().contentResolver.openInputStream(payload)
                viewModel.handleUploadPhoto(inputStream)
            } else {
                // else remove temp uri
                removeTempUri(payload)

            }
        }

    private val editPhotoResultCallback =
        registerForActivityResult(EditImageContract()) { result ->
            if (result != null) {
                val inputStream = requireContext().contentResolver.openInputStream(result)
                viewModel.handleUploadPhoto(inputStream)
            } else {
                val (payload) = binding.pendingAction as PendingAction.EditAction
                removeTempUri(payload.second)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(AvatarActionsDialog.AVATAR_ACTIONS_KEY) { _, bundle ->
            when (bundle[AvatarActionsDialog.SELECT_ACTION_KEY] as String) {
                AvatarActionsDialog.CAMERA_KEY -> viewModel.handleCameraAction(prepareTempUri())
                AvatarActionsDialog.GALLERY_KEY -> viewModel.handleGalleryAction()
                AvatarActionsDialog.DELETE_KEY -> viewModel.handleDeleteAction()
                AvatarActionsDialog.EDIT_KEY -> {
                    lifecycleScope.launch(Dispatchers.IO) {
                        // Glide submit get it is sync call, don't call on UI thread
                        val sourceFile = Glide.with(requireActivity()).asFile().load(binding.avatar).submit().get()
                        val sourceUri = FileProvider.getUriForFile(
                            requireContext(),
                            "${requireContext().packageName}.provider",
                            sourceFile
                        )
                        withContext(Dispatchers.Main) {
                            viewModel.handleEditAction(sourceUri, prepareTempUri())
                        }
                    }
                }
            }
        }
    }

    override fun setupViews() {
        iv_avatar.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavProfileToDialogAvatarAction(binding.avatar.isNotBlank())
            viewModel.navigate(NavigationCommand.To(action.actionId, action.arguments))
        }

        viewModel.observePermissions(viewLifecycleOwner) {
            // launch callback for request permissions
            permissionsResultCallback.launch(it.toTypedArray())
        }

        viewModel.observeActivityResults(viewLifecycleOwner) {
            when (it) {
                is PendingAction.GalleryAction -> galleryResultCallback.launch(it.payload)
                is PendingAction.SettingsAction -> settingResultCallback.launch(it.payload)
                is PendingAction.CameraAction -> cameraResultCallback.launch(it.payload)
                is PendingAction.EditAction -> editPhotoResultCallback.launch(it.payload)
            }
        }
    }

    private fun updateAvatar(avatarUrl: String) {
        if (avatarUrl.isBlank()) {
            Glide.with(this)
                .load(R.drawable.ic_avatar)
                .into(iv_avatar)
        } else {
            Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.ic_avatar)
                .apply(RequestOptions.circleCropTransform())
                .into(iv_avatar)
        }
    }

    private fun prepareTempUri(): Uri {
        val timestamp = SimpleDateFormat("HHmmss", Locale.US).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        // create empty temp file with unique name
        val tempFile = File.createTempFile(
            "JPG${timestamp}",
            ".jpg",
            storageDir
        )

        // must return content:uri not file: uri
        val contentUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            tempFile
        )

        Log.e("ProfileFragment", "file uri: ${tempFile.toUri()} content uri: $contentUri")

        return contentUri
    }

    private fun removeTempUri(uri: Uri) {
        requireContext().contentResolver.delete(uri, null, null)
    }

    inner class ProfileBinding: Binding() {

        var pendingAction: PendingAction? = null

        var avatar by RenderProp("") {
            updateAvatar(it)
        }

        var name by RenderProp("") {
            tv_name.text = it
        }

        var about by RenderProp("") {
            tv_about.text = it
        }

        var rating by RenderProp(0) {
            tv_rating.text = "Rating: $it"
        }

        var respect by RenderProp(0) {
            tv_respect.text = "Respect: $it"
        }

        override fun bind(data: IViewModelState) {
            data as ProfileState
            if (data.avatar != null) avatar = data.avatar
            if (data.name != null) name = data.name
            if (data.about != null) about = data.about
            rating = data.rating
            respect = data.respect
            pendingAction = data.pendingAction
        }

    }

}
