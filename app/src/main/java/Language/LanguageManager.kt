package Language

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.LocaleList
import java.util.Locale

class LanguageManager(private val context: Context):ContextWrapper(context){
    companion object {
        fun wrap(context: Context, locale: Locale): ContextWrapper {
            var newContext = context
            val res = context.resources
            val configuration = res.configuration

            if (locale != Locale.getDefault()) {
                Locale.setDefault(locale)
                configuration.setLocale(locale)
                configuration.setLocales(LocaleList(locale))
                newContext = context.createConfigurationContext(configuration)
            }
            return LanguageManager(newContext?:context)
        }
    }
}