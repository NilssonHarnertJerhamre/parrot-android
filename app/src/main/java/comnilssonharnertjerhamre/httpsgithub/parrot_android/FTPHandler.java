package comnilssonharnertjerhamre.httpsgithub.parrot_android;

import android.util.Log;
import android.widget.ImageButton;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by henriknh on 5/19/17.
 */

public class FTPHandler {

    public static Map<Integer, Boolean> queue = new HashMap<Integer, Boolean>();

    private static String ip = "a4220.research.ltu.se";
    private static String username = "ftp-guest";
    private static String password = "qwerty";

    /*//private static String ip = "ec2-52-35-30-107.us-west-2.compute.amazonaws.com";
    private static String ip = "52.35.30.107";
    private static String username = "parrot";
    private static String password = "parrot";
*/

    public static void upload(int id, String path) {
        Uploader upload = new Uploader(id, path);
        Thread t = new Thread(upload);
        t.start();
    }

    public static class Uploader implements Runnable {

        private int id;
        private String path;

        public Uploader(int id, String path) {
            this.id = id;
            this.path = path;
        }

        public void run() {

            FTPClient con = null;

            try {
                con = new FTPClient();

                con.connect(ip);

                if (con.login(username, password)) {

                    con.enterLocalPassiveMode(); // important!
                    con.setFileType(FTP.BINARY_FILE_TYPE);
                    String data = path;

                    FileInputStream in = new FileInputStream(new File(data));
                    boolean result = con.storeFile("/files/" + id + ".3gp", in);
                    in.close();

                    Log.d("FTPHandler", "uploaded: " + result);
                    if (result) Log.v("upload result", "succeeded");
                    con.logout();
                    con.disconnect();
                } else {
                    Log.d("error", "cant connect");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void download(int id, String path) {
        Downloader download = new Downloader(id, path);
        Thread t = new Thread(download);
        t.start();
    }

    public static class Downloader implements Runnable {

        private int id;
        private String path;

        public Downloader(int id, String path) {
            this.id = id;
            this.path = path;

            queue.put(id, false);
        }

        public void run() {
            FTPClient con = null;

            try
            {
                con = new FTPClient();
                con.connect(ip);

                if (con.login(username, password))
                {
                    con.enterLocalPassiveMode(); // important!
                    con.setFileType(FTP.BINARY_FILE_TYPE);
                    String data = path;

                    OutputStream out = new FileOutputStream(new File(data));
                    boolean result = con.retrieveFile("/files/" + id + ".3gp", out);
                    out.close();
                    if (result) {

                        queue.put(id, true);

                        Log.v("download result", "succeeded");
                    }
                    con.logout();
                    con.disconnect();
                }
            }
            catch (Exception e)
            {
                Log.v("download result","failed");
                e.printStackTrace();
            }

        }
    }
}
