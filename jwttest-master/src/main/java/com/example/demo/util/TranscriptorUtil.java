package com.example.demo.util;

import com.ibm.icu.text.Transliterator;

public class TranscriptorUtil {
    public static final String LATIN_TO_CYRILLIC = "Latin-Russian/BGN";
    public static final String CYRILLIC_TO_LATIN = "Russian-Latin/BGN";

    public static String translateToCyrillic(String initialText) {
        Transliterator toCyrillicTrans = Transliterator.getInstance(LATIN_TO_CYRILLIC);
        return toCyrillicTrans.transliterate(initialText);
    }

    public static String translateToLatin(String initialText) {
        Transliterator toLatinTrans = Transliterator.getInstance(CYRILLIC_TO_LATIN);
        return toLatinTrans.transliterate(initialText);
    }

}
