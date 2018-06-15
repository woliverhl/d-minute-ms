package cl.usach.dminute.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import cl.usach.dminute.dto.Constants;
import cl.usach.dminute.dto.TemaDto;
import cl.usach.dminute.entity.Acta;
import cl.usach.dminute.entity.Tema;
import cl.usach.dminute.exception.ValidacionesException;
import cl.usach.dminute.repository.ActaJpa;
import cl.usach.dminute.repository.TemaJpa;
import cl.usach.dminute.service.TemaService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("temaService")
public class TemaImpl implements TemaService {
	
	@Autowired
	@Qualifier("temaJpa")
	private TemaJpa temaJpa;
	
	@Autowired
	@Qualifier("actaJpa")
	private ActaJpa actaJpa;
	
	@Override
	public TemaDto guardarModificar(TemaDto guardar) {
		if (log.isInfoEnabled()) {
			log.info("TemaImpl.guardarModificar.INIT");
			log.info("TemaImpl.guardarModificar.tema: " + guardar.toString());
		}
		try {
			Acta acta = actaJpa.findByActaId(guardar.getActaId());
			if (acta != null) {
				Tema nuevo = new Tema();
				nuevo.setId(guardar.getId());
				nuevo.setActa(acta);
				nuevo.setDiscusion(guardar.getDiscusion().toUpperCase());
				nuevo.setNombre(guardar.getNombre().toUpperCase());
				nuevo = temaJpa.save(nuevo);
				guardar.setId(nuevo.getId());
			}else {
				throw new Exception();
			}			
		} catch (Exception ex) {
			if (log.isErrorEnabled()) {
				log.info("TemaImpl.guardarModificar.ERROR - " + ex.getMessage());
			}
			throw new ValidacionesException(Constants.ERROR_TECNICO_GENERICO_COD, Constants.ERROR_TEMA_ERROR, null);
		}
		if (log.isInfoEnabled()) {
			log.info("TemaImpl.guardarModificar.FIN");
		}	
		return guardar;
	}

	@Override
	public void eliminar(TemaDto guardar) {
		if (log.isInfoEnabled()) {
			log.info("TemaImpl.eliminar.INIT");
			log.info("TemaImpl.eliminar.tema: " + guardar.toString());
		}
		try {
			Acta acta = actaJpa.findByActaId(guardar.getActaId());
			if (acta != null) {
				Tema delete = temaJpa.findOne(guardar.getId());
				if (delete != null)
					temaJpa.delete(delete);
				else
					throw new Exception();
			}else
				throw new Exception();									
		} catch (Exception ex) {
			if (log.isErrorEnabled()) {
				log.info("TemaImpl.eliminar.ERROR - " + ex.getMessage());
			}
			throw new ValidacionesException(Constants.ERROR_TECNICO_GENERICO_COD, Constants.ERROR_TEMA_DEL_ERROR, null);
		}
		if (log.isInfoEnabled()) {
			log.info("TemaImpl.eliminar.FIN");
		}		
	}

	@Override
	public List<TemaDto> listarTemaActa(long actaId) {
		if (log.isInfoEnabled()) {
			log.info("TemaImpl.listarTemaActa.INIT");
			log.info("TemaImpl.listarTemaActa.actaId: " + actaId);
		}
		List<TemaDto> lista = new ArrayList<TemaDto>();
		try {
			Acta acta = actaJpa.findByActaId(actaId);
			if (acta == null) {
				throw new Exception();
			}
			List<Tema> retorno = temaJpa.findByActaActaId(actaId);
			for (Tema tema : retorno) {
				TemaDto temaDto = new TemaDto();
				temaDto.setActaId(tema.getActa().getActaId());
				temaDto.setDiscusion(tema.getDiscusion());
				temaDto.setId(tema.getId());
				temaDto.setNombre(tema.getNombre());
				lista.add(temaDto);
			}						
		} catch (Exception ex) {
			if (log.isErrorEnabled()) {
				log.info("TemaImpl.listarTemaActa.ERROR - " + ex.getMessage());
			}
			throw new ValidacionesException(Constants.ERROR_TECNICO_GENERICO_COD, Constants.ERROR_TECNICO_MENSAJE, null);
		}
		if (log.isInfoEnabled()) {
			log.info("TemaImpl.listarTemaActa.lista: " + lista.toString());
			log.info("TemaImpl.listarTemaActa.FIN");
		}		
		return lista;
	}

}