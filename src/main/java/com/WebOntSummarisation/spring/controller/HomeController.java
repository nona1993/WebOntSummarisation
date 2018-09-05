package com.WebOntSummarisation.spring.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
//import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.WebOntSummarisation.spring.model.User;

@Controller
public class HomeController {
	private static final Log logger = LogFactory
			.getLog(HomeController.class);
	
	String uploadStatus="";
	OWLOntology inputOntology;
	OWLOntologyManager manager;
	Operation op = new Operation();
	List<String> allSymbols = Collections.emptyList();
	Set<OWLEntity> entities = Collections.EMPTY_SET; //.emptyList();
	ArrayList<String> axiomsList = new ArrayList<String>();
	List<String> interpolants = Collections.emptyList();
	List<String> fameResults = Collections.emptyList();
	List<String> modularisationResults = Collections.emptyList();
	
	boolean isFileUpload = false;
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		System.out.println("Home Page Requested, locale = " + locale);
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("uploadStatus", uploadStatus);
		
		model.addAttribute("serverTime", formattedDate);
		model.addAttribute("performOperation", op);
		model.addAttribute("allSymbols",allSymbols);
		model.addAttribute("entities", entities);
		model.addAttribute("axioms", axiomsList.iterator());
		model.addAttribute("interpolants", interpolants.iterator());
		model.addAttribute("fameResults", fameResults.iterator());
		model.addAttribute("modularisationResults", modularisationResults.iterator());
		System.out.println(axiomsList.size());
		return "home";
	}
	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public String update(Locale locale, Model model) {
		System.out.println("Update Page Requested, locale = " + locale);
		return "upload";
	}
	/**
	 * Upload single file using Spring Controller
	 */
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String uploadFileHandler(@RequestParam("name") String name,
			@RequestParam("file") MultipartFile file, Model model) {
		if(file.isEmpty() || name.isEmpty()) {
			uploadStatus = "Failed to upload - Provide a valid owl file and name";
			model.addAttribute("uploadStatus", uploadStatus);
			return "redirect:/";
		}
		if (!file.isEmpty()) {
			try {
				byte[] bytes = file.getBytes();

				// Creating the directory to store file
				String rootPath = System.getProperty("catalina.home");
				File dir = new File(rootPath + File.separator + "tmpFiles");
				if (!dir.exists())
					dir.mkdirs();

				// Create the file on server
				File serverFile = new File(dir.getAbsolutePath()
						+ File.separator + name);
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();

				logger.info("Server File Location=" + serverFile.getAbsolutePath());
				
				// ** Signature **
				 manager=OWLManager.createOWLOntologyManager();
				 Set<OWLEntity> symbols=new HashSet<OWLEntity>();
				 inputOntology = manager.loadOntologyFromOntologyDocument(serverFile);
				 entities = inputOntology.getSignature();
				// model.addAttribute("entities", entities);
				 
				 //String [] allSymbols = new String[entities.size()];
				 allSymbols=new ArrayList<String>();
				 int i=0;
				 for (OWLEntity entity : entities) {
					// allSymbols[i++]=entity.getIRI().getShortForm().toLowerCase();
					 allSymbols.add(entity.getIRI().getShortForm().toLowerCase());
				 }
				 model.addAttribute("performOperation", op);
				 model.addAttribute("allSymbols", allSymbols);
				 //----------------------------------------
				 
				 // ** Axioms **
				 Set<OWLAxiom> axioms = inputOntology.getAxioms();
				 axiomsList.clear();
					for (OWLAxiom a : axioms) {
						OWLObjectRenderer renderer = new uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer();
						String axValue = renderer.render(a);
						if(axValue.equals("")==false)
							axiomsList.add(axValue);
					}
				model.addAttribute("axioms", axiomsList.iterator());
				//-------------------------------------------------------
				uploadStatus = "Active ontology: " + name;
				model.addAttribute("uploadStatus", uploadStatus);
				return "home";
			} catch (Exception e) {
				uploadStatus = "You failed to upload " + name + " => " + e.getMessage();
				model.addAttribute("uploadStatus", uploadStatus);
				return "home";
			}
		} else {
			uploadStatus = "You failed to upload " + name + " because the file was empty.";
			model.addAttribute("uploadStatus", uploadStatus);
			return "redirect:/";
		}
	}
	@RequestMapping(value = "/downloadLethe", method = RequestMethod.GET, produces = "application/owl")
    public @ResponseBody void downloadLethe(HttpServletResponse response) throws IOException {
    	String FILE_PATH = "lethe_modified.owl";
        String APPLICATION_OWL = "application/owl";
        
        File file = getFile(FILE_PATH);
        InputStream in = new FileInputStream(file);

        response.setContentType(APPLICATION_OWL);
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
        response.setHeader("Content-Length", String.valueOf(file.length()));
        FileCopyUtils.copy(in, response.getOutputStream());
    }
	@RequestMapping(value = "/downloadFame", method = RequestMethod.GET, produces = "application/owl")
    public @ResponseBody void downloadFame(HttpServletResponse response) throws IOException {
    	String FILE_PATH = "fame_modified.owl";
        String APPLICATION_OWL = "application/owl";
        
        File file = getFile(FILE_PATH);
        InputStream in = new FileInputStream(file);

        response.setContentType(APPLICATION_OWL);
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
        response.setHeader("Content-Length", String.valueOf(file.length()));
        FileCopyUtils.copy(in, response.getOutputStream());
    }
	@RequestMapping(value = "/downloadMod", method = RequestMethod.GET, produces = "application/owl")
    public @ResponseBody void downloadMod(HttpServletResponse response) throws IOException {
    	String FILE_PATH = "mod_modified.owl";
        String APPLICATION_OWL = "application/owl";
        
        File file = getFile(FILE_PATH);
        InputStream in = new FileInputStream(file);

        response.setContentType(APPLICATION_OWL);
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
        response.setHeader("Content-Length", String.valueOf(file.length()));
        FileCopyUtils.copy(in, response.getOutputStream());
    }
    private File getFile(String _fileName) throws FileNotFoundException {
    	String rootPath = System.getProperty("catalina.home");
    	File dir = new File(rootPath +  File.separator + "webapps" + File.separator + "WebOntSummarisation" + File.separator + "ontologies");
		//File dir = new File(rootPath + File.separator + "tmpFiles");
		
		// Create the file on server
		File outputFile = new File(dir.getAbsolutePath()
				+ File.separator + _fileName);
		
        if (!outputFile.exists()){
            throw new FileNotFoundException("file with path: " + _fileName + " was not found.");
        }
        return outputFile;
    }
	@RequestMapping(value = "/execute", method = RequestMethod.POST)
	public String performOperation(@ModelAttribute("performOperation") Operation _op, BindingResult result, Model model) throws OWLOntologyCreationException, CloneNotSupportedException, UnsupportedEncodingException, OWLOntologyStorageException {
		// modelMap.addAttribute("error", result.getAllErrors());
		op.setSymbols(_op.getSymbols());
		//op.setApproximateFixPoint(_op.getApproximateFixPoint());
		//op.setDis(_op.isDis());
		op.setForgetting(_op.getForgetting());
		//op.setRepresentation(_op.getRepresentation());
		op.setMethod(_op.getMethod());
		
		if(op.getForgetting().toLowerCase().equals("interpolation")) {
			if(op.getMethod().toLowerCase().equals("alc")) {
				interpolants = UtilityHelper.computeALCABoxInterpolant(manager,inputOntology, op.getSymbols());
				//interpolants = UtilityHelper.forgetUsingFame(inputOntology, op.getSymbols());
				model.addAttribute("interpolants", interpolants.iterator());
			}else {// if(op.getMethod().toLowerCase().equals("alch")) {
				interpolants = UtilityHelper.computeALCHTBoxInterpolant(manager,inputOntology, op.getSymbols());
				model.addAttribute("interpolants", interpolants.iterator());
			}
		}else {
			if(op.getMethod().toLowerCase().equals("alc")) {
				interpolants = UtilityHelper.forgetUsingALCABox(manager,inputOntology, op.getSymbols());
				model.addAttribute("interpolants", interpolants.iterator());
			}else {//if(op.getMethod().toLowerCase().equals("alch")) {
				interpolants = UtilityHelper.forgetUsingALCHTBox(manager,inputOntology, op.getSymbols());
				model.addAttribute("interpolants", interpolants.iterator());
			}
		}
			
		fameResults = UtilityHelper.forgetUsingFame(manager,inputOntology, op.getSymbols());
		System.out.println("Fame Results:" + fameResults.size());
		if(fameResults.isEmpty()==false) {
			System.out.println(fameResults.get(0));
		}
		modularisationResults = UtilityHelper.modularise(manager,inputOntology, op.getSymbols());
		
		model.addAttribute("fameResults", fameResults.iterator());
		model.addAttribute("modularisationResults", modularisationResults.iterator());
		
		//System.out.println(op.getSymbols().size());
		//System.out.println(op.getForgetting());
		//System.out.println(op.getRepresentation());
		return "redirect:/";
	}
}
