package unikom.gery.damang.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import unikom.gery.damang.R
import unikom.gery.damang.adapter.PageAdapter
import unikom.gery.damang.sqlite.ddl.DBHelper
import unikom.gery.damang.util.SharedPreference

class WalkthroughtActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var sharedPreference: SharedPreference
    private lateinit var viewPager: ViewPager
    private var layouts = intArrayOf(
            R.layout.lottie_slide1,
            R.layout.lottie_slide2,
            R.layout.lottie_slide3,
            R.layout.lottie_slide4,
            R.layout.lottie_slide5
    )
    private lateinit var dotsLayout: LinearLayout
    private lateinit var dots: Array<ImageView>
    private lateinit var pageAdapter: PageAdapter
    private lateinit var cardViewMulai: CardView
    private lateinit var btnMulai : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //statusbar dan actionbar
        supportActionBar?.hide()
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        setContentView(R.layout.activity_walkthrought)
        //
        sharedPreference = SharedPreference(applicationContext)
        if (!sharedPreference.isFirstTime) {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        } else {
            var dbHelper: DBHelper = DBHelper(applicationContext)
        }
        viewPager = findViewById(R.id.pager)
        pageAdapter = PageAdapter(layouts, applicationContext)
        viewPager.adapter = pageAdapter
        dotsLayout = findViewById(R.id.dots)
        cardViewMulai = findViewById(R.id.cvMulai)
        btnMulai = findViewById(R.id.btnMulai)
        btnMulai.setOnClickListener(this)
        cardViewMulai.visibility = View.INVISIBLE
        //
        createDots(0)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }

            override fun onPageSelected(p0: Int) {
                createDots(p0)

                if (p0 == layouts.size - 1) {
                    cardViewMulai.visibility = View.VISIBLE
                } else {
                    cardViewMulai.visibility = View.INVISIBLE
                }
            }
        })
    }

    fun createDots(position: Int) {
        dotsLayout.removeAllViews()
        dots = Array(layouts.size) { ImageView(this) }
        for (i in layouts.indices) {
            dots[i] = ImageView(this)
            if (i == position) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dots))
            } else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_dots))
            }
            val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            params.setMargins(4, 0, 4, 0)
            dotsLayout.addView(dots[i], params)
        }
    }

    override fun onClick(p0: View?) {
        if (p0 == btnMulai) {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            sharedPreference.isFirstTime = false
            finish()
        }
    }
}