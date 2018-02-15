package cl.usach.dminute.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
@Entity
@Table(name = "acta")
public class Acta {

	@Id
	@GeneratedValue
	@Column(name = "actaId")
	private long actaId;
	@Column(name = "fecha")
	private Date fecha;
	@Column(name = "resumen")
	private String resumen;
	@Column(name = "correlativo")
	private long correlativo;	
	@ManyToOne
	private Proyecto proyecto;
	
}
