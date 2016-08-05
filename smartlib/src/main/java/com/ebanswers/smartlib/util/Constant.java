package com.ebanswers.smartlib.util;

/**
 * Created by Callanna on 2016/8/3.
 */
public class Constant {
    public static final  String TYPE_LOCAL = "local";
    public static final  String TYPE_CLOUD = "cloud";
    public static final String GARMMAR = "#ABNF 1.0 UTF-8;\nlanguage zh-CN;\nmode voice;\nroot $main;\n$main = $cmd;\n$cmd = %s;";
    public static final String GRAMMAR_TYPE_ABNF = "abnf";
    public static final String GARMMAR2 = "#ABNF 1.0 UTF-8;\n" +
            "language zh-CN; \n" +
            "mode voice;\n" +
            "root $main;\n" +
            "$main = $place1 到 $place2;\n" +
            "$place1 = 北京|武汉|南京|天津|东京;\n" +
            "$place2 = 上海|合肥;";
}
