package unikom.gery.damang.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class PageAdapter : PagerAdapter {

    private var inflater: LayoutInflater
    private var context: Context
    private var layouts: IntArray

    constructor(layouts: IntArray, context: Context) : super() {
        this.context = context
        this.layouts = layouts
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    }

    override fun isViewFromObject(p0: View, `p1`: Any): Boolean {
        return p0 == `p1`
    }

    override fun getCount(): Int {
        return layouts.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var view: View = inflater.inflate(layouts[position], container, false)
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        var view: View = `object` as View
        container.removeView(view)

    }

}