package anissia.android

import android.content.Context
import android.widget.Toast

object CustomToast {
    private var toast: Toast? = null
    fun showToast(context: Context, message: String) {
        toast?.cancel()

        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast?.show()
    }
}