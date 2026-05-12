package com.javafx.App;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.javafx.Model.CocheCompra;

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

        // Usar la clase utilitaria para generar el informe
        java.io.File archivoDestino = GeneradorInformes.exportarInformeVehiculoPDF(cocheSeleccionado);

        if (archivoDestino != null) {
            AlertaEstilizada.mostrarInformacion(
                "Informe exportado",
                "Exito",
                "El informe del vehiculo se ha exportado correctamente.\n\nUbicación del archivo:\n" + archivoDestino.getAbsolutePath()
            );
        } else {
            AlertaEstilizada.mostrarError(
                "Error al exportar informe",
                "No se pudo exportar el informe",
                "Ocurrio un error al generar el PDF. Revise los logs para mas detalles."
            );
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
            File carpetaInformer = new File(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "AutoReach", "Informer");
            if (!carpetaInformer.exists()) {
                carpetaInformer.mkdirs();
            }
            File archivoDestino = new File(carpetaInformer, nombreArchivo);

            // Exportar a PDF
            JasperExportManager.exportReportToPdfFile(jasperPrint, archivoDestino.getAbsolutePath());

            AlertaEstilizada.mostrarInformacion(
                "Informe exportado",
                "Exito",
                "El informe PDF se ha exportado correctamente.\n\nUbicación del archivo:\n" + archivoDestino.getAbsolutePath()
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
            try {
                if (btnRealizarCompra.getScene() != null && btnRealizarCompra.getScene().getWindow() != null) {
                    Stage s = (Stage)btnRealizarCompra.getScene().getWindow();
                    s.getIcons().add(new Image("Assets/dar-dinero.png"));
                }
            } catch (Exception e) {
                System.err.println("No se pudo aplicar el icono: " + e.getMessage());
            }
        });
    }

}
