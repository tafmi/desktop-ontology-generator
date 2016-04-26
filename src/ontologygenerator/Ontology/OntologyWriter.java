
package ontologygenerator.Ontology;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDFS;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import ontologygenerator.File.FileAttributes;
import ontologygenerator.File.FileMetadata;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

public class OntologyWriter {
    
    private final Map<String,String> writtenLocationIndividuals;
    private final Map<String,String> writtenPersonIndividuals;
    private final Map<String,String> writtenOrganisationIndividuals;
    
    private int currentLocationInstance;
    private int currentFileInstance;
    private int currentPersonInstance;
    private int currentOrganisationInstance;
    private int currentNameInstance;
    private int currentPersonNameInstance;
    
    private final  OntModel model;
    private final String ns;
    
    private OntClass thingClass; 
    private OntClass personClass;
    private OntClass organisationClass;
    private OntClass locationClass;
    private OntClass nameClass;
    private OntClass personNameClass;
    private OntClass fileClass;
    
    private ObjectProperty hasLocation;
    private ObjectProperty hasName;
    private ObjectProperty personName;
    private ObjectProperty hasAuthor;
    private ObjectProperty authors;
    private ObjectProperty relatedThing;
    private ObjectProperty relatedFile;
    
    private DatatypeProperty path;
    private DatatypeProperty name;
    private DatatypeProperty fileType;
    private DatatypeProperty creationTime;
    private DatatypeProperty lastModificationTime;
    private DatatypeProperty lastAccessTime;
    private DatatypeProperty size;
    
    private final String output = "Desktop_Ontology.owl";
    
    
    public OntologyWriter(){
        
        writtenLocationIndividuals = new HashMap<>();
        writtenPersonIndividuals = new HashMap<>();
        writtenOrganisationIndividuals = new HashMap<>();
        currentLocationInstance = 0;
        currentFileInstance = 0;
        currentPersonInstance = 0;
        currentOrganisationInstance = 0;
        currentNameInstance = 0;
        currentPersonNameInstance = 0;
        model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM);     
        try (InputStream in = FileManager.get().open(output)) {
            if (in == null)
            {
                throw new IllegalArgumentException( "File"+output+" not found");
            }
            model.read(in, "");
        }
        catch (IOException ex) {
            System.out.println(String.format("Error: %s", ex.getMessage()));
        }
        ns=model.getNsPrefixURI("");
        
        
         
    }
    
    public void structureOntology(){
        
        createClasses();
        createObjectProperties();
        createDatatypeProperties();
    }
    
    private void createClasses(){
        
        createOntologyClass("Thing","");
        createOntologyClass("Living_Thing","Thing");
        createOntologyClass("Person","Living_Thing");
        createOntologyClass("Non_Living_Thing","Thing");
        createOntologyClass("Artefact","Non_Living_Thing");
        createOntologyClass("Organisation","Thing");
        createOntologyClass("Value_Class","");
        createOntologyClass("Location","Value_Class");
        createOntologyClass("Name","Value_Class");
        createOntologyClass("Person_Name","Name");
        createOntologyClass("Storage_Media","Value_Class");
        createOntologyClass("File","Storage_Media");
        
        thingClass = model.getOntClass(ns+"Thing");
        personClass = model.getOntClass(ns+"Person");
        organisationClass = model.getOntClass(ns+"Organisation");
        locationClass = model.getOntClass(ns+"Location");
        nameClass = model.getOntClass(ns+"Name");
        personNameClass = model.getOntClass(ns+"Person_Name");
        fileClass = model.getOntClass(ns+"File");
    }
    
    private void createObjectProperties(){
        
        hasLocation = model.createObjectProperty(ns+"hasLocation");
        hasLocation.addDomain(fileClass);
        hasLocation.addRange(locationClass);
        hasName = model.createObjectProperty(ns+"hasName");
        hasName.addDomain(thingClass);
        hasName.addRange(nameClass);
        personName = model.createObjectProperty(ns+"personName");
        personName.addDomain(personClass);
        personName.addRange(personNameClass);
        hasAuthor = model.createObjectProperty(ns+"hasAuthor");
        hasAuthor.addDomain(fileClass);
        hasAuthor.addRange(personClass);
        authors = model.createObjectProperty(ns+"Authors");
        authors.addDomain(personClass);
        authors.addRange(fileClass);
        relatedThing = model.createObjectProperty(ns+"relatedThing");
        relatedThing.addDomain(thingClass);
        relatedThing.addDomain(fileClass);
        relatedThing.addRange(thingClass);
        relatedFile = model.createObjectProperty(ns+"relatedFile");
        relatedFile.addDomain(thingClass);
        relatedFile.addDomain(locationClass);
        relatedFile.addRange(fileClass);
        
        
    }
    
    private void createDatatypeProperties(){
        
        path = model.createDatatypeProperty(ns+"path");
        path.addDomain(locationClass);
        name = model.createDatatypeProperty(ns+"name");
        name.addDomain(nameClass);
        name.addDomain(personNameClass);
        creationTime = model.createDatatypeProperty(ns+"creatioTime");
        creationTime.addDomain(fileClass);
        lastModificationTime = model.createDatatypeProperty(ns+"lastModificationTime");
        lastModificationTime.addDomain(fileClass);
        lastAccessTime = model.createDatatypeProperty(ns+"lastAccessTime");
        lastAccessTime.addDomain(fileClass);
        fileType = model.createDatatypeProperty(ns+"fileType");
        fileType.addDomain(fileClass);
        size = model.createDatatypeProperty(ns+"size");
        size.addDomain(fileClass);
    }
    
    public  void createOntologyClass(String ontClass,String superClass){
       
           OntClass ontclass=model.createClass(ns+ontClass);
           if(!superClass.equals("")){
               OntClass superclass=model.getOntClass(ns+superClass);
               model.add(ontclass,RDFS.subClassOf,superclass);
           }
    }
    
    public void insertFileIndividuals(File file) throws IOException, FileNotFoundException, SAXException, TikaException{
        
        
        FileAttributes attributes = new FileAttributes(file);
        FileMetadata metadata = new FileMetadata(file);
        
        String fileLocation = metadata.getFileParentPath();
        Individual locationIndividual = createLocationIndividual(fileLocation);
        
        String fileName = metadata.getFileName();
        Individual fileIndividual = createFileIndividual(fileName,attributes);
        createHasLocationRelation(fileIndividual,locationIndividual);
        createRelatedFileRelation(locationIndividual,fileIndividual);
        
        String author = metadata.getAuthor();
        if(author!=null && !author.matches("^\\s*$") && !author.equals("")){
            Individual personIndividual = createPersonIndividual(author);
            createHasAuthorRelation(fileIndividual,personIndividual);
            createAuthorsRelation(personIndividual,fileIndividual);
        }
        String organisation = metadata.getCompany();
        if(organisation!=null && !organisation.matches("^\\s*$") && !organisation.equals("")){ 
            Individual organisationIndividual = createOrganisationIndividual(organisation);
            createRelatedThingRelation(fileIndividual,organisationIndividual);
            createRelatedFileRelation(organisationIndividual,fileIndividual);
        }
         
    }
    
    private Individual createNameIndividual(String name){
        
        String instance="Name_"+currentNameInstance;
        currentNameInstance++;
        
        Individual nameIndividual = model.createIndividual(ns+instance,nameClass);
        nameIndividual.addLiteral(this.name,name);
        return nameIndividual;
    }
    
    private Individual createPersonNameIndividual(String personName){
        
        String instance="Person_Name_"+currentPersonNameInstance;
        currentPersonNameInstance++;
        
        Individual personNameIndividual = model.createIndividual(ns+instance,personNameClass);
        personNameIndividual.addLiteral(name,personName);
        return personNameIndividual;
    }
    
    private Individual createFileIndividual(String filename,FileAttributes attributes){
        String instance="File_"+currentFileInstance;
        currentFileInstance++;
        
        Individual nameIndividual = createNameIndividual(filename);
        
        Individual fileIndividual = model.createIndividual(ns+instance,fileClass);
        fileIndividual.addLiteral(fileType,attributes.getType());
        fileIndividual.addLiteral(creationTime,attributes.getCreationTime());
        fileIndividual.addLiteral(lastModificationTime,attributes.getModificationTime());
        fileIndividual.addLiteral(lastAccessTime,attributes.getAccessTime());
        fileIndividual.addLiteral(size,attributes.getSize());
        
        createHasNameRelation(fileIndividual,nameIndividual);
        
        return fileIndividual;
    }
    
    private Individual createLocationIndividual(String path){
        
       String instance;
       Individual locationIndividual;
       if(writtenLocationIndividuals.containsKey(path)){
          instance = writtenLocationIndividuals.get(path);
          locationIndividual = model.getIndividual(ns+instance);
       }
       else{
          instance = "Location_"+currentLocationInstance;
          writtenLocationIndividuals.put(path,instance);
          locationIndividual = model.createIndividual(ns+instance,locationClass);   
          locationIndividual.addLiteral(this.path, path);   
          currentLocationInstance++;
       } 
             
       return locationIndividual;
    }
    
    private Individual createPersonIndividual(String personName){
        
        String instance;
        Individual personIndividual;
        if(writtenPersonIndividuals.containsKey(personName)){
          instance = writtenPersonIndividuals.get(personName);
          personIndividual = model.getIndividual(ns+instance);
        }
        else{
          instance = "Person_"+currentPersonInstance;
          writtenPersonIndividuals.put(personName,instance);
          personIndividual = model.createIndividual(ns+instance,personClass);
          Individual personNameIndividual = createPersonNameIndividual(personName);
          createPersonNameRelation(personIndividual,personNameIndividual);
          currentPersonInstance++;
        }

        return personIndividual;
    }
    
    
    private Individual createOrganisationIndividual(String companyName){
     
        String instance;
        Individual orgIndividual;
        if(writtenOrganisationIndividuals.containsKey(companyName)){
          instance = writtenOrganisationIndividuals.get(companyName);
          orgIndividual = model.getIndividual(ns+instance);
        }
        else{
          instance = "Organisation_"+currentOrganisationInstance;
          writtenOrganisationIndividuals.put(companyName,instance);
          orgIndividual = model.createIndividual(ns+instance,organisationClass);
          Individual nameIndividual = createNameIndividual(companyName);
          createHasNameRelation(orgIndividual,nameIndividual);
          currentOrganisationInstance++;
        }
        
        return orgIndividual;
    }
    
    
    private void createHasLocationRelation(Individual fileIndividual,Individual locationIndividual){

        model.add(fileIndividual,hasLocation,locationIndividual);
        
    }
    
    private void createHasNameRelation(Individual thingIndividual,Individual nameIndividual){

        model.add(thingIndividual,hasName,nameIndividual);
        
    }
    
    private void createHasAuthorRelation(Individual fileIndividual,Individual authorIndividual){

        model.add(fileIndividual,hasAuthor,authorIndividual);
        
    }
    
    private void createPersonNameRelation(Individual personIndividual,Individual personNameIndividual){
        
        model.add(personIndividual,personName,personNameIndividual);
        
    }
    
    private void createRelatedThingRelation(Individual fromIndividual,Individual toIndividual){

        model.add(fromIndividual,relatedThing,toIndividual);
        
    }
    
    private void createRelatedFileRelation(Individual fromIndividual,Individual fileIndividual){
        
        model.add(fromIndividual,relatedFile,fileIndividual);
        
    }
    
    private void createAuthorsRelation(Individual personIndividual,Individual fileIndividual){

        model.add(personIndividual,authors,fileIndividual);
        
    }
    
    public void write()throws FileNotFoundException, IOException{
        FileWriter out = null;
        try {
            out = new FileWriter(output);
            model.writeAll(out, "RDF/XML-ABBREV", null);
        } 
        finally {
            if (out != null) {
                  try {out.close();} catch (IOException ignore) {}
            }
        }
    }
    
}
