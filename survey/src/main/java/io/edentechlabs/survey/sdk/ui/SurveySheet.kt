package io.edentechlabs.survey.sdk.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.edentechlabs.survey.sdk.R
import io.edentechlabs.survey.sdk.data.network.dto.Survey
import java.lang.Exception

internal class SurveySheet : BottomSheetDialogFragment() {

    private var survey: Survey? = null
    private var listener: SurveySheetAction? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_dialog, container, false)

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val closeBtn = view.findViewById<ImageView>(R.id.etl_survey_close_icon)
        closeBtn?.setOnClickListener {
            dialog?.dismiss()
        }
        this.survey?.url?.let {
            val webView = view.findViewById<WebView>(R.id.etl_survey_web_view)
            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    if (url != null) {
                        if (url.contains("#close")) {
                            listener?.onFinished(survey)
                            dialog?.dismiss()
                        }
                    }
                }
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    return false
                }
            }
            webView?.settings?.javaScriptEnabled = true
            webView?.loadUrl(it)
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            val transaction = manager.beginTransaction()
            transaction.add(this, tag)
            transaction.commitAllowingStateLoss()
        } catch (e: Exception) {

        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        this.listener?.onDismiss()
        super.onDismiss(dialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as? BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog?.findViewById<FrameLayout>(R.id.etl_survey_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            val layoutParams = bottomSheet?.layoutParams
            if (layoutParams != null) {
                Resources.getSystem()?.displayMetrics?.heightPixels?.let { height ->
                    layoutParams.height = height
                }
            }
            bottomSheet?.layoutParams = layoutParams
            behavior.skipCollapsed = true
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    fun setSurvey(survey: Survey) {
        this.survey = survey
    }

    fun setActionsListener(listener: SurveySheetAction) {
        this.listener = listener
    }

    companion object {
        fun getInstance(survey: Survey, listener: SurveySheetAction): SurveySheet {
            val instance = SurveySheet()
            instance.setSurvey(survey)
            instance.setActionsListener(listener)
            return instance
        }
    }
}

internal interface SurveySheetAction {
    fun onFinished(survey: Survey?)
    fun onDismiss()
}