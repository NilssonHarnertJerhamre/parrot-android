package comnilssonharnertjerhamre.httpsgithub.parrot_android;

import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

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

    public static boolean uploading = false;
    public static Map<Integer, Boolean> queue = new HashMap<Integer, Boolean>();


    // Info for connecting to FTP server.
    private static String ip = "ec2-34-210-104-209.us-west-2.compute.amazonaws.com";
    private static String username = "parrot";
    private static String password = "parrot";

    /*

    Connecting to FTP server and uploading file to server.

     */

    public static void upload(int id, String path) {
        Uploader upload = new Uploader(id, path);
        Thread t = new Thread(upload);
        t.start();
    }

    // New class because this functionality is needed to run in a separate thread.
    public static class Uploader implements Runnable {

        private int id;
        private String path;

        public Uploader(int id, String path) {
            this.id = id;
            this.path = path;
        }

        public void run() {

            FTPClient con = null;

            // Blocking other uploads.
            uploading = true;

            try {
                con = new FTPClient();

                con.connect(ip);

                if (con.login(username, password)) {

                    // Default is ascii so changing to binary
                    con.setFileType(FTP.BINARY_FILE_TYPE);
                    String data = path;

                    Log.d("Uploading", data);

                    // Getting file stream and storing it on server.
                    FileInputStream in = new FileInputStream(new File(data));
                    con.changeWorkingDirectory("files");
                    boolean result = con.storeFile("" + id + ".3gp", in);
                    showServerReply(con);
                    in.close();

                    Log.d("FTPHandler", "uploaded: " + result);
                    if (result) {
                        // Unlocking uploading
                        uploading = false;
                        Log.v("upload result", "succeeded");
                    }
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

    /*

    Connecting to FTP server and downloading file from server.

     */

    public static void download(int id, String path) {
        Downloader download = new Downloader(id, path);
        Thread t = new Thread(download);
        t.start();
    }

    // New class because this functionality is needed to run in a separate thread.
    public static class Downloader implements Runnable {

        private int id;
        private String path;

        public Downloader(int id, String path) {
            this.id = id;
            this.path = path;

            // Adding file id to queue so that listener in FragmentFeed know that file is not yet downloaded.
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
                    // Default is ascii so changing to binary
                    con.setFileType(FTP.BINARY_FILE_TYPE);
                    String data = path;

                    // Creating file stream and getting file form server to store on mobile.
                    OutputStream out = new FileOutputStream(new File(data));
                    boolean result = con.retrieveFile("files/" + id + ".3gp", out);
                    out.close();
                    if (result) {

                        // Setting file to downloaded, true. So that listener know that file is downloaded.
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

    /*
    Helper function to troubleshoot errors received when doing requests to FTP server.
     */

    private static void showServerReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                System.out.println("SERVER: " + aReply);
            }
        }
    }
}
