package com.example.flagquiz

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.setMargins
import com.example.flagquiz.databinding.ActivityMainBinding
import eightbitlab.com.blurview.RenderScriptBlur


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private var count: Int = 0
    private var process: Int = 10
    private var flags: ArrayList<FlagData> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        loadFlags()
        setData(count)
    }

    private fun setData(c: Int) {
        binding.flag.setImageResource(flags[c].image)
        val randomCountry = randomCountry(flags[c].country.toUpperCase())
        var button: Button
        val buttonParams = LinearLayout.LayoutParams(115, 125)
        buttonParams.setMargins(5, 1, 5,1)

        for (i in 0 until 7) {
            button = Button(this)
            button.setOnClickListener(this)
            button.text = randomCountry[i]
            button.setTextColor(Color.parseColor("#FFFFFF"))
            button.setBackgroundResource(R.drawable.button_style)
            button.typeface = Typeface.DEFAULT_BOLD
            button.layoutParams = buttonParams
            binding.letter1.addView(button)
        }

        for (i in 7 until 14) {
            button = Button(this)
            button.setOnClickListener(this)
            button.text = randomCountry[i]
            button.setTextColor(Color.parseColor("#FFFFFF"))
            button.setBackgroundResource(R.drawable.button_style)
            button.typeface = Typeface.DEFAULT_BOLD
            button.layoutParams = buttonParams
            binding.letter2.addView(button)
        }
    }

    private fun randomCountry(str: String): ArrayList<String> {
        val stringList = ArrayList<String>()
        val a = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val l = str.length
        for (i in 0 until l) {
            stringList.add(str[i].toString())
        }
        val b = 14-l
        val str2 = a.substring(0, b)

        for (j in 0 until b) {
            stringList.add(str2[j].toString())
        }

        stringList.shuffle()
        return stringList
    }

    private fun loadFlags() {
        flags.add(FlagData(R.drawable.brazil, "Brazil"))
        flags.add(FlagData(R.drawable.turkey, "Turkey"))
        flags.add(FlagData(R.drawable.russian, "Russia"))
        flags.add(FlagData(R.drawable.india, "India"))
        flags.add(FlagData(R.drawable.pakistan, "Pakistan"))
        flags.add(FlagData(R.drawable.japan, "Japan"))
        flags.add(FlagData(R.drawable.saudia, "Saudi"))
        flags.add(FlagData(R.drawable.ukraine, "Ukraine"))
        flags.add(FlagData(R.drawable.iran, "Iran"))
        flags.add(FlagData(R.drawable.canada, "Canada"))
    }

    override fun onClick(v: View?) {
        val button1 = v as Button
        Log.d("aaa", "${button1.text}")

        val button2 = Button(this)
        val buttonParams = LinearLayout.LayoutParams(115 , 125 )
        buttonParams.setMargins(5)
        button2.layoutParams = buttonParams
        button2.setTextColor(Color.parseColor("#FFFFFF"))
        button2.setBackgroundResource(R.drawable.button_style)
        button2.typeface = Typeface.DEFAULT_BOLD
        button2.text = button1.text
        button1.visibility = View.INVISIBLE
        binding.answer.addView(button2)

        button2.setOnClickListener {
            button1.visibility = View.VISIBLE
            binding.answer.removeView(button2)
        }

        val childCount = binding.answer.childCount
        if (childCount == flags[count].country.length) {
            val stringBuilder = StringBuilder()
            for (i in 0 until childCount) {
                val btn = binding.answer.getChildAt(i) as Button
                stringBuilder.append(btn.text.toString())
            }

            if (stringBuilder.toString() == flags[count].country.toUpperCase()) {
                animator1()
                blocker()
                binding.tv.setOnClickListener {
                    if (binding.tv.text == "Next") {
                        animator2()
                        count++
                        process += 10
                        progressBar()
                        cleaner(count)
                    }
                    else {
                        animator2()
                        count = 0
                        process = 10
                        progressBar()
                        cleaner(count)
                    }
                }
            }
        }
        else if (childCount == 9) {
            animator1()
            binding.blurL.setBackgroundResource(R.drawable.blur_bg_red)
            binding.tv.text = "Retry"
            binding.tv.setBackgroundColor(Color.parseColor("#FF0000"))
            binding.tv.setOnClickListener {
                animator2()
                progressBar()
                cleaner(count)
            }
        }
    }

    private fun animator1() {
        blur()
        binding.blurL.visibility = View.VISIBLE
        val animation1 = AnimationUtils.loadAnimation(this, R.anim.scale1)
        animation1.duration = 650

        if (count < 9) {
            binding.blurL.setBackgroundResource(R.drawable.blur_bg_green)
            binding.tv.text = "Next"
            binding.tv.setBackgroundColor(Color.parseColor("#29C61E"))
        }
        else {
            binding.blurL.setBackgroundResource(R.drawable.blur_bg_yellow)
            binding.tv.text = "Restart"
            binding.tv.setBackgroundColor(Color.parseColor("#FFFF00"))
        }
        binding.blurL.startAnimation(animation1)
    }

    private fun animator2() {
        val animation2 = AnimationUtils.loadAnimation(this, R.anim.scale2)
        animation2.duration = 650
        binding.blurL.startAnimation(animation2)
    }

    private fun blur() {
        val radius = 5f
        val decorView = window.decorView
        val rootView = binding.main
        val windowBackground = decorView.background

        binding.blurL.setupWith(rootView, RenderScriptBlur(this))
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(radius)
    }

    private fun blocker() {
        binding.answer.forEach {
            it.isClickable = false
        }

        binding.letter1.children.forEach {
            it.isClickable = false
        }

        binding.letter2.children.forEach {
            it.isClickable = false
        }
    }

    private fun cleaner(c: Int) {
        binding.blurL.visibility = View.INVISIBLE
        binding.answer.removeAllViews()
        binding.letter1.removeAllViews()
        binding.letter2.removeAllViews()
        loadFlags()
        setData(c)
    }

    private fun progressBar() {
        binding.progressBar.progress = process
        binding.level.text = "10/${count+1}"
    }
}