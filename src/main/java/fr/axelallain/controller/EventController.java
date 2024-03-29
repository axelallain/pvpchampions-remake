package fr.axelallain.controller;

import fr.axelallain.entity.Utilisateur;
import fr.axelallain.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import fr.axelallain.UserPrincipal;
import fr.axelallain.entity.Commentaire;
import fr.axelallain.entity.Event;
import fr.axelallain.service.CommentaireService;
import fr.axelallain.service.EventService;

import java.util.ArrayList;

@Controller
public class EventController {
	
	@Autowired
	private EventService eventService;
	
	@Autowired
	private CommentaireService commentaireService;

	@Autowired
	private UtilisateurService utilisateurService;
	
	@GetMapping("/ajouter")
	public String ajouterForm(Model model) {
		model.addAttribute("event", new Event());
		
		UserPrincipal cuser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long cuserid = cuser.getId();
		model.addAttribute("cuserid", cuserid);
		
		return "ajouter";
	}
	
	@PostMapping("/ajouter")
	public String ajouterSubmit(Event event) {
		UserPrincipal cuser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long cuserid = cuser.getId();

		eventService.ajouter(event);
		
		return "redirect:/profil/events/" + cuserid;
	}
	
	@GetMapping("/event/{id}")
	public String ficheEvent(@PathVariable Long id, Model model) {
		model.addAttribute("event", eventService.findEventById(id));		
		model.addAttribute("commentaire", new Commentaire());
		
		UserPrincipal cuser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long cuserid = cuser.getId();
		model.addAttribute("cuserid", cuserid);
		
		model.addAttribute("commentaires", commentaireService.findAllCommentairesByEventId(id));

		Event event = eventService.findEventById(id);
		model.addAttribute("participants", event.getUtilisateurs());
		
		return "event";
	}

	@PostMapping("/event/{id}/participer")
	public String participer(@PathVariable Long id, Model model) {
		UserPrincipal cuser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long cuserid = cuser.getId();

		Event event = eventService.findEventById(id);

		ArrayList<Utilisateur> participants = new ArrayList<>(event.getUtilisateurs());
		if (participants.size() != 0) {

			if (participants.contains(utilisateurService.findById(cuserid))) {
				System.out.println("Cet utilisateur participe déjà.");
			} else if(event.getUtilisateurs().size() >= 5) {
				System.out.println("Ce groupe est complet.");
			} else {
				event.getUtilisateurs().add(utilisateurService.findById(cuserid));
				eventService.ajouter(event);
			}

		} else {
			event.getUtilisateurs().add(utilisateurService.findById(cuserid));
			eventService.ajouter(event);
		}

		return "redirect:/event/" + id;
	}
	
}