package com.dicoding.nutrifact.custom

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.dicoding.nutrifact.R
import com.google.android.material.textfield.TextInputLayout

class CustomEditText : AppCompatEditText {
    var isCharacterPasswordValid = false
    var isEmailFormatValid = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
                validate()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    fun validate(): Boolean {
        val textInputLayout = parent?.parent as? TextInputLayout
        val input = text.toString()

        return when {
            input.isEmpty() -> {
                textInputLayout?.error = context.getString(R.string.error_empty_field)
                false
            }
            inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD || inputType == 129 -> {
                isCharacterPasswordValid = input.length > 7
                textInputLayout?.error = if (isCharacterPasswordValid) null
                else context.getString(R.string.error_password_min_length)
                isCharacterPasswordValid
            }
            inputType == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS || inputType == 33 -> {
                isEmailFormatValid =
                    Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}").matches(input)
                textInputLayout?.error = if (isEmailFormatValid) null
                else context.getString(R.string.error_format_email)
                isEmailFormatValid
            } else -> {
                textInputLayout?.error = null
                true
            }
        }
    }
}