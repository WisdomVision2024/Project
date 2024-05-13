package Language

import java.util.Locale

data class LanguageSetting (
    val language: Language,
    val locale: Locale = language.locale
)