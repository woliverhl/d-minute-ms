package cl.usach.dminute.entity;

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
@Table(name = "usuarioActa")
public class UsuarioActa {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	@Column(name = "asiste")
	private Boolean asiste;
	@Column(name = "secretario")
	private Boolean secretario;
	@ManyToOne
	private Usuario usuario;
	@ManyToOne
	private Acta acta;
}
