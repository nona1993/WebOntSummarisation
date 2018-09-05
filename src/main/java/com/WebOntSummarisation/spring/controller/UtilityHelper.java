package com.WebOntSummarisation.spring.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
//import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.SetOntologyID;
import org.semanticweb.owlapi.modularity.OntologySegmenter;


import forgetting.Fame;
import uk.ac.man.cs.lethe.forgetting.AlcOntologyForgetter;
import uk.ac.man.cs.lethe.forgetting.AlchTBoxForgetter;
import uk.ac.man.cs.lethe.forgetting.IOWLForgetter;
import uk.ac.man.cs.lethe.interpolation.AlcOntologyInterpolator;
import uk.ac.man.cs.lethe.interpolation.AlchTBoxInterpolator;
import uk.ac.man.cs.lethe.interpolation.IOWLInterpolator;
import uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

public class UtilityHelper {
	public static List<String> computeALCHTBoxInterpolant(OWLOntologyManager manager,OWLOntology inputOntology,List<String> _symbols) throws OWLOntologyStorageException{
		System.out.println("Inside ALCHBoxInterpol");
		IOWLInterpolator interpolator = new AlchTBoxInterpolator();
		Set<OWLEntity> symbols = new HashSet<OWLEntity>();
		// Add symbols
		for (int i = 0; i < _symbols.size(); i++) {
			symbols.add(findEntity(inputOntology, _symbols.get(i)));
		}
		OWLOntology interpolant = interpolator.uniformInterpolant(inputOntology, symbols);
		Set<OWLAxiom> axioms = interpolant.getAxioms();// .getTBoxAxioms(true);
		List<String> results = new ArrayList<String>();
		for (OWLAxiom a : axioms) {

			// System.out.println(a.getAnnotations().size());
			OWLObjectRenderer renderer = new uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer();
			String axValue = renderer.render(a);
			results.add(axValue);
			// results.add(a.toString());
			// System.out.println(a.toString());
		}

		// Save ontology in Manchester format
		//String rootPath = System.getProperty("catalina.home");
		//File dir = new File(rootPath + File.separator + "tmpFiles");
		
		String rootPath = System.getProperty("catalina.home");
		File dir = new File(rootPath +  File.separator + "webapps" + File.separator + "WebOntSummarisation" + File.separator + "ontologies");
		
		// Create the file on server
		File outputFile = new File(dir.getAbsolutePath() + File.separator + "lethe_modified.owl");
		if (outputFile.exists() == true) {
			outputFile.delete();
			System.out.println("Deleted it.");
		}

		OWLOntologyFormat format = manager.getOntologyFormat(inputOntology);
		ManchesterOWLSyntaxOntologyFormat manSyntaxFormat = new ManchesterOWLSyntaxOntologyFormat();
		if (format.isPrefixOWLOntologyFormat()) {
			manSyntaxFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
			System.out.println("Inside");
		}
		IRI ontologyIRI = IRI.create(outputFile.toURI());
		OWLOntologyID newOntologyID = new OWLOntologyID(ontologyIRI);
		SetOntologyID setOntologyID = new SetOntologyID(interpolant, newOntologyID);
		manager.applyChange(setOntologyID);
		System.out.println("\nOntology IRI:" + interpolant.getOntologyID().toString());
		// manager.setOntologyDocumentIRI(interpolant, );
		manager.saveOntology(interpolant, manSyntaxFormat, IRI.create(outputFile.toURI()));

		return results;
	 }
	public static List<String> forgetUsingALCHTBox(OWLOntologyManager manager,OWLOntology inputOntology,List<String> _symbols) throws OWLOntologyStorageException{
		System.out.println("Inside forgetUsingALCHTBox");
		IOWLForgetter forgetter = new AlchTBoxForgetter();
		
		Set<OWLEntity> symbols = new HashSet<OWLEntity>();
		// Add symbols
		for (int i = 0; i < _symbols.size(); i++) {
			symbols.add(findEntity(inputOntology, _symbols.get(i)));
		}
		OWLOntology ontologyAfterForget = forgetter.forget(inputOntology, symbols);
		Set<OWLAxiom> axioms = ontologyAfterForget.getAxioms();// .getTBoxAxioms(true);
		List<String> results = new ArrayList<String>();
		for (OWLAxiom a : axioms) {

			// System.out.println(a.getAnnotations().size());
			OWLObjectRenderer renderer = new uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer();
			String axValue = renderer.render(a);
			results.add(axValue);
			// results.add(a.toString());
			// System.out.println(a.toString());
		}

		// Save ontology in Manchester format
//		String rootPath = System.getProperty("catalina.home");
//		File dir = new File(rootPath + File.separator + "tmpFiles");

		String rootPath = System.getProperty("catalina.home");
		File dir = new File(rootPath +  File.separator + "webapps" + File.separator + "WebOntSummarisation" + File.separator + "ontologies");
		
		// Create the file on server
		File outputFile = new File(dir.getAbsolutePath() + File.separator + "lethe_modified.owl");
		if (outputFile.exists() == true) {
			outputFile.delete();
			System.out.println("Deleted it.");
		}

		OWLOntologyFormat format = manager.getOntologyFormat(inputOntology);
		ManchesterOWLSyntaxOntologyFormat manSyntaxFormat = new ManchesterOWLSyntaxOntologyFormat();
		if (format.isPrefixOWLOntologyFormat()) {
			manSyntaxFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
			System.out.println("Inside");
		}
		IRI ontologyIRI = IRI.create(outputFile.toURI());
		OWLOntologyID newOntologyID = new OWLOntologyID(ontologyIRI);
		SetOntologyID setOntologyID = new SetOntologyID(ontologyAfterForget, newOntologyID);
		manager.applyChange(setOntologyID);
		System.out.println("\nOntology IRI:" + ontologyAfterForget.getOntologyID().toString());
		// manager.setOntologyDocumentIRI(interpolant, );
		manager.saveOntology(ontologyAfterForget, manSyntaxFormat, IRI.create(outputFile.toURI()));

		return results;
	 }
	public static OWLEntity findEntity(OWLOntology inputOntology,String name){
		 Set<OWLEntity> entities = inputOntology.getSignature();
		for (OWLEntity entity : entities) {
			String eName = entity.getIRI().getShortForm().toLowerCase();
			if (eName.equals(name)==true)
				return entity;
		}
		return null;
	 }
	 public static List<String> computeALCABoxInterpolant(OWLOntologyManager manager,OWLOntology inputOntology,List<String> _symbols) throws OWLOntologyStorageException{
		 System.out.println("Inside ALCABoxInterpol");
		 IOWLInterpolator interpolator = new AlcOntologyInterpolator();
		 Set<OWLEntity> symbols=new HashSet<OWLEntity>();
		 // Add symbols
		 for(int i=0;i<_symbols.size();i++){
			 symbols.add(findEntity(inputOntology,_symbols.get(i)));
		 }
		 OWLOntology interpolant = interpolator.uniformInterpolant(inputOntology, symbols);
		 System.out.println("After interpolant");
		 Set<OWLAxiom> axioms = interpolant.getABoxAxioms(true);//.getAxioms(true);//.getAxioms();//.getTBoxAxioms(true);
		 List<String> results = new ArrayList<String>();
		 for (OWLAxiom a : axioms) {
			 
			//System.out.println(a.getAnnotations().size());
			OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();
			String axValue = renderer.render(a);
			results.add(axValue);
			//System.out.println(a.toString());
		}
		 
			// Save ontology in Manchester format
//			String rootPath = System.getProperty("catalina.home");
//			File dir = new File(rootPath + File.separator + "tmpFiles");
			
		 	//String rootPath = System.getProperty("user.dir");
		 String rootPath = System.getProperty("catalina.home");
		 	File dir = new File(rootPath +  File.separator + "webapps" + File.separator + "WebOntSummarisation" + File.separator + "ontologies");
			
			// Create the file on server
			File outputFile = new File(dir.getAbsolutePath()
					+ File.separator + "lethe_modified.owl");
			if(outputFile.exists()==true){
				outputFile.delete();
				System.out.println("Deleted it.");
			}
			
			OWLOntologyFormat format = manager.getOntologyFormat(inputOntology);
			ManchesterOWLSyntaxOntologyFormat manSyntaxFormat = new ManchesterOWLSyntaxOntologyFormat();
			if (format.isPrefixOWLOntologyFormat()) {
				manSyntaxFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
				System.out.println("Inside");
			}
			IRI ontologyIRI = IRI.create(outputFile.toURI());
			OWLOntologyID newOntologyID = new OWLOntologyID(ontologyIRI);
			SetOntologyID setOntologyID = new SetOntologyID(interpolant, newOntologyID);
			manager.applyChange(setOntologyID);
			System.out.println("\nOntology IRI:" + interpolant.getOntologyID().toString());
			// manager.setOntologyDocumentIRI(interpolant, );
			manager.saveOntology(interpolant, manSyntaxFormat, IRI.create(outputFile.toURI()));
			
		 System.out.println("outside");
		 return results;
	 }
	 public static List<String> forgetUsingALCABox(OWLOntologyManager manager,OWLOntology inputOntology,List<String> _symbols) throws OWLOntologyStorageException{
		 System.out.println("Inside forgetUsingALCABox");
		 IOWLForgetter forgetter = new AlcOntologyForgetter();
		 Set<OWLEntity> symbols=new HashSet<OWLEntity>();
		 // Add symbols
		 for(int i=0;i<_symbols.size();i++){
			 symbols.add(findEntity(inputOntology,_symbols.get(i)));
		 }
		 OWLOntology ontologyAfterForget = forgetter.forget(inputOntology, symbols);
		 System.out.println("After interpolant");
		 Set<OWLAxiom> axioms = ontologyAfterForget.getABoxAxioms(true);//.getAxioms(true);//.getAxioms();//.getTBoxAxioms(true);
		 List<String> results = new ArrayList<String>();
		 for (OWLAxiom a : axioms) {
			 
			//System.out.println(a.getAnnotations().size());
			OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();
			String axValue = renderer.render(a);
			results.add(axValue);
			//System.out.println(a.toString());
		}
		 
			// Save ontology in Manchester format
//			String rootPath = System.getProperty("catalina.home");
//			File dir = new File(rootPath + File.separator + "tmpFiles");
			
		 String rootPath = System.getProperty("catalina.home");
		 	File dir = new File(rootPath +  File.separator + "webapps" + File.separator + "WebOntSummarisation" + File.separator + "ontologies");
			
			// Create the file on server
			File outputFile = new File(dir.getAbsolutePath()
					+ File.separator + "lethe_modified.owl");
			if(outputFile.exists()==true){
				outputFile.delete();
				System.out.println("Deleted it.");
			}
			
			OWLOntologyFormat format = manager.getOntologyFormat(inputOntology);
			ManchesterOWLSyntaxOntologyFormat manSyntaxFormat = new ManchesterOWLSyntaxOntologyFormat();
			if (format.isPrefixOWLOntologyFormat()) {
				manSyntaxFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
				System.out.println("Inside");
			}
			IRI ontologyIRI = IRI.create(outputFile.toURI());
			OWLOntologyID newOntologyID = new OWLOntologyID(ontologyIRI);
			SetOntologyID setOntologyID = new SetOntologyID(ontologyAfterForget, newOntologyID);
			manager.applyChange(setOntologyID);
			System.out.println("\nOntology IRI:" + ontologyAfterForget.getOntologyID().toString());
			// manager.setOntologyDocumentIRI(interpolant, );
			manager.saveOntology(ontologyAfterForget, manSyntaxFormat, IRI.create(outputFile.toURI()));
			
		 System.out.println("outside");
		 return results;
	 }
	 public static OWLClass findClass(OWLOntology inputOntology,String name){
		 Set<OWLClass> classes = inputOntology.getClassesInSignature();
		for (OWLClass cls : classes) {
			String clsName = cls.getIRI().getShortForm().toLowerCase();
			if (clsName.equals(name)==true)
				return cls;
		}
		return null;
	 }
	 public static OWLObjectProperty findProperty(OWLOntology inputOntology,String name){
		 Set<OWLObjectProperty> properties = inputOntology.getObjectPropertiesInSignature();
		for (OWLObjectProperty prop : properties) {
			String propName = prop.getIRI().getShortForm().toLowerCase();
			if (propName.equals(name)==true)
				return prop;
		}
		return null;
	 }
	 public static List<String> forgetUsingFame(OWLOntologyManager manager,OWLOntology inputOntology,List<String> _symbols) throws OWLOntologyCreationException, CloneNotSupportedException, OWLOntologyStorageException{
		 System.out.println("Inside forgetUsingFame");
		 Fame fa = new Fame();
		 Set<OWLObjectProperty> properties = new HashSet<OWLObjectProperty>();
		 Set<OWLClass> classes = new HashSet<OWLClass>();
			// Add symbols
			
			for (int i = 0; i < _symbols.size(); i++) {
				String eName = _symbols.get(i);
				OWLClass cls = findClass(inputOntology,eName);
				if(cls!=null)
					classes.add(cls);
				else{
					OWLObjectProperty prop = findProperty(inputOntology,eName);
					if(prop!=null)
						properties.add(prop);
				}
			}
			OWLOntology modified = fa.FameRC(properties, classes, inputOntology);
			Set<OWLAxiom> axioms = modified.getAxioms();
			List<String> results = new ArrayList<String>();
			for (OWLAxiom a : axioms) {
				OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();
				String axValue = renderer.render(a);
				results.add(axValue);
			}
			
			// Save ontology in Manchester format
			
//			String rootPath = System.getProperty("catalina.home");
//			File dir = new File(rootPath + File.separator + "tmpFiles");
			
			String rootPath = System.getProperty("catalina.home");
			File dir = new File(rootPath +  File.separator + "webapps" + File.separator + "WebOntSummarisation" + File.separator + "ontologies");
			
			// Create the file on server
			File outputFile = new File(dir.getAbsolutePath()
					+ File.separator + "fame_modified.owl");
			if(outputFile.exists()==true){
				outputFile.delete();
				System.out.println("Deleted it.");
			}
			OWLOntologyFormat format = manager.getOntologyFormat(inputOntology);
			ManchesterOWLSyntaxOntologyFormat manSyntaxFormat = new ManchesterOWLSyntaxOntologyFormat();
			if (format.isPrefixOWLOntologyFormat()) {
				manSyntaxFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
				System.out.println("Inside");
			}
			IRI ontologyIRI = IRI.create(outputFile.toURI());
			OWLOntologyID newOntologyID = new OWLOntologyID(ontologyIRI);
			SetOntologyID setOntologyID = new SetOntologyID(modified, newOntologyID);
			manager.applyChange(setOntologyID);
			System.out.println("\nOntology IRI:" + modified.getOntologyID().toString());
			// manager.setOntologyDocumentIRI(interpolant, );
			manager.saveOntology(modified, manSyntaxFormat, IRI.create(outputFile.toURI()));
			
			// Save to Json format 
			//ConsoleMain.main(new String[]{"-file", outputFile.getAbsolutePath()});
			//System.out.println("Finished.");
			
			// Save ontology in Manchester format
//			File outputFile = new File("inputFiles/fame_modified.owl");
//			OWLOntologyFormat format = manager.getOntologyFormat(inputOntology);
//			ManchesterOWLSyntaxOntologyFormat manSyntaxFormat = new ManchesterOWLSyntaxOntologyFormat();
//			if (format.isPrefixOWLOntologyFormat()) {
//				manSyntaxFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
//				System.out.println("Inside");
//			}
//			IRI ontologyIRI = IRI.create(outputFile.toURI());
//			OWLOntologyID newOntologyID = new OWLOntologyID(ontologyIRI);
//			SetOntologyID setOntologyID = new SetOntologyID(modified, newOntologyID);
//			manager.applyChange(setOntologyID);
//			System.out.println("\nOntology IRI:" + modified.getOntologyID().toString());
//			// manager.setOntologyDocumentIRI(interpolant, );
//			manager.saveOntology(modified, manSyntaxFormat, IRI.create(outputFile.toURI()));
			
		 
			 System.out.println("outside");
			 return results;
	 }
	 public static List<String> modularise(OWLOntologyManager manager,OWLOntology inputOntology,List<String> _symbols) throws UnsupportedEncodingException, OWLOntologyCreationException, CloneNotSupportedException, OWLOntologyStorageException {
			
			OntologySegmenter seg = new SyntacticLocalityModuleExtractor(manager, inputOntology, ModuleType.TOP);// extractor.SubsetExtractor();
			Set<OWLEntity> symbols=new HashSet<OWLEntity>();
			for (int i = 0; i < _symbols.size(); i++) {
				String eName = _symbols.get(i);
				OWLEntity entity = findEntity(inputOntology,_symbols.get(i));
				if(entity!=null) {
					symbols.add(entity);
				}
			}
			
			// OutputFile Path
//			String rootPath = System.getProperty("catalina.home");
//			File dir = new File(rootPath + File.separator + "tmpFiles");
			
			String rootPath = System.getProperty("catalina.home");
			File dir = new File(rootPath +  File.separator + "webapps" + File.separator + "WebOntSummarisation" + File.separator + "ontologies");
			
			if (!dir.exists())
				dir.mkdirs();

			// Create the file on server
			File outputFile = new File(dir.getAbsolutePath()
					+ File.separator + "mod_modified.owl");
			if(outputFile.exists()==true){
				outputFile.delete();
				System.out.println("Deleted it.");
			}
			//File outputFile = new File("inputFiles/modified_Modularisation.owl");
			OWLOntology modifiedOntology = seg.extractAsOntology(symbols, IRI.create(outputFile.toURI()));
			// Display axioms
			Set<OWLAxiom> axioms = modifiedOntology.getAxioms();
			List<String> results = new ArrayList<String>();
			for (OWLAxiom a : axioms) {
				// System.out.println(a.getAnnotations().size());
				OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();
				String axValue = renderer.render(a);
				if(axValue.isEmpty()==false)
					results.add(axValue);
			}
			 System.out.println("outside");
			 
			// Save ontology in Manchester format
			OWLOntologyFormat format = manager.getOntologyFormat(inputOntology);
			ManchesterOWLSyntaxOntologyFormat manSyntaxFormat = new ManchesterOWLSyntaxOntologyFormat();
			if (format.isPrefixOWLOntologyFormat()) {
				manSyntaxFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
				System.out.println("Inside");
			}
			manager.saveOntology(modifiedOntology, manSyntaxFormat);//, IRI.create(outputFile.toURI()));
			manager.removeOntology(modifiedOntology);
			 return results;
			 
			// Save ontology in Manchester format
//			OWLOntologyFormat format = manager.getOntologyFormat(inputOntology);
//			ManchesterOWLSyntaxOntologyFormat manSyntaxFormat = new ManchesterOWLSyntaxOntologyFormat();
//			if (format.isPrefixOWLOntologyFormat()) {
//				manSyntaxFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
//				System.out.println("Inside");
//			}
//			manager.saveOntology(modifiedOntology, manSyntaxFormat, IRI.create(outputFile.toURI()));
		}
}
