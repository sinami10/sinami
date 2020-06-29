package org.sid.metier;

import java.util.Date;

import org.sid.dao.CompteRepository;
import org.sid.dao.OperationRepository;
import org.sid.entities.Compte;
import org.sid.entities.CompteCourant;
import org.sid.entities.Operation;
import org.sid.entities.Retrait;
import org.sid.entities.Versement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional
public class BanqueMetierImpl implements IBanque{
   @Autowired
	private CompteRepository compteRepository;
   @Autowired
   private OperationRepository operationRepository;
	@Override
	public Compte consulterCompte(String codeCpte) {
		Compte cp = compteRepository.findById(codeCpte).orElse(null);
		if(cp==null) throw new RuntimeException("Compte Introuvable");  // c'est une exception non surveiller
		return cp;
	}

	@Override
	public void verser(String codeCpte, double montant) {
		Compte cp = consulterCompte(codeCpte);
		Versement v = new Versement(new Date(),montant,cp);
		operationRepository.save(v);
		cp.setSolde(cp.getSolde()+montant);
		compteRepository.save(cp);
		
		
	}

	@Override
	public void retirer(String codeCpte, double montant) {
		Compte cp = consulterCompte(codeCpte);
		double facilitesCaisse = 0;
		if (cp instanceof CompteCourant) {
		    facilitesCaisse = ((CompteCourant) cp).getDecouvert();
				 if ( cp.getSolde()+facilitesCaisse < montant ) 
					 throw new RuntimeException("Solde insuffisant");}
		Retrait r = new Retrait(new Date(),montant,cp);
		operationRepository.save(r);
		cp.setSolde(cp.getSolde()-montant);
		compteRepository.save(cp);
		
	}

	@Override
	public void virement(String codeCpte1, String codeCpte2, double montant) {
		if(codeCpte1.equals(codeCpte2)) throw new RuntimeException("Impossible Virement Sur Le Meme Compte");
		retirer(codeCpte1, montant);
		retirer(codeCpte2, montant);		
	}

	@Override
	public Page<Operation> listOperationsCompte(String codeCompte, int page, int size) {
		// TODO Auto-generated method stub
		return operationRepository.listOperation(codeCompte,PageRequest.of(page, size));
	}


}
