/*
 * Copyright (c) 2015.  James Morris Studios
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jamesmorrisstudios.com.randremind.utilities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import jamesmorrisstudios.com.randremind.application.App;

/**
 * File writer class that can take images or strings and read/write them to app hidden directories
 * or to the app cache dir on the sd card.
 * <p/>
 * Created by James on 9/19/2014.
 */
public final class FileWriter {
    public static final String TAG = "FileWriter";

    /**
     * Checks if the given file exists
     *
     * @param fileName The name of the file
     * @param external If internal or external storage
     * @return True if the file exists, false otherwise
     */
    public synchronized static boolean doesFileExist(@NonNull String fileName, boolean external) {
        return getFile(fileName, external).exists();
    }

    /**
     * Gets the Uri of the file. Useful mostly for external files
     *
     * @param fileName The name of the file
     * @param external If internal or external storage
     * @return The URI pointing to the file
     */
    @NonNull
    public static URI getFileUri(@NonNull String fileName, boolean external) {
        File file = getFile(fileName, external);
        return file.toURI();
    }

    /**
     * Writes a generic byte array to a file
     *
     * @param fileName The name of the file
     * @param bytes    The byte array to write
     * @param external If internal or external storage
     * @return True if successful
     */
    public synchronized static boolean writeFile(@NonNull String fileName, @NonNull byte[] bytes, boolean external) {
        File file = getFile(fileName, external);
        FileOutputStream outputStream;
        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    return false;
                }
            }
            outputStream = App.getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(bytes);
            outputStream.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Reads a file and returns the byte array of its contents
     *
     * @param fileName The name of the file
     * @param external If internal or external storage
     * @return Byte array of the file contents
     */
    @Nullable
    public synchronized static byte[] readFile(@NonNull String fileName, boolean external) {
        File file = getFile(fileName, external);
        if (!file.exists()) {
            return null;
        }
        byte[] bytes;
        FileInputStream inputStream;
        try {
            inputStream = App.getContext().openFileInput(fileName);
            if (inputStream != null) {
                bytes = readBytes(inputStream);
                inputStream.close();
                return bytes;
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    /**
     * Deletes the given file
     *
     * @param fileName The name of the file
     * @param external If internal or external storage
     * @return True if successful
     */
    public synchronized static boolean deleteFile(@NonNull String fileName, boolean external) {
        File file = getFile(fileName, external);
        return file.delete();
    }

    /**
     * Gets a file handle for manipulation
     *
     * @param fileName The name of the file
     * @param external If internal or external storage
     * @return The File handle
     */
    @NonNull
    public static File getFile(@NonNull String fileName, boolean external) {
        if (external) {
            return new File(App.getContext().getExternalCacheDir(), fileName);
        } else {
            return new File(App.getContext().getFilesDir(), fileName);
        }
    }

    /**
     * Takes an inputStream and reads it into a byte array
     *
     * @param inputStream Byte input stream
     * @return Byte array of the input stream
     * @throws IOException
     */
    @NonNull
    private static byte[] readBytes(@NonNull InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

}
