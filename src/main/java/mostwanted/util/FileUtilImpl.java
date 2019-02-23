package mostwanted.util;

import java.io.*;

public class FileUtilImpl implements FileUtil {
    @Override
    public String readFile(String filePath) throws IOException {
        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(
                                        new File(filePath))));
        String line = "";
        StringBuilder sb = new StringBuilder();

        while ((line = reader.readLine())!= null){
            sb.append(line).append(System.lineSeparator());
        }

        return sb.toString().trim();
    }
}
