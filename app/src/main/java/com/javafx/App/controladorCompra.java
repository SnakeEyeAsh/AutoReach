package com.javafx.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.javafx.Model.CocheCompra;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class controladorCompra implements Initializable{

    // Coche seleccionado para la compra
    private static CocheCompra cocheSeleccionado = null;

    @FXML
    private Button btnDescargarPDF;

    @FXML
    private Button btnDescargarInfoVehiculo;

    @FXML
    private Button btnRealizarCompra;

    @FXML
    private Button btnRevetirCompra;

    @FXML
    private TextField txtBanco;

    @FXML
    private TextField txtCInicial;

    @FXML
    private TextField txtCfinal;

    @FXML
    private TextField txtCuota;

    @FXML
    private TextField txtIban;

    @FXML
    private TextField txtTitular;

    /**
     * Establece el coche seleccionado para la compra
     * @param coche El coche seleccionado desde la tabla principal
     */
    public static void setCocheSeleccionado(CocheCompra coche) {
        cocheSeleccionado = coche;
    }

    /**
     * Obtiene el coche seleccionado actual
     * @return El coche seleccionado o null si no hay ninguno
     */
    public static CocheCompra getCocheSeleccionado() {
        return cocheSeleccionado;
    }

    @FXML
    void efectuarCompra(ActionEvent event) {
        AlertaEstilizada.mostrarInformacion(
            "Funcion en desarrollo",
            "Proximamente",
            "Esta funcionalidad estara disponible en una version futura"
        );
    }

    @FXML
    void revertirCompra(ActionEvent event) {
        AlertaEstilizada.mostrarInformacion(
            "Funcion en desarrollo",
            "Proximamente",
            "Esta funcionalidad estara disponible en una version futura"
        );
    }

    @FXML
    void descargarInformeVehiculo(ActionEvent event) {
        // Validar que hay un coche seleccionado
        if (cocheSeleccionado == null) {
            AlertaEstilizada.mostrarAdvertencia(
                "Sin vehiculo seleccionado",
                "Seleccione un vehiculo",
                "Debe seleccionar un vehiculo de la tabla antes de descargar el informe."
            );
            return;
        }

        try {
            // Obtener datos del vehiculo
            String matricula = cocheSeleccionado.getMatricula() != null ? cocheSeleccionado.getMatricula() : "-";
            String marca = cocheSeleccionado.getMarca() != null ? cocheSeleccionado.getMarca() : "-";
            String modelo = cocheSeleccionado.getModelo() != null ? cocheSeleccionado.getModelo() : "-";
            String ano = cocheSeleccionado.getAño() != null ? cocheSeleccionado.getAño() : "-";
            String color = cocheSeleccionado.getColor() != null ? cocheSeleccionado.getColor() : "-";
            String precio = cocheSeleccionado.getPrecio() != null ? cocheSeleccionado.getPrecio() : "-";
            String diametroLlantas = cocheSeleccionado.getDiametroLlantas() != null ? cocheSeleccionado.getDiametroLlantas() : "-";
            String disenoRuedas = cocheSeleccionado.getDiseñoRuedas() != null ? cocheSeleccionado.getDiseñoRuedas() : "-";
            String interior = cocheSeleccionado.getInterior() != null ? cocheSeleccionado.getInterior() : "-";
            String material = cocheSeleccionado.getMaterial() != null ? cocheSeleccionado.getMaterial() : "-";

            // Construir HTML del informe
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html xmlns='http://www.w3.org/1999/xhtml'><head><meta charset='UTF-8' /><style>");
            html.append("@media print { body { -webkit-print-color-adjust: exact !important; print-color-adjust: exact !important; } }");
            html.append("body { font-family: 'Segoe UI', Arial, sans-serif; margin: 0; padding: 20px; background: #f5f5f5; }");
            html.append(".header { background: linear-gradient(135deg, #1E3A8A, #3B82F6); color: white; padding: 25px; border-radius: 10px; margin-bottom: 20px; }");
            html.append(".header h1 { margin: 0 0 10px 0; font-size: 22px; }");
            html.append(".header .matricula { font-size: 32px; font-weight: bold; margin: 10px 0; }");
            html.append(".header .fecha { font-size: 12px; opacity: 0.9; text-align: right; }");
            html.append(".titulo-vehiculo { text-align: center; color: #1E3A8A; font-size: 24px; font-weight: bold; margin: 20px 0; }");
            html.append(".linea { border-bottom: 2px solid #E5E7EB; width: 200px; margin: 0 auto 20px auto; }");
            html.append(".section { background: white; border-radius: 10px; padding: 20px; margin-bottom: 20px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }");
            html.append(".section-bar { background: #F9FAFB; border-left: 4px solid #F97316; border-radius: 8px; padding: 20px; margin-bottom: 20px; }");
            html.append(".section h2 { color: #1E3A8A; font-size: 14px; margin: 0 0 15px 0; }");
            html.append(".grid { display: flex; flex-wrap: wrap; gap: 20px; }");
            html.append(".campo { flex: 1; min-width: 200px; }");
            html.append(".campo-label { color: #6B7280; font-size: 11px; font-weight: bold; margin-bottom: 5px; }");
            html.append(".campo-valor { color: #1E3A8A; font-size: 14px; font-weight: bold; }");
            html.append(".specs-container { display: flex; gap: 15px; margin: 20px 0; }");
            html.append(".spec-box { flex: 1; background: #F0F9FF; border-radius: 8px; padding: 15px; text-align: center; }");
            html.append(".spec-label { color: #6B7280; font-size: 10px; font-weight: bold; }");
            html.append(".spec-valor { color: #0369A1; font-size: 18px; font-weight: bold; margin-top: 8px; }");
            html.append(".precio-container { background: #DBEAFE; border-radius: 10px; padding: 20px; text-align: center; margin: 20px auto; max-width: 350px; }");
            html.append(".precio-label { color: #6B7280; font-size: 11px; font-weight: bold; }");
            html.append(".precio-valor { color: #1E3A8A; font-size: 28px; font-weight: bold; margin-top: 10px; }");
            html.append(".footer { text-align: center; color: #6B7280; font-size: 10px; margin-top: 30px; padding-top: 15px; border-top: 1px solid #E5E7EB; }");
            html.append("</style></head><body>");

            // Header
            html.append("<div class='header'>");
            html.append("<div class='fecha'>Fecha: ").append(LocalDate.now()).append("</div>");
            html.append("<h1>Ficha del Vehiculo</h1>");
            html.append("<div class='matricula'>").append(matricula).append("</div>");
            html.append("</div>");

            // Titulo vehiculo
            html.append("<div class='titulo-vehiculo'>").append(marca).append(" ").append(modelo).append("</div>");
            html.append("<div class='linea'></div>");

            // Datos generales
            html.append("<div class='section-bar'>");
            html.append("<div class='grid'>");
            html.append("<div class='campo'><div class='campo-label'>MARCA</div><div class='campo-valor'>").append(marca).append("</div></div>");
            html.append("<div class='campo'><div class='campo-label'>MODELO</div><div class='campo-valor'>").append(modelo).append("</div></div>");
            html.append("</div>");
            html.append("<div class='grid' style='margin-top: 15px;'>");
            html.append("<div class='campo'><div class='campo-label'>ANO</div><div class='campo-valor'>").append(ano).append("</div></div>");
            html.append("<div class='campo'><div class='campo-label'>COLOR</div><div class='campo-valor'>").append(color).append("</div></div>");
            html.append("</div>");
            html.append("</div>");

            // Especificaciones tecnicas
            html.append("<div class='section'>");
            html.append("<h2>ESPECIFICACIONES TECNICAS</h2>");
            html.append("<div class='specs-container'>");
            html.append("<div class='spec-box'><div class='spec-label'>DIAMETRO LLANTAS</div><div class='spec-valor'>").append(diametroLlantas).append(" pulg</div></div>");
            html.append("<div class='spec-box'><div class='spec-label'>DISENO RUEDAS</div><div class='spec-valor'>").append(disenoRuedas).append("</div></div>");
            html.append("<div class='spec-box'><div class='spec-label'>INTERIOR</div><div class='spec-valor'>").append(interior).append("</div></div>");
            html.append("</div>");
            html.append("<div class='campo' style='margin-top: 15px;'><div class='campo-label'>MATERIAL</div><div class='campo-valor'>").append(material).append("</div></div>");
            html.append("</div>");

            // Precio
            html.append("<div class='precio-container'>");
            html.append("<div class='precio-label'>PRECIO DE VENTA</div>");
            html.append("<div class='precio-valor'>").append(precio).append(" EUR</div>");
            html.append("</div>");

            // Footer
            html.append("<div class='footer'>AutoReach - Sistema de Gestion de Vehiculos</div>");
            html.append("</body></html>");

            // Guardar automaticamente en la carpeta Informer
            String nombreArchivo = "informe_vehiculo_" + matricula + "_" + LocalDate.now() + ".pdf";
            File carpetaInformer = new File(System.getProperty("user.dir"), "Informer");
            if (!carpetaInformer.exists()) {
                carpetaInformer.mkdirs();
            }
            File archivoDestino = new File(carpetaInformer, nombreArchivo);

            // Generar PDF usando OpenHTMLToPDF
            try (OutputStream os = new FileOutputStream(archivoDestino)) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                builder.withHtmlContent(html.toString(), null);
                builder.toStream(os);
                builder.run();
            }

            AlertaEstilizada.mostrarInformacion(
                "Informe exportado",
                "Exito",
                "El informe del vehiculo se ha guardado correctamente.\n\n" + archivoDestino.getAbsolutePath()
            );

        } catch (Exception e) {
            AlertaEstilizada.mostrarError(
                "Error al exportar informe",
                "No se pudo exportar el informe",
                "Error: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    @FXML
    void descargarInformePDF(ActionEvent event) {
        try {
            String banco = txtBanco.getText();
            String iban = txtIban.getText();
            String titular = txtTitular.getText();
            String cuotaInicial = txtCInicial.getText();
            String cuotaFinal = txtCfinal.getText();
            String cuotaMensual = txtCuota.getText();

            // Validar que hay datos para generar el informe
            if (banco.isEmpty() && iban.isEmpty() && titular.isEmpty()) {
                AlertaEstilizada.mostrarAdvertencia(
                    "Datos incompletos",
                    "Sin datos",
                    "Por favor, rellene al menos los datos del banco, IBAN y titular."
                );
                return;
            }

            // Preparar parametros para el informe Jasper
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("FECHA", LocalDate.now().toString());
            parametros.put("BANCO", banco);
            parametros.put("IBAN", iban);
            parametros.put("TITULAR", titular);
            parametros.put("CUOTA_INICIAL", cuotaInicial);
            parametros.put("CUOTA_FINAL", cuotaFinal);
            parametros.put("CUOTA_MENSUAL", cuotaMensual);

            // Cargar y compilar el informe desde .jrxml
            InputStream reportStream = getClass().getResourceAsStream("/informes/informe_compra.jrxml");

            if (reportStream == null) {
                AlertaEstilizada.mostrarError(
                    "Error",
                    "Plantilla no encontrada",
                    "No se encontro el archivo de plantilla del informe."
                );
                return;
            }

            // Compilar el .jrxml a JasperReport
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // Llenar el informe con los parametros (sin datasource, usamos JREmptyDataSource)
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, new JREmptyDataSource());

            // Guardar automaticamente en la carpeta Informer
            String nombreArchivo = "informe_compra_" + LocalDate.now() + ".pdf";
            File carpetaInformer = new File(System.getProperty("user.dir"), "Informer");
            if (!carpetaInformer.exists()) {
                carpetaInformer.mkdirs();
            }
            File archivoDestino = new File(carpetaInformer, nombreArchivo);

            // Exportar a PDF
            JasperExportManager.exportReportToPdfFile(jasperPrint, archivoDestino.getAbsolutePath());

            AlertaEstilizada.mostrarInformacion(
                "Informe exportado",
                "Exito",
                "El informe PDF se ha guardado correctamente.\n\n" + archivoDestino.getAbsolutePath()
            );

        } catch (Exception e) {
            AlertaEstilizada.mostrarError(
                "Error al exportar informe",
                "No se pudo exportar el informe",
                "Error: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
         Platform.runLater(()->{
            Stage s = (Stage)btnRealizarCompra.getScene().getWindow();
            s.getIcons().add(new Image("Assets/dar-dinero.png"));
        });
    }

}
