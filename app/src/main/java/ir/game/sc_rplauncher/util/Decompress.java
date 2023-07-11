package ir.game.sc_rplauncher.util;

import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import timber.log.Timber;

public class Decompress {
    private File _zipFile;
    private InputStream _zipFileStream;
    private static final String ROOT_LOCATION = Environment.getExternalStorageDirectory().getPath();
    private static final String TAG = "UNZIPUTIL";

    public Decompress(File zipFile) {
        _zipFile = zipFile;

        _dirChecker("");
    }

    public Decompress(InputStream zipFile) {
        _zipFileStream = zipFile;

        _dirChecker("");
    }

    public Boolean unzip() {
        try {
            Timber.tag(TAG).i("Starting to unzip");
            InputStream fin = _zipFileStream;
            if (fin == null) {
                fin = new FileInputStream(_zipFile);
            }
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null) {
                Timber.tag(TAG).v("Unzipping %s", ze.getName());

                if (ze.isDirectory()) {
                    _dirChecker(ROOT_LOCATION + "/" + ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(new File(ROOT_LOCATION, ze.getName()));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int count;

                    // reading and writing
                    while ((count = zin.read(buffer)) != -1) {
                        baos.write(buffer, 0, count);
                        byte[] bytes = baos.toByteArray();
                        fout.write(bytes);
                        baos.reset();
                    }

                    fout.close();
                    zin.closeEntry();
                }

            }
            zin.close();
            Timber.tag(TAG).i("Finished unzip");
            return true;
        } catch (Exception e) {
            Timber.tag(TAG).e(e, "Unzip Error");
            return false;
        }

    }

    private void _dirChecker(String dir) {
        File f = new File(dir);
        Timber.tag(TAG).i("creating dir %s", dir);

        dir.length();
        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }
}
