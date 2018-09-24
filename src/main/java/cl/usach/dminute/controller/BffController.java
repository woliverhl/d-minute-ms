package cl.usach.dminute.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.usach.dminute.dto.ActaDto;
import cl.usach.dminute.dto.EntradaLista;
import cl.usach.dminute.dto.ListarActaDialogica;
import cl.usach.dminute.dto.ProyectoDto;
import cl.usach.dminute.dto.ProyectoUsuariosDto;
import cl.usach.dminute.dto.UsuarioActaDto;
import cl.usach.dminute.entity.Usuario;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/bff")
public class BffController {

	@Autowired
	@Qualifier("actaController")
	private ActaController actaController;
	
	@Autowired
	@Qualifier("proyectoController")
	private ProyectoController proyectoController;
	
	@Autowired
	@Qualifier("usuarioController")
	private UsuarioController usuarioController;
	
	@GetMapping(value = "/listarMinutaProyecto/{proyectoid}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ListarActaDialogica listarMinutaProyectoById(@PathVariable(value = "proyectoid") Long proyectoid, HttpServletRequest request) {
		
		if(log.isInfoEnabled()) {
			log.info("BffController.listarMinutaProyectoById.INIT");
			log.info("BffController.listarMinutaProyectoById.ProyectoId:" + proyectoid.toString());
		}
		
		ListarActaDialogica retorno = new ListarActaDialogica();
		
		ProyectoDto proyectoDto = proyectoController.proyectoById(proyectoid, request);
		
		if (proyectoDto != null){
			retorno.setProyectoDto(proyectoDto);
			
			retorno.setListaActa(actaController.listarActaProyecto(proyectoid));
			
			if (retorno.getListaActa().size()>0) {
				ActaDto actaDto = actaController.getActa(retorno.getListaActa().get(0).getActaId());
				List<ProyectoUsuariosDto> listaRetorno = new ArrayList<ProyectoUsuariosDto>();
				for (ProyectoUsuariosDto usuario : retorno.getProyectoDto().getUsuariosNuevoProyecto()) {
	    			boolean bandera = true;
					for (UsuarioActaDto usuarioActa : actaDto.getUsuarioActa()) {
						if (usuario.getUsername().equalsIgnoreCase(usuarioActa.getUsername())) {
							bandera = false;
							break;
						}
					}
					if (bandera){
						listaRetorno.add(usuario);
					}
				}
				retorno.getProyectoDto().setUsuariosNuevoProyecto(listaRetorno);
				retorno.setActaDto(actaDto);
			}
		}
			
		if(log.isInfoEnabled()) {
			log.info("BffController.listarMinutaProyectoById.retorno:" + retorno.toString());
			log.info("BffController.listarMinutaProyectoById.FIN");
		}
			
		return retorno;
	}
	
    @PostMapping(value="/listaUsuarioFiltro", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Usuario> listUser(@RequestBody EntradaLista entradaLista, HttpServletRequest request){
    	if(log.isInfoEnabled()) {
			log.info("UsuarioController.listUser.INIT");
		}
    	List<Usuario> listaRespuesta = usuarioController.listUser();
    	List<Usuario> listaRetorno = new ArrayList<Usuario>();
    	if (entradaLista.getTipo().equalsIgnoreCase("PROY")){
    		ProyectoDto proyectoDto = proyectoController.proyectoById(entradaLista.getNumero(), request);
    		for (Usuario usuario : listaRespuesta) {
    			boolean bandera = true;
				for (ProyectoUsuariosDto usuarioPry : proyectoDto.getUsuariosNuevoProyecto()) {
					if (usuario.getUsername().equalsIgnoreCase(usuarioPry.getUsername())) {
						bandera = false;
						break;
					}
				}
				if (bandera){
					listaRetorno.add(usuario);
				}
			}
    	}	
    	else{
    		listaRetorno = listaRespuesta; 
    	}
    		
    	return listaRetorno;
    }
	
}