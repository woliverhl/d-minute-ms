package cl.usach.dminute.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import cl.usach.dminute.entity.*;
import cl.usach.dminute.service.*;
import cl.usach.dminute.util.Utilitario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import cl.usach.dminute.dto.ActaDto;
import cl.usach.dminute.dto.Constants;
import cl.usach.dminute.dto.UsuarioActaDto;
import cl.usach.dminute.exception.ValidacionesException;
import cl.usach.dminute.repository.ActaJpa;
import cl.usach.dminute.repository.CallStoreProcedureImpl;
import cl.usach.dminute.repository.ProyectoJpa;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("actaService")
public class ActaImpl implements ActaService {

	@Autowired
	@Qualifier("actaJpa")
	private ActaJpa actaJpa;

	@Autowired
	@Qualifier("proyectoJpa")
	private ProyectoJpa proyectoJpa;

	@Autowired
	@Qualifier("callStoreProcedureImpl")
	private CallStoreProcedureImpl callStoreProcedureImpl;

	@Autowired
	@Qualifier("usuarioActaService")
	private UsuarioActaService usuarioActaService;

	@Autowired
	@Qualifier("usuarioService")
	private UsuarioService usuarioService;

	@Autowired
	@Qualifier("temaService")
	private TemaService temaService;

	@Autowired
	@Qualifier("elementoDialogoService")
	private ElementoDialogoService elementoDialogoService;

	@Override
	public Acta guardarModificar(ActaDto guardar, String userName) {
		if (log.isInfoEnabled()) {
			log.info("ActaImpl.guardarModificarActa.INIT");
			log.info("ActaImpl.guardarModificarActa.acta: " + guardar.toString());
		}
		validarEdicionActa(guardar.getActaId(), userName);
		if (log.isInfoEnabled()) {
			log.info("ActaImpl.guardarModificarActa.usuarioOK: " + userName.toString());
		}
		Acta acta = null;
		List<UsuarioActaDto> listaUsuario = guardar.getUsuarioActa();
		try {
			if (proyectoJpa.findByProyectoId(guardar.getProyectoId()) != null) {
				acta = new Acta();
				acta.setActaId(guardar.getActaId());
				acta.setEstado(Constants.estadoActivo);
				acta.setFecha(Utilitario.formatoFecha(guardar.getFecha()));
				acta.setHoraIncio(guardar.getHoraInicio());
				acta.setHoraFin(guardar.getHoraFin());
				Proyecto _pry = new Proyecto();
				_pry.setProyectoId(guardar.getProyectoId());
				acta.setProyecto(_pry);
				acta.setResumen(guardar.getResumen().toUpperCase());
				Usuario lider = new Usuario();
				lider.setUsername(guardar.getUsername());
				acta.setUsuario(lider);
				if (acta.getActaId() == 0){
					acta.setCorrelativo(countActasProyecto(guardar.getProyectoId()));
				}else{
					acta.setCorrelativo(guardar.getCorrelativo());
				}	
				acta = actaJpa.save(acta);
			}
			if (acta == null)
				throw new Exception();
			else {
				if (log.isInfoEnabled()) {
					log.info("ActaImpl.guardarModificarActa.acta: " + acta.toString());
					log.info("ActaImpl.guardarModificarActa.eliminarUsuario");
				}
				usuarioActaService.delete(acta.getActaId());
				if (log.isInfoEnabled()) {
					log.info("ActaImpl.guardarModificarActa.recorriendoUsuarios");
				}
				for (UsuarioActaDto usuarioActaDto : listaUsuario) {
					if (log.isInfoEnabled()) {
						log.info("ActaImpl.guardarModificarActa.grabandoUsuario.INI");
					}
					Usuario validacion = null;
					try {
						validacion = usuarioService.findOne(usuarioActaDto.getUsername());
					} catch (Exception ex) {
						if (log.isErrorEnabled()) {
							log.info("ActaImpl.guardarModificarActa.grabandoUsuario.usuarioInvalido: "
									+ usuarioActaDto.getUsername());
						}
					}
					if (validacion != null) {
						UsuarioActa usuarioActa = new UsuarioActa();
						usuarioActa.setActa(acta);
						usuarioActa.setAsiste(usuarioActaDto.getAsiste());
						usuarioActa.setSecretario(usuarioActaDto.getSecretario());
						usuarioActa.setUsuario(validacion);
						if (log.isInfoEnabled()) {
							log.info("ActaImpl.guardarModificarActa.grabandoUsuario: " + usuarioActa);
						}
						usuarioActaService.save(usuarioActa);
					}
					if (log.isInfoEnabled()) {
						log.info("ActaImpl.guardarModificarActa.grabandoUsuario.FIN");
					}
				}
			}
		} catch (Exception ex) {
			if (log.isErrorEnabled()) {
				log.info("ActaImpl.guardarModificarActa.ERROR - " + ex.getMessage());
			}
			throw new ValidacionesException(Constants.ERROR_TECNICO_GENERICO_COD, Constants.ERROR_ACTA_ERROR, null);
		}
		if (log.isInfoEnabled()) {
			log.info("ActaImpl.guardarModificarActa.FIN");
		}
		return acta;
	}

	@Override
	public void eliminar(ActaDto guardar, String userName) {
		if (log.isInfoEnabled()) {
			log.info("ActaImpl.eliminarActa.INIT");
			log.info("ActaImpl.eliminarActa.acta: " + guardar.toString());
		}
		validarEdicionActa(guardar.getActaId(), userName);
		if (log.isInfoEnabled()) {
			log.info("ActaImpl.eliminarActa.usuarioOK: " + userName.toString());
		}
		Acta acta = null;
		try {
			if (proyectoJpa.findByProyectoId(guardar.getProyectoId()) != null) {
				acta = new Acta();
				acta.setActaId(guardar.getActaId());
				acta.setCorrelativo(guardar.getCorrelativo());
				acta.setEstado(Constants.estadoBloqueado);
				acta.setFecha(Utilitario.formatoFecha(guardar.getFecha()));
				Proyecto _pry = new Proyecto();
				_pry.setProyectoId(guardar.getProyectoId());
				acta.setProyecto(_pry);
				acta.setResumen(guardar.getResumen());
				acta.setHoraIncio(guardar.getHoraInicio());
				acta.setHoraFin(guardar.getHoraFin());
				Usuario lider = new Usuario();
				lider.setUsername(guardar.getUsername());
				acta.setUsuario(lider);
				acta = actaJpa.save(acta);
			}
			if (acta == null)
				throw new Exception();
		} catch (Exception ex) {
			if (log.isErrorEnabled()) {
				log.info("ActaImpl.eliminarActa.ERROR - " + ex.getMessage());
			}
			throw new ValidacionesException(Constants.ERROR_TECNICO_GENERICO_COD, Constants.ERROR_ACTA_ERROR, null);
		}
		if (log.isInfoEnabled()) {
			log.info("ActaImpl.eliminarActa.FIN");
		}
	}

	@Override
	public List<ActaDto> listarActaProyecto(long proyectoId) {
		if (log.isInfoEnabled()) {
			log.info("ActaImpl.listarActaProyecto.INIT");
			log.info("ActaImpl.listarActaProyecto.Proyecto: " + proyectoId);
		}
		List<ActaDto> listarActa = new ArrayList<ActaDto>();
		try {
			if (proyectoJpa.findByProyectoId(proyectoId) != null) {
				listarActa = callStoreProcedureImpl.buscarActasProyecto(proyectoId);
			} else {
				throw new ValidacionesException(Constants.ERROR_TECNICO_GENERICO_COD, Constants.ERROR_PROYECTO_NOEXISTE,
						null);
			}
		} catch (Exception ex) {
			if (log.isErrorEnabled()) {
				log.info("ActaImpl.listarActaProyecto.ERROR - " + ex.getMessage());
			}
			throw ex;
		}
		if (log.isInfoEnabled()) {
			log.info("ActaImpl.listarActaProyecto.FIN");
		}
		return listarActa;
	}

