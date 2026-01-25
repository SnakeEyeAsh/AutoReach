package com.javafx.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;

import com.javafx.Model.CocheCompra;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

/**
 * Clase utilitaria para la generacion de informes PDF de vehiculos.
 * Centraliza la logica de generacion para evitar codigo duplicado.
 */
public class GeneradorInformes {

    private GeneradorInformes() {
        // Constructor privado para evitar instanciacion
    }

    /**
     * Genera el HTML del informe de un vehiculo
     * @param coche El coche del que generar el informe
     * @return String con el HTML del informe
     */
    public static String generarHtmlInformeVehiculo(CocheCompra coche) {
        if (coche == null) {
            return "<html><body><p>Error: No hay vehiculo seleccionado</p></body></html>";
        }

        // Obtener datos del vehiculo con valores por defecto
        String matricula = valorSeguro(coche.getMatricula());
        String marca = valorSeguro(coche.getMarca());
        String modelo = valorSeguro(coche.getModelo());
        String ano = valorSeguro(coche.getAño());
        String color = valorSeguro(coche.getColor());
        String precio = valorSeguro(coche.getPrecio());
        String diametroLlantas = valorSeguro(coche.getDiametroLlantas());
        String disenoRuedas = valorSeguro(coche.getDiseñoRuedas());
        String interior = valorSeguro(coche.getInterior());
        String material = valorSeguro(coche.getMaterial());

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

        return html.toString();
    }

    /**
     * Exporta el informe de un vehiculo a PDF
     * @param coche El coche del que generar el informe
     * @return El archivo generado o null si hubo error
     */
    public static File exportarInformeVehiculoPDF(CocheCompra coche) {
        if (coche == null) {
            return null;
        }

        try {
            String html = generarHtmlInformeVehiculo(coche);
            String matricula = valorSeguro(coche.getMatricula());

            // Crear nombre de archivo
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
                builder.withHtmlContent(html, null);
                builder.toStream(os);
                builder.run();
            }

            return archivoDestino;

        } catch (Exception e) {
            System.err.println("Error al exportar informe de vehiculo: " + e.getMessage());
            return null;
        }
    }

    /**
     * Retorna un valor seguro para mostrar (evita nulls)
     * @param valor El valor a verificar
     * @return El valor o "-" si es null/vacio
     */
    private static String valorSeguro(String valor) {
        return (valor != null && !valor.trim().isEmpty()) ? valor : "-";
    }

    /**
     * Metodo de compatibilidad para obtener el ano del coche
     * Maneja tanto getAño() como getAno()
     */
    private static String valorSeguro(CocheCompra coche, String campo) {
        try {
            switch (campo) {
                case "ano":
                    return valorSeguro(coche.getAño());
                case "disenoRuedas":
                    return valorSeguro(coche.getDiseñoRuedas());
                default:
                    return "-";
            }
        } catch (Exception e) {
            return "-";
        }
    }
}
