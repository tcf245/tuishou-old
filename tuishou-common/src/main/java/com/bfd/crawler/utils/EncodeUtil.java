package com.bfd.crawler.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EncodeUtil {
    private static final Log LOG = LogFactory.getLog(EncodeUtil.class);
    private static final Set<String> NORMAL_CHARSETS = new HashSet();
    private static final Pattern[] patterns;

    public EncodeUtil() {
    }

    public static String getHtmlEncode(byte[] data) {
        return getHtmlEncode(data, "UTF8");
    }

    public static String getHtmlEncode(byte[] data, String defaultEncode) {
        String encode = guessEncode(data);
        if (StringUtils.isEmpty(encode) || !NORMAL_CHARSETS.contains(encode)) {
            String pageEncode = getPageEncode(new String(data));
            if (StringUtils.isEmpty(pageEncode)) {
                if (StringUtils.isEmpty(encode) || "nomatch".equalsIgnoreCase(encode)) {
                    LOG.warn("Guess encode faild, use defaultEncode");
                    encode = defaultEncode;
                }
            } else {
                LOG.info("Guess innormal encode=" + encode + ", guessing page encode=" + pageEncode);
                encode = pageEncode;
            }
        }

        if (encode.equalsIgnoreCase("GB2312") || encode.equalsIgnoreCase("GB18030")) {
            encode = "GBK";
        }

        return encode;
    }

    public static synchronized String guessEncode(byte[] data) {
        int len = data.length;
        if (len > 8192) {
            len = 8192;
        }

        byte[] destbuf = new byte[len];
        System.arraycopy(data, 0, destbuf, 0, len);

        try {
            String[] encodes = (new EncodeDetector()).detectChineseEncode(new ByteArrayInputStream(destbuf));
            return encodes[0];
        } catch (IOException var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static String guessEncode(String data) {
        return guessEncode(data.getBytes());
    }

    private static String getPageEncode(String data) {
        for(int i = 0; i < patterns.length; ++i) {
            Matcher matcher = patterns[i].matcher(data);
            if (matcher != null && matcher.find()) {
                LOG.info("Matched index=" + i + ", 1=" + matcher.group(1) + ", cnt=" + matcher.groupCount());
                return matcher.group(matcher.groupCount());
            }
        }

        return null;
    }

    public static String getURLEncode(String url) {
        try {
            URL url_ = new URL(url);
            InputStream is = url_.openStream();

            byte[] b = new byte[1024];
            byte[] e = new byte[4194304];

            int len;
            int j;
            for(len = 0; (j = is.read(b)) != -1; len += j) {
                System.arraycopy(b, 0, e, len, j);
            }

            byte[] bb = new byte[len];
            System.arraycopy(e, 0, bb, 0, len);
            return getHtmlEncode(bb, "UTF8");
        } catch (MalformedURLException var8) {
            var8.printStackTrace();
        } catch (IOException var9) {
            var9.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) throws Exception {
        String data = FileUtil.readFromFile2Str("/home/ian/aa.dat");
        byte[] bytes = DataUtil.unzipAndDecode(data);
        final byte[] bytes2 = FileUtil.readFromFile("/home/ian/bb.dat");
        Thread thread = new Thread(new Runnable() {
            public void run() {
                String ss = "";
                System.out.println("thd2=====" + System.currentTimeMillis());

                for(int i = 0; i < 10000; ++i) {
                    String code = EncodeUtil.getHtmlEncode(bytes2);
                    if (!ss.equalsIgnoreCase(code)) {
                        ss = code;
                        System.out.println("thd2=====" + code);
                    }
                }

                System.out.println("thd2=====" + System.currentTimeMillis());
            }
        });
        thread.start();
        String ss = "";
        System.out.println("thd1=====" + System.currentTimeMillis());

        for(int i = 0; i < 10000; ++i) {
            String code = getHtmlEncode(bytes);
            if (!ss.equalsIgnoreCase(code)) {
                ss = code;
                System.out.println("thd1=====" + code);
            }
        }

        System.out.println("thd1=====" + System.currentTimeMillis());
    }

    public static void main2(String[] args) {
        byte[][] bbs = new byte[10][];
        URL[] urls = new URL[10];
        String path = "test.txt";
        FileInputStream fi = null;
        DataInputStream di = null;

        try {
            fi = new FileInputStream(path);
            di = new DataInputStream(fi);

            for(int i = 0; i < 10 && di.available() != 0; ++i) {
                String line = di.readLine();
                urls[i] = new URL(line);
                System.out.println("start get url=" + urls[i]);
                InputStream is = urls[i].openStream();

                byte[] b = new byte[1024];
                byte[] e = new byte[4194304];

                int len;
                int j;
                for(len = 0; (j = is.read(b)) != -1; len += j) {
                    System.arraycopy(b, 0, e, len, j);
                }

                byte[] bb = new byte[len];
                System.arraycopy(e, 0, bb, 0, len);
                bbs[i] = bb;
                System.out.println("get url=" + urls[i]);
                System.out.println("data=" + (new String(bb)).toString().substring(0, 100));
            }
        } catch (Exception var14) {
            var14.printStackTrace();
        }

        long begin = System.currentTimeMillis();
        int count = 0;

        while(count < 10000) {
            byte[][] arr$ = bbs;
            int len$ = bbs.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                byte[] bb = arr$[i$];
                getHtmlEncode(bb, "UTF8");
                ++count;
            }
        }

        long end = System.currentTimeMillis();
        long time = end - begin;
        System.out.println(time);
    }

    static {
        NORMAL_CHARSETS.add("GB18030");
        NORMAL_CHARSETS.add("GB2312");
        NORMAL_CHARSETS.add("UTF8");
        NORMAL_CHARSETS.add("UTF-8");
        NORMAL_CHARSETS.add("GBK");
        patterns = new Pattern[3];
        patterns[0] = Pattern.compile("<meta\\s+charset=['\"]?([^'\"/\\s]+)[^>/\\?]*/?>", 2);
        patterns[1] = Pattern.compile("<meta\\s+http-equiv=['\"]?Content-Type['\"]?\\s+content=['\"]?text/html;\\s+charset=([^'\"/\\s]+)[\\s'\"]\\s*/?>", 2);
        patterns[2] = Pattern.compile("<meta\\s+content=['\"]?text/html;\\s+charset=([^'\"/\\s]+)[\\s'\"]\\s+http-equiv=['\"]?Content-Type['\"]?\\s*/?>", 2);
    }
}
