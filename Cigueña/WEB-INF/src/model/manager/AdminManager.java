package model.manager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.swing.plaf.synth.SynthSpinnerUI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitterReturnValueHandler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.org.apache.bcel.internal.generic.RETURN;

import jdk.nashorn.internal.parser.JSONParser;
import jdk.nashorn.internal.runtime.ECMAException;

//@Service indica que la clase es un bean de la capa de negocio
@Service
public class AdminManager {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);

	}
	// listar los menus de la base de datos para los clientes
	public List<Map<String, Object>> ListaDeMenusCliente() {
		String xsql = "Select m.* from personal p , usuarios u , rol r , menus m, rolmen rm where u.login='cliente' and u.codp=p.codp and u.codr=r.codr and r.codr=rm.codr and m.codm=rm.codm;";
		return this.jdbcTemplate.queryForList(xsql, new Object[] { });
	}
	

	// Verificacion del logeo de usuarios
	public int VerificaUsuarios(String xusuario, String xpassword) {
		String xsql = "SELECT count(*) FROM usuarios where (login=?) and (password=?);";
		return this.jdbcTemplate.queryForObject(xsql, new Object[] { xusuario, xpassword }, Integer.class);
	}

	// Verificacion sies un cliente el usuario
	public int VerificaCliente(String xusuario, String xpassword) {
		String xsql = "SELECT count(*) FROM usuarios where (login=?) and (password=?) and codr=3000;";
		return this.jdbcTemplate.queryForObject(xsql, new Object[] { xusuario, xpassword }, Integer.class);
	}
	
	// listar los menus de la base de datos
	public List<Map<String, Object>> xlistarMenus(String xusuario) {
		String xsql = "Select m.* from personal p , usuarios u , rol r , menus m, rolmen rm where u.login=? and u.codp=p.codp and u.codr=r.codr and r.codr=rm.codr and m.codm=rm.codm;";
		return this.jdbcTemplate.queryForList(xsql, new Object[] { xusuario });
	}

	// LISTA LOS PROCESOS Y MEPRO DE LA BAS DE DATOS
	public List<Map<String, Object>> xlistarSubmenus() {
		String xsql = "select m.codm, p.nombre, p.enlace from mepro m, procesos p, menus me  where (m.codprocesos=p.codprocesos and me.codm=m.codm)";
		return this.jdbcTemplate.queryForList(xsql, new Object[] {});
	}

	/// listar el nombre del ususarion en parte inferior izquierda
	public List<Map<String, Object>> VernombreMenu(String xusuario) {
		String xsql = "Select p.* from personal p , usuarios u where u.login=? and u.codp=p.codp";
		return this.jdbcTemplate.queryForList(xsql, new Object[] { xusuario });
	}

	//////////////////// consultas del primer proceso de menu
	//////////////////// almacenero///////////////////////////////
	//// Datos de la tabla DESCUENTOS DE tratamientos
	public List<Map<String, Object>> xlistaRegistrosConPromocion() {
		String xsql = "SELECT * FROM registroconpromocion";
		return this.jdbcTemplate.queryForList(xsql, new Object[] {});
	}

	// nuevo registro de producto con promocion
	public Map<String, Object> Registrando(String codigopu, String categoria, String nombre, String tamano,
			int cantidad, int preciou, int preciot, String fecha, String tipo, String foto) throws ParseException {
		String xsql = "";
		SimpleDateFormat xfecha = new SimpleDateFormat("yyyy-MM-dd");
		java.sql.Date sqlDate = new java.sql.Date(xfecha.parse(fecha).getTime());

		Map<String, Object> resp = new HashMap();
		try {
			resp.put("estado", 1);
			xsql = "INSERT INTO registroconpromocion(codigopu,categoria,nombre,tamano,cantidad,preciou,preciot,fecha,tipodep,foto) values(?,?,?,?,?,?,?,?,?,?);";
			this.jdbcTemplate.queryForMap(xsql, new Object[] { codigopu, categoria, nombre, tamano, cantidad, preciou,
					preciot, sqlDate, tipo, foto });

		} catch (Exception e) {
			// TODO: handle exception
			resp.put("estado", null);
		}
		return resp;
	}

	//////////////////// fin primer proceso de menu

	//////////////////// inicio proceso 2 ///////////////////////////////
	//// Datos de la tabla DESCUENTOS DE tratamientos
	public List<Map<String, Object>> xlistaRegistrosSinPromocion() {
		String xsql = "SELECT * FROM productos order by id_producto";
		return this.jdbcTemplate.queryForList(xsql, new Object[] {});
	}

	/*
	 * // verificacion de un nuevo registro de venta sin promocion public
	 * Map<String, Object> verificardatos(String codigop, String categoria,
	 * String nombre, String tamano, int cantidad, int preciou, int preciot,
	 * String fecha, String foto) throws ParseException { String xsql = "";
	 * SimpleDateFormat xfecha = new SimpleDateFormat("yyyy-MM-dd");
	 * java.sql.Date sqlDate = new java.sql.Date(xfecha.parse(fecha).getTime());
	 * String resp1 = ""; Map<String, Object> resp = new HashMap(); try { xsql =
	 * "INSERT INTO ventasinpromocion(codigop,categoria,nombre,tamano,cantidad,preciou,foto) values(?,?,?,?,?,?,?);"
	 * ; this.jdbcTemplate.queryForMap(xsql, new Object[] { codigop, categoria,
	 * nombre, tamano, cantidad, preciou, foto });
	 * 
	 * } catch (DuplicateKeyException dke) { resp.put("estado", 1);
	 * System.out.println("duplicado!!"); } catch (Exception e) {
	 * resp.put("estado", 2);
	 * 
	 * } return resp;
	 * 
	 * }
	 */
	/*
	 * /// insertando//
	 * 
	 * @Transactional(rollbackFor = Exception.class, propagation =
	 * Propagation.REQUIRES_NEW) public int registrostablas(String codigop,
	 * String categoria, String nombre, String tamano, int cantidad, int
	 * preciou, int preciot, String fecha, String foto) throws ParseException {
	 * String xsql = ""; SimpleDateFormat xfecha = new
	 * SimpleDateFormat("yyyy-MM-dd"); java.sql.Date sqlDate = new
	 * java.sql.Date(xfecha.parse(fecha).getTime());
	 * 
	 * Map map; int a=1,b=1; // insertando tabla registro sin promocion
	 * System.out.println("Nuevo"); xsql =
	 * "INSERT INTO registrosinpromocion(codigop,categoria,nombre,tamano,cantidad,preciou,preciot,fecha,foto) values(?,?,?,?,?,?,?,?,?);"
	 * ; return this.jdbcTemplate.queryForObject(xsql, new Object[] { codigop,
	 * categoria, nombre, tamano, cantidad, preciou, preciot, sqlDate, foto},
	 * Integer.class);
	 * 
	 * 
	 * }
	 * 
	 * 
	 * /* resp.put("estado", 1); System.out.println("duplicado"); xsql =
	 * "Update ventasinpromocion set cantidad=cantidad+? where codigop=? and categoria=? and nombre=? and tamano=?"
	 * ; resp = this.jdbcTemplate.queryForMap(xsql, new Object[] { cantidad,
	 * codigop, categoria, nombre, tamano }); resp.put("estado", 1);
	 */
	// Verificacion tabla
	// @Transactional(rollbackFor = Exception.class, propagation =
	// Propagation.REQUIRES_NEW)
	public int verificarRegistro(String codigop) {
		String xsql = "select count(*) FROM inventariosinpromocion where codigop=?";
		return this.jdbcTemplate.queryForObject(xsql, new Object[] { codigop }, Integer.class);
	}

	// modificando tabla ventasinpromocion
	public Map<String, Object> registrotabla10(String codigop, String categoria, String nombre, String tamano,
			int cantidad, int preciou, int preciot, String fecha, String foto) throws ParseException {
		String xsql = "";
		SimpleDateFormat xfecha = new SimpleDateFormat("yyyy-MM-dd");
		java.sql.Date sqlDate = new java.sql.Date(xfecha.parse(fecha).getTime());
		Map<String, Object> resp = new HashMap();
		try {
			xsql = "Update inventariosinpromocion set cantidad=cantidad+? where codigop=? and categoria=? and nombre=? and tamano=? and estado=?";
			resp = this.jdbcTemplate.queryForMap(xsql,
					new Object[] { cantidad, codigop, categoria, nombre, tamano, 1 });
			resp.put("estado", 1);
		} catch (Exception e) {
			resp.put("estado", null);
		}
		return resp;
	}

	// insert tabla registro
	public Map<String, Object> registrotabla1(String codigop, String categoria, String nombre, String tamano,
			int cantidad, int preciou, int preciot, String fecha, String foto) throws ParseException {
		String xsql = "";
		SimpleDateFormat xfecha = new SimpleDateFormat("yyyy-MM-dd");
		java.sql.Date sqlDate = new java.sql.Date(xfecha.parse(fecha).getTime());
		Map<String, Object> resp = new HashMap();
		try {
			xsql = "INSERT INTO inventariosinpromocion(codigop,categoria,nombre,tamano,cantidad,preciou,foto) values(?,?,?,?,?,?,?);";
			resp = this.jdbcTemplate.queryForMap(xsql,
					new Object[] { codigop, categoria, nombre, tamano, cantidad, preciou, foto });
			System.out.println(resp);
		} catch (Exception e) {
			resp.put("estado", null);
		}
		return resp;
	}

	// insert tabla registrosinpromocion
	public Map<String, Object> registrotabla2(String codigop, String categoria, String nombre, String tamano,
			int cantidad, int preciou, int preciot, String fecha, String foto, String hora) throws ParseException {

		System.out.println(hora);
		String xsql = "";
		SimpleDateFormat xfecha = new SimpleDateFormat("yyyy-MM-dd");
		java.sql.Date sqlDate = new java.sql.Date(xfecha.parse(fecha).getTime());

		DateFormat xhora = new SimpleDateFormat("HH:mm:ss");

		Date horas = new Date(xhora.parse(hora).getTime());
		System.out.println(horas);

		Map<String, Object> resp = new HashMap();
		try {
			xsql = "INSERT INTO registrosinpromocion(codigop, categoria, nombre, tamano, cantidad, preciou, preciot,fecha, foto, hora)VALUES (?, ?, ?, ?, ?, ?, ?,?, ?, ?);";
			resp = this.jdbcTemplate.queryForMap(xsql, new Object[] { codigop, categoria, nombre, tamano, cantidad,
					preciou, preciot, sqlDate, foto, horas });

		} catch (Exception e) {
			resp.put("estado", null);
		}
		return resp;
	}

	///////////////////////// fin proceso 2/////////////////////////

	///////////////////////// inicio de proceso 3/////////////////////////

	public List<Map<String, Object>> xlistaDeReavastecimiento() {
		String xsql = "select *from productos where cantidad<=10 order by id_producto;";
		return this.jdbcTemplate.queryForList(xsql, new Object[] {});
	}

	//// traendo datos para la modificacion de productos sin promocion
	public List<Map<String, Object>> modificando(String id_producto) {
		String xsql = "select *from productos where id_producto=? order by id_producto";
		return this.jdbcTemplate.queryForList(xsql, new Object[] { id_producto });
	}

	/// modal 4 modificando
	public Map<String, Object> modal4Datos(String id_producto, int cantidad, int preciou) throws ParseException {
		String xsql = "";
		Map<String, Object> resp = new HashMap();
		try {
			xsql = "Update productos set cantidad=cantidad+?, preciou=?,estado=1 where id_producto=?";
			this.jdbcTemplate.queryForMap(xsql, new Object[] { cantidad, preciou, id_producto });
			resp.put("estado", 1);
		} catch (Exception e) {
			resp.put("estado", null);
		}
		return resp;
	}

	/*
	 * // agregadno a lista reavastecimiento public Map<String, Object>
	 * agregar(String codigopu, String categoria, String nombre, String tamano,
	 * int cantidad, int preciou, String fecha) throws ParseException { String
	 * xsql = ""; SimpleDateFormat xfecha = new SimpleDateFormat("yyyy-MM-dd");
	 * java.sql.Date sqlDate = new java.sql.Date(xfecha.parse(fecha).getTime());
	 * 
	 * Map<String, Object> resp = new HashMap(); try { resp.put("estado", 1);
	 * xsql =
	 * "INSERT INTO public.newlista(codigop, categoria, nombre, tamano, cantidad, preciou, fecha,estado)VALUES (?, ?, ?, ?, ?, ?, ?, ?);"
	 * ; this.jdbcTemplate.queryForMap(xsql, new Object[] { codigopu, categoria,
	 * nombre, tamano, cantidad, preciou, sqlDate, 0 });
	 * 
	 * } catch (Exception e) { // TODO: handle exception resp.put("estado",
	 * null); } return resp; }
	 */
	// cambio de estado al presionar boton agregar
	public Map<String, Object> CambioEstado(String id_producto) throws ParseException {
		String xsql = "";

		Map<String, Object> resp = new HashMap();
		try {
			xsql = "UPDATE productos  SET estado=0 where id_producto=?;";
			resp = this.jdbcTemplate.queryForMap(xsql, new Object[] { id_producto });
			resp.put("estado", 1);
		} catch (Exception e) {
			resp.put("estado", null);
		}
		return resp;
	}

	//// new lista modal
	public List<Map<String, Object>> xlistaRegistrosnewlista() {
		String xsql = "SELECT * FROM productos where estado=0 and cantidad<=10";
		return this.jdbcTemplate.queryForList(xsql, new Object[] {});
	}

	/// numero registro para modal3
	public List<Map<String, Object>> numeroRegistroModal3() {
		String xsql = "SELECT count(*)  FROM productos where estado=0 and cantidad<=10";
		return this.jdbcTemplate.queryForList(xsql, new Object[] {});
	}

	// insert tabla registrosinpromocion
	public int insertarDatos(String codigo, String categoria, String nombre, String tamano, int pedido)
			throws ParseException {
		Date date = new Date();
		SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
		String stringDate = DateFor.format(date);
		java.sql.Date sqlDate = new java.sql.Date(DateFor.parse(stringDate).getTime());
		System.out.println(sqlDate);

		Date today = Calendar.getInstance().getTime();
		String sfecha = DateFor.format(today);

		// Print it!
		System.out.println("Today is: " + sfecha);
		String xsql = "INSERT INTO pedido(categoria,nombre,tamano,pedido,id_productop,fecha)VALUES (?, ?, ?, ?, ?, ?);";
		return this.jdbcTemplate.update(xsql, new Object[] { categoria, nombre, tamano, pedido, codigo, sfecha });
	}

	/// numero registro para modal3
	public List<Map<String, Object>> FormVentas() {
		String xsql = "SELECT *  FROM productos  where estadov=0 order by id_producto";
		return this.jdbcTemplate.queryForList(xsql, new Object[] {});
	}

	/// buscador
	public List<Map<String, Object>> busqueda(String dato) {
		String sql = "select * FROM productos where concat(id_producto,' ',categoria,' ',nombre,' ',tamano) LIKE ?";
		return this.jdbcTemplate.queryForList(sql, new Object[] { "%" + dato + "%" });
	}

	/// numero registro para modal6
	public List<Map<String, Object>> numeroRegistroModal6() {
		String xsql = "SELECT count(*)  FROM productos where estadov=0 ";
		return this.jdbcTemplate.queryForList(xsql, new Object[] {});
	}

	/// numero registro para modal6
	public List<Map<String, Object>> Nform() {
		String xsql = "SELECT count(*)  FROM productos where estadov=0 ";
		return this.jdbcTemplate.queryForList(xsql, new Object[] {});
	}

	// cambiando de estado a 3 para la venta
	public Map<String, Object> CambioEstadov(String id_producto) throws ParseException {
		String xsql = "";

		Map<String, Object> resp = new HashMap();
		try {
			xsql = "UPDATE productos  SET estadov=0 where id_producto=?;";
			resp = this.jdbcTemplate.queryForMap(xsql, new Object[] { id_producto });
			resp.put("estado", 1);
		} catch (Exception e) {
			resp.put("estado", null);
		}
		return resp;
	}

	// cambiando de estado a 1 para la venta
	public Map<String, Object> CambioEstadov1(String id_producto) throws ParseException {
		String xsql = "";

		Map<String, Object> resp = new HashMap();
		try {
			xsql = "UPDATE productos  SET estadov=1 where id_producto=?;";
			resp = this.jdbcTemplate.queryForMap(xsql, new Object[] { id_producto });
			resp.put("estado", 1);
		} catch (Exception e) {
			resp.put("estado", null);
		}
		return resp;
	}

	// cambiando de estadov a 1 para la venta de todos los estados
	public Map<String, Object> CambioTotalEstadov1() throws ParseException {
		String xsql = "";
		Map<String, Object> resp = new HashMap();
		try {
			xsql = "UPDATE productos SET estadov=1";
			resp = this.jdbcTemplate.queryForMap(xsql, new Object[] {});
			resp.put("estado", 1);
		} catch (Exception e) {
			resp.put("estado", null);
		}
		return resp;
	}

	// descuento de cantidades para el inventario
	public int DesInventario(String id, int Cventa) throws ParseException {
		/*
		 * Date date = new Date(); SimpleDateFormat DateFor = new
		 * SimpleDateFormat("dd/MM/yyyy"); String stringDate =
		 * DateFor.format(date); java.sql.Date sqlDate = new
		 * java.sql.Date(DateFor.parse(stringDate).getTime());
		 * System.out.println(sqlDate); Date today =
		 * Calendar.getInstance().getTime(); String sfecha =
		 * DateFor.format(today);
		 * 
		 * // Print it! System.out.println("Today is: " + sfecha);
		 */
		String xsql = "UPDATE productos SET cantidad=cantidad-? WHERE id_producto=?";
		return this.jdbcTemplate.update(xsql, new Object[] { Cventa, id });
	}


	// Verificacion del logeo de usuarios
	public int verificarAletorio(String numt) {
		String xsql = "SELECT count(*) FROM ticket where id_ticket=?";
		return this.jdbcTemplate.queryForObject(xsql, new Object[] { numt }, Integer.class);
	}
	// insertar datos de la venta en la base de datos tabla ticket
		public int InsertarDatosTicket(String id_ticket, String comprador, String text, int ventaTotal, String ventaText ) throws ParseException {
			
			  Date date = new Date(); 
			  SimpleDateFormat DateFor = new  SimpleDateFormat("dd/MM/yyyy"); 
			  String stringDate = DateFor.format(date); java.sql.Date sqlDate = new
			  java.sql.Date(DateFor.parse(stringDate).getTime());
			  System.out.println(sqlDate);
			
			String xsql = "INSERT INTO ticket(id_ticket, nombre_comprador, detalles, venta_total, venta_texto, fecha) VALUES (?, ?, ?, ?, ?, ?);";
			return this.jdbcTemplate.update(xsql, new Object[] {id_ticket, comprador, text, ventaTotal, ventaText, sqlDate});
		}
//// listando pedidos acumulados
		public List<Map<String, Object>> pedidosAcumulados() {
			String xsql = "SELECT *  FROM pedidos_de_clientes order by comprador ;";
			return this.jdbcTemplate.queryForList(xsql, new Object[] {});
		}
		
//camiando estadp
			public Map<String, Object> cambiandoEstado(int id) throws ParseException {
				String xsql = "";
				Map<String, Object> resp = new HashMap();
				try {
					xsql = "UPDATE pedidos_de_clientes SET estado=1 WHERE id_pedidos=?; ";
					resp = this.jdbcTemplate.queryForMap(xsql, new Object[] {id});
					resp.put("estado", 1);
				} catch (Exception e) {
					resp.put("estado", null);
				}
				return resp;
			}

//camiando estadp
			public Map<String, Object> cambiandoEstado2(int id) throws ParseException {
				String xsql = "";
				Map<String, Object> resp = new HashMap();
				try {
					xsql = "UPDATE pedidos_de_clientes SET estado=2 WHERE id_pedidos=?; ";
					resp = this.jdbcTemplate.queryForMap(xsql, new Object[] {id});
					resp.put("estado", 1);
				} catch (Exception e) {
					resp.put("estado", null);
				}
				return resp;
			}

	//// listando pedidos acumulados
			public List<Map<String, Object>> verdetalles(int id) {
				String xsql = "SELECT * FROM pedidos_de_clientes where id_pedidos = ?;";
				return this.jdbcTemplate.queryForList(xsql, new Object[] {id});
			}

			
			
/////////clientesconsultas
		public List<Map<String, Object>> CatalogoSinPromocion() {
			String xsql = "SELECT *  FROM catalogosp order by id_catalogosp";
			return this.jdbcTemplate.queryForList(xsql, new Object[] {});
		}
/////////carrito de compras solo para verificar el catalogo
		public List<Map<String, Object>> carritoDeComprasVerificando(String codp) {
			String xsql = "SELECT id_catalogosp FROM carrito_compras where id_codp=? order by id_catalogosp;";
			return this.jdbcTemplate.queryForList(xsql, new Object[] {codp});
		}
		// cambiando de estadov a 0 para carrito de compras
		public Map<String, Object> cambio_estado_Carrito(String id) throws ParseException {
			String xsql = "";
			Map<String, Object> resp = new HashMap();
			try {
				xsql = "UPDATE catalogosp SET estado=0 where id_catalogosp=?";
				resp = this.jdbcTemplate.queryForMap(xsql, new Object[] {id});
				resp.put("estado", 1);
			} catch (Exception e) {
				resp.put("estado", null);
			}
			return resp;
		}
		// cambiando de estadov a 0 para carrito de compras
				public Map<String, Object> insertCarito(String id_catalogosp,String codp, String categoria, String nombre, String tamano, String foto,int precio) throws ParseException {
					String xsql = "";
					Map<String, Object> resp = new HashMap();
					try {
						xsql = "INSERT INTO carrito_compras(id_catalogosp, id_codp, categoria, nombre, tamano,foto, precio)VALUES (?, ?, ?, ?, ?, ?, ?);";
						resp = this.jdbcTemplate.queryForMap(xsql, new Object[] {id_catalogosp,codp,categoria,nombre,tamano,foto,precio});
						resp.put("estado", 1);
					} catch (Exception e) {
						resp.put("estado", null);
					}
					return resp;
				}
/////////carrito de compras
		public List<Map<String, Object>> carritoDeCompras(String codp) {
			String xsql = "SELECT * FROM carrito_compras where id_codp=? order by id_catalogosp;";
			return this.jdbcTemplate.queryForList(xsql, new Object[] {codp});
		}
		/// numero registro para modal6
		public List<Map<String, Object>> contarRegistros() {
			String xsql = "SELECT count(*)  FROM catalogosp where estado=0 ";
			return this.jdbcTemplate.queryForList(xsql, new Object[] {});
		}		
		// cambiando de estadov a 1 para catalogosp
		public Map<String, Object> estadoCambio_Catalogo1(String id) throws ParseException {
			String xsql = "";
			Map<String, Object> resp = new HashMap();
			try {
				xsql = "UPDATE catalogosp SET estado=1 where id_catalogosp=?";
				resp = this.jdbcTemplate.queryForMap(xsql, new Object[] {id});
				resp.put("estado", 1);
			} catch (Exception e) {
				resp.put("estado", null);
			}
			return resp;
		}
		// eliminando un registro de carrito de compras
		public Map<String, Object> eliminarDeCarrito(int id_carrito, String id_catalogosp,String codp) throws ParseException {
			String xsql = "";
			Map<String, Object> resp = new HashMap();
			try {
				xsql = "DELETE FROM carrito_compras WHERE id_carrito=? and id_catalogosp=? and id_codp=?;";
				resp = this.jdbcTemplate.queryForMap(xsql, new Object[] {id_carrito,id_catalogosp,codp});
				resp.put("estado", 1);
			} catch (Exception e) {
				resp.put("estado", null);
			}
			return resp;
		}
		// Verificacion crear usuarios
		public int usuarioNuevo(String buscar) {
			String xsql = "SELECT count(*) FROM usuarios where (login=?)";
			return this.jdbcTemplate.queryForObject(xsql, new Object[] { buscar}, Integer.class);
		}
		// Verificacion crear codp
		public int usuarioCodp(String buscar) {
			String xsql = "SELECT count(*) FROM personal where (codp=?)";
			return this.jdbcTemplate.queryForObject(xsql, new Object[] { buscar}, Integer.class);
		}
		// insertando datos a la tabla personal de nuevo usuario
				public Map<String, Object> personalInsertando(String codp,String nombre,String ap,String am,String fecha,String direccion,String telefono) throws ParseException {
					SimpleDateFormat xfecha = new SimpleDateFormat("yyyy-MM-dd");
					java.sql.Date sqlDate = new java.sql.Date(xfecha.parse(fecha).getTime());
					String xsql = "";
					Map<String, Object> resp = new HashMap();
					try {
						xsql = "INSERT INTO personal(codp, nombre, ap, am, fnac, direccion, telefono, cargo, estado) VALUES (?, ?, ?, ?, ?, ?, ?, 'CLIENTE', 1);";
						resp = this.jdbcTemplate.queryForMap(xsql, new Object[] {codp,nombre,ap,am,sqlDate,direccion,telefono});
						resp.put("estado", 1);
					} catch (Exception e) {
						resp.put("estado", null);
					}
					return resp;
				}
	// insertando datos a la tabla Usuario de nuevo usuario
	public Map<String, Object> usuarioTabla(String codp,String usuario,String contraseña) throws ParseException {
		
		String xsql = "";
		Map<String, Object> resp = new HashMap();
		try {
			xsql = "INSERT INTO usuarios(login, password, codp, estado, codr) VALUES (?, ?, ?, 1, 3000);";
			resp = this.jdbcTemplate.queryForMap(xsql, new Object[] {usuario,contraseña,codp});
			resp.put("estado", 1);
		} catch (Exception e) {
			resp.put("estado", null);
		}
		return resp;
	}
	
	
	
	// insertando datos a la tabla pedidos de clientes
		public Map<String, Object> pedidosdeclientes(String comprador,String direccion,int telefono, String gps, String detalles,int total) throws ParseException {
			  Date date = new Date(); 
			  SimpleDateFormat DateFor = new  SimpleDateFormat("dd/MM/yyyy"); 
			  String stringDate = DateFor.format(date); java.sql.Date sqlDate = new
			  java.sql.Date(DateFor.parse(stringDate).getTime());
			  System.out.println(stringDate);
			  
			String xsql = "";
			Map<String, Object> resp = new HashMap();
			try {
				xsql = "INSERT INTO pedidos_de_clientes(fecha, comprador, direccion, telefono, ubicacion, detalles, estado,total)VALUES (?, ?, ?, ?, ?, ?, ?,?);";
;
				resp = this.jdbcTemplate.queryForMap(xsql, new Object[] {stringDate,comprador,direccion,telefono,gps,detalles,0,total});
				resp.put("estado", 1);
			} catch (Exception e) {
				resp.put("estado", null);
			}
			return resp;
		}
	
}