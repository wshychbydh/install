package com.eye.cool.install.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.eye.cool.install.params.PromptParams
import com.eye.cool.install.support.IPromptListener

/**
 *Created by ycb on 2019/12/16 0016
 */
internal class PromptDialog : DialogActivity(), IPromptListener {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setFinishOnTouchOutside(false)
    promptParams?.apply {
      val view = prompt.createView(this@PromptDialog, title, content, this@PromptDialog)
      if (view != null) {
        setContentView(view)
      }
    }
  }

  override fun onUpgrade() {
    promptListener?.onUpgrade()
    finish()
  }

  override fun onCancel() {
    promptListener?.onCancel()
    finish()
  }

  override fun onBackPressed() {
  }

  override fun onDestroy() {
    super.onDestroy()
    promptParams = null
    promptListener = null
  }

  companion object {

    private var promptParams: PromptParams? = null
    private var promptListener: IPromptListener? = null

    fun show(context: Context, promptParams: PromptParams, promptListener: IPromptListener) {
      this.promptParams = promptParams
      this.promptListener = promptListener
      val intent = Intent(context, PromptDialog::class.java)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(intent)
    }
  }
}