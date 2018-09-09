package de.unikl.hci.abbas.behaviometric.Demo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;

import junit.framework.Test;

import java.io.*;
import java.util.LinkedList;

import de.unikl.hci.abbas.behaviometric.Demo.activities.TestModelActivity;
import de.unikl.hci.abbas.behaviometric.Demo.activities.TrainModelActivity;
import de.unikl.hci.abbas.behaviometric.TouchLogger.utils.DeviceInfo;

public class DataWriter {
    private String basename = null;
    private File outputFolder = null;
    //private File dataTar = null;

     /**
     * Create a new data writer
     * @param c The Android app context that created this writer
     * @throws IOException If there's a problem accessing the destination path
     */
    public DataWriter(Context c) throws IOException {
        if(isExternalStorageAvailable()) {
            init(c);
        } else {
            throw new IOException("/sdcard/ storage not available for writing");
        }
    }

    /**
     * Write text data to file, overwriting if it already exists
     * @param buffer Buffer of text data
     * @param filename Name of the destination file. Will be placed under /sdcard/Behaviometric/
     */
    public void writeTextData(final StringBuffer buffer, final String filename) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    File outputFile = new File(outputFolder, basename + "_" + filename);
                    final boolean DONT_APPEND = false;

                    FileWriter fw = new FileWriter(outputFile, DONT_APPEND);
                    BufferedWriter out = new BufferedWriter(fw);

                    out.write(buffer.toString());

                    out.close();
                    fw.close();

                    //gatherIntoTar();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(r).start();
    }

    /**
     * Write gesture data to a compressed file, overwriting if it already exists
     * @param data List of gesture data lines
     */

    public void writeCompressedLoggerData(final LinkedList<LoggerData> data) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    //File outputFile = new File(outputFolder,basename + "_" + filename);
                    File outputFile = new File(outputFolder, basename);
                    final boolean DONT_APPEND = false;

                    FileOutputStream fileOut = new FileOutputStream(outputFile);
                    // GZIPOutputStream zipOut = new GZIPOutputStream(fileOut);
                    // BufferedWriter textOut = new BufferedWriter(new OutputStreamWriter(zipOut, "UTF-8"));
                    BufferedWriter textOut = new BufferedWriter(new OutputStreamWriter(fileOut));

                    for(LoggerData gld : data) {
                        String line = String.format("%s%n", gld.toString());
                        textOut.append(line);
                    }

                    // textOut.close();
                    // zipOut.close();
                    textOut.close();

                    // gatherIntoTar();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(r).start();
    }

    /**
     * Write gesture data to a compressed file, overwriting if it already exists
     * @param data List of gesture data lines
     * @param filename Name of the destination file. Will be placed under /sdcard/Behaviometric/
     */

    public void writeCompressedLoggerData(final LinkedList<LoggerData> data, final String filename) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    File outputFile = new File(outputFolder,basename + "_" + filename);
                    //File outputFile = new File(outputFolder, basename);
                    final boolean DONT_APPEND = false;

                    FileOutputStream fileOut = new FileOutputStream(outputFile);
                    // GZIPOutputStream zipOut = new GZIPOutputStream(fileOut);
                    // BufferedWriter textOut = new BufferedWriter(new OutputStreamWriter(zipOut, "UTF-8"));
                    BufferedWriter textOut = new BufferedWriter(new OutputStreamWriter(fileOut));

                    for(LoggerData gld : data) {
                        String line = String.format("%s%n", gld.toString());
                        textOut.append(line);
                    }

                    // textOut.close();
                    // zipOut.close();
                    textOut.close();

                    // gatherIntoTar();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(r).start();
    }

    /**
     *
     * @return A handle to the tar file containing all the generated files
     */
    /*
    public File getDataTar() {
        return dataTar;
    }
    */
    /**
     * Take all this program's files on the external storage and gather them into a single tar file
     */
    /*
    private void gatherIntoTar() throws IOException {
        // Delete the existing tar file
        if(dataTar.exists()) {
            dataTar.delete();
        }

        // Bundle up all output files into a new tar
        FileOutputStream fileOut = new FileOutputStream(dataTar);
        BufferedOutputStream buffOut = new BufferedOutputStream(fileOut);
        TarOutputStream tarOut = new TarOutputStream(buffOut);

        for(File f : outputFolder.listFiles()) {
            // Ignore the generated tar file
            if(!f.getAbsolutePath().equals(dataTar.getAbsolutePath())) {
                tarOut.putNextEntry(new TarEntry(f, f.getName()));
                FileInputStream fileIn = new FileInputStream(f);
                BufferedInputStream buffIn = new BufferedInputStream(fileIn);

                int count;
                byte data[] = new byte[2048];
                while ((count = buffIn.read(data)) != -1) {
                    tarOut.write(data, 0, count);
                }

                buffIn.close();
                fileIn.close();
            }
        }

        tarOut.close();
        buffOut.close();
        fileOut.close();
    }
    */

    /**
     * Initialize the destination folder for the log files
     * @param c Android app context in which this writer is used
     */
    private void init(Context c) {
        // Create the output directory if needed
        outputFolder = new File(Environment.getExternalStorageDirectory(), "Behaviometric");
        if(!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        // Point to the tar containing all the output files
        String deviceID = DeviceInfo.isSet() ? DeviceInfo.getRandomID() : Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceManufacturer = Build.MANUFACTURER.replaceAll("\\s+", "");
        String deviceModel = Build.MODEL.replaceAll("\\s+", "");
        //basename = String.format("%s_%s--%s", deviceID, deviceManufacturer, deviceModel);

        String name = "";
        String mode = "";

        if (TrainModelActivity.mMode != null && !TrainModelActivity.mMode.isEmpty()) {
            name = TrainModelActivity.mName;
            mode = TrainModelActivity.mMode;
        }

        if (TestModelActivity.mModeTest != null && !TestModelActivity.mModeTest.isEmpty()) {
            name = TestModelActivity.mNameTest;
            mode = TestModelActivity.mModeTest;
        }

        basename = String.format("%s_datafile_%s", name, mode);
        //dataTar = new File(outputFolder, basename + ".tar");
    }

    /**
     *
     * @return True if the external storage is available for reading and writing
     */
    public static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public String getBasename() {
        return basename;
    }
}

