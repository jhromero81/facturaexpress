package com.codewise.facturaexpress;

// Importaciones de controladores y servicios de la aplicación
import com.codewise.facturaexpress.controller.*;
import com.codewise.facturaexpress.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

// Clase principal que inicia la aplicación Spring Boot
@SpringBootApplication
public class FacturaexpressApplication {

	// Metodo principal punto de entrada de la aplicación
	public static void main(String[] args) {
		SpringApplication.run(FacturaexpressApplication.class, args);
	}

	// Bean que registra el servlet de clientes en la ruta /clientes
	@Bean
	public ServletRegistrationBean<ClienteServlet> clienteServlet(
			ClienteService clienteService, LogAuditoriaService logService) {
		ServletRegistrationBean<ClienteServlet> bean =
				new ServletRegistrationBean<>(new ClienteServlet(clienteService, logService), "/clientes");
		bean.setLoadOnStartup(1);
		return bean;
	}

	// Bean que registra el servlet de productos en la ruta /productos
	@Bean
	public ServletRegistrationBean<ProductoServlet> productoServlet(
			ProductoService productoService, LogAuditoriaService logService) {
		ServletRegistrationBean<ProductoServlet> bean =
				new ServletRegistrationBean<>(new ProductoServlet(productoService, logService), "/productos");
		bean.setLoadOnStartup(1);
		return bean;
	}

	// Bean que registra el servlet de facturas en la ruta /facturas
	@Bean
	public ServletRegistrationBean<FacturaServlet> facturaServlet(
			FacturaService facturaService, ClienteService clienteService,
			ProductoService productoService, LogAuditoriaService logService) {
		ServletRegistrationBean<FacturaServlet> bean =
				new ServletRegistrationBean<>(new FacturaServlet(facturaService, clienteService, productoService, logService), "/facturas");
		bean.setLoadOnStartup(1);
		return bean;
	}

	// Bean que registra el servlet de inicio de sesión en la ruta /login
	@Bean
	public ServletRegistrationBean<LoginServlet> loginServlet(LoginService loginService) {
		ServletRegistrationBean<LoginServlet> bean =
				new ServletRegistrationBean<>(new LoginServlet(loginService), "/login");
		bean.setLoadOnStartup(1);
		return bean;
	}

	// Bean que registra el servlet de cierre de sesión en la ruta /logout
	@Bean
	public ServletRegistrationBean<LogoutServlet> logoutServlet() {
		return new ServletRegistrationBean<>(new LogoutServlet(), "/logout");
	}

	// Bean que registra el servlet del panel principal en la ruta /dashboard
	@Bean
	public ServletRegistrationBean<DashboardServlet> dashboardServlet(ReportesService reportesService) {
		ServletRegistrationBean<DashboardServlet> bean =
				new ServletRegistrationBean<>(new DashboardServlet(reportesService), "/dashboard");
		bean.setLoadOnStartup(1);
		return bean;
	}

	// Bean que registra el servlet de punto de venta en la ruta /ventas
	@Bean
	public ServletRegistrationBean<VentaServlet> ventaServlet(
			FacturaService facturaService, ClienteService clienteService,
			ProductoService productoService, LogAuditoriaService logService) {
		ServletRegistrationBean<VentaServlet> bean =
				new ServletRegistrationBean<>(new VentaServlet(facturaService, clienteService, productoService, logService), "/ventas");
		bean.setLoadOnStartup(1);
		return bean;
	}

	// Bean que registra el servlet de reportes en la ruta /reportes
	@Bean
	public ServletRegistrationBean<ReportesServlet> reportesServlet(
			ReportesService reportesService, ReporteService reporteService, PdfService pdfService) {
		ServletRegistrationBean<ReportesServlet> bean =
				new ServletRegistrationBean<>(new ReportesServlet(reportesService, reporteService, pdfService), "/reportes");
		bean.setLoadOnStartup(1);
		return bean;
	}

	// Bean que registra el servlet de configuración en la ruta /configuracion
	@Bean
	public ServletRegistrationBean<ConfiguracionServlet> configuracionServlet(
			ConfiguracionEmpresaService configService, LogAuditoriaService logService) {
		ServletRegistrationBean<ConfiguracionServlet> bean =
				new ServletRegistrationBean<>(new ConfiguracionServlet(configService, logService), "/configuracion");
		bean.setLoadOnStartup(1);
		return bean;
	}

	// Bean que registra el servlet de errores del sistema en la ruta /errores
	@Bean
	public ServletRegistrationBean<ErrorSistemaServlet> errorSistemaServlet(
			ErrorSistemaService errorService, LogAuditoriaService logService) {
		ServletRegistrationBean<ErrorSistemaServlet> bean =
				new ServletRegistrationBean<>(new ErrorSistemaServlet(errorService, logService), "/errores");
		bean.setLoadOnStartup(1);
		return bean;
	}

	// Bean que registra el servlet de logs de auditoria en la ruta /logs
	@Bean
	public ServletRegistrationBean<LogAuditoriaServlet> logAuditoriaServlet(LogAuditoriaService logService) {
		ServletRegistrationBean<LogAuditoriaServlet> bean =
				new ServletRegistrationBean<>(new LogAuditoriaServlet(logService), "/logs");
		bean.setLoadOnStartup(1);
		return bean;
	}

	// Bean que registra el servlet de administración de usuarios en la ruta /usuarios
	@Bean
	public ServletRegistrationBean<UserAdminServlet> userAdminServlet(
			UsuarioAdminService usuarioAdminService, LogAuditoriaService logService) {
		ServletRegistrationBean<UserAdminServlet> bean =
				new ServletRegistrationBean<>(new UserAdminServlet(usuarioAdminService, logService), "/usuarios");
		bean.setLoadOnStartup(1);
		return bean;
	}

	// Bean que registra el servlet de respaldo y restauración en la ruta /backup
	@Bean
	public ServletRegistrationBean<BackupRestoreServlet> backupRestoreServlet() {
		ServletRegistrationBean<BackupRestoreServlet> bean =
				new ServletRegistrationBean<>(new BackupRestoreServlet(), "/backup");
		bean.setLoadOnStartup(1);
		return bean;
	}

	// Bean que configura la página de bienvenida como index.jsp
	@Bean
	public WebServerFactoryCustomizer<TomcatServletWebServerFactory> welcomePageCustomizer() {
		return factory -> factory.addContextCustomizers(
				context -> context.addWelcomeFile("index.jsp")
		);
	}
}
