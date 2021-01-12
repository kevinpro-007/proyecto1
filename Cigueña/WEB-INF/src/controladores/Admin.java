package controladores;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.text.DateFormat;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.xml.ws.Response;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import model.manager.AdminManager;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;

@Controller
public class Admin {
	@Autowired
	AdminManager adminManager;

	@RequestMapping({ "Index.html" })
	public String index(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			// Not created yet. Now do so yourself.			
		} else {
			session.invalidate();
			System.out.println("cerrando Session ");
		}
		List<?> lista = this.adminManager.ListaDeMenusCliente();
		model.addAttribute("xslista", lista);
		return "PaginaPrincipal";
	}

	@RequestMapping({ "Verifica.html" })
	public String verifica(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String xusuario = request.getParameter("xuser");
		String xpassword = request.getParameter("xpass");
		System.out.println(xusuario);
		System.out.println(xpassword);

		int res = this.adminManager.VerificaUsuarios(xusuario, xpassword);
		if (res == 1) {
			int res_cliente = this.adminManager.VerificaCliente(xusuario, xpassword);	
				if(res_cliente==1){
					HttpSession session = request.getSession(true);
					session.setAttribute("usuario", xusuario);
					response.sendRedirect("MenuCliente.html");
				}else{
					// crear la session
					HttpSession session = request.getSession(true);
					session.setAttribute("usuario", xusuario);
					response.sendRedirect("Menu.html");
				}		
		} else {
			model.addAttribute("mensaje", "Usuario No Autorizado");

		}
		System.out.println("valor de res es " + res);
		return "mensaje";
	}
	//pagina principal
	@RequestMapping({ "MenuCliente.html" })
	public String PaginaPrincipal(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession se = request.getSession(true);
		String xusuario = (String) se.getAttribute("usuario");
		try {
			if (xusuario == null) {
				// ERRORs
				return "mensaje";
			} else {
				System.out.println("usuario conectado::" + xusuario);
				List<?> xusuarionom = this.adminManager.VernombreMenu(xusuario);
				model.addAttribute("xuser", xusuarionom);
			}
			List<?> lista = this.adminManager.ListaDeMenusCliente();
			model.addAttribute("xslista", lista);
			
			response.encodeRedirectURL("MenuCliente.html");
		} catch (final Exception e) {
			System.err.println("Exception caught: " + e.getMessage());
		}
		return "MenuCliente";
	}
	@RequestMapping({ "Menu.html" })
	public String menu(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession se = request.getSession(true);
		String xusuario = (String) se.getAttribute("usuario");
		try {
			if (xusuario == null) {
				// ERRORs
				return "mensaje";
			} else {
				System.out.println("usuario conectado::" + xusuario);
				List<?> xusuarionom = this.adminManager.VernombreMenu(xusuario);
				model.addAttribute("xuser", xusuarionom);
			}
			// listar menus
			List<?> listaMenus = this.adminManager.xlistarMenus(xusuario);
			model.addAttribute("xlistaMenu", listaMenus);

			List<?> listaSubmenus = this.adminManager.xlistarSubmenus();
			model.addAttribute("xlistaSubmen", listaSubmenus);

			response.encodeRedirectURL("Menu.html");
		} catch (final Exception e) {
			System.err.println("Exception caught: " + e.getMessage());
		}
		return "menu";
	}

	@RequestMapping({ "Mensaje.html" })
	public String mensaje(Model model) throws IOException {
		return "mensaje";
	}

	///////////////////////// primer proceso de menu almacenero
	@RequestMapping("RegistroConPromocion.html")
	public String listaConPromocion(Model model, HttpServletRequest request) throws IOException {
		System.out.println("enviando html");
		List<?> listaRegistrosConPromocion = this.adminManager.xlistaRegistrosConPromocion();
		model.addAttribute("xlista", listaRegistrosConPromocion);
		return "almacenero/ListaDeProductosConPromocion";
	}

	@RequestMapping("xNewRegistroConPromocion.html")
	public String mostrarmodal(Model model) throws IOException {
		return "almacenero/Modal1";
	}

	/// recibir datos de solicitud
	@RequestMapping("GuardadoDeProductosConPromocion.html")
	public @ResponseBody String Modifico(HttpServletRequest request)
			throws IOException, NumberFormatException, ParseException {
		String codigo = request.getParameter("xcodigopu");
		String categoria = request.getParameter("xcategoria");
		String nombre = request.getParameter("xnombre");
		String tamaño = request.getParameter("xtamaño");
		String cantidad = request.getParameter("xcantidad");
		String preciou = request.getParameter("xpreciou");
		String preciot = request.getParameter("xpreciot");
		String fecha = request.getParameter("xfecha");
		String tipo = request.getParameter("xtipo");
		String foto = request.getParameter("xfoto");
		System.out.println(codigo);
		System.out.println(categoria);
		System.out.println(nombre);
		System.out.println(tamaño);
		System.out.println(cantidad);
		System.out.println(preciou);
		System.out.println(preciot);
		System.out.println(fecha);
		System.out.println(tipo);
		System.out.println(foto);
		String pack = foto;
		String sSubCadena = pack.substring(12, pack.length());
		System.out.println(sSubCadena);
		Map<String, Object> NuevoRegistro = this.adminManager.Registrando(codigo.toUpperCase(), categoria.toUpperCase(),
				nombre.toUpperCase(), tamaño.toUpperCase(), Integer.parseInt(cantidad), Integer.parseInt(preciou),
				Integer.parseInt(preciot), fecha, tipo.toUpperCase(), sSubCadena);
		System.out.println("resultado es :" + NuevoRegistro.get(1));
		if (NuevoRegistro.get(1) == null) {
			return "Se Guardo Exitosamente";
		} else {
			return "Ocurrio Un problema";
		}
	}

	//////////////////////// fin de primer proceso de menu almacenero

	///// inicio servidor de proceso 2 alamacenero/////
	@RequestMapping("RegistroSinPromocion.html")
	public String listaSinPromocion(Model model, HttpServletRequest request) throws IOException {
		List<?> listaRegistrosSinPromocion = this.adminManager.xlistaRegistrosSinPromocion();
		model.addAttribute("xlista", listaRegistrosSinPromocion);
		return "almacenero/ListaDeProductosSinPromocion";
	}

	@RequestMapping("xNewRegistroSinPromocion.html")
	public String mostrarmoda2(Model model) throws IOException {
		return "almacenero/Modal2";
	}

	/*
	 * /// recibir datos de solicitud
	 * 
	 * @RequestMapping("GuardadoDeProductosSinPromocion.html")
	 * public @ResponseBody String Guardar1(HttpServletRequest request,
	 * HttpServletResponse response) throws IOException, NumberFormatException,
	 * ParseException { String codigo = request.getParameter("xcodigop"); String
	 * categoria = request.getParameter("xcategoria"); String nombre =
	 * request.getParameter("xnombre"); String tamaño =
	 * request.getParameter("xtamaño"); String cantidad =
	 * request.getParameter("xcantidad"); String preciou =
	 * request.getParameter("xpreciou"); String preciot =
	 * request.getParameter("xpreciot"); String fecha =
	 * request.getParameter("xfecha"); String foto =
	 * request.getParameter("xfoto"); System.out.println(codigo);
	 * System.out.println(categoria); System.out.println(nombre);
	 * System.out.println(tamaño); System.out.println(cantidad);
	 * System.out.println(preciou); System.out.println(preciot);
	 * System.out.println(fecha); System.out.println(foto); String pack = foto;
	 * String sSubCadena = pack.substring(12, pack.length());
	 * System.out.println(sSubCadena); /// verificar// Map<String, Object> m =
	 * this.adminManager.verificardatos(codigo.toUpperCase(),
	 * categoria.toUpperCase(), nombre.toUpperCase(), tamaño.toUpperCase(),
	 * Integer.parseInt(cantidad), Integer.parseInt(preciou),
	 * Integer.parseInt(preciot), fecha, sSubCadena);
	 * System.err.println("resultado llego: " + m.get("estado")); Object
	 * i=1,i1=2; if (m.get(1)==null) { //response.sendRedirect("x.html"); return
	 * "Ocurrio Un problema"; } else { if (i1.equals(m.get("estado"))) { return
	 * "Se Guardo Exitosamente"; }else{ return "Ocurrio Un problema"; }
	 * 
	 * } }
	 */
	/// recibir datos de solicitud
	@RequestMapping("GuardadoDeProductosSinPromocion.html")
	// @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName =
	// { "Execption" })
	public @ResponseBody String guarda(HttpServletRequest request)
			throws IOException, NumberFormatException, ParseException {
		String codigo = request.getParameter("xcodigop");
		String categoria = request.getParameter("xcategoria");
		String nombre = request.getParameter("xnombre");
		String tamaño = request.getParameter("xtamaño");
		String cantidad = request.getParameter("xcantidad");
		String preciou = request.getParameter("xpreciou");
		String preciot = request.getParameter("xpreciot");
		String fecha = request.getParameter("xfecha");
		String foto = request.getParameter("xfoto");
		System.out.println(codigo);
		System.out.println(categoria);
		System.out.println(nombre);
		System.out.println(tamaño);
		System.out.println(cantidad);
		System.out.println(preciou);
		System.out.println(preciot);
		System.out.println(fecha);
		System.out.println(foto);
		String pack = foto;
		String sSubCadena = pack.substring(12, pack.length());
		System.out.println(sSubCadena);
		/// verificar//
		int res = this.adminManager.verificarRegistro(codigo.toUpperCase());
		System.out.println("numero llego es :" + res);
		if (res == 1) {
			//// caso en que se actualiza datos y se registra los datos
			System.out.println("asd");
			Map<String, Object> NuevoRegistro1 = this.adminManager.registrotabla10(codigo.toUpperCase(),
					categoria.toUpperCase(), nombre.toUpperCase(), tamaño.toUpperCase(), Integer.parseInt(cantidad),
					Integer.parseInt(preciou), Integer.parseInt(preciot), fecha, sSubCadena);
			System.out.println("resultado es :" + NuevoRegistro1.get(1));
			if (NuevoRegistro1.get(1) == null) {
				DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
				Date date = new Date();
				System.out.println("Hora actual: " + dateFormat.format(date));
				String hr = dateFormat.format(date);
				Map<String, Object> NuevoRegistro2 = this.adminManager.registrotabla2(codigo.toUpperCase(),
						categoria.toUpperCase(), nombre.toUpperCase(), tamaño.toUpperCase(), Integer.parseInt(cantidad),
						Integer.parseInt(preciou), Integer.parseInt(preciot), fecha, sSubCadena, hr);
				System.out.println("resultado es :" + NuevoRegistro2.get(1));
				if (NuevoRegistro2.get(1) == null) {
					return "Se Guardo Exitosamente";
				}
			} else {
				return "Ocurrio Un problema";
			}
		} else {
			//// caso en el que se ingresan datos en las 2 tablas
			Map<String, Object> NuevoRegistro = this.adminManager.registrotabla1(codigo.toUpperCase(),
					categoria.toUpperCase(), nombre.toUpperCase(), tamaño.toUpperCase(), Integer.parseInt(cantidad),
					Integer.parseInt(preciou), Integer.parseInt(preciot), fecha, sSubCadena);
			System.out.println("resultado es :" + NuevoRegistro.get(1));
			if (NuevoRegistro.get(1) == null) {
				DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
				Date date = new Date();
				System.out.println("Hora actual: " + dateFormat.format(date));
				String hr = dateFormat.format(date);
				Map<String, Object> NuevoRegistro2 = this.adminManager.registrotabla2(codigo.toUpperCase(),
						categoria.toUpperCase(), nombre.toUpperCase(), tamaño.toUpperCase(), Integer.parseInt(cantidad),
						Integer.parseInt(preciou), Integer.parseInt(preciot), fecha, sSubCadena, hr);
				System.out.println("resultado es :" + NuevoRegistro2.get(1));
				if (NuevoRegistro2.get(1) == null) {
					return "Se Guardo Exitosamente";
				}
			} else {
				return "Ocurrio Un problema";
			}
		}
		return "Ocurrio Un problema";
	}

	/*
	 * 
	 * List<Boolean> m = (List<Boolean>)
	 * this.adminManager.RegistrandoSinPromocion(codigo.toUpperCase(),
	 * categoria.toUpperCase(), nombre.toUpperCase(), tamaño.toUpperCase(),
	 * Integer.parseInt(cantidad), Integer.parseInt(preciou),
	 * Integer.parseInt(preciot), fecha, sSubCadena);
	 * System.out.println(m.get(0); int i=0; if (i == 1) { return "duplicado"; }
	 * else { return "no duplicado"; }
	 */
	/*
	 * //servicio 1// Map<String, Object> NuevoRegistrosinPromocion =
	 * this.adminManager.RegistrandoSinPromocion(codigo.toUpperCase(),
	 * categoria.toUpperCase(), nombre.toUpperCase(), tamaño.toUpperCase(),
	 * Integer.parseInt(cantidad), Integer.parseInt(preciou),
	 * Integer.parseInt(preciot), fecha, sSubCadena);
	 * 
	 * System.out.println("resultado es :" +
	 * NuevoRegistrosinPromocion.get("estado")); int i=(int)
	 * NuevoRegistrosinPromocion.get("estado"); if (i==1) { return "Guardado"; }
	 * else { return "error"; }
	 */

	///////////////////////// fin proceso 2/////////////////////////

	///////////////////////// inicio de proceso 3/////////////////////////

	@RequestMapping("VerReavastecimiento.html")
	public String VerReavastecimiento(Model model, HttpServletRequest request) throws IOException {
		List<?> ReavastecimientolistaRegistrosSinPromocion = this.adminManager.xlistaDeReavastecimiento();
		model.addAttribute("xlista", ReavastecimientolistaRegistrosSinPromocion);
		return "almacenero/ListaDeReavastecimiento";
	}

	//// mdofiicando cantidad de productos sin promocion
	@RequestMapping("modalModificar.html")
	public String modificar(Model model, HttpServletRequest request) throws IOException {
		String codigo = request.getParameter("xcodigop");
		List<?> datos = this.adminManager.modificando(codigo.toUpperCase());
		model.addAttribute("xlista", datos);
		return "almacenero/Modal4";
	}

	/// trendo datos de modal4
	/// recibir datos de solicitud
	@RequestMapping("modificarDatos.html")
	public @ResponseBody String Modificomodal4(HttpServletRequest request)
			throws IOException, NumberFormatException, ParseException {
		String codigo = request.getParameter("xcodigop");
		String cantidad = request.getParameter("xcantidad");
		String preciou = request.getParameter("xpreciou");

		System.out.println(codigo);
		System.out.println(cantidad);
		System.out.println(preciou);

		Map<String, Object> NuevoRegistro = this.adminManager.modal4Datos(codigo.toUpperCase(),
				Integer.parseInt(cantidad), Integer.parseInt(preciou));
		System.out.println("resultado es :" + NuevoRegistro.get(1));
		if (NuevoRegistro.get(1) == null) {
			return "Se Guardo Exitosamente";
		} else {
			return "Ocurrio Un problema";
		}
	}
	///////

	@RequestMapping("EnvioRegistro.html")
	public @ResponseBody String newlista(HttpServletRequest request)
			throws IOException, NumberFormatException, ParseException {
		String codigo = request.getParameter("xcodigop");
		Map<String, Object> cambio = this.adminManager.CambioEstado(codigo.toUpperCase());
		System.out.println("resultado es :" + cambio.get(1));
		if (cambio.get(1) == null) {
			return "Agregado Exitosamente";
		} else {
			return "Ocurrio Un problema";
		}

	}

	//// modal 3 vista de reavastecimiento
	@RequestMapping("verlista.html")
	public String mostrarmodal3(Model model, HttpServletRequest request) throws IOException {
		System.out.println("recibiendo peticion html");
		List<?> newlista1 = this.adminManager.xlistaRegistrosnewlista();
		model.addAttribute("xlista", newlista1);
		List<?> newlista2 = this.adminManager.numeroRegistroModal3();
		model.addAttribute("xxlista", newlista2);
		return "almacenero/Modal3";
	}

	@RequestMapping("DATOS_PRODUCTOS.html")
	public @ResponseBody String datosproductos(HttpServletRequest request, ServletResponse response)
			throws IOException, NumberFormatException, ParseException {
		String codigo = request.getParameter("datos");
		// Map<String, Object> Data=new HashMap<>();
		System.out.println("datos productos");
		String codigos[] = request.getParameterValues("codigos[]");
		String categorias[] = request.getParameterValues("categoria[]");
		String nombres[] = request.getParameterValues("nombre[]");
		String tamanos[] = request.getParameterValues("tamano[]");
		String pedidos[] = request.getParameterValues("pedidos[]");

		int cantcodigos = codigos.length;
		// int cantpedidos = pedidos.length;
		String resp = "";

		for (int i = 0; i <= cantcodigos - 1; i++) {
			String cod = codigos[i];
			String categoria = categorias[i];
			String nombre = nombres[i];
			String tamano = tamanos[i];
			String pedido = pedidos[i];
			System.out.println(cod + " " + categoria + " " + nombre + " " + tamano + " " + pedido);
			try {
				this.adminManager.insertarDatos(cod.toUpperCase(), categoria.toUpperCase(), nombre.toUpperCase(),
						tamano.toUpperCase(), Integer.parseInt(pedido));
				resp = "Agregado Exitosamente";
				// Data.put("mensaje","El producto fue registrado
				// correctamente");
			} catch (Exception e) {
				// Data.put("mensaje","Error al registrar los productos");
				resp = "ERROR";
				System.out.println("error servidor");
			}
		}
		return resp;
	}

	///// fin proceso 3
	/////////////////// inicio proceso 4 reportes
	@RequestMapping("PedidoDeReavastecimiento.html")
	public String PedidoDeReavastecimiento(Model model, HttpServletRequest request) throws IOException {
		// List<?> fecha = this.adminManager.fechaReporte();
		// model.addAttribute("xlista", fecha);
		return "almacenero/Modal5Reportes";
	}

	@Autowired
	private DataSource dataSource;

	@RequestMapping("ReportesDePedidos.html")
	public void ReportePersonalPEspe(HttpServletResponse res, HttpServletRequest req)
			throws JRException, IOException, SQLException, Exception {
		String fecha = req.getParameter("fecha");

		String string = fecha;
		String[] parts = string.split("-");
		String anio = parts[0]; // 123
		String mes = parts[1]; // 654321
		String dia = parts[2];

		String xfecha = dia + "/" + mes + "/" + anio;

		// Create an instance of SimpleDateFormat used for formatting
		// the string representation of date according to the chosen pattern
		DateFormat df = new SimpleDateFormat(xfecha);

		// Get the today date using Calendar object.
		Date today = Calendar.getInstance().getTime();
		// Using DateFormat format method we can create a string
		// representation of a date with the defined format.
		String todayAsString = df.format(today);

		// Print it!
		System.out.println("Today is: " + todayAsString);
		System.out.println(xfecha);
		System.out.println(fecha);
		// System.out.println(sqlDate);

		Map<String, Object> params = new HashMap<>();
		params.put("fecha", todayAsString);

		// GENERA EL REPORTE
		String fileSalida = "Personal"; // nombre con el que se genera el PDF
		HttpSession sesion = req.getSession();
		String rutaCompleta = sesion.getServletContext().getRealPath("reportes/pedidos.jasper"); // le
																									// paso
																									// el
																									// nombre
																									// del
																									// JASPER
																									// ;
																									// pueden
																									// ponerlo
																									// como
																									// variable
		byte[] bytes;
		try {
			// En params se pasan los paramtros a la consulta
			bytes = JasperRunManager.runReportToPdf(rutaCompleta, params, this.dataSource.getConnection()); // le
																											// paso
																											// la
																											// conexion
																											// a
																											// la
																											// BD
			res.reset();
			res.setContentType("application/pdf");
			res.setHeader("Content-disposition", "inline; filename=" + fileSalida + "_" + Math.random() + ".pdf");
			res.setHeader("Cache-Control", "max-age=30");
			res.setHeader("Pragma", "No-cache");
			res.setDateHeader("Expires", 0);
			res.setContentLength(bytes.length);
			ServletOutputStream ouputStream = res.getOutputStream();
			System.err.println("entra");
			ouputStream.write(bytes, 0, bytes.length);
			ouputStream.flush();
			ouputStream.close();

			// si todo va bien retorna -
			// if (true) return "1";
		} catch (JRException e) {
			e.printStackTrace();
			System.err.println("Exception caught:" + e.getMessage());
		}
		// FIN DE GENERAR REPORTE

	}
///creadon numero aletorio para el formulario
	@RequestMapping("NumAletorio.html")
	public String num(Model model) throws IOException {
		int aletorio=(int) (Math.random()*10000000+1);		
		System.out.println(aletorio);
		int res=0;
		String numt= String.valueOf(aletorio);
		res = this.adminManager.verificarAletorio(numt);		
		if (res == 1) {		
			model.addAttribute("numAle",aletorio+1);
			return "vendedor/aletorio";
		}else{
			model.addAttribute("numAle",aletorio);
			return "vendedor/aletorio";
		}
		
		
	}
	
	///////////////////// vendedor modal1
	@RequestMapping("FormularioDeVentas.html")
	public String formVenta(Model model) throws IOException {	
		List<?> datos = this.adminManager.FormVentas();
		model.addAttribute("xlista", datos);
		List<?> newlista2 = this.adminManager.numeroRegistroModal6();
		model.addAttribute("xxlista", newlista2);
		List<?> Nfor = this.adminManager.Nform();
		model.addAttribute("nfor", Nfor);
		return "vendedor/modal1";
	}

	// super buscador
	@RequestMapping({ "buscar.html" })
	public String buscador(HttpServletRequest req, HttpServletResponse res, Model m) {
		String buscar = req.getParameter("buscador");
		List<?> nuevalista = this.adminManager.busqueda(buscar.toUpperCase());
		m.addAttribute("xlista", nuevalista);
		return "vendedor/superbusqueda";
	}

	@RequestMapping("Estado3.html")
	public @ResponseBody String Estado3(HttpServletRequest request)
			throws IOException, NumberFormatException, ParseException {
		String id = request.getParameter("dato");
		Map<String, Object> cambio = this.adminManager.CambioEstadov(id.toUpperCase());
		if (cambio.get(1) == null) {
			return "1";
		} else {
			return "0";
		}
	}

	// estado 1
	@RequestMapping("Estado1.html")
	public @ResponseBody String Estado1(HttpServletRequest request)
			throws IOException, NumberFormatException, ParseException {
		String id = request.getParameter("dato");
		Map<String, Object> cambio = this.adminManager.CambioEstadov1(id.toUpperCase());
		if (cambio.get(1) == null) {
			return "1";
		} else {
			return "0";
		}
	}
	// todos estadov 1
		@RequestMapping("TodosEstadov1.html")
		public @ResponseBody String TodosEstadov1(HttpServletRequest request)
				throws IOException, NumberFormatException, ParseException {			
			Map<String, Object> cambiot = this.adminManager.CambioTotalEstadov1();
			if (cambiot.get(1) == null) {
				return "1";
			} else {
				return "0";
			}
		}

	/// se recepciona losdatos de una venta
	@RequestMapping("Ventas.html")
	public @ResponseBody String Ventas(HttpServletRequest request, ServletResponse response)
			throws IOException, NumberFormatException, ParseException {
		// String codigo = request.getParameter("ventas");
		System.out.println("datos de la venta llegados al servidor: ");
		
		String vcodigos[] = request.getParameterValues("vcodigos[]");
		String vcategorias[] = request.getParameterValues("vcategoria[]");
		String vnombres[] = request.getParameterValues("vnombre[]");
		String vtamanos[] = request.getParameterValues("vtamano[]");
		String vpreciou[] = request.getParameterValues("vpreciou[]");
		String venta[] = request.getParameterValues("venta[]");
		String ventasub[] = request.getParameterValues("ventasubtotal[]");
		String ventatotal = request.getParameter("ventatotal");
		String comprador = request.getParameter("nombre_comprador");
		String TextTotal = request.getParameter("VentaTotalEnTexto");
		String id_ticket = request.getParameter("ticket");
		int cantcodigos = vcodigos.length;
		// int cantpedidos = pedidos.length;
		String resp = "";
		String text = "";
		int cont=1;
		for (int i = 0; i <= cantcodigos-1; i++) {
			String id = vcodigos[i];
			String categoria = vcategorias[i];
			String nombre = vnombres[i];
			String tamano = vtamanos[i];
			String precio = vpreciou[i];
			String ventav = venta[i];
			String ventas = ventasub[i];
			/*System.out.println(id + " " + categoria + " " + nombre + " " + tamano + " " + precio + " " + ventav + "u "
					+ ventas + "bs");*/
			text = text + " "+cont+"     " + categoria + "     " + nombre + "     " + tamano + "     Precio :" + precio + "     " + ventav + "unids     "
					+ ventas + "Bs     \n";
	// haciendo el descuento de productos vendidos alinventario
			try {
				this.adminManager.DesInventario(id.toUpperCase(),Integer.parseInt(ventav));
				resp="exito";
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e);
				resp="Error";
			}
			cont++;
		}
		System.out.println("ID_ticket: " + id_ticket);
		System.out.println("comprador: " + comprador);
		System.out.println("datos: \n"+text);		
		System.out.println("venta total: " + ventatotal);		
		System.out.println("total en texto: " + TextTotal);
		///gardando registro completo 
		try {
			this.adminManager.InsertarDatosTicket(id_ticket.toUpperCase(), comprador.toUpperCase(), text , Integer.parseInt(ventatotal), TextTotal.toUpperCase()  );
			resp="exito";
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			resp="Error";
		}		
		return resp;
	}
	
//ticket de venta
	@RequestMapping("Ticket_Venta.html")
	public void ticket(HttpServletResponse res, HttpServletRequest req)
			throws JRException, IOException, SQLException, Exception {
		String id = req.getParameter("id_ticket");
		System.out.println(id);
		Map<String, Object> params = new HashMap<>();
		params.put("Id_Ticket", id);

		// GENERA EL REPORTE
		String fileSalida = "TicketVenta"; // nombre con el que se genera el PDF
		HttpSession sesion = req.getSession();
		String rutaCompleta = sesion.getServletContext().getRealPath("reportes/Ticket_Venta.jasper"); 
		System.out.println(rutaCompleta);
		byte[] bytes;
		try {
			// En params se pasan los paramtros a la consulta
			bytes = JasperRunManager.runReportToPdf(rutaCompleta, params, this.dataSource.getConnection()); 
			res.setContentType("application/pdf");
			res.setHeader("Content-disposition", "inline; filename=" + fileSalida + "_" + Math.random() + ".pdf");
			res.setHeader("Cache-Control", "max-age=30");
			res.setHeader("Pragma", "No-cache");
			res.setDateHeader("Expires", 0);
			res.setContentLength(bytes.length);
			ServletOutputStream ouputStream = res.getOutputStream();
			System.err.println("entra");
			ouputStream.write(bytes, 0, bytes.length);
			ouputStream.flush();
			ouputStream.close();

			// si todo va bien retorna -
			// if (true) return "1";
		} catch (JRException e) {
			e.printStackTrace();
			System.err.println("Exception caught:" + e.getMessage());
		}
		// FIN DE GENERAR REPORTE

	}
	//traendo los pedidos recibidos por los clientes de cigueña
	@RequestMapping("xListarPedidosAcumulados.html")
	public String mostrarPedidosAcumulados(Model model) throws IOException {
		List<?> newlista1 = this.adminManager.pedidosAcumulados();
		model.addAttribute("xlista", newlista1);

		return "vendedor/PedidosAcumulados";
	}
	/////menu 2
	@RequestMapping("verdetalles.html")
	public String verdetalles(Model model,HttpServletRequest request) throws IOException {
		String id_pedido = request.getParameter("xid");
		System.out.println(id_pedido);
		
		List<?> lista = this.adminManager.verdetalles(Integer.parseInt(id_pedido));
		model.addAttribute("detalle", lista);
		return "vendedor/modal2";
	}
	
	////cambiando el estado de pedidos acumulados
	
	@RequestMapping("PedidoAcumuladoEstado.html")
	public @ResponseBody String PedidoAcumuladoEstado(HttpServletRequest request)
			throws IOException, NumberFormatException, ParseException {			
		String id=request.getParameter("xid");
		Map<String, Object> cambiot = this.adminManager.cambiandoEstado(Integer.parseInt(id));
		if (cambiot.get(1) == null) {
			return "1";
		} else {
			return "0";
		}
	}

	////cambiando el estado de pedidos acumulados
	
	@RequestMapping("PedidoAcumuladoEstado2.html")
	public @ResponseBody String PedidoAcumuladoEstado2(HttpServletRequest request)
			throws IOException, NumberFormatException, ParseException {			
		String id=request.getParameter("xid");
		Map<String, Object> cambiot = this.adminManager.cambiandoEstado2(Integer.parseInt(id));
		if (cambiot.get(1) == null) {
			return "1";
		} else {
			return "0";
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	///pagina principal de los clientes////////////////////////////////////////////////////////////////////////////////////////////////
	///menu 1
	@RequestMapping("Carusel.html")
	public String menu1(Model model) throws IOException {		
		return "Cliente/Carusel";
	}
	/////menu 2
	@RequestMapping("CatalogoSinPromocion.html")
	public String CatalogoConPromocion(Model model,HttpServletRequest request) throws IOException {
		String codp=request.getParameter("xcodp");
		List<?> Promocion = this.adminManager.CatalogoSinPromocion();
		model.addAttribute("xlistaP", Promocion);
		List<?> carrtio = this.adminManager.carritoDeComprasVerificando(codp);
		model.addAttribute("xplista", carrtio);
		return "Cliente/ProductosConPromocion";
	}
	// todos estado 0 a carrito de compras
	@RequestMapping("Cambio_Estado_Carrito.html")
	public @ResponseBody String Cambio_Estado_Carrito(HttpServletRequest request)
			throws IOException, NumberFormatException, ParseException {	
		String id_catalogo = request.getParameter("id_catalogo_sp");
		String codp = request.getParameter("id_codp_sp");
		String categoria = request.getParameter("categoria_sp");
		String nombre = request.getParameter("nombre_sp");
		String tamano = request.getParameter("tamano_sp");
		String foto = request.getParameter("foto_sp");
	
		String precio = request.getParameter("precio_sp");
	
		System.out.println(id_catalogo+" "+codp+" "+categoria+" "+nombre+" "+tamano+" "+foto+" "+precio);
		Map<String, Object> cambiot = this.adminManager.cambio_estado_Carrito(id_catalogo.toUpperCase());
			if (cambiot.get(1) == null) {
				Map<String, Object> insertandoDatos = this.adminManager.insertCarito(id_catalogo.toUpperCase(),codp,categoria.toUpperCase()
						,nombre.toUpperCase(),tamano.toUpperCase(),foto,
						Integer.parseInt(precio));
				if (insertandoDatos.get(1) == null)	return "Agregado";
				else return "Error";				
			} else {
				return "Error";
			}
	}
	/////carrito de compras
	@RequestMapping("Carrito_De_Compras.html")
	public String Carrito_De_Compras(Model model,HttpServletRequest request) throws IOException {
		String codp=request.getParameter("xcodp");
		List<?> vercarrito = this.adminManager.carritoDeCompras(codp);
		model.addAttribute("xlista", vercarrito);
		List<?> newlista2 = this.adminManager.contarRegistros();
		model.addAttribute("xxlista", newlista2);
		return "Cliente/CarritoDeCompras";
	}
	///recibiendo datos de carrito de compras de borrar
	@RequestMapping("EstadoYcarritoCompras.html")
	public @ResponseBody String EstadoYcarritoCompras(HttpServletRequest request)
			throws IOException, NumberFormatException, ParseException {	
		String id_catalogo = request.getParameter("xid_catalogo");
		String id_carrito = request.getParameter("xid_carrito");		
		String codp = request.getParameter("xcodp");
	
		System.out.println(id_catalogo+" "+id_carrito+" "+codp);
		Map<String, Object> cambiot = this.adminManager.estadoCambio_Catalogo1(id_catalogo.toUpperCase());
			if (cambiot.get(1) == null) {
				Map<String, Object> insertandoDatos = this.adminManager.eliminarDeCarrito(Integer.parseInt(id_carrito),id_catalogo.toUpperCase(),codp);
				if (insertandoDatos.get(1) == null)	return "Agregado";
				else return "Error";				
			} else {
				return "Error";
			}
	}
	///Usuario modal mostrando
	@RequestMapping("RegistroUsuario.html")
	public String RegistroUsuario(Model model) throws IOException {		
		return "RegistroDeUsuarios";
	}
	
	///bien o no usuario
	@RequestMapping("buscarUsuario.html")
	public @ResponseBody String x(HttpServletRequest request)
			throws IOException, NumberFormatException, ParseException {	
		String busqueda=request.getParameter("buscador");
		//System.out.println(busqueda);
		int res = this.adminManager.usuarioNuevo(busqueda);
		if(res==1){
			return "1";
		}else{
			return "0";
		}
		
	}
	///bien o no codp
		@RequestMapping("buscarUsuarioCop.html")
		public @ResponseBody String buscarUsuarioCop(HttpServletRequest request)
				throws IOException, NumberFormatException, ParseException {	
			String busqueda=request.getParameter("buscador");
			//System.out.println(busqueda);
			int res = this.adminManager.usuarioCodp(busqueda);
			if(res==1){
				return "1";
			}else{
				return "0";
			}
			
		}
	///new registro de usuario
	@RequestMapping("DatosNewRegistro.html")
	public @ResponseBody String DatosNewRegistro(HttpServletRequest request)
			throws IOException, NumberFormatException, ParseException {	
		String codp = request.getParameter("r_codp");
		String nombre = request.getParameter("r_nombre");
		String ap = request.getParameter("r_ap");
		String am = request.getParameter("r_am");
		String fecha = request.getParameter("r_fecha");
		String direccion = request.getParameter("r_direccion");
		String telefono = request.getParameter("r_telefono");
		String usuario = request.getParameter("r_usuario");
		String contraseña = request.getParameter("r_contraseña1");
		
		Map<String, Object> cambiot = this.adminManager.personalInsertando(codp, nombre.toUpperCase(),ap.toUpperCase(),am.toUpperCase(),fecha,direccion.toUpperCase(),telefono.toUpperCase());
			if (cambiot.get(1) == null) {
				Map<String, Object> insertandoDatos = this.adminManager.usuarioTabla(codp,usuario,contraseña);
				if (insertandoDatos.get(1) == null)	return "Agregado";
				else return "Error";				
			} else {
				return "Error";
			}
	}
	///google
	@RequestMapping("userHome.html")
	public String userHome(Model model) throws IOException {	
		 model.addAttribute("user", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		return "RegistroDeUsuarios";
	}
	///mostrando formulario de referencia de entrega
	@RequestMapping("DatosDeReferencia.html")
	public String DatosDeReferencia(Model model) throws IOException {	
		return "Cliente/ReferenciaDeEntrega";
	}
	/////menu 2
	@RequestMapping("CatalogoSinPromocionPUBLICO.html")
	public String CatalogoSinPromocionPUBLICO(Model model,HttpServletRequest request) throws IOException {
		List<?> Promocion = this.adminManager.CatalogoSinPromocion();
		model.addAttribute("xlistaP", Promocion);
		return "publico/Catalogo_Sin_Promocion";
	}
	/// se recepciona losdatos de una venta
	@RequestMapping("DatosPedidos.html")
	public @ResponseBody String DatosPedidos(HttpServletRequest request, ServletResponse response)
			throws IOException, NumberFormatException, ParseException {
		// String codigo = request.getParameter("ventas");
		System.out.println("datos de la venta llegados al servidor: ");
		
		String c_id_carrito[] = request.getParameterValues("c_id_carrito[]");
		String c_categorias[] = request.getParameterValues("c_categoria[]");
		String c_nombres[] = request.getParameterValues("c_nombre[]");
		String c_tamanos[] = request.getParameterValues("c_tamano[]");
		String c_preciou[] = request.getParameterValues("c_preciou[]");
		String c_venta[] = request.getParameterValues("c_venta[]");
		String c_ventasub[] = request.getParameterValues("c_ventasubtotal[]");
		
		String ventatotal = request.getParameter("valor");		
		String comprador = request.getParameter("e_nombre");
		String direccion = request.getParameter("e_direccion");
		String telefono = request.getParameter("e_telefono");
		String gps = request.getParameter("e_gpss");
		int cantid = c_id_carrito.length;
		
		// int cantpedidos = pedidos.length;
		String resp = "";
		String text = "";
		int cont=1;
		for (int i = 0; i <= cantid-1; i++) {
			String id = c_id_carrito[i];
			String categoria = c_categorias[i];
			String nombre = c_nombres[i];
			String tamano = c_tamanos[i];
			String precio = c_preciou[i];
			String ventav = c_venta[i];
			String ventas = c_ventasub[i];
			/*System.out.println(id + " " + categoria + " " + nombre + " " + tamano + " " + precio + " " + ventav + "u "
					+ ventas + "bs");*/
			text = text + " "+cont+"   " + categoria + "   " + nombre + "   " + tamano + "   Precio :" + precio + "   " + ventav + " Unidades   "
					+ ventas + "Bs  \n\n";
	// haciendo el descuento de productos vendidos alinventario
		/*	try {
				this.adminManager.DesInventario(id.toUpperCase(),Integer.parseInt(ventav));
				resp="exito";
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e);
				resp="Error";
			}*/
			cont++;
		}
		System.out.println("comprador: " + comprador);
		System.out.println("datos: \n"+text);		
		System.out.println("venta total: " + ventatotal);		
		System.out.println("telefono : " + telefono);
		System.out.println("direccion : " + direccion);
		System.out.println("Gps : " + gps);
		///gardando registro completo 
	
		try {
			this.adminManager.pedidosdeclientes(comprador.toUpperCase(),direccion.toUpperCase(),Integer.parseInt(telefono),gps, text,Integer.parseInt(ventatotal));
			resp="exito";
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			resp="Error";
		}		
		return resp;
	}
	
	
}
