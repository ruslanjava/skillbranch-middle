package ru.skillbranch.gameofthrones.ui.houses

import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.ColorInt
import androidx.appcompat.widget.SearchView
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_houses.*
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.ui.RootActivity
import kotlin.math.hypot
import kotlin.math.max

class HousesFragment: Fragment() {

    private lateinit var colors: Array<Int>
    private lateinit var housesPagerAdapter: HousesPagerAdapter

    @ColorInt
    private var currentColor: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        housesPagerAdapter = HousesPagerAdapter(childFragmentManager)
        colors = requireContext().run {
            arrayOf(
                getColor(R.color.stark_primary),
                getColor(R.color.lannister_primary),
                getColor(R.color.targaryen_primary),
                getColor(R.color.baratheon_primary),
                getColor(R.color.greyjoy_primary),
                getColor(R.color.martel_primary),
                getColor(R.color.tyrel_primary)
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        with (menu.findItem(R.id.action_search)?.actionView as SearchView) {
            queryHint = "Search character"
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_houses, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as RootActivity).setSupportActionBar(toolbar)

        if (currentColor == -1) {
            currentColor = colors[0]
            appbar.setBackgroundColor(currentColor)
        }

        view_pager.adapter = housesPagerAdapter

        with (tabs) {
            setupWithViewPager(view_pager)
            addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
                override fun onTabSelected(tab: TabLayout.Tab) {
                    val position = tab.position

                    // if colors different, do animate
                    if (appbar.background is ColorDrawable) {
                        if ((appbar.background as ColorDrawable).color != colors[position]) {
                            val rect = Rect()
                            val tabView = tab.view as View

                            tabView.postDelayed( {
                                tabView.getGlobalVisibleRect(rect)
                                animateAppBarReval(position, rect.centerX(), rect.centerY())
                                Log.e("HousesFragment", "$rect: ${rect.centerX()} ${rect.centerY()}")
                            }, 300)
                        }
                    }
                }
            })
        }
    }

    private fun animateAppBarReval(position: Int, centerX: Int, centerY: Int) {
        val endRadius = max(
            hypot(centerX.toDouble(), centerY.toDouble()),
            hypot(appbar.width.toDouble() - centerX.toDouble(), centerY.toDouble())
        )

        with(reveal_view) {
            visibility = View.VISIBLE
            setBackgroundColor(colors[position])
        }

        ViewAnimationUtils.createCircularReveal(
            reveal_view,
            centerX,
            centerY,
            0f,
            endRadius.toFloat()
        ).apply {
            duration = 300L
            doOnEnd {
                appbar?.setBackgroundColor(colors[position])
                reveal_view?.visibility = View.INVISIBLE
            }
        }
    }

}