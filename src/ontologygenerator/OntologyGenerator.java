
package ontologygenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import ontologygenerator.Ontology.OntologyWriter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

public class OntologyGenerator {
    
    private OntologyWriter writer;
    
    
    public void start(String input) throws IOException, FileNotFoundException, SAXException, TikaException{
        Logger.getRootLogger().setLevel(Level.OFF);
        writer = new OntologyWriter();
        writer.structureOntology();
        
        File inputDirectory = new File(input);
        processDirectory(inputDirectory);
        writer.write();
    }
    
    private void processDirectory(File directory) throws IOException, FileNotFoundException, SAXException, TikaException{
      
        File[] files=directory.listFiles();
        if(files.length == 0){
            return;
        }
        for(File file:files){
           if(file.isDirectory()){
               processDirectory(file);
           }
           else{
               processFile(file);
           }
        } 
        
          
    }
    
    private void processFile(File file) throws IOException, FileNotFoundException, SAXException, TikaException{
        if(file.isHidden() || !file.canRead()){
           return;
        }
        
        writer.insertFileIndividuals(file);
        
    }
    
    
}
