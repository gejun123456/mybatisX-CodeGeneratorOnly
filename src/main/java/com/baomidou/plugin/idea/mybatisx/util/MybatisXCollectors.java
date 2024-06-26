package com.baomidou.plugin.idea.mybatisx.util;

import java.util.stream.Collector;

public class MybatisXCollectors {


    public static Collector<CharSequence, MultiStringJoiner, String> joining(String delimiter) {
        return joining(delimiter, 3);
    }

    public static Collector<CharSequence, MultiStringJoiner, String> joining(String delimiter,
                                                                             int step) {
        return joining(delimiter, "", "", step);
    }


    public static Collector<CharSequence, MultiStringJoiner, String> joining(String delimiter,
                                                                             String prefix,
                                                                             String suffix,
                                                                             int step) {
        return Collector.of(() -> new MultiStringJoiner(delimiter, prefix, suffix, "\n", step),
            MultiStringJoiner::add,
            MultiStringJoiner::merge,
            MultiStringJoiner::toString);
    }


    /**
     * 多列换行连接器
     * 例如总共有7个字段,希望每3列换一行
     * 原始输入:
     * a,b,c,d,e,f,g
     * 使用多列连接器的输出:
     * a,b,c,
     * d,e,f,
     * g
     */
    private static class MultiStringJoiner {

        private final CharSequence prefix;
        private final CharSequence suffix;
        int currentIndex = 0;
        private final String delimiter;
        private final String newLine;
        private final int step;
        private final StringBuilder stringBuilder = new StringBuilder();
        /**
         * @param delimiter 分隔符
         * @param newLine   换行标识
         * @param step      换行步长
         */
        public MultiStringJoiner(String delimiter, CharSequence prefix, CharSequence suffix, String newLine, int step) {
            this.delimiter = delimiter;
            this.prefix = prefix;
            this.suffix = suffix;
            this.newLine = newLine;
            this.step = step;
        }

        public MultiStringJoiner add(CharSequence str) {
            stringBuilder.append(str).append(delimiter);
            currentIndex++;
            if (currentIndex % step == 0) {
                stringBuilder.append(newLine);
            }
            return this;
        }

        @Override
        public String toString() {
            if (currentIndex == 0) {
                return "";
            }
            final int lastDelimiterIndex = stringBuilder.lastIndexOf(delimiter);
            return prefix + stringBuilder.substring(0, lastDelimiterIndex) + suffix;
        }


        /**
         * 目前没有用合并的场景, 所以这里就不实现了
         *
         * @param str
         * @return
         */
        public MultiStringJoiner merge(MultiStringJoiner str) {
            return this;
        }
    }
}
