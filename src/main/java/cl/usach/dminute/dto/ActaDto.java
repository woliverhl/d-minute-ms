package cl.usach.dminute.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ActaDto {
	
	private long actaId;
	private Date fecha;
	private String resumen;
	private long correlativo;	
	private String estado;
	private long proyectoId;
	private List<UsuarioActaDto> usuarioActa;
	public List<TemaDto> temaActa;
	
}
