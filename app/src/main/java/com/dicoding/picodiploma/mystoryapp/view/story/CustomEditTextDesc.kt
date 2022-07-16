package com.dicoding.picodiploma.mystoryapp.view.story

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.dicoding.picodiploma.mystoryapp.R

class CustomEditTextDesc : AppCompatEditText {
    var type = ""

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (type == "description") {
                    if (s.isEmpty()) {
                        error = context.getString(R.string.desc_warning)
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {
                // nothing
            }
        })
    }
}