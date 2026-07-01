package com.codewise.facturaexpress.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Entidad que almacena la configuracion de la empresa
@Entity
@Table(name = "configuracion_empresa")
public class ConfiguracionEmpresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nit;

    @Column(name = "razon_social")
    private String razonSocial;

    @Column(name = "email_facturacion")
    private String emailFacturacion;

    private String telefono;
    private String direccion;

    @Column(name = "resolucion_dian")
    private String resolucionDian;

    @Column(name = "certificado_vence")
    private LocalDate certificadoVence;

    @Column(name = "notif_email")
    private boolean notifEmail;

    @Column(name = "notif_push")
    private boolean notifPush;

    @Column(name = "alertas_dian")
    private boolean alertasDian;

    @Column(name = "recordatorios")
    private boolean recordatorios;

    private LocalDateTime actualizada;

    // Actualiza la fecha de modificacion antes de persistir o actualizar
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.actualizada = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
    public String getEmailFacturacion() { return emailFacturacion; }
    public void setEmailFacturacion(String emailFacturacion) { this.emailFacturacion = emailFacturacion; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getResolucionDian() { return resolucionDian; }
    public void setResolucionDian(String resolucionDian) { this.resolucionDian = resolucionDian; }
    public LocalDate getCertificadoVence() { return certificadoVence; }
    public void setCertificadoVence(LocalDate certificadoVence) { this.certificadoVence = certificadoVence; }
    public boolean isNotifEmail() { return notifEmail; }
    public void setNotifEmail(boolean notifEmail) { this.notifEmail = notifEmail; }
    public boolean isNotifPush() { return notifPush; }
    public void setNotifPush(boolean notifPush) { this.notifPush = notifPush; }
    public boolean isAlertasDian() { return alertasDian; }
    public void setAlertasDian(boolean alertasDian) { this.alertasDian = alertasDian; }
    public boolean isRecordatorios() { return recordatorios; }
    public void setRecordatorios(boolean recordatorios) { this.recordatorios = recordatorios; }
    public LocalDateTime getActualizada() { return actualizada; }
    public void setActualizada(LocalDateTime actualizada) { this.actualizada = actualizada; }
}
