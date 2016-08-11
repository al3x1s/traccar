/*
 * Copyright 2016 alexis.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.model;

import com.ning.http.util.Base64;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.traccar.Context;

/**
 *
 * @author alexis
 */
public class Pictures {

    private final Map<Integer, PackagePicture> pictures = new LinkedHashMap<>();   // <PictureNumber, package
    private final Long deviceId;

    public Pictures(Long deviceId) {
        this.deviceId = deviceId;
    }

    public void addPackage(int pictureNumber, int totalPackage, int seqNumber, String hexRaw) throws SQLException {
        PackagePicture packagePicture = new PackagePicture();
        if (pictures.containsKey(pictureNumber)) {
            packagePicture = pictures.get(pictureNumber);
        }
        packagePicture.set(seqNumber, hexRaw);
        pictures.put(pictureNumber, packagePicture);
        if (pictures.get(pictureNumber).getPackageLenth() == totalPackage) {
            savePicture(pictureNumber);
        }
    }

    public void savePicture(int pictureNumber) throws SQLException {
        PackagePicture packagePicture = pictures.get(pictureNumber);
        Map<Integer, String> packages = packagePicture.getPackages();
        StringBuilder sb = new StringBuilder();
        String[] ordered = new String[packages.size()];
        for (Map.Entry<Integer, String> entry : packages.entrySet()) {
            ordered[entry.getKey() - 1] = entry.getValue();
        }
        for (String entry : ordered) {
            sb.append(entry);
        }
        Context.getDataManager().saveImage(deviceId, hexToBase64(sb.toString()));
        pictures.remove(pictureNumber);
    }

    public String hexToBase64(String hex) {
        if ((hex.length()) % 2 > 0) {
            throw new NumberFormatException("Input string was not in a correct format.");
        }
        byte[] buffer = new byte[hex.length() / 2];
        int i = 0;
        while (i < hex.length()) {
            buffer[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
            i += 2;
        }
        return Base64.encode(buffer);
    }
}

class PackagePicture {

    private Map<Integer, String> packages = new LinkedHashMap<>();

    public Map<Integer, String> getPackages() {
        return packages;
    }

    public int getPackageLenth() {
        return packages.size();
    }

    public void setPackages(Map<Integer, String> packages) {
        this.packages = packages;
    }

    public void set(int key, String value) {
        if (value != null && !value.isEmpty()) {
            packages.put(key, value);
        }
    }
}
