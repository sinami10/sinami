package org.sid.web;

import org.sid.entities.Compte;
import org.sid.entities.Operation;
import org.sid.metier.IBanque;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BanqueController {
	
	@Autowired
	private IBanque iBanque;
	
	@RequestMapping("/operations")
	public String index() {	
		return "comptes";
	}
	
	@RequestMapping("/consulterCompte")
	public String consulter(Model model,String codeCompte,@RequestParam(name = "page",defaultValue = "0")int page,@RequestParam(name = "size",defaultValue =  "4 ")int size) {
		model.addAttribute("codeCompte",codeCompte);
		try {
			Compte cp = iBanque.consulterCompte(codeCompte);
			Page<Operation> pageOperations = iBanque.listOperationsCompte(codeCompte, page, size);
			model.addAttribute("listOperation", pageOperations);
			int [] pages = new int[pageOperations.getTotalPages()];
			model.addAttribute("pages", pages);
			model.addAttribute("compte", cp);
		} catch (Exception e) {
		model.addAttribute("exception", e);
		}		
		return "comptes";
	}
	
	@RequestMapping(value = "/saveOperation", method = RequestMethod.POST)
	public String saveOperation(Model model,String typeOperation,double montant,String codeCompte,String codeCompte2) {
		
		try {
			if(typeOperation.equals("VERS")) {		
			iBanque.verser(codeCompte, montant);
			}else if(typeOperation.equals("RETR")) {		
			iBanque.retirer(codeCompte, montant);
			}if(typeOperation.equals("VIR")) {		
			iBanque.virement(codeCompte, codeCompte2, montant);;
			}
		} catch (Exception e) {
		    model.addAttribute("error", e);
		    return "redirect:/consulterCompte?codeCompte="+codeCompte+"&error="+e.getMessage();  
		}
			
		return "redirect:/consulterCompte?codeCompte="+codeCompte;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


}
