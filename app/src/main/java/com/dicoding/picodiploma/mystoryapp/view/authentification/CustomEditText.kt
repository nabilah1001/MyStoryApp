package com.dicoding.picodiploma.mystoryapp.view.authentification

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import com.dicoding.picodiploma.mystoryapp.R

class CustomEditText : AppCompatEditText {

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
                if(type == "password") {
                    if(s.length < 6) {
                        error = context.getString(R.string.password_warning)
                    }
                }
                if(type == "email") {
                    if(!Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                        error = context.getString(R.string.email_warning)
                    }
                }
                if(type == "name") {
                    if(s.isEmpty()) {
                        error = context.getString(R.string.name_warning)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // nothing
            }
        })
    }

}