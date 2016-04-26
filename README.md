### desktop-ontology-generator
This application populates an ontology file (.owl) with data from user's filesystem. It gets a filesystem 

directory as input and it creates ontology individuals for directories, files, file authors and file related 

organisations or companies. The output is written in file "Desktop_Ontology.owl".
#### usage

to run this application in terminal:

1. cd project's directory

2. java -jar OntologyGenerator.jar path/to/input/directory

##### note

Desktop_Ontology.owl is an empty ontology file created by Protege, that it gets populated by this application.

If you like to populate another ontogy file change the "output" variable in OntologyWriter class.
