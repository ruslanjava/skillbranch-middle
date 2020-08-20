package ru.skillbranch.skillarticles.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import kotlinx.android.synthetic.main.activity_root.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.ui.auth.AuthFragmentArgs
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand
import ru.skillbranch.skillarticles.viewmodels.base.Notify

abstract class BaseActivity<T : BaseViewModel<out IViewModelState>> : AppCompatActivity() {

    protected abstract val viewModel: T
    protected abstract val layout: Int

    val toolbarBuilder = ToolbarBuilder()
    val bottombarBuilder = BottombarBuilder()

    lateinit var navController: NavController

    //set listeners, tuning views
    abstract fun subscribeOnState(state: IViewModelState)
    abstract fun renderNotification(notify: Notify)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        setSupportActionBar(toolbar)

        viewModel.observeState(this) { subscribeOnState(it) }
        viewModel.observeNotifications(this) { renderNotification(it) }
        viewModel.observeNavigation(this) { subscribeOnNavigation(it) }

        navController = findNavController(R.id.nav_host_fragment)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.saveState()
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        viewModel.restoreState()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun subscribeOnNavigation(command: NavigationCommand) {
        when (command) {
            is NavigationCommand.To -> {
                navController.navigate(command.destination, command.args, command.options, command.extras)
            }
            is NavigationCommand.StartLogin -> {
                val destinationId = command.privateDestination ?: -1
                val args = AuthFragmentArgs(destinationId)
                navController.navigate(R.id.auth, args.toBundle())
            }
            is NavigationCommand.FinishLogin -> {
                navController.navigate(R.id.finish_login)
                val destinationId = command.privateDestination ?: -1
                if (destinationId != -1) {
                    navController.navigate(destinationId)
                }
            }
        }
    }

}

class ToolbarBuilder {

    var subtitle: String? = null
    var logo: String? = null
    var visibility: Boolean = true
    var items: MutableList<MenuItemHolder> = mutableListOf()

    fun setSubtitle(subtitle: String): ToolbarBuilder = apply {
        this.subtitle = subtitle
    }

    fun setLogo(logo: String): ToolbarBuilder = apply {
        this.logo = logo
    }

    fun setVisibility(isVisible: Boolean): ToolbarBuilder = apply {
        this.visibility = isVisible
    }

    fun addMenuItem(item: MenuItemHolder): ToolbarBuilder = apply {
        items.add(item)
    }

    fun invalidate(): ToolbarBuilder = apply {
        this.subtitle = null
        this.logo = null
        this.visibility = true
        this.items.clear()
    }

    fun prepare(prepareFn: (ToolbarBuilder.() -> Unit)?): ToolbarBuilder = apply {
        invalidate()
        prepareFn?.invoke(this)
    }

    fun build(context: FragmentActivity) {
        with(context.toolbar) {
            subtitle = this@ToolbarBuilder.subtitle
            if (this@ToolbarBuilder.logo != null) {
                val logoSize = context.dpToIntPx(40)
                val logoMargin = context.dpToIntPx(16)
                val logoPlaceholder = ContextCompat.getDrawable(context, R.drawable.logo_placeholder)

                logo = logoPlaceholder

                val logo = children.last() as? ImageView
                if (logo != null) {
                    logo.scaleType = ImageView.ScaleType.CENTER_CROP
                    (logo.layoutParams as? Toolbar.LayoutParams)?.let {
                        it.width = logoSize
                        it.height = logoSize
                        it.marginEnd = logoMargin
                        logo.layoutParams = it
                    }
                }

                Glide.with(context)
                        .load(this@ToolbarBuilder.logo)
                        .apply(RequestOptions.circleCropTransform())
                        .override(logoSize)
                        .into(logo!!)

            } else {
                logo = null
            }
        }
    }

}

data class MenuItemHolder(
        val title: String,
        val menuId: Int,
        val icon: Int,
        val actionViewLayout: Int?,
        val clickListener: ((MenuItem) -> Unit)? = null
)

class BottombarBuilder() {

    private var visible: Boolean = true
    private val views = mutableListOf<Int>()
    private val tempViews = mutableListOf<Int>()

    fun addView(layoutId: Int) = apply {
        views.add(layoutId)
    }

    fun setVisibility(isVisible: Boolean) = apply {
        this.visible = isVisible
    }

    fun prepare(prepareFn: (BottombarBuilder.()-> Unit)?) = apply {
        prepareFn?.invoke(this)
    }

    fun invalidate() = apply {
        visible = true
        views.clear()
    }

    fun build(context: FragmentActivity) {

        // show appbar if hidden due to scroll behavior
        context.appbar.setExpanded(true, true)

        // remove temp vies
        if (tempViews.isNotEmpty()) {
            tempViews.forEach {
                val view = context.container.findViewById<View>(it)
                context.container.removeView(view)
            }
            tempViews.clear()
        }

        // add new bottom bar views
        if (views.isNotEmpty()) {
            val inflater = LayoutInflater.from(context)
            views.forEach {
                val view = inflater.inflate(it, context.container, false)
                context.container.addView(view)
                tempViews.add(view.id)
            }
        }

        with(context.nav_view) {
            isVisible = visible
                    // show bottombar if hidden due to scroll behavior
            ((layoutParams as CoordinatorLayout.LayoutParams).behavior as HideBottomViewOnScrollBehavior)
                    .slideUp(this)
        }

    }

}