	@Override
	public ActaDto getActa(long actaId) {
		if (log.isInfoEnabled()) {
			log.info("ActaImpl.getActa.INIT");
			log.info("ActaImpl.getActa.NumeroActa: " + actaId);
		}
		ActaDto actaDto = new ActaDto();
		try {
			Acta acta = actaJpa.findByActaId(actaId);
			if (acta == null)
				throw new ValidacionesException(Constants.ERROR_TECNICO_GENERICO_COD, Constants.ERROR_ACTA_NOEXISTE,
						null);
			if (log.isInfoEnabled()) {
				log.info("ActaImpl.getActa.acta: " + acta.toString());
			}
			actaDto.setActaId(acta.getActaId());
			actaDto.setCorrelativo(acta.getCorrelativo());
			actaDto.setEstado(acta.getEstado());
			actaDto.setFecha(Utilitario.formatoFecha(acta.getFecha()));
			actaDto.setProyectoId(acta.getProyecto().getProyectoId());
			actaDto.setResumen(acta.getResumen());
			actaDto.setHoraInicio(acta.getHoraIncio());
			actaDto.setHoraFin(acta.getHoraFin());
			actaDto.setUsername(acta.getUsuario().getUsername());
			List<UsuarioActaDto> listaUsuarioActaResponse = callStoreProcedureImpl
					.buscarUsuarioActaProyectoAll(acta.getProyecto().getProyectoId());
			List<UsuarioActaDto> validacion = listaUsuarioActaResponse.stream()
					.filter(a -> Objects.equals(a.getActaId(), actaDto.getActaId())).collect(Collectors.toList());
			actaDto.setTareaPendiente(elementoDialogoService.getListaAllElementoDialogoActaPendientes(acta.getProyecto().getProyectoId(), acta.getActaId()));

			actaDto.setUsuarioActa(validacion);
			actaDto.setTemaActa(temaService.listarTemaActa(actaId));
			if (log.isInfoEnabled()) {
				log.info("ActaImpl.getActa.retorno:" + actaDto);
			}
		} catch (Exception ex) {
			if (log.isErrorEnabled()) {
				log.info("ActaImpl.getActa.ERROR - " + ex.getMessage());
			}
			throw ex;
		}
		if (log.isInfoEnabled()) {
			log.info("ActaImpl.getActa.FIN");
		}
		return actaDto;
	}
	
	@Override
	public void validarEdicionActa(long actaId, String userName) {
		if (actaId > 0)
			if (!callStoreProcedureImpl.validaPermiso(actaId, userName, Constants.ACT))
				throw new ValidacionesException(Constants.ERROR_PERMISO_GENERICO_COD, Constants.ERROR_PERMISO_ERROR,
						null);
	}

	public ActaDto getActaId(long actaId) {
		if (log.isInfoEnabled()) {
			log.info("ActaImpl.getActa.INIT");
			log.info("ActaImpl.getActa.NumeroActa: " + actaId);
		}
		ActaDto actaDto = new ActaDto();
		try {
			Acta acta = actaJpa.findByActaId(actaId);
			if (acta == null)
				throw new ValidacionesException(Constants.ERROR_TECNICO_GENERICO_COD, Constants.ERROR_ACTA_NOEXISTE,
						null);
			if (log.isInfoEnabled()) {
				log.info("ActaImpl.getActa.acta: " + acta.toString());
			}
			actaDto.setActaId(acta.getActaId());
			actaDto.setCorrelativo(acta.getCorrelativo());
			actaDto.setEstado(acta.getEstado());
			actaDto.setFecha(Utilitario.formatoFecha(acta.getFecha()));
			actaDto.setProyectoId(acta.getProyecto().getProyectoId());
			actaDto.setResumen(acta.getResumen());
			actaDto.setHoraInicio(acta.getHoraIncio());
			actaDto.setHoraFin(acta.getHoraFin());
			actaDto.setUsername(acta.getUsuario().getUsername());
			if (log.isInfoEnabled()) {
				log.info("ActaImpl.getActa.retorno:" + actaDto);
			}
		} catch (Exception ex) {
			if (log.isErrorEnabled()) {
				log.info("ActaImpl.getActa.ERROR - " + ex.getMessage());
			}
			throw ex;
		}
		if (log.isInfoEnabled()) {
			log.info("ActaImpl.getActa.FIN");
		}
		return actaDto;
	}

	private long countActasProyecto(long proyectoId) {
		long numero = callStoreProcedureImpl.contarActasProyecto(proyectoId);
		if (numero == 0)
			return 1;
		else
			return numero + 1;
	}

}
