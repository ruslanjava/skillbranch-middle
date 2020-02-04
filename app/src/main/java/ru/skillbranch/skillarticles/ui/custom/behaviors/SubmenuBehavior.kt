package ru.skillbranch.skillarticles.ui.custom.behaviors

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginRight
import ru.skillbranch.skillarticles.ui.custom.ArticleSubmenu
import ru.skillbranch.skillarticles.ui.custom.Bottombar

class SubmenuBehavior : CoordinatorLayout.Behavior<ArticleSubmenu>() {

    override fun layoutDependsOn(parent: CoordinatorLayout, child: ArticleSubmenu, dependency: View): Boolean {
        return dependency is Bottombar
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: ArticleSubmenu, dependency: View): Boolean {
        if ((dependency is Bottombar) && dependency.translationY != 0.0f) {
            animate(child, dependency)
            return true
        }
        return false
    }

    private fun animate(child: ArticleSubmenu, dependency: Bottombar) {
        val fraction = dependency.translationY / dependency.height
        child.translationX = (child.width + child.marginRight) * fraction
    }

}