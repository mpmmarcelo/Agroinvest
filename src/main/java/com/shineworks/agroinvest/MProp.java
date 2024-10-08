//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.shineworks.agroinvest;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class MProp {
    private static final String FILE_DIR = "config.properties";
    private static Properties prop;
    private static InputStream fis;
    private static OutputStream fos;

    private MProp() {
    }

    private static void load() {
        prop = new Properties();

        try {
            fis = new FileInputStream("config.properties");
            prop.load(fis);
        } catch (IOException var9) {
            //MDialog.createErrAlert(var9);
        } finally {
            try {
                fis.close();
            } catch (IOException var8) {
                //MDialog.createErrAlert(var8);
            }

        }

    }

    public static String getString(MProp.Constant key) {
        load();
        return prop.getProperty(key.toString());
    }

    public static int getInteger(MProp.Constant key) {
        load();
        return Integer.valueOf(prop.getProperty(key.toString()));
    }

    public static boolean getBoolean(MProp.Constant key) {
        load();
        return Boolean.valueOf(prop.getProperty(key.toString()));
    }

    public static void store(MProp.Constant key, String value) {
        load();

        try {
            fos = new FileOutputStream("config.properties");
            prop.setProperty(key.toString(), value);
            prop.store(fos, (String)null);
        } catch (IOException var11) {
            //MDialog.createErrAlert(var11);
        } finally {
            try {
                fos.close();
            } catch (IOException var10) {
                //MDialog.createErrAlert(var10);
            }

        }

    }

    public static Properties getProp() {
        return prop;
    }

    public static List<String> loadList(String fileDir) {
        try {
            //https://mkyong.com/java/how-to-read-utf-8-encoded-data-from-a-file-java/
            //https://www.devmedia.com.br/trabalhando-com-arquivos-utf-8-em-java/27551
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(fileDir), "UTF-8"));
            Throwable var2 = null;

            try {
                StringBuilder sb = new StringBuilder();

                for(String line = br.readLine(); line != null; line = br.readLine()) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                }

                String everything = sb.toString();
                List var6 = Arrays.asList(everything.split(System.lineSeparator()));
                return var6;
            } catch (Throwable var16) {
                var2 = var16;
                throw var16;
            } finally {
                if (br != null) {
                    if (var2 != null) {
                        try {
                            br.close();
                        } catch (Throwable var15) {
                            var2.addSuppressed(var15);
                        }
                    } else {
                        br.close();
                    }
                }

            }
        } catch (Exception var18) {
            //MDialog.createErrAlert(var18);
            return null;
        }
    }

    public static enum Constant {
        DB_CLIENT,
        DB_ADMIN,
        USER,
        PASS;

        private Constant() {
        }
    }
}
