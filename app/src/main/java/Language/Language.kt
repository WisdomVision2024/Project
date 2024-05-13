package Language

import java.util.Locale

enum class Language {
    Chinese,  // 中文
    English, // 英文
    Japanese,// 日文
    Korean,// 韓文
    French;//法文

    val locale: Locale
        get() = when (this) {
            English -> Locale("en")
            Chinese -> Locale("zh")
            Japanese -> Locale("ja")
            Korean -> Locale("ko")
            French -> Locale("fr")
        }
}
