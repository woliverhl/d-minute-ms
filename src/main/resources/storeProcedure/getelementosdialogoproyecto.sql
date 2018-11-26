USE `heroku_97b21c584329283`;
DROP procedure IF EXISTS `getelementosdialogoproyecto`;

DELIMITER $$
USE `heroku_97b21c584329283`$$
CREATE PROCEDURE `getelementosdialogoproyecto`(IN _proyectoid bigint(20))
BEGIN
	
		if exists(select * from proyecto where proyecto_id =  _proyectoid) then 
        
        	 SELECT el.id,
				el.descripcion,
				el.estado,
				el.fecha_compromiso,
				el.elemento_dialogo_id,
				el.tema_id,
				el.tipo_elemento_dialogo_cod_rol,
				el.usuario_username,
                el.titulo
			FROM elemento_dialogo el
            WHERE el.tema_id in (select id from tema inner join acta on tema.acta_acta_id = acta.acta_id where acta.proyecto_proyecto_id =  _proyectoid)
            AND el.estado <> "DELE"
            ORDER BY el.fecha_compromiso;

        end if;
        
END$$

DELIMITER ;