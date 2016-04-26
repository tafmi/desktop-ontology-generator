
package ontologygenerator.Utils;

import java.io.File;

public class Utils {

    public static String getFileExtension(File file) {
        return file.isDirectory() ? "" : (file.getName().substring(file.getName().lastIndexOf(".") + 1));
    }
    
}
