
package ontologygenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import ontologygenerator.Exception.InvalidArgumentException;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

public class Main {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     * @throws org.xml.sax.SAXException
     * @throws org.apache.tika.exception.TikaException
     */
    public static void main(String[] args) throws FileNotFoundException, SAXException, TikaException {
        
        try {
            if (args.length != 1) {
                throw new InvalidArgumentException();
            }
            else{  
                String inputDirectory = GetInputPath(args[0]);
                
                
                OntologyGenerator generator = new OntologyGenerator();
                generator.start(inputDirectory);
                
            }
        } catch (IOException ex) {
            System.out.println(String.format("Error: %s", ex.getMessage()));
        } catch (InvalidArgumentException invalidArgEx) {
            System.out.println("Application usage:");
            System.out.println("<executable_name> <input_directory>");
        }
    }
    
    private static String GetInputPath(String argument) throws FileNotFoundException, IOException {
        String inputDirectory = argument;

        File file = new File(inputDirectory);

        if (!file.exists()) {
            throw new FileNotFoundException("Input directory does not exist. Please specify a valid input directory.");
        }

        if (!file.isDirectory()) {
            throw new IOException("The input path that was specified is not a directory. Please specify a valid directory.");
        }
        return inputDirectory;
    }

    
}
