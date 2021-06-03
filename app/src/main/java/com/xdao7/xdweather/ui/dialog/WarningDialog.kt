package com.xdao7.xdweather.ui.dialog

import android.content.Context
import android.graphics.Outline
import android.os.Bundle
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.app.AppCompatDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.xdao7.xdweather.R
import com.xdao7.xdweather.databinding.DialogWarningBinding
import com.xdao7.xdweather.logic.model.response.qweather.WarningResponse
import com.xdao7.xdweather.ui.adapter.WarningAdapter
import com.xdao7.xdweather.utils.dp2px

class WarningDialog(
    context: Context,
    private val warningList: List<WarningResponse.Warning>,
    themeResId: Int = R.style.Dialog
) : AppCompatDialog(context, themeResId) {

    private val binding = DialogWarningBinding.inflate(layoutInflater)
    private var dialogWidth = 0
    private var dialogHeight = 0

    init {
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dialogWidth = dp2px(context, 300f)
        dialogHeight = dp2px(context, 500f)
        window?.attributes?.apply {
            width = dialogWidth
            height = dialogHeight
        }
        initViews()
    }

    private fun initViews() {
        binding.apply {
            clBg.apply {
                outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View?, outline: Outline?) {
                        outline?.setRoundRect(
                            0,
                            0,
                            dialogWidth,
                            dialogHeight,
                            dp2px(context, 15f).toFloat()
                        )
                    }
                }
                clipToOutline = true
            }
            rvWarning.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = WarningAdapter(context, warningList)
            }
        }
    }
}