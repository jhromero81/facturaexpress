package com.codewise.facturaexpress;

import com.codewise.facturaexpress.controller.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.tomcat.TomcatWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * Punto de entrada principal. Arranca Spring Boot y registra los
 * servlets Jakarta EE como beans para el manejo de peticiones HTTP.
 */
@SpringBootApplication
public class FacturaexpressApplication {

	/**
	 * Inicia la aplicación Spring Boot embebida.
	 */
	public static void main(String[] args) {
		SpringApplication.run(FacturaexpressApplication.class, args);
	}

	/**
	 * Registra el servlet de clientes en la ruta /clientes.
	 */
	@Bean
	public ServletRegistrationBean<ClienteServlet> clienteServlet() {
		ServletRegistrationBean<ClienteServlet> bean = new ServletRegistrationBean<>(new ClienteServlet(), "/clientes");
		bean.setLoadOnStartup(1);
		return bean;
	}

	/**
	 * Registra el servlet de productos en la ruta /productos.
	 */
	@Bean
	public ServletRegistrationBean<ProductoServlet> productoServlet() {
		ServletRegistrationBean<ProductoServlet> bean = new ServletRegistrationBean<>(new ProductoServlet(), "/productos");
		bean.setLoadOnStartup(1);
		return bean;
	}

	/**
	 * Registra el servlet de facturas en la ruta /facturas.
	 */
	@Bean
	public ServletRegistrationBean<FacturaServlet> facturaServlet() {
		ServletRegistrationBean<FacturaServlet> bean = new ServletRegistrationBean<>(new FacturaServlet(), "/facturas");
		bean.setLoadOnStartup(1);
		return bean;
	}

	/**
	 * Registra el servlet de login en la ruta /login.
	 */
	@Bean
	public ServletRegistrationBean<LoginServlet> loginServlet() {
		ServletRegistrationBean<LoginServlet> bean = new ServletRegistrationBean<>(new LoginServlet(), "/login");
		bean.setLoadOnStartup(1);
		return bean;
	}

	/**
	 * Registra el servlet de logout en la ruta /logout.
	 */
	@Bean
	public ServletRegistrationBean<LogoutServlet> logoutServlet() {
		return new ServletRegistrationBean<>(new LogoutServlet(), "/logout");
	}

	/**
	 * Registra el servlet del dashboard en la ruta /dashboard.
	 */
	@Bean
	public ServletRegistrationBean<DashboardServlet> dashboardServlet() {
		ServletRegistrationBean<DashboardServlet> bean = new ServletRegistrationBean<>(new DashboardServlet(), "/dashboard");
		bean.setLoadOnStartup(1);
		return bean;
	}

	/**
	 * Registra el servlet de ventas en la ruta /ventas.
	 */
	@Bean
	public ServletRegistrationBean<VentaServlet> ventaServlet() {
		ServletRegistrationBean<VentaServlet> bean = new ServletRegistrationBean<>(new VentaServlet(), "/ventas");
		bean.setLoadOnStartup(1);
		return bean;
	}

	/**
	 * Registra el servlet de reportes en la ruta /reportes.
	 */
	@Bean
	public ServletRegistrationBean<ReportesServlet> reportesServlet() {
		ServletRegistrationBean<ReportesServlet> bean = new ServletRegistrationBean<>(new ReportesServlet(), "/reportes");
		bean.setLoadOnStartup(1);
		return bean;
	}

	/**
	 * Configura el contenedor Tomcat embebido para que sirva index.jsp
	 * como welcome file en la ra&iacute;z (http://localhost:8080/).
	 */
	@Bean
	public WebServerFactoryCustomizer<TomcatWebServerFactory> welcomePageCustomizer() {
		return factory -> factory.addContextCustomizers(
				context -> context.addWelcomeFile("index.jsp")
		);
	}
}
