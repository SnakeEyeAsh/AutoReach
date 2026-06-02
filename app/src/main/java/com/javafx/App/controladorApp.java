package com.javafx.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.util.Duration;

import javafx.animation.TranslateTransition;

import com.javafx.Model.Agente;
import com.javafx.Model.Alquiler;
import com.javafx.Model.Cliente;
import com.javafx.Model.Coche;
import com.javafx.Model.CocheCompleto;
import com.javafx.Model.CocheCompra;
import com.javafx.Model.Disponible;
import com.javafx.Model.ValidadorAgente;
import com.javafx.Model.ValidadorAlquiler;
import com.javafx.Service.GeminiService;
import com.javafx.Model.ValidadorCliente;
import com.javafx.JDBC.Conexion;
import com.javafx.Model.ValidadorCoche;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class controladorApp implements Initializable {

    private Pane panelActual;

    private Cliente cliente;

    /** Coche seleccionado en la tabla de compras; lo lee controladorEmergente. */
    static CocheCompra cocheParaComprar;

    private Boolean editarCliente = false;

    private boolean editandoAlquiler = false;

    private boolean sesionActiva = false;

    private String matriculaSeleccionada = null;
    private final ScheduledExecutorService refreshScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "matriculas-refresh");
        t.setDaemon(true);
        return t;
    });
    private ScheduledFuture<?> refreshTask = null;

    // Backing lists para los ComboBox de modelo (evita acumular listeners)
    private final ObservableList<String> modelosAlquilerData =
            FXCollections.observableArrayList();
    private final FilteredList<String> filteredModelosAlquiler =
            new FilteredList<>(modelosAlquilerData, p -> true);

    private final ObservableList<String> modelosCompraData =
            FXCollections.observableArrayList();
    private final FilteredList<String> filteredModelosCompra =
            new FilteredList<>(modelosCompraData, p -> true);

    @FXML
    private TextField ingresoAño;

    @FXML
    private TextField ingresoBastidor;

    @FXML
    private TextField ingresoColor;

    @FXML
    private TextField ingresoDiseñoLlantas;

    @FXML
    private TextField ingresoDllantas;

    @FXML
    private TextField ingresoKms;

    @FXML
    private TextField ingresoMarca;

    @FXML
    private TextField ingresoMatricula;

    @FXML
    private TextField ingresoModelo;

    @FXML
    private TextField ingresoPrecio;

    @FXML
    private MenuItem blanco;

    @FXML
    private MenuItem negro;

    @FXML
    private MenuItem azul;

    @FXML
    private MenuItem gris;

    @FXML
    private MenuButton mnMarcaCompra;

    @FXML
    private ComboBox<String> cmbModeloCompra;

    @FXML
    private TableView<CocheCompra> tblCochesCompra;

    @FXML
    private TableColumn<CocheCompra, String> colMatriculaCompra;

    @FXML
    private TableColumn<CocheCompra, String> colMarcaCompra;

    @FXML
    private TableColumn<CocheCompra, String> colModeloCompra;

    @FXML
    private TableColumn<CocheCompra, String> colAñoCompra;

    @FXML
    private TableColumn<CocheCompra, String> colPrecioCompra;

    @FXML
    private TableView<Disponible> TblCoincidencias;

    @FXML
    private TableColumn<Disponible, String> clnAñoCoche;

    @FXML
    private TableColumn<Disponible, String> clnColorCoche;

    @FXML
    private TableColumn<Disponible, String> clnDisponibleCoche;

    @FXML
    private TableColumn<Disponible, String> clnMatriculaCoche;

    @FXML
    private TableColumn<Disponible, String> clnPrecioCoche;

    @FXML
    private Button btnEliminarCliente;

    @FXML
    private Button btnInvitarCliente;

    @FXML
    private Button btnCerrarSesionCliente;

    @FXML
    private Button btnEditarCoche;

    @FXML
    private MenuButton mnElegirMarca;

    @FXML
    private ComboBox<String> cmbElegirModelo;

    @FXML
    private ComboBox<String> cmbEstadoPagoAlquiler;

    @FXML
    private ListView<Coche> listaCoincidencias;

    @FXML
    private TableView<Coche> TablaCoches;

    @FXML
    private BarChart<String, Number> chartCochesPorAño;

    @FXML
    private CategoryAxis xAxisAño;

    @FXML
    private NumberAxis yAxisAño;

    @FXML
    private RadioButton rbFemenino;

    @FXML
    private RadioButton rbMasculino;

    @FXML
    private ToggleGroup toggleSexo;

    @FXML
    private TableColumn<Coche, String> clnAño;

    @FXML
    private TableColumn<Coche, String> clnBastidor;

    @FXML
    private TableColumn<Coche, String> clnColor;

    @FXML
    private TableColumn<Coche, String> clnMarca;

    @FXML
    private TableColumn<Coche, String> clnModelo;

    @FXML
    private Button btnIrAIngreso;

    @FXML
    private Button btnAgentes;

    @FXML
    private Button btnAceptarIngreso;

    @FXML
    private Button btnCancelarIngresos;

    @FXML
    private Button btnEditarIngreso;

    @FXML
    private Button btnBorrarIngreso;

    @FXML
    private Button btnBorrarAlq;

    @FXML
    private Button btnEditarAlquiler;

    @FXML
    private Button btnCancelarAlquiler;

    @FXML
    private Button btnConirmarAlquiler;

    @FXML
    private Button btnAlquiler;

    @FXML
    private Button btnAñadirNuevoCliente;

    @FXML
    private Button btnCancelarEdicionIngreso;

    @FXML
    private Button btnVerReporte;

    @FXML
    private Button btnDescargarPDF;

    @FXML
    private Button btnBuscarCliente;

    @FXML
    private Button btnCancelarBCompra;

    @FXML
    private Button btnCancelarCompra;

    @FXML
    private WebView webViewInformeCompra;

    @FXML
    private Button btnVerInformeCompra;

    @FXML
    private Button btnDescargarInformeCompra;

    @FXML
    private Button btnCancelarNuevoCliente;

    @FXML
    private Button btnCancelarSeleccion;

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private Button btnCompra;

    @FXML
    private Button btnCompras;

    @FXML
    private Button btnInformeVehiculo;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnEditarCliente;

    @FXML
    private ImageView btnInicio;

    @FXML
    private Button btnInicioSesion;

    @FXML
    private Button btnIrAInicio;

    @FXML
    private Button btnNuevoCliente;

    @FXML
    private Button btnSeleccionCLiente;

    @FXML
    private DatePicker cldEntrega;

    @FXML
    private DatePicker cldRecoger;

    @FXML
    private TableColumn<Alquiler, String> clnDNIAlq;

    @FXML
    private TableColumn<Alquiler, String> clnFechaAlq;

    @FXML
    private TableColumn<Alquiler, String> clnHora;

    @FXML
    private TableColumn<Alquiler, String> clnHoraAlq;

    @FXML
    private TableColumn<Alquiler, String> clnMatricula;

    @FXML
    private TableColumn<Alquiler, String> clnNombre;

    @FXML
    private TableColumn<Alquiler, String> clnMatriculaAlq;

    @FXML
    private TableColumn<Cliente, String> clnDniRegistro;

    @FXML
    private TableColumn<Cliente, String> clnNombreRegistro;

    @FXML
    private Label lblAño;

    @FXML
    private Label lblEdad;

    @FXML
    private Label lblEmail;

    @FXML
    private Label lblKms;

    @FXML
    private Label lblMarca;

    @FXML
    private Label lblModelo;

    @FXML
    private Label lblNBastidor;

    @FXML
    private Label lblNombre;

    @FXML
    private Label lblNumero;

    @FXML
    private Label lblSexo;

    @FXML
    private Pane paneLogin;

    @FXML
    private AnchorPane panelLoginCliente;

    @FXML
    private AnchorPane panelNuevoCliente;
    /////////////////////////////////////////// PANELES //////////////////////////////////////

    @FXML
    private Pane panelRegistro;

    @FXML
    private Pane panelVentas;

    @FXML
    private Pane pantallaAlquiler;

    @FXML
    private Pane pantallaSesion;

    @FXML
    private Pane panelIngreso;

    @FXML
    private Pane panelAgentes;

    @FXML private Pane panelChat;
    @FXML private Button btnChat;
    @FXML private TextField txtMensajeChat;
    @FXML private VBox vboxMensajes;
    @FXML private ScrollPane scrollChat;
    @FXML private Label lblEstadoChat;
    @FXML private Button btnEnviarChat;
    @FXML private Button btnLimpiarChat;

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    @FXML
    private MenuButton sclColorCompra;

    @FXML
    private MenuButton slcMatriculaLogin;

    @FXML
    private ListView<String> tablaDeInformacion;

    @FXML
    private TableView<Cliente> tablaRegistro;

    @FXML
    private TableView<Alquiler> tblAlquiler;

    @FXML
    private TextField txtApellidos;

    @FXML
    private PasswordField pfContraseña;

    @FXML
    private TextField txtPasswordVisible;

    @FXML
    private Button btnMostrarPass;

    @FXML
    private TextField txtDireccion;

    @FXML
    private TextField txtDni;

    @FXML
    private ComboBox<String> comboDni;

    @FXML
    private TextField txtEmail;

    @FXML
    private DatePicker dpFechaNac;

    @FXML
    private TextField txtHoraEntrega;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtUser;

    @FXML
    private TextField txtAgenteUsername;

    @FXML
    private PasswordField pfAgentePassword;

    @FXML
    private TextField txtAgenteNombre;

    @FXML
    private TextField txtAgenteApellidos;

    @FXML
    private TextField txtAgenteTelefono;

    @FXML
    private TextField txtAgenteEmail;

    @FXML
    private TableView<Agente> tablaAgentes;

    @FXML
    private TableColumn<Agente, String> colAgenteUsuario;

    @FXML
    private TableColumn<Agente, String> colAgenteNombre;

    @FXML
    private TableColumn<Agente, String> colAgenteEmail;

    @FXML
    private Button btnGuardarAgente;

    @FXML
    private Button btnCancelarAgente;

    @FXML
    private Button btnEditarAgente;

    @FXML
    private Button btnEliminarAgente;

    @FXML
    private Button btnRefrescarAgentes;

    @FXML
    private Button btnVistaPreviaAgente;

    @FXML
    private Button btnDescargarPDFAgente;

    @FXML
    private VBox ventanaLogin;

    private ValidadorCliente validador;
    private ValidadorCoche validadorC;
    private ValidadorAlquiler validadorAlquiler;
    private ValidadorAgente validadorAgente;
    private Agente agenteEnEdicion = null;
    private final GeminiService geminiService = new GeminiService();
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        boolean conexionExitosa = Conexion.realizarConexion();

        if (!conexionExitosa) {
            // Mostrar alerta de error de conexión
            Platform.runLater(() -> {
                AlertaEstilizada.mostrarError(
                    "Error de conexión",
                    "No se pudo conectar a la base de datos",
                    "Revisa la configuración de la base de datos en Application.properties.\n\n" +
                    "Posibles causas:\n" +
                    "• Credenciales incorrectas\n" +
                    "• Base de datos no disponible\n" +
                    "• Problemas de red o firewall\n\n" +
                    "Verifica los logs de la consola para más detalles."
                );
            });
        }

        Platform.runLater(() -> {
            // Verificar que la escena esté disponible antes de acceder a ella
            if (btnIrAInicio.getScene() != null) {
                Stage s = (Stage) btnIrAInicio.getScene().getWindow();
                if (s != null) {
                    s.getIcons().add(new Image("Assets/auto-limpio.png"));

                    // Cargar hoja de estilos CSS
                    try {
                        URL cssURL = getClass().getResource("/styles.css");
                        if (cssURL != null) {
                            String css = cssURL.toExternalForm();
                            s.getScene().getStylesheets().add(css);
                            System.out.println("✓ CSS cargado exitosamente: " + css);
                        } else {
                            System.err.println("✗ No se encontró el archivo styles.css en resources");
                        }
                    } catch (Exception e) {
                        System.err.println("✗ Error al cargar styles.css: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } else {
                // Si la escena no está disponible aún, intentar de nuevo
                Platform.runLater(() -> {
                    if (btnIrAInicio.getScene() != null) {
                        Stage s = (Stage) btnIrAInicio.getScene().getWindow();
                        if (s != null) {
                            s.getIcons().add(new Image("Assets/auto-limpio.png"));

                            // Cargar hoja de estilos CSS
                            try {
                                URL cssURL = getClass().getResource("/styles.css");
                                if (cssURL != null) {
                                    String css = cssURL.toExternalForm();
                                    s.getScene().getStylesheets().add(css);
                                    System.out.println("✓ CSS cargado exitosamente: " + css);
                                } else {
                                    System.err.println("✗ No se encontró el archivo styles.css en resources");
                                }
                            } catch (Exception e) {
                                System.err.println("✗ Error al cargar styles.css: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
        validador = new ValidadorCliente();
        validadorC = new ValidadorCoche();
        validadorAlquiler = new ValidadorAlquiler();
        validadorAgente = new ValidadorAgente();

        validadorAlquiler.inicializarValidadores(cldRecoger, cldEntrega, txtHoraEntrega);
        validadorC.inicializarValidadores(ingresoMarca, ingresoModelo, ingresoAño, ingresoKms, ingresoBastidor,
                ingresoPrecio, ingresoDllantas, ingresoDiseñoLlantas, ingresoColor, ingresoMatricula);
        validador.inicializarValidadores(txtNombre, txtApellidos, txtDireccion, txtTelefono, dpFechaNac, txtDni,
                txtEmail, toggleSexo);

        validadorAgente.inicializarValidadores(txtAgenteUsername, txtAgenteNombre, txtAgenteApellidos,
                txtAgenteTelefono, txtAgenteEmail);

        // Configurar tooltip para el campo de hora de entrega
        Tooltip tooltipHora = new Tooltip("Formato: HH:mm (ejemplo: 14:30, 09:15)");
        txtHoraEntrega.setTooltip(tooltipHora);

        // Configurar tooltips para campos de cliente
        dpFechaNac.setTooltip(new Tooltip("Selecciona tu fecha de nacimiento"));
        txtEmail.setTooltip(new Tooltip("Formato: usuario@dominio.com (ejemplo: juan.perez@gmail.com)"));
        txtDireccion.setTooltip(new Tooltip("Dirección completa (ejemplo: Calle Mayor 123, Madrid)"));

        // Configurar tooltips para campos de ingreso de coches
        ingresoAño.setTooltip(new Tooltip("Año del vehículo (ejemplo: 2023)"));
        ingresoBastidor.setTooltip(new Tooltip("Número de bastidor VIN de 17 caracteres (ejemplo: 1HGBH41JXMN109186)"));
        ingresoColor.setTooltip(new Tooltip("Color del vehículo (ejemplo: Rojo, Azul Metalizado, Negro)"));
        ingresoDiseñoLlantas.setTooltip(new Tooltip("Diseño de llantas (ejemplo: Aleación, Radial, Deportiva)"));
        ingresoDllantas.setTooltip(new Tooltip("Diámetro de llantas en pulgadas (ejemplo: 16, 17, 18)"));
        ingresoKms.setTooltip(new Tooltip("Kilometraje del vehículo (ejemplo: 15000.5)"));
        ingresoMarca.setTooltip(new Tooltip("Marca del vehículo (ejemplo: Toyota, BMW, Ford)"));
        ingresoMatricula.setTooltip(new Tooltip("Matrícula del vehículo: 4 números + 3 letras (ejemplo: 1234ABC)"));
        ingresoModelo.setTooltip(new Tooltip("Modelo del vehículo (ejemplo: Corolla, Serie 3, Focus)"));
        ingresoPrecio.setTooltip(new Tooltip("Precio base del vehículo (ejemplo: 25000.00)"));

        cambiarColores.cambiar(btnIrAInicio, btnAlquiler, btnCompras, btnIrAIngreso, btnAgentes, btnChat);
        iniciarTablas();
        ocultarTodos();
        desactivarBotones();
        mostrarPanel(paneLogin);
        deshabilitarBotonesCliente();
        llenarMenuMarcas();
        llenarMenuMarcasCompra(); // Llenar marcas para pantalla de compra
        configurarComboDni(); // Configurar ComboBox de DNI con filtrado
        btnEditarCoche.setDisable(true); // Deshabilitar btnEditarCoche inicialmente

        // Sincronizar PasswordField y TextField del login
        pfContraseña.textProperty().addListener((obs, o, n) -> {
            if (!txtPasswordVisible.getText().equals(n)) txtPasswordVisible.setText(n);
        });
        txtPasswordVisible.textProperty().addListener((obs, o, n) -> {
            if (!pfContraseña.getText().equals(n)) pfContraseña.setText(n);
        });

        // Deshabilitar ComboBox de modelo hasta que se seleccione una marca
        cmbElegirModelo.setDisable(true);
        cmbElegirModelo.setEditable(true);

        // Estado de pago del alquiler
        cmbEstadoPagoAlquiler.getItems().addAll("Pendiente", "Pagado");
        cmbEstadoPagoAlquiler.getSelectionModel().selectFirst();

        // Deshabilitar ComboBox de modelo de compra hasta que se seleccione una marca
        cmbModeloCompra.setDisable(true);
        cmbModeloCompra.setEditable(true);

        // Inicializar tabla de coches para compra
        inicializarTablaCochesCompra();

        cliente = new Cliente("Ana", "Rodriguez", "12345678B", 623162788,
                "ana.rodriguez@email.com", "Avenida de la Constitución 42, Barcelona");

        // Configurar deselección al hacer clic fuera de las tablas
        configurarDeseleccionTablas();

        // Configurar menús contextuales para las tablas
        configurarMenusContextuales();

        // Listener para habilitar controles cuando se selecciona un coche en alquiler
        TblCoincidencias.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = newSelection != null;
            // Habilitar/deshabilitar controles de la parte inferior
            cldRecoger.setDisable(!haySeleccion);
            cldEntrega.setDisable(!haySeleccion);
            txtHoraEntrega.setDisable(!haySeleccion);
            btnConirmarAlquiler.setDisable(!haySeleccion);
            btnCancelarAlquiler.setDisable(!haySeleccion);
        });

        // Listener para habilitar botones de editar/borrar cuando se selecciona un alquiler
        tblAlquiler.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = newSelection != null;
            btnEditarAlquiler.setDisable(!haySeleccion);
            btnBorrarAlq.setDisable(!haySeleccion);
        });
    }

    //////////////////////////////////////////////////////////////// -----LOGIN-----
    /// - //////////////////////////////////////////////////////////////

    @FXML
    void toggleMostrarPassword(ActionEvent event) {
        boolean mostrar = !txtPasswordVisible.isVisible();
        txtPasswordVisible.setVisible(mostrar);
        txtPasswordVisible.setManaged(mostrar);
        pfContraseña.setVisible(!mostrar);
        pfContraseña.setManaged(!mostrar);
        btnMostrarPass.setText(mostrar ? "🔒" : "👁");
        if (mostrar) txtPasswordVisible.requestFocus();
        else pfContraseña.requestFocus();
    }

    @FXML
    void IniciarSesion(ActionEvent event) {
        String user = txtUser.getText().trim();
        String pass = pfContraseña.getText();

        if (user.isEmpty()) {
            aplicarEfectoShake(txtUser);
            aplicarEstiloError(txtUser);
            return;
        }
        if (pass.isEmpty()) {
            aplicarEfectoShake(pfContraseña);
            aplicarEstiloError(pfContraseña);
            return;
        }

        btnInicioSesion.setDisable(true);

        new Thread(() -> {
            try {
                Conexion.login(user, pass);

                javafx.application.Platform.runLater(() -> {
                    txtUser.setStyle("");
                    pfContraseña.setStyle("");
                    pfContraseña.clear();
                    btnInicioSesion.setDisable(false);

                    mostrarPanel(panelRegistro);
                    btnAlquiler.setDisable(false);
                    btnIrAInicio.setDisable(false);
                    btnCerrarSesion.setDisable(false);
                    btnIrAIngreso.setDisable(false);
                    btnCompras.setDisable(false);
                    btnAgentes.setDisable(false);
                    btnChat.setDisable(false);
                });

            } catch (Exception ex) {
                System.err.println("[LOGIN ERROR] " + ex.getClass().getName() + ": " + ex.getMessage());
                ex.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    btnInicioSesion.setDisable(false);
                    aplicarEfectoShake(txtUser);
                    aplicarEfectoShake(pfContraseña);
                    aplicarEstiloError(txtUser);
                    aplicarEstiloError(pfContraseña);
                    AlertaEstilizada.mostrarAdvertencia(
                        "Acceso denegado",
                        "No se pudo iniciar sesión",
                        ex.getMessage()
                    );
                });
            }
        }).start();
    }

    //////////////////////////// ---LOGIN
    /// CLIENTE--- /////////////////////////////////////////

    @FXML
    void crearCliente(ActionEvent event) {

        comboDni.setDisable(true);
        btnSeleccionCLiente.setDisable(true);
        btnEditarCliente.setDisable(true);
        btnCancelarSeleccion.setDisable(true);
        btnEliminarCliente.setDisable(true);
        btnInvitarCliente.setDisable(true);

        txtNombre.setDisable(false);
        txtApellidos.setDisable(false);
        txtDireccion.setDisable(false);
        txtTelefono.setDisable(false);
        dpFechaNac.setDisable(false);
        txtDni.setDisable(false);
        txtEmail.setDisable(false);
        btnAñadirNuevoCliente.setDisable(false);
        btnCancelarNuevoCliente.setDisable(false);
        rbMasculino.setDisable(false);
        rbFemenino.setDisable(false);
    }

    @FXML
    void añadirCliente(ActionEvent event) {

        // Validar el formulario antes de guardar
        if (validador.validarYMostrarErrores()) {

            String nombre = txtNombre.getText();
            String apellidos = txtApellidos.getText();
            String direccion = txtDireccion.getText();
            String telefono = txtTelefono.getText();
            String fechaNac = dpFechaNac.getValue() != null ? dpFechaNac.getValue().toString() : "";
            String dni = txtDni.getText();
            String email = txtEmail.getText();
            String sexo = rbMasculino.isSelected() ? "Masculino" : (rbFemenino.isSelected() ? "Femenino" : "");

            // Verificar si ya existe un cliente con ese DNI
            List<Cliente> clientesExistentes = Conexion.buscarClientes(dni);
            if (!clientesExistentes.isEmpty()) {
                AlertaEstilizada.mostrarAdvertencia(
                    "DNI duplicado",
                    "El cliente ya existe",
                    "Ya existe un cliente registrado con el DNI " + dni + ". No se puede crear un duplicado."
                );
                return;
            }

            try {
                Conexion.crearCliente(nombre, apellidos, dni, telefono, email, direccion, fechaNac, sexo);
                AlertaEstilizada.mostrarInformacion(
                    "Cliente añadido",
                    "Éxito",
                    "El cliente ha sido creado correctamente"
                );
                limpiarCliente();
                iniciarTablas();
            } catch (Exception error) {
                AlertaEstilizada.mostrarError(
                    "Error de base de datos",
                    "No se pudo crear el cliente",
                    "Error: " + error.getMessage()
                );
            }
        }
    }

    @FXML
    void cancelarNuevoCliente(ActionEvent event) {

        limpiarCliente();

    }
    /*
     * ==================================BUSCAR=====================================
     * ======
     */

    @FXML
    void buscarLogin(ActionEvent event) {

        comboDni.setDisable(false);
        btnSeleccionCLiente.setDisable(false);
        btnEditarCliente.setDisable(false);
        btnCancelarSeleccion.setDisable(false);
        btnEliminarCliente.setDisable(false);
        btnInvitarCliente.setDisable(false);

        txtNombre.setDisable(true);
        txtApellidos.setDisable(true);
        txtDireccion.setDisable(true);
        txtTelefono.setDisable(true);
        dpFechaNac.setDisable(true);
        txtDni.setDisable(true);
        txtEmail.setDisable(true);
        btnAñadirNuevoCliente.setDisable(true);
        btnCancelarNuevoCliente.setDisable(true);
        rbMasculino.setDisable(true);
        rbFemenino.setDisable(true);

    }

    @FXML
    void seleccionarCliente(ActionEvent event) {
        if (editarCliente) {
            // Validar formulario antes de guardar cambios
            if (validador.validarYMostrarErrores()) {

                // Confirmacion antes de guardar cambios
                boolean confirmado = AlertaEstilizada.confirmar(
                    "Confirmar cambios",
                    "¿Guardar cambios del cliente?",
                    "Vas a guardar los cambios de " + txtNombre.getText() + " " + txtApellidos.getText() +
                    ".\n\n¿Deseas continuar?"
                );

                if (!confirmado) {
                    return;
                }

                try {
                    Cliente c = tablaRegistro.getSelectionModel().getSelectedItem();

                    // Actualizar datos del cliente con los valores del formulario
                    c.setNombre(txtNombre.getText());
                    c.setApellidos(txtApellidos.getText());
                    c.setDireccion(txtDireccion.getText());
                    c.setTelefono(Conexion.parseLongSafe(txtTelefono.getText(), 0L));
                    c.setEmail(txtEmail.getText());
                    Conexion.editarCliente(c);

                    AlertaEstilizada.mostrarInformacion(
                        "Cliente actualizado",
                        "Exito",
                        "El cliente ha sido actualizado correctamente"
                    );

                    editarCliente = false;
                    limpiarCliente();
                    iniciarTablas();
                    deshabilitarBotonesCliente();

                } catch (NumberFormatException e) {
                    AlertaEstilizada.mostrarError(
                        "Error de formato",
                        "Telefono invalido",
                        "El telefono debe contener solo numeros"
                    );
                } catch (Exception e) {
                    AlertaEstilizada.mostrarError(
                        "Error al actualizar",
                        "No se pudo actualizar el cliente",
                        "Error: " + e.getMessage()
                    );
                }
            }
        } else {
            // Modo selección normal (no edición)
            Cliente c = tablaRegistro.getSelectionModel().getSelectedItem();
            if (c != null) {
                // Asignar cliente seleccionado a la variable privada
                cliente = c;

                // Rellenar los labels con la información del cliente
                lblNombre.setText("Nombre: " + cliente.getNombre() + " " + cliente.getApellidos());
                lblNumero.setText("Teléfono: " + cliente.getTelefono());
                lblEmail.setText("Email: " + cliente.getEmail());
                lblSexo.setText("DNI: " + cliente.getDni());
                lblEdad.setText("Dirección: " + cliente.getDireccion());

                // Activar sesión
                sesionActiva = true;

                // Cargar matrículas de coches del cliente
                cargarMatriculasCliente();
                iniciarRefrescoMatriculas();

                // Mostrar el panel de sesión
                mostrarPanel(pantallaSesion);
            } else {
                AlertaEstilizada.mostrarAdvertencia(
                    "Sin selección",
                    "Ningún cliente seleccionado",
                    "Por favor, selecciona un cliente de la tabla"
                );
            }
        }
    }

    @FXML
    void editarCliente(ActionEvent event) {
        Cliente c = tablaRegistro.getSelectionModel().getSelectedItem();

        if (c == null) {
            AlertaEstilizada.mostrarAdvertencia(
                "Ningun cliente seleccionado",
                "Selecciona un cliente",
                "Por favor, selecciona un cliente de la tabla para editarlo"
            );
            return;
        }

        // Confirmacion antes de editar
        boolean confirmado = AlertaEstilizada.confirmar(
            "Confirmar edicion",
            "¿Editar cliente?",
            "Vas a modificar los datos de " + c.getNombre() + " " + c.getApellidos() +
            ".\n\n¿Deseas continuar?"
        );

        if (!confirmado) {
            return;
        }

        // Activar modo edicion
        editarCliente = true;

        // Cargar datos del cliente en los campos
        txtNombre.setText(c.getNombre());
        txtNombre.setDisable(false);

        txtApellidos.setText(c.getApellidos());
        txtApellidos.setDisable(false);

        txtDireccion.setText(c.getDireccion());
        txtDireccion.setDisable(false);

        // Convertir telefono a String
        txtTelefono.setText(String.valueOf(c.getTelefono()));
        txtTelefono.setDisable(false);

        txtDni.setText(c.getDni());
        txtDni.setDisable(true); // DNI no se debe editar

        txtEmail.setText(c.getEmail());
        txtEmail.setDisable(false);

        // Fecha de nacimiento
        if (c.getFechaNacimiento() != null && !c.getFechaNacimiento().isBlank()) {
            try {
                dpFechaNac.setValue(java.time.LocalDate.parse(c.getFechaNacimiento()));
            } catch (Exception ex) {
                dpFechaNac.setValue(null);
            }
        } else {
            dpFechaNac.setValue(null);
        }
        dpFechaNac.setDisable(true);

        // Deshabilitar otros botones durante la edicion
        btnEliminarCliente.setDisable(true);
        btnCancelarSeleccion.setDisable(false);
        btnSeleccionCLiente.setDisable(false); // Este boton guardara los cambios
        btnEditarCliente.setDisable(true);
        btnInvitarCliente.setDisable(true);
        btnNuevoCliente.setDisable(true);
        btnBuscarCliente.setDisable(true);
        btnAñadirNuevoCliente.setDisable(true); // Deshabilitar para evitar confusión

        AlertaEstilizada.mostrarInformacion(
            "Modo edicion",
            "Editando cliente",
            "Modifica los campos necesarios y pulsa 'Aceptar' para guardar los cambios."
        );
    }

    @FXML
    void eliminarCliente(ActionEvent event) {
        Cliente c = tablaRegistro.getSelectionModel().getSelectedItem();

        if (c == null) {
            AlertaEstilizada.mostrarAdvertencia(
                "Ningún cliente seleccionado",
                "Selecciona un cliente",
                "Por favor, selecciona un cliente de la tabla para eliminarlo"
            );
            return;
        }

        // Confirmar eliminación
        boolean confirmado = AlertaEstilizada.confirmar(
            "Confirmar eliminación",
            "¿Eliminar cliente?",
            "¿Estás seguro de que deseas eliminar a " + c.getNombre() + " " + c.getApellidos() + "?"
        );

        if (confirmado) {
            try {
                Conexion.eliminarCliente(c);
                AlertaEstilizada.mostrarInformacion(
                    "Cliente eliminado",
                    "Éxito",
                    "El cliente ha sido eliminado correctamente"
                );
                iniciarTablas();
                limpiarCliente();
            } catch (Exception e) {
                AlertaEstilizada.mostrarError(
                    "Error al eliminar",
                    "No se pudo eliminar el cliente",
                    "Error: " + e.getMessage()
                );
            }
        }
    }

    @FXML
    void invitarCliente(ActionEvent event) {
        Cliente c = tablaRegistro.getSelectionModel().getSelectedItem();
        if (c == null) {
            AlertaEstilizada.mostrarAdvertencia(
                "Ningún cliente seleccionado",
                "Selecciona un cliente",
                "Por favor, selecciona un cliente de la tabla para invitarlo"
            );
            return;
        }
        String email = c.getEmail();
        if (email == null || email.isBlank()) {
            AlertaEstilizada.mostrarAdvertencia(
                "Sin email",
                "El cliente no tiene email",
                "No se puede enviar la invitación porque el cliente no tiene email registrado"
            );
            return;
        }
        boolean confirmado = AlertaEstilizada.confirmar(
            "Enviar invitación",
            "¿Invitar a " + c.getNombre() + "?",
            "Se enviará un email a " + email + " para que configure su acceso al sistema"
        );
        if (!confirmado) return;
        try {
            Conexion.registrarAuth(email);
            AlertaEstilizada.mostrarInformacion(
                "Invitación enviada",
                "Éxito",
                "Se ha enviado el email de configuración a " + email
            );
        } catch (Exception e) {
            AlertaEstilizada.mostrarError(
                "Error al invitar",
                "No se pudo enviar la invitación",
                e.getMessage()
            );
        }
    }

    @FXML
    void cancelarSeleccion(ActionEvent event) {
        // Cancelar modo edición y limpiar campos
        editarCliente = false;
        limpiarCliente();
        deshabilitarBotonesCliente();
        btnNuevoCliente.setDisable(false);
        btnBuscarCliente.setDisable(false);
        btnSeleccionCLiente.setDisable(false);
        btnEditarCliente.setDisable(false);
        btnEliminarCliente.setDisable(false);
        btnInvitarCliente.setDisable(false);
    }

    //////////////////////////////////////////////////////////////////////////////// -
    /// -------Alquiler--------///////////////////////////////// /
    /// /////////////////////////////////////////////////////////////////////////
    ///
    ///
    ///
    /// CREAR Y AÑADIR FUNCION QUE cambie el estado de disponibilidad de los
    /// vehiculos cuando son alquilados
    ///
    ///
    ///
    /// //////////////////////////////////////////////////////////////////////////////

    @FXML
    void confirmarAlquiler(ActionEvent event) {

        if(validadorAlquiler.validarYMostrarErrores()){
            if (!editandoAlquiler) {
                Disponible d = TblCoincidencias.getSelectionModel().getSelectedItem();

                // Validar que hay un coche seleccionado
                if (d == null) {
                    AlertaEstilizada.mostrarAdvertencia(
                        "Sin seleccion",
                        "Selecciona un vehiculo",
                        "Por favor, selecciona un vehiculo de la tabla antes de confirmar el alquiler"
                    );
                    return;
                }

                LocalDate fecha = cldRecoger.getValue();
                LocalDate fechaFin = cldEntrega.getValue();

                // Validar fechas
                if (fecha == null || fechaFin == null) {
                    AlertaEstilizada.mostrarAdvertencia(
                        "Fechas incompletas",
                        "Selecciona las fechas",
                        "Por favor, selecciona las fechas de recogida y entrega"
                    );
                    return;
                }

                long dias = ChronoUnit.DAYS.between(fecha, fechaFin);
                double precioDia = Conexion.parseDoubleSafe(d.getPrecio(), 0.0);
                double precioTotal = dias * precioDia;

                String estadoPago = cmbEstadoPagoAlquiler.getSelectionModel().getSelectedItem();
                if (estadoPago == null) estadoPago = "Pendiente";

                // Confirmación con toda la info antes de registrar
                String resumen = String.format(
                    "Matrícula      : %s%n" +
                    "Año / Color    : %s  /  %s%n" +
                    "Cliente        : %s %s  (DNI: %s)%n" +
                    "Fecha recogida : %s%n" +
                    "Fecha entrega  : %s%n" +
                    "Días           : %d%n" +
                    "Precio/día     : %.2f €%n" +
                    "Total          : %.2f €%n" +
                    "Estado de pago : %s",
                    d.getMatricula(),
                    d.getAño(), d.getColor(),
                    cliente.getNombre(), cliente.getApellidos(), cliente.getDni(),
                    fecha, fechaFin,
                    dias,
                    precioDia,
                    precioTotal,
                    estadoPago
                );

                boolean confirmado = AlertaEstilizada.confirmar(
                    "Confirmar alquiler",
                    "¿Registrar el siguiente alquiler?",
                    resumen
                );
                if (!confirmado) return;

                if("si".equalsIgnoreCase(d.getDisponible())){
                    try {
                        int transId = Conexion.crearAlquiler(
                            fecha.toString(),
                            fechaFin.toString(),
                            precioTotal + "",
                            d.getPrecio(),
                            estadoPago,
                            1 + "",
                            txtHoraEntrega.getText(),
                            d.getMatricula(),
                            cliente.getDni());

                        Conexion.actualizarAlquilado(d.getMatricula());

                        // Crear documento CONTRATO_ALQUILER siempre
                        String contenido = "CONTRATO DE ALQUILER\n"
                                + "====================\n"
                                + "Transacción ID : " + transId + "\n"
                                + "Vehículo       : " + d.getMatricula() + "\n"
                                + "Cliente        : " + cliente.getNombre() + " " + cliente.getApellidos() + "\n"
                                + "DNI            : " + cliente.getDni() + "\n"
                                + "Fecha recogida : " + fecha + "\n"
                                + "Fecha entrega  : " + fechaFin + "\n"
                                + "Días           : " + dias + "\n"
                                + "Monto total    : " + String.format("%.2f €", precioTotal) + "\n"
                                + "Estado de pago : " + estadoPago + "\n"
                                + "Pendiente de firma por el cliente y el agente.";
                        Conexion.crearDocumento(cliente.getId(), transId, "CONTRATO_ALQUILER", contenido);

                        AlertaEstilizada.mostrarInformacion(
                            "Alquiler confirmado",
                            "Exito",
                            "El alquiler se ha registrado correctamente"
                        );
                        iniciarTablas();
                        limpiarAlquiler();
                    } catch (Exception e) {
                        AlertaEstilizada.mostrarError(
                            "Error al crear alquiler",
                            "No se pudo registrar el alquiler",
                            "Error: " + e.getMessage()
                        );
                    }
                }else{
                    AlertaEstilizada.mostrarError(
                        "Coche no disponible",
                        "Cuidado",
                        "No puedes alquilar un coche que no se encuentra disponible"
                    );
                }

            } else {
                Alquiler a = tblAlquiler.getSelectionModel().getSelectedItem();

                // Validar que hay un alquiler seleccionado
                if (a == null) {
                    AlertaEstilizada.mostrarAdvertencia(
                        "Sin seleccion",
                        "Selecciona un alquiler",
                        "Por favor, selecciona un alquiler de la tabla para editarlo"
                    );
                    return;
                }

                // Validar fechas
                if (cldRecoger.getValue() == null || cldEntrega.getValue() == null) {
                    AlertaEstilizada.mostrarAdvertencia(
                        "Fechas incompletas",
                        "Selecciona las fechas",
                        "Por favor, selecciona las fechas de recogida y entrega"
                    );
                    return;
                }

                try {
                    Timestamp t1 = Timestamp.valueOf(cldRecoger.getValue().atStartOfDay());
                    Timestamp t2 = Timestamp.valueOf(cldEntrega.getValue().atStartOfDay());

                    Conexion.editarAlquiler(t1, txtHoraEntrega.getText(), t2, a.getMatricula(), a.getDni());

                    AlertaEstilizada.mostrarInformacion(
                        "Alquiler actualizado",
                        "Exito",
                        "El alquiler se ha actualizado correctamente"
                    );
                } catch (Exception e) {
                    AlertaEstilizada.mostrarError(
                        "Error al actualizar",
                        "No se pudo actualizar el alquiler",
                        "Error: " + e.getMessage()
                    );
                }
                iniciarTablas();
                limpiarAlquiler();
            }
        }

    }

    @FXML
    void cancelarAlquiler(ActionEvent event) {
        limpiarAlquiler();
    }

    private void limpiarAlquiler() {
        mnElegirMarca.setText("Marca");
        mnElegirMarca.setDisable(false);
        cmbElegirModelo.getSelectionModel().clearSelection();
        TblCoincidencias.getItems().clear();
        cldRecoger.setValue(null);
        cldRecoger.getEditor().clear();
        cldRecoger.setDisable(true);
        txtHoraEntrega.clear();
        txtHoraEntrega.setDisable(true);
        cldEntrega.setValue(null);
        cldEntrega.getEditor().clear();
        cldEntrega.setDisable(true);
        if (cmbEstadoPagoAlquiler != null) cmbEstadoPagoAlquiler.getSelectionModel().selectFirst();
        btnConirmarAlquiler.setDisable(true);
        editandoAlquiler = false;
    }

    @FXML
    void editarAlq(ActionEvent event) {
        Alquiler a = tblAlquiler.getSelectionModel().getSelectedItem();

        if (a == null) {
            AlertaEstilizada.mostrarAdvertencia(
                "Sin seleccion",
                "Selecciona un alquiler",
                "Debes seleccionar un alquiler de la tabla para editarlo"
            );
            return;
        }

        // Confirmacion antes de editar
        boolean confirmado = AlertaEstilizada.confirmar(
            "Confirmar edicion",
            "¿Editar alquiler?",
            "Vas a modificar el alquiler del vehiculo " + a.getMatricula() +
            ".\n\n¿Deseas continuar?"
        );

        if (!confirmado) {
            return;
        }

        editandoAlquiler = true;

        mnElegirMarca.setDisable(true);
        cmbElegirModelo.setDisable(true);

        // Habilitar los campos editables aunque no haya coche seleccionado en la tabla
        cldRecoger.setDisable(false);
        cldEntrega.setDisable(false);
        txtHoraEntrega.setDisable(false);
        btnConirmarAlquiler.setDisable(false);
        btnCancelarAlquiler.setDisable(false);

        Timestamp fechaR = a.getFechaRecogida();
        Timestamp fechaE = a.getFechaDevolucion();
        String hora = a.getHoraDevolucion();

        if (fechaR != null) cldRecoger.setValue(fechaR.toLocalDateTime().toLocalDate());
        if (fechaE != null) cldEntrega.setValue(fechaE.toLocalDateTime().toLocalDate());
        txtHoraEntrega.setText(hora != null ? hora : "");

        AlertaEstilizada.mostrarInformacion(
            "Modo edicion",
            "Editando alquiler",
            "Modifica los campos necesarios y pulsa 'Confirmar' para guardar los cambios."
        );
    }

    @FXML
    void borrarAlq(ActionEvent event) {
        Alquiler a = tblAlquiler.getSelectionModel().getSelectedItem();

        if (a == null) {
            AlertaEstilizada.mostrarAdvertencia(
                "Ningún alquiler seleccionado",
                "Selecciona un alquiler",
                "Por favor, selecciona un alquiler de la tabla para eliminarlo"
            );
            return;
        }

        // Confirmar eliminación
        boolean confirmado = AlertaEstilizada.confirmar(
            "Confirmar eliminación",
            "¿Eliminar alquiler?",
            "¿Estás seguro de que deseas eliminar el alquiler del vehículo " + a.getMatricula() + "?"
        );

        if (confirmado) {
            try {
                Conexion.eliminarAlquiler(a.getMatricula());
                Conexion.liberarCoche(a.getMatricula());
                AlertaEstilizada.mostrarInformacion(
                    "Alquiler eliminado",
                    "Éxito",
                    "El alquiler ha sido eliminado correctamente"
                );
                iniciarTablas();
            } catch (Exception e) {
                AlertaEstilizada.mostrarError(
                    "Error al eliminar",
                    "No se pudo eliminar el alquiler",
                    "Error: " + e.getMessage()
                );
            }
        }
    }

    @FXML
    void buscarMatricula(ActionEvent event) {

        String marca = mnElegirMarca.getText();
        String modelo = cmbElegirModelo.getSelectionModel().getSelectedItem();

        // Validar que se haya seleccionado al menos una marca
        if (marca == null || marca.equals("Marca") || marca.trim().isEmpty()) {
            AlertaEstilizada.mostrarAdvertencia(
                "Marca no seleccionada",
                "Selecciona una marca",
                "Por favor, selecciona una marca antes de buscar"
            );
            return;
        }

        // Buscar por marca y modelo si ambos están seleccionados, sino solo por marca
        if (modelo != null && !modelo.trim().isEmpty()) {
            TblCoincidencias.setItems(Conexion.mostrarCoincidencias(marca, modelo));
        } else {
            TblCoincidencias.setItems(Conexion.mostrarCoincidencias(marca));
        }

        clnAñoCoche.setCellValueFactory(new PropertyValueFactory<>("año"));
        clnDisponibleCoche.setCellValueFactory(new PropertyValueFactory<>("Disponible"));
        clnColorCoche.setCellValueFactory(new PropertyValueFactory<>("Color"));
        clnPrecioCoche.setCellValueFactory(new PropertyValueFactory<>("Precio"));
        clnMatriculaCoche.setCellValueFactory(new PropertyValueFactory<>("matricula"));

    }

    @FXML
    void cancelarBusquedaMatricula(ActionEvent event) {
        mnElegirMarca.setText("Marca");
        cmbElegirModelo.getSelectionModel().clearSelection();
        TblCoincidencias.getItems().clear();
    }

    /**
     * Búsqueda automática de coincidencias cuando se selecciona un modelo
     * Se ejecuta desde el listener del ComboBox de modelos
     */
    private void buscarCoincidenciasAutomatico() {
        String marca = mnElegirMarca.getText();
        String modelo = cmbElegirModelo.getSelectionModel().getSelectedItem();

        // Validar que hay marca seleccionada
        if (marca == null || marca.equals("Marca") || marca.trim().isEmpty()) {
            TblCoincidencias.getItems().clear();
            return;
        }

        // Buscar por marca y modelo si ambos están seleccionados, sino solo por marca
        if (modelo != null && !modelo.trim().isEmpty()) {
            TblCoincidencias.setItems(Conexion.mostrarCoincidencias(marca, modelo));
        } else {
            TblCoincidencias.setItems(Conexion.mostrarCoincidencias(marca));
        }

        // Configurar las columnas de la tabla
        clnAñoCoche.setCellValueFactory(new PropertyValueFactory<>("año"));
        clnDisponibleCoche.setCellValueFactory(new PropertyValueFactory<>("Disponible"));
        clnColorCoche.setCellValueFactory(new PropertyValueFactory<>("Color"));
        clnPrecioCoche.setCellValueFactory(new PropertyValueFactory<>("Precio"));
        clnMatriculaCoche.setCellValueFactory(new PropertyValueFactory<>("matricula"));
    }

    ///
    /////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////// -----------COMPRA------------//////////////////////////////////////////////
    /// / ///////////////////////////////////////////////////////////////////////////////////

    @FXML
    void efectuarCompra(ActionEvent event) {

        CocheCompra seleccionado = tblCochesCompra.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertaEstilizada.mostrarAdvertencia("Sin selección", "Selecciona un vehículo",
                    "Debes seleccionar un coche de la tabla antes de comprar.");
            return;
        }
        if (!sesionActiva || cliente == null) {
            AlertaEstilizada.mostrarAdvertencia("Sesión requerida", "Inicia sesión",
                    "Debes iniciar sesión antes de realizar una compra.");
            return;
        }

        cocheParaComprar = seleccionado;

        // Asegurar que el cliente tiene su id de BD
        if (cliente.getId() <= 0) {
            int dbId = Conexion.obtenerClienteIdPorDni(cliente.getDni());
            cliente.setId(dbId);
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ventanaInfo.fxml"));
            Parent root = loader.load();

            // Pasar datos del cliente al controlador emergente
            controladorEmergente ctrl = loader.getController();
            ctrl.setClienteId(cliente.getId());
            ctrl.setClienteNombre(cliente.getNombre() + " " + cliente.getApellidos());

            Stage stage = new Stage();
            stage.setTitle("Compra");
            stage.initModality(Modality.WINDOW_MODAL);
            if (btnCompra.getScene() != null && btnCompra.getScene().getWindow() != null) {
                stage.initOwner(btnCompra.getScene().getWindow());
            }
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Después del diálogo: refrescar tabla para ocultar el coche comprado
            actualizarTablaCochesCompra();

        } catch (IOException e) {
            System.out.println(e.getMessage() + " – Error al efectuar Compra");
        }

    }

    @FXML
    void cancelarCompra(ActionEvent event) {
        AlertaEstilizada.mostrarInformacion(
            "Función en desarrollo",
            "Próximamente",
            "Esta funcionalidad estará disponible en una versión futura"
        );
    }

    @FXML
    void verPagosPendientesVenta(ActionEvent event) {
        abrirVentanaPagos("VENTA");
    }

    @FXML
    void verPagosPendientesAlquiler(ActionEvent event) {
        abrirVentanaPagos("ALQUILER");
    }

    @FXML
    void verDocumentosVenta(ActionEvent event) {
        abrirVentanaDocumentos("CONTRATO_VENTA");
    }

    @FXML
    void verDocumentosAlquiler(ActionEvent event) {
        abrirVentanaDocumentos("CONTRATO_ALQUILER");
    }

    private void abrirVentanaDocumentos(String tipo) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/ventanaDocumentos.fxml"));
            javafx.scene.Parent root = loader.load();

            controladorDocumentos ctrl = loader.getController();
            ctrl.setTipo(tipo);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Documentos — " + ("CONTRATO_VENTA".equals(tipo) ? "Ventas" : "Alquileres"));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();
        } catch (java.io.IOException e) {
            System.err.println("Error al abrir ventana de documentos: " + e.getMessage());
        }
    }

    private void abrirVentanaPagos(String tipo) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/ventanaPagos.fxml"));
            javafx.scene.Parent root = loader.load();

            controladorPagos ctrl = loader.getController();
            ctrl.setTipo(tipo);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Pagos pendientes — " + ("VENTA".equals(tipo) ? "Ventas" : "Alquileres"));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();
        } catch (java.io.IOException e) {
            System.err.println("Error al abrir ventana de pagos: " + e.getMessage());
        }
    }

    @FXML
    void descargarInformeVehiculo(ActionEvent event) {
        CocheCompra cocheSeleccionado = tblCochesCompra.getSelectionModel().getSelectedItem();

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
            File carpetaInformer = new File(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "AutoReach", "Informer");
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
                "El informe del vehiculo se ha exportado correctamente.\n\nUbicación del archivo:\n" + archivoDestino.getAbsolutePath()
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
    void BuscarCompra(ActionEvent event) {
        // Simplemente ejecutar la búsqueda automática
    }

    @FXML
    void CancelarBusquedaCompra(ActionEvent event) {
        // Limpiar criterios de búsqueda
        mnMarcaCompra.setText("Marca");
        cmbModeloCompra.getSelectionModel().clearSelection();
        cmbModeloCompra.getEditor().clear();
        cmbModeloCompra.setDisable(true);
        sclColorCompra.setText("Color");

        // Limpiar la tabla y el WebView
        if (tblCochesCompra != null) {
            tblCochesCompra.getItems().clear();
        }
        if (webViewInformeCompra != null) {
            webViewInformeCompra.getEngine().loadContent("<html><body style='display:flex;justify-content:center;align-items:center;height:100%;color:#6B7280;font-family:sans-serif;'><p>Seleccione un vehículo de la tabla</p></body></html>");
        }
        // Deshabilitar boton de informe
        if (btnInformeVehiculo != null) {
            btnInformeVehiculo.setDisable(true);
        }
    }

    private void inicializarTablaCochesCompra() {
        // Configurar columnas de la tabla
        colMatriculaCompra.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        colMarcaCompra.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colModeloCompra.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colAñoCompra.setCellValueFactory(new PropertyValueFactory<>("año"));
        colPrecioCompra.setCellValueFactory(new PropertyValueFactory<>("precio"));

        // Listener para mostrar detalles al seleccionar un coche
        tblCochesCompra.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostrarDetallesCocheCompra(newSelection);
                btnInformeVehiculo.setDisable(false);
            } else {
                btnInformeVehiculo.setDisable(true);
            }
        });

        // Listener para actualizar tabla cuando se selecciona un modelo
        cmbModeloCompra.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                actualizarTablaCochesCompra();
            }
        });
    }

    private void actualizarTablaCochesCompra() {
        String marca = mnMarcaCompra.getText();
        String modelo = cmbModeloCompra.getSelectionModel().getSelectedItem();

        // Buscar coches con los criterios
        ObservableList<CocheCompra> coches = Conexion.buscarCocheFlexible(marca, modelo, null, null, null);

        // Actualizar la tabla
        tblCochesCompra.setItems(coches);

        if (coches.isEmpty()) {
            webViewInformeCompra.getEngine().loadContent(
                "<html><body style='display:flex;justify-content:center;align-items:center;height:100%;color:#F97316;font-family:sans-serif;'>" +
                "<p>No se encontraron vehículos</p></body></html>"
            );
        } else {
            webViewInformeCompra.getEngine().loadContent(
                "<html><body style='display:flex;justify-content:center;align-items:center;height:100%;color:#059669;font-family:sans-serif;'>" +
                "<p>Se encontraron " + coches.size() + " vehículos. Seleccione uno para ver detalles.</p></body></html>"
            );
        }
    }

    private void mostrarDetallesCocheCompra(CocheCompra coche) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>");
        html.append("body { font-family: 'Segoe UI', sans-serif; margin: 8px; background: #FAFAFA; }");
        html.append("h3 { color: #1E3A8A; margin: 0 0 8px 0; font-size: 14px; border-bottom: 2px solid #F97316; padding-bottom: 4px; }");
        html.append(".info { font-size: 11px; margin: 4px 0; }");
        html.append(".label { color: #6B7280; }");
        html.append(".value { color: #1F2937; font-weight: bold; }");
        html.append(".precio { color: #059669; font-size: 16px; font-weight: bold; margin-top: 10px; }");
        html.append("</style></head><body>");
        html.append("<h3>").append(coche.getMarca()).append(" ").append(coche.getModelo()).append("</h3>");
        html.append("<p class='info'><span class='label'>Año:</span> <span class='value'>").append(coche.getAño()).append("</span></p>");
        html.append("<p class='info'><span class='label'>Color:</span> <span class='value'>").append(coche.getColor()).append("</span></p>");
        html.append("<p class='info'><span class='label'>Llantas:</span> <span class='value'>").append(coche.getDiseñoRuedas()).append(" - ").append(coche.getDiametroLlantas()).append("\"</span></p>");
        html.append("<p class='info'><span class='label'>Matrícula:</span> <span class='value'>").append(coche.getMatricula()).append("</span></p>");
        html.append("<p class='precio'>").append(coche.getPrecio()).append(" €</p>");
        html.append("</body></html>");

        webViewInformeCompra.getEngine().loadContent(html.toString());
    }

    @FXML
    void verInformeCocheCompra(ActionEvent event) {
        // Mostrar detalles del coche seleccionado en la tabla
        CocheCompra cocheSeleccionado = tblCochesCompra.getSelectionModel().getSelectedItem();
        if (cocheSeleccionado != null) {
            mostrarDetallesCocheCompra(cocheSeleccionado);
        } else {
            // Si no hay selección, actualizar tabla
            actualizarTablaCochesCompra();
        }
    }

    @FXML
    void descargarInformeCocheCompra(ActionEvent event) {
        try {
            // Obtener solo marca y modelo
            String marca = mnMarcaCompra.getText();
            String modelo = cmbModeloCompra.getSelectionModel().getSelectedItem();

            // Verificar que se haya seleccionado al menos la marca
            if (marca == null || marca.equals("Marca")) {
                AlertaEstilizada.mostrarAdvertencia(
                    "Sin selección",
                    "Seleccione criterios",
                    "Por favor, seleccione al menos una marca para generar el informe PDF"
                );
                return;
            }

            // Buscar coches solo por marca y modelo
            ObservableList<CocheCompra> cochesEncontrados = Conexion.buscarCocheFlexible(marca, modelo, null, null, null);

            if (cochesEncontrados == null || cochesEncontrados.isEmpty()) {
                AlertaEstilizada.mostrarAdvertencia(
                    "Sin resultados",
                    "No hay vehículos",
                    "No se encontraron vehículos con los criterios seleccionados"
                );
                return;
            }

            // Cargar el archivo .jasper compilado desde resources
            InputStream reportStream = getClass().getResourceAsStream("/informes/informe_inventario_coches_autoreach.jasper");

            if (reportStream == null) {
                AlertaEstilizada.mostrarError(
                    "Error de reporte",
                    "Archivo no encontrado",
                    "No se encontró el archivo de reporte"
                );
                return;
            }

            // Convertir a lista de Maps con los nombres de campo correctos para el reporte
            List<Map<String, Object>> datosReporte = new ArrayList<>();
            for (CocheCompra c : cochesEncontrados) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("num_bastidor", c.getMatricula()); // Usar matrícula como identificador
                fila.put("marca", c.getMarca());
                fila.put("modelo", c.getModelo());
                try {
                    fila.put("año", Integer.parseInt(c.getAño().replaceAll("[^0-9]", "")));
                } catch (NumberFormatException e) {
                    fila.put("año", 0);
                }
                fila.put("matricula", c.getMatricula());
                datosReporte.add(fila);
            }

            // Crear DataSource con los datos
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(datosReporte);

            // Parámetros del reporte
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("REPORT_TITLE", "VEHÍCULOS SELECCIONADOS");
            parametros.put("REPORT_SUBTITLE", "Búsqueda: " + marca + (modelo != null ? " - " + modelo : ""));

            // Llenar el reporte con el DataSource
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parametros, dataSource);

            // Guardar automaticamente en la carpeta Informer
            String nombreArchivo = "informe_vehiculos_" + marca.toLowerCase() + "_" + LocalDate.now() + ".pdf";
            File carpetaInformer = new File(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "AutoReach", "Informer");
            if (!carpetaInformer.exists()) {
                carpetaInformer.mkdirs();
            }
            File archivoDestino = new File(carpetaInformer, nombreArchivo);

            JasperExportManager.exportReportToPdfFile(jasperPrint, archivoDestino.getAbsolutePath());

            AlertaEstilizada.mostrarInformacion(
                "Informe exportado",
                "Éxito",
                "El informe se ha exportado correctamente.\n\nUbicación del archivo:\n" + archivoDestino.getAbsolutePath()
            );

        } catch (Exception e) {
            AlertaEstilizada.mostrarError(
                "Error al exportar",
                "No se pudo generar el informe",
                "Error: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////// -
    /// -------INGRESOS----------------- ///////////////////////////////////
    /// / //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @FXML
    void aceptarIngreso(ActionEvent event) {

        if (validadorC.validarYMostrarErrores()) {
            String marca = ingresoMarca.getText();
            String modelo = ingresoModelo.getText();
            String año = ingresoAño.getText();
            String kms = ingresoKms.getText();
            String nBastidor = ingresoBastidor.getText();
            String precio = ingresoPrecio.getText();
            String diametro = ingresoDllantas.getText();
            String diseñoLlantas = ingresoDiseñoLlantas.getText();
            String color = ingresoColor.getText();
            String matricula = ingresoMatricula.getText();

            // Verificar si ya existe un coche con esa matrícula
            CocheCompleto cocheExistente = Conexion.buscarCochePorMatricula(matricula);
            if (cocheExistente != null) {
                AlertaEstilizada.mostrarAdvertencia(
                    "Matrícula duplicada",
                    "El coche ya existe",
                    "Ya existe un coche registrado con la matrícula " + matricula + ". No se puede crear un duplicado."
                );
                return;
            }

            try {

                Conexion.crearIngreso(nBastidor,
                        marca,
                        modelo,
                        año,
                        kms,
                        true,
                        true,
                        precio,
                        color,
                        diseñoLlantas,
                        diametro,
                        matricula);

                AlertaEstilizada.mostrarInformacion(
                    "Coche añadido",
                    "Éxito",
                    "El coche ha sido registrado correctamente"
                );
                limpiarIngreso();
                iniciarTablas();

            } catch (Exception e) {
                AlertaEstilizada.mostrarError(
                    "Error de base de datos",
                    "No se pudo crear el coche",
                    "Error: " + e.getMessage()
                );
            }
        }
    }

    @FXML
    void cancelarIngreso(ActionEvent event) {
        limpiarIngreso();
    }

    @FXML
    void editarIngreso(ActionEvent event) {
        Coche c = TablaCoches.getSelectionModel().getSelectedItem();

        if (c == null) {
            AlertaEstilizada.mostrarAdvertencia(
                "Ningun coche seleccionado",
                "Selecciona un coche",
                "Por favor, selecciona un coche de la tabla para editarlo"
            );
            return;
        }

        // Confirmacion antes de editar
        boolean confirmado = AlertaEstilizada.confirmar(
            "Confirmar edicion",
            "¿Editar vehiculo?",
            "Vas a modificar los datos del vehiculo " + c.getMarca() + " " + c.getModelo() +
            " (Matricula: " + c.getMatricula() + ").\n\n¿Deseas continuar?"
        );

        if (!confirmado) {
            return;
        }

        btnAceptarIngreso.setDisable(true);
        btnCancelarIngresos.setDisable(true);
        btnBorrarIngreso.setDisable(true);
        btnEditarCoche.setDisable(false); // Habilitar btnEditarCoche

        CocheCompleto cc = Conexion.buscarIngreso(c.getBastidor());

        ingresoMarca.setText(cc.getMarca());
        ingresoModelo.setText(cc.getModelo());
        ingresoAño.setText(cc.getAño() + "");
        ingresoKms.setText(cc.getKilometraje() + "");
        ingresoBastidor.setText(cc.getNumBastidor());
        ingresoPrecio.setText(cc.getPrecioBase() + "");
        ingresoDllantas.setText(cc.getDiametroLlantas() + "");
        ingresoDiseñoLlantas.setText(cc.getDisenoLlantas());
        ingresoColor.setText(cc.getColor());
        ingresoMatricula.setText(cc.getMatricula());

        AlertaEstilizada.mostrarInformacion(
            "Modo edicion",
            "Editando vehiculo",
            "Modifica los campos necesarios y pulsa 'Guardar Cambios' para actualizar el vehiculo."
        );
    }

    @FXML
    void editarCoche(ActionEvent event) {
        // Confirmacion antes de guardar cambios
        boolean confirmado = AlertaEstilizada.confirmar(
            "Confirmar cambios",
            "¿Guardar cambios?",
            "Vas a guardar los cambios del vehiculo " + ingresoMarca.getText() + " " + ingresoModelo.getText() +
            " (Matricula: " + ingresoMatricula.getText() + ").\n\n¿Deseas continuar?"
        );

        if (!confirmado) {
            return;
        }

        try {
            Conexion.actualizarCoche(ingresoBastidor.getText(),
                    ingresoMarca.getText(),
                    ingresoModelo.getText(),
                    ingresoAño.getText(),
                    ingresoKms.getText(),
                    true,
                    true,
                    ingresoPrecio.getText(),
                    ingresoColor.getText(),
                    ingresoDiseñoLlantas.getText(),
                    ingresoDllantas.getText(),
                    ingresoMatricula.getText());

            AlertaEstilizada.mostrarInformacion(
                "Vehiculo actualizado",
                "Exito",
                "Los cambios del vehiculo han sido guardados correctamente"
            );
        } catch (Exception e) {
            AlertaEstilizada.mostrarError(
                "Error al actualizar",
                "No se pudieron guardar los cambios",
                "Error: " + e.getMessage()
            );
        }

        btnAceptarIngreso.setDisable(false);
        btnCancelarIngresos.setDisable(false);
        btnBorrarIngreso.setDisable(false);
        btnEditarCoche.setDisable(true);

        limpiarIngreso();
        iniciarTablas();

    }

    @FXML
    void borrarIngreso(ActionEvent event) {
        Coche c = TablaCoches.getSelectionModel().getSelectedItem();

        // Validar que haya un coche seleccionado
        if (c == null) {
            AlertaEstilizada.mostrarAdvertencia(
                "Seleccion requerida",
                "No hay ningun coche seleccionado",
                "Por favor, seleccione un coche de la tabla antes de intentar eliminarlo."
            );
            return;
        }

        // Confirmacion antes de eliminar
        boolean confirmado = AlertaEstilizada.confirmar(
            "Confirmar eliminacion",
            "¿Eliminar vehiculo?",
            "¿Estas seguro de que deseas eliminar el vehiculo " + c.getMarca() + " " + c.getModelo() +
            " (Matricula: " + c.getMatricula() + ")?\n\nEsta accion no se puede deshacer."
        );

        if (!confirmado) {
            return;
        }

        try {
            Conexion.borrarCoche(c.getBastidor());
            AlertaEstilizada.mostrarInformacion(
                "Vehiculo eliminado",
                "Exito",
                "El vehiculo ha sido eliminado correctamente"
            );
        } catch (Exception e) {
            AlertaEstilizada.mostrarError(
                "Error al eliminar",
                "No se pudo eliminar el vehiculo",
                "Error: " + e.getMessage()
            );
        }
        limpiarIngreso();
        iniciarTablas();

    }

    @FXML
    void cancelarEdicionIngreso(ActionEvent event) {
        // Limpiar todos los campos de ingreso
        limpiarIngreso();

        // Deseleccionar la tabla
        TablaCoches.getSelectionModel().clearSelection();

        // Rehabilitar botones
        btnAceptarIngreso.setDisable(false);
        btnCancelarIngresos.setDisable(false);
        btnBorrarIngreso.setDisable(false);
        btnEditarCoche.setDisable(true);
    }

    @FXML
    void verReporteInventario(ActionEvent event) {
        try {
            // Obtener datos de la tabla de coches
            ObservableList<Coche> listaCoches = Conexion.mostrarCoches();

            if (listaCoches == null || listaCoches.isEmpty()) {
                AlertaEstilizada.mostrarAdvertencia(
                    "Reporte vacío",
                    "Sin datos",
                    "No hay coches en la base de datos para mostrar en el reporte."
                );
                return;
            }

            // Contar coches por año para la gráfica
            Map<String, Integer> cochesPorAño = new HashMap<>();
            for (Coche c : listaCoches) {
                String año = c.getAño();
                cochesPorAño.put(año, cochesPorAño.getOrDefault(año, 0) + 1);
            }

            // Encontrar el máximo para escalar las barras (mínimo 1 para evitar división por cero)
            int maxCoches = Math.max(cochesPorAño.values().stream().mapToInt(Integer::intValue).max().orElse(1), 1);

            // Generar HTML del reporte con gráfica
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html xmlns='http://www.w3.org/1999/xhtml'><head><meta charset='UTF-8' /><style>");
            html.append("body { font-family: 'Segoe UI', Arial, sans-serif; margin: 20px; background: #f5f5f5; }");
            html.append(".header { background: linear-gradient(135deg, #1E3A8A, #3B82F6); color: white; padding: 20px; border-radius: 10px; margin-bottom: 20px; }");
            html.append(".header h1 { margin: 0; font-size: 24px; }");
            html.append(".header p { margin: 5px 0 0 0; opacity: 0.9; }");
            html.append(".section { background: white; border-radius: 10px; padding: 20px; margin-bottom: 20px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }");
            html.append(".section h2 { color: #1E3A8A; border-bottom: 3px solid #F97316; padding-bottom: 10px; margin-top: 0; }");
            html.append(".chart-container { display: flex; align-items: flex-end; gap: 15px; height: 200px; padding: 20px 10px 40px 10px; }");
            html.append(".bar-wrapper { display: flex; flex-direction: column; align-items: center; flex: 1; }");
            html.append(".bar { background: linear-gradient(to top, #F97316, #FBBF24); border-radius: 5px 5px 0 0; width: 100%; min-width: 40px; transition: all 0.3s; }");
            html.append(".bar:hover { background: linear-gradient(to top, #EA580C, #F59E0B); transform: scaleY(1.02); }");
            html.append(".bar-label { margin-top: 8px; font-weight: bold; color: #374151; font-size: 12px; }");
            html.append(".bar-value { color: white; font-weight: bold; font-size: 14px; text-align: center; padding: 5px; }");
            html.append("table { width: 100%; border-collapse: collapse; margin-top: 10px; }");
            html.append("th { background: #1E3A8A; color: white; padding: 12px; text-align: left; }");
            html.append("td { padding: 10px; border-bottom: 1px solid #E5E7EB; }");
            html.append("tr:hover { background: #F3F4F6; }");
            html.append(".total { background: #DBEAFE; padding: 15px; border-radius: 8px; text-align: center; margin-top: 20px; }");
            html.append(".total span { font-size: 28px; font-weight: bold; color: #1E3A8A; }");
            html.append("</style></head><body>");

            // Header
            html.append("<div class='header'>");
            html.append("<h1>Reporte de Inventario - AutoReach</h1>");
            html.append("<p>Fecha: ").append(LocalDate.now()).append("</p>");
            html.append("</div>");

            // Sección de gráfica
            html.append("<div class='section'>");
            html.append("<h2>Vehículos por Año de Fabricación</h2>");
            html.append("<div class='chart-container'>");

            // Generar barras ordenadas por año
            cochesPorAño.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    int altura = (int) ((entry.getValue() * 150.0) / maxCoches);
                    html.append("<div class='bar-wrapper'>");
                    html.append("<div class='bar' style='height: ").append(Math.max(altura, 20)).append("px;'>");
                    html.append("<div class='bar-value'>").append(entry.getValue()).append("</div>");
                    html.append("</div>");
                    html.append("<div class='bar-label'>").append(entry.getKey()).append("</div>");
                    html.append("</div>");
                });

            html.append("</div></div>");

            // Sección de tabla
            html.append("<div class='section'>");
            html.append("<h2>Listado de Vehículos</h2>");
            html.append("<table><tr><th>Matrícula</th><th>Marca</th><th>Modelo</th><th>Año</th><th>Color</th><th>Bastidor</th></tr>");

            for (Coche c : listaCoches) {
                html.append("<tr>");
                html.append("<td>").append(c.getMatricula()).append("</td>");
                html.append("<td>").append(c.getMarca()).append("</td>");
                html.append("<td>").append(c.getModelo()).append("</td>");
                html.append("<td>").append(c.getAño()).append("</td>");
                html.append("<td>").append(c.getColor()).append("</td>");
                html.append("<td>").append(c.getBastidor()).append("</td>");
                html.append("</tr>");
            }

            html.append("</table>");
            html.append("<div class='total'>Total de vehículos: <span>").append(listaCoches.size()).append("</span></div>");
            html.append("</div>");

            html.append("</body></html>");

            // Crear ventana con WebView para mostrar el reporte
            Stage reporteStage = new Stage();
            reporteStage.setTitle("Reporte de Inventario - AutoReach");
            reporteStage.initModality(Modality.WINDOW_MODAL);
            if (btnVerReporte.getScene() != null && btnVerReporte.getScene().getWindow() != null) {
                reporteStage.initOwner(btnVerReporte.getScene().getWindow());
            }

            WebView webView = new WebView();
            webView.getEngine().loadContent(html.toString());

            Scene scene = new Scene(webView, 900, 700);
            reporteStage.setScene(scene);
            reporteStage.show();

        } catch (Exception e) {
            AlertaEstilizada.mostrarError(
                "Error al generar reporte",
                "No se pudo generar el reporte",
                "Error: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    @FXML
    void descargarReportePDF(ActionEvent event) {
        try {
            // Obtener datos de la tabla de coches
            ObservableList<Coche> listaCoches = Conexion.mostrarCoches();

            if (listaCoches == null || listaCoches.isEmpty()) {
                AlertaEstilizada.mostrarAdvertencia(
                    "Reporte vacío",
                    "Sin datos",
                    "No hay coches en la base de datos para exportar."
                );
                return;
            }

            // Contar coches por año para la gráfica
            Map<String, Integer> cochesPorAño = new HashMap<>();
            for (Coche c : listaCoches) {
                String año = c.getAño();
                cochesPorAño.put(año, cochesPorAño.getOrDefault(año, 0) + 1);
            }

            // Mínimo 1 para evitar división por cero
            int maxCoches = Math.max(cochesPorAño.values().stream().mapToInt(Integer::intValue).max().orElse(1), 1);

            // Generar HTML del reporte con gráfica (igual que verReporteInventario)
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html xmlns='http://www.w3.org/1999/xhtml'><head><meta charset='UTF-8' /><title>Reporte de Inventario</title><style>");
            html.append("@media print { body { -webkit-print-color-adjust: exact !important; print-color-adjust: exact !important; } }");
            html.append("body { font-family: 'Segoe UI', Arial, sans-serif; margin: 20px; background: #f5f5f5; }");
            html.append(".header { background: linear-gradient(135deg, #1E3A8A, #3B82F6); color: white; padding: 20px; border-radius: 10px; margin-bottom: 20px; }");
            html.append(".header h1 { margin: 0; font-size: 24px; }");
            html.append(".header p { margin: 5px 0 0 0; opacity: 0.9; }");
            html.append(".section { background: white; border-radius: 10px; padding: 20px; margin-bottom: 20px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }");
            html.append(".section h2 { color: #1E3A8A; border-bottom: 3px solid #F97316; padding-bottom: 10px; margin-top: 0; }");
            html.append(".chart-container { display: flex; align-items: flex-end; gap: 15px; height: 200px; padding: 20px 10px 40px 10px; }");
            html.append(".bar-wrapper { display: flex; flex-direction: column; align-items: center; flex: 1; }");
            html.append(".bar { background: linear-gradient(to top, #F97316, #FBBF24); border-radius: 5px 5px 0 0; width: 100%; min-width: 40px; }");
            html.append(".bar-label { margin-top: 8px; font-weight: bold; color: #374151; font-size: 12px; }");
            html.append(".bar-value { color: white; font-weight: bold; font-size: 14px; text-align: center; padding: 5px; }");
            html.append("table { width: 100%; border-collapse: collapse; margin-top: 10px; }");
            html.append("th { background: #1E3A8A; color: white; padding: 12px; text-align: left; }");
            html.append("td { padding: 10px; border-bottom: 1px solid #E5E7EB; }");
            html.append("tr:nth-child(even) { background: #F9FAFB; }");
            html.append(".total { background: #DBEAFE; padding: 15px; border-radius: 8px; text-align: center; margin-top: 20px; }");
            html.append(".total span { font-size: 28px; font-weight: bold; color: #1E3A8A; }");
            html.append("</style></head><body>");

            html.append("<div class='header'>");
            html.append("<h1>Reporte de Inventario - AutoReach</h1>");
            html.append("<p>Fecha: ").append(LocalDate.now()).append("</p>");
            html.append("</div>");

            html.append("<div class='section'>");
            html.append("<h2>Vehículos por Año de Fabricación</h2>");
            html.append("<div class='chart-container'>");

            cochesPorAño.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    int altura = (int) ((entry.getValue() * 150.0) / maxCoches);
                    html.append("<div class='bar-wrapper'>");
                    html.append("<div class='bar' style='height: ").append(Math.max(altura, 20)).append("px;'>");
                    html.append("<div class='bar-value'>").append(entry.getValue()).append("</div>");
                    html.append("</div>");
                    html.append("<div class='bar-label'>").append(entry.getKey()).append("</div>");
                    html.append("</div>");
                });

            html.append("</div></div>");

            html.append("<div class='section'>");
            html.append("<h2>Listado de Vehículos</h2>");
            html.append("<table><tr><th>Matrícula</th><th>Marca</th><th>Modelo</th><th>Año</th><th>Color</th><th>Bastidor</th></tr>");

            for (Coche c : listaCoches) {
                html.append("<tr>");
                html.append("<td>").append(c.getMatricula()).append("</td>");
                html.append("<td>").append(c.getMarca()).append("</td>");
                html.append("<td>").append(c.getModelo()).append("</td>");
                html.append("<td>").append(c.getAño()).append("</td>");
                html.append("<td>").append(c.getColor()).append("</td>");
                html.append("<td>").append(c.getBastidor()).append("</td>");
                html.append("</tr>");
            }

            html.append("</table>");
            html.append("<div class='total'>Total de vehículos: <span>").append(listaCoches.size()).append("</span></div>");
            html.append("</div>");
            html.append("</body></html>");

            // Guardar automaticamente en la carpeta Informer
            String nombreArchivo = "reporte_inventario_coches_" + LocalDate.now() + ".pdf";
            File carpetaInformer = new File(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "AutoReach", "Informer");
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
                "Reporte exportado",
                "Éxito",
                "El reporte PDF se ha exportado correctamente.\n\nUbicación del archivo:\n" + archivoDestino.getAbsolutePath()
            );

        } catch (Exception e) {
            AlertaEstilizada.mostrarError(
                "Error al exportar reporte",
                "No se pudo exportar el reporte",
                "Error: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////// /
    /// ////////////////////------ACERCA DE----------------//////////////////////////////
    /// //////////
    /// / /////////////////////////////////////////////////////////////////////////////////////
    @FXML
    void mostrarAcercaDe(ActionEvent event) {
        AlertaEstilizada.mostrarInformacion(
            "Acerca de AutoReach",
            "AutoReach v1.0.0",
            "Sistema de Gestion de Alquiler y Venta de Vehiculos\n\n" +
            "Desarrollado por: Raidel\n" +
            "Tecnologias: JavaFX 25 + PostgreSQL\n" +
            "Framework UI: AtlantaFX (Nord Dark)\n\n" +
            "Estado: En desarrollo\n" +
            "Fecha: 2025"
        );
    }

    ///////////////////////////////////////////////////////////////////////////////////////////// /
    /// ////////////////////------SALIR----------------//////////////////////////////
    /// //////////
    /// / /////////////////////////////////////////////////////////////////////////////////////
    @FXML
    void cerrarSesion(ActionEvent event) {
        boolean confirmado = AlertaEstilizada.confirmar(
            "Salir del programa",
            "Saliendo del Programa",
            "¿Seguro que quieres salir del programa?"
        );

        if (confirmado) {
            // Desactivar sesión antes de cerrar
            sesionActiva = false;
            cliente = null;
            detenerRefrescoMatriculas();
            if (btnIrAInicio.getScene() != null && btnIrAInicio.getScene().getWindow() != null) {
                Stage escena = (Stage) btnIrAInicio.getScene().getWindow();
                escena.close();
            }
        }
    }

    /////////////////////////////////////////////////////////// CAMBIAR ENTRE
    /// PANELES /////////////////////////////////////////////
    @FXML
    void irAInicio(ActionEvent event) {
        // Si hay sesión activa, mostrar el perfil del cliente en lugar de la selección
        if (sesionActiva && cliente != null) {
            mostrarPanel(pantallaSesion);
        } else {
            mostrarPanel(panelRegistro);
        }
        cambiarColores.cambiar(btnIrAInicio, btnAlquiler, btnCompras, btnIrAIngreso, btnAgentes, btnChat);
    }

    @FXML
    void cerrarSesionCliente(ActionEvent event) {
        // Confirmar antes de cerrar sesión
        boolean confirmado = AlertaEstilizada.confirmar(
            "Cerrar sesión",
            "¿Cerrar sesión del cliente?",
            "Se cerrará la sesión de " + (cliente != null ? cliente.getNombre() + " " + cliente.getApellidos() : "cliente actual") +
            ".\n\n¿Deseas continuar?"
        );

        if (!confirmado) {
            return;
        }

        // Limpiar datos de sesión
        sesionActiva = false;
        cliente = null;
        detenerRefrescoMatriculas();
        matriculaSeleccionada = null;

        // Limpiar labels del perfil
        lblNombre.setText("Nombre");
        lblNumero.setText("Numero");
        lblEmail.setText("Email");
        lblSexo.setText("DNI");
        lblEdad.setText("Dirección");

        // Limpiar información del vehículo
        lblMarca.setText("Marca");
        lblModelo.setText("Modelo");
        lblAño.setText("Año");
        lblKms.setText("Kilometraje");
        lblNBastidor.setText("Numero de Bastidor");

        // Mostrar panel de selección de cliente
        mostrarPanel(panelRegistro);

        AlertaEstilizada.mostrarInformacion(
            "Sesión cerrada",
            "Sesión cerrada correctamente",
            "Puedes seleccionar otro cliente o crear uno nuevo"
        );
    }

    @FXML
    void irAalquiler(ActionEvent event) {
        if (!sesionActiva) {
            AlertaEstilizada.mostrarAdvertencia(
                "Acceso denegado",
                "Sesión no iniciada",
                "Debes seleccionar un cliente antes de acceder al módulo de alquiler"
            );
            return;
        }
        mostrarPanel(pantallaAlquiler);
        cambiarColores.cambiar(btnAlquiler, btnIrAInicio, btnCompras, btnIrAIngreso, btnAgentes, btnChat);
    }

    @FXML
    void irAcompras(ActionEvent event) {
        if (!sesionActiva) {
            AlertaEstilizada.mostrarAdvertencia(
                "Acceso denegado",
                "Sesión no iniciada",
                "Debes seleccionar un cliente antes de acceder al módulo de compras"
            );
            return;
        }
        mostrarPanel(panelVentas);
        cambiarColores.cambiar(btnCompras, btnIrAInicio, btnAlquiler, btnIrAIngreso, btnAgentes, btnChat);
    }

    @FXML
    void irAIngresos(ActionEvent event) {
        mostrarPanel(panelIngreso);
        cambiarColores.cambiar(btnIrAIngreso, btnCompras, btnIrAInicio, btnAlquiler, btnAgentes, btnChat);
    }

    @FXML
    void irAAgentes(ActionEvent event) {
        if (!AlertaEstilizada.mostrarLoginAgentes()) {
            return;
        }
        refrescarTablaAgentes();
        mostrarPanel(panelAgentes);
        cambiarColores.cambiar(btnAgentes, btnIrAInicio, btnAlquiler, btnCompras, btnIrAIngreso, btnChat);
    }

    private boolean chatScrollListenerActivo = false;

    @FXML
    void irAChat(ActionEvent event) {
        mostrarPanel(panelChat);
        cambiarColores.cambiar(btnChat, btnIrAInicio, btnAlquiler, btnCompras, btnIrAIngreso, btnAgentes);

        // Registrar listener de auto-scroll solo una vez
        if (!chatScrollListenerActivo) {
            vboxMensajes.heightProperty().addListener((obs, oldVal, newVal) ->
                javafx.application.Platform.runLater(() -> scrollChat.setVvalue(1.0)));
            chatScrollListenerActivo = true;
        }

        // Bloquear input hasta que el contexto est\u00e9 listo
        txtMensajeChat.setDisable(true);
        btnEnviarChat.setDisable(true);
        lblEstadoChat.setText("Cargando datos...");

        new Thread(() -> {
            String contexto = construirContextoGemini();
            geminiService.setContexto(contexto);
            javafx.application.Platform.runLater(() -> {
                lblEstadoChat.setText("\u2713 Datos cargados");
                txtMensajeChat.setDisable(false);
                btnEnviarChat.setDisable(false);
                txtMensajeChat.requestFocus();
            });
        }).start();
    }

    @FXML
    void enviarMensajeChat(ActionEvent event) {
        String mensaje = txtMensajeChat.getText().trim();
        if (mensaje.isEmpty()) return;

        agregarMensajeChat(mensaje, true);
        txtMensajeChat.clear();
        btnEnviarChat.setDisable(true);
        lblEstadoChat.setText("Pensando...");

        new Thread(() -> {
            try {
                String respuesta = geminiService.enviarMensaje(mensaje);
                javafx.application.Platform.runLater(() -> {
                    agregarMensajeChat(respuesta, false);
                    btnEnviarChat.setDisable(false);
                    lblEstadoChat.setText("");
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    agregarMensajeChat("Error: " + e.getMessage(), false);
                    btnEnviarChat.setDisable(false);
                    lblEstadoChat.setText("Error");
                });
            }
        }).start();
    }

    @FXML
    void limpiarChat(ActionEvent event) {
        vboxMensajes.getChildren().clear();
        geminiService.limpiarHistorial();
        lblEstadoChat.setText("");
        agregarMensajeChat("Hola, soy el asistente de AutoReach. Puedes preguntarme sobre " +
                "clientes, coches, alquileres y agentes del sistema.", false);
    }

    private void agregarMensajeChat(String texto, boolean esUsuario) {
        javafx.scene.control.Label burbuja = new javafx.scene.control.Label(texto);
        burbuja.setWrapText(true);
        burbuja.setMaxWidth(560);
        burbuja.setStyle(esUsuario
                ? "-fx-background-color: #0076a8; -fx-text-fill: white; " +
                  "-fx-padding: 12 16; -fx-background-radius: 12; " +
                  "-fx-font-family: 'Segoe UI'; -fx-font-size: 12;"
                : "-fx-background-color: #1a1a2e; -fx-text-fill: #e0e6f0; " +
                  "-fx-padding: 12 16; -fx-background-radius: 12; " +
                  "-fx-font-family: 'Segoe UI'; -fx-font-size: 12;");

        javafx.scene.layout.HBox contenedor = new javafx.scene.layout.HBox(burbuja);
        contenedor.setAlignment(esUsuario
                ? javafx.geometry.Pos.CENTER_RIGHT
                : javafx.geometry.Pos.CENTER_LEFT);

        vboxMensajes.getChildren().add(contenedor);

        javafx.application.Platform.runLater(() ->
            scrollChat.setVvalue(1.0));
    }

    private String construirContextoGemini() {
        StringBuilder ctx = new StringBuilder();
        ctx.append("Eres el asistente inteligente de AutoReach, sistema de gestión de alquiler y venta de vehículos.\n");
        ctx.append("REGLAS OBLIGATORIAS — debes seguirlas siempre sin excepción:\n");
        ctx.append("1. Responde ÚNICAMENTE en español, sin importar el idioma del usuario.\n");
        ctx.append("2. Usa SOLO texto plano. Prohibido usar markdown: sin asteriscos, sin #, sin guiones de lista, sin negrita, sin cursiva.\n");
        ctx.append("3. Solo responde preguntas sobre AutoReach (clientes, coches, alquileres, agentes). Si preguntan otra cosa, di exactamente: 'Solo puedo ayudarte con información del sistema AutoReach.'\n");
        ctx.append("4. Usa los datos que te proporciono. No inventes información.\n");
        ctx.append("5. Para estadísticas o totales, calcula con los datos disponibles.\n");
        ctx.append("6. Los coches en 'COCHES DISPONIBLES' están libres ahora mismo.\n\n");

        try {
            // ── CLIENTES ──────────────────────────────────────────────
            javafx.collections.ObservableList<com.javafx.Model.Cliente> clientes =
                    com.javafx.JDBC.Conexion.mostrarClientes();
            ctx.append("=== CLIENTES REGISTRADOS (").append(clientes.size()).append(") ===\n");
            for (com.javafx.Model.Cliente c : clientes) {
                ctx.append("ID:").append(c.getId())
                   .append(" | ").append(c.getNombre()).append(" ").append(c.getApellidos())
                   .append(" | DNI: ").append(c.getDni())
                   .append(" | Tel: ").append(c.getTelefono())
                   .append(" | Email: ").append(c.getEmail())
                   .append(" | Dirección: ").append(c.getDireccion())
                   .append("\n");
            }

            // ── COCHES DISPONIBLES ────────────────────────────────────
            javafx.collections.ObservableList<com.javafx.Model.Coche> coches =
                    com.javafx.JDBC.Conexion.mostrarCoches();
            ctx.append("\n=== COCHES DISPONIBLES (").append(coches.size()).append(") ===\n");
            for (com.javafx.Model.Coche c : coches) {
                ctx.append("Matrícula: ").append(c.getMatricula())
                   .append(" | ").append(c.getMarca()).append(" ").append(c.getModelo())
                   .append(" | Año: ").append(c.getAño())
                   .append(" | Color: ").append(c.getColor())
                   .append(" | Km: ").append(c.getKms())
                   .append(" | Precio: ").append(c.getPrecio()).append("€")
                   .append(" | Bastidor: ").append(c.getBastidor())
                   .append("\n");
            }

            // Mapa DNI → nombre cliente para cruzar datos
            java.util.Map<String, String> nombrePorDni = new java.util.HashMap<>();
            for (com.javafx.Model.Cliente c : clientes) {
                nombrePorDni.put(c.getDni(), c.getNombre() + " " + c.getApellidos());
            }

            // ── ALQUILERES ACTIVOS ────────────────────────────────────
            javafx.collections.ObservableList<com.javafx.Model.Alquiler> alquileres =
                    com.javafx.JDBC.Conexion.mostrarAlquiler();
            ctx.append("\n=== ALQUILERES ACTIVOS (").append(alquileres.size()).append(") ===\n");
            for (com.javafx.Model.Alquiler a : alquileres) {
                String nombreCliente = nombrePorDni.getOrDefault(a.getDni(), a.getDni());
                ctx.append("Matrícula: ").append(a.getMatricula())
                   .append(" | Cliente: ").append(nombreCliente).append(" (DNI: ").append(a.getDni()).append(")")
                   .append(" | Recogida: ").append(a.getFechaRecogida())
                   .append(" | Devolución prevista: ").append(a.getFechaDevolucion())
                   .append(" | Hora: ").append(a.getHoraDevolucion())
                   .append("\n");
            }

            // ── TRANSACCIONES DE VENTA ────────────────────────────────
            javafx.collections.ObservableList<com.javafx.Model.Transaccion> ventas =
                    com.javafx.JDBC.Conexion.obtenerTodasLasTransacciones("VENTA");
            ctx.append("\n=== TRANSACCIONES DE VENTA (").append(ventas.size()).append(") ===\n");
            for (com.javafx.Model.Transaccion t : ventas) {
                ctx.append("ID:").append(t.getId())
                   .append(" | Matrícula: ").append(t.getMatricula())
                   .append(" | Cliente: ").append(t.getCliente())
                   .append(" | Agente: ").append(t.getAgente().isBlank() ? "Sin asignar" : t.getAgente())
                   .append(" | Monto: ").append(t.getMonto())
                   .append(" | Estado pago: ").append(t.getEstadoPago())
                   .append(" | Fecha: ").append(t.getFecha())
                   .append("\n");
            }

            // ── TRANSACCIONES DE ALQUILER ─────────────────────────────
            javafx.collections.ObservableList<com.javafx.Model.Transaccion> transAlquileres =
                    com.javafx.JDBC.Conexion.obtenerTodasLasTransacciones("ALQUILER");
            ctx.append("\n=== TRANSACCIONES DE ALQUILER (").append(transAlquileres.size()).append(") ===\n");
            for (com.javafx.Model.Transaccion t : transAlquileres) {
                ctx.append("ID:").append(t.getId())
                   .append(" | Matrícula: ").append(t.getMatricula())
                   .append(" | Cliente: ").append(t.getCliente())
                   .append(" | Agente: ").append(t.getAgente().isBlank() ? "Sin asignar" : t.getAgente())
                   .append(" | Monto: ").append(t.getMonto())
                   .append(" | Estado pago: ").append(t.getEstadoPago())
                   .append(" | Fecha: ").append(t.getFecha())
                   .append("\n");
            }

            // ── AGENTES ───────────────────────────────────────────────
            javafx.collections.ObservableList<com.javafx.Model.Agente> agentes =
                    com.javafx.JDBC.Conexion.mostrarAgentes();
            ctx.append("\n=== AGENTES DEL SISTEMA (").append(agentes.size()).append(") ===\n");
            for (com.javafx.Model.Agente a : agentes) {
                ctx.append("ID:").append(a.getId())
                   .append(" | ").append(a.getNombre()).append(" ").append(a.getApellidos())
                   .append(" | Usuario: ").append(a.getUsername())
                   .append(" | Email: ").append(a.getEmail())
                   .append(" | Tel: ").append(a.getTelefono())
                   .append(" | Activo: ").append(a.isActivo() ? "Sí" : "No")
                   .append("\n");
            }

            // ── RESUMEN ESTADÍSTICO ───────────────────────────────────
            ctx.append("\n=== RESUMEN ESTADÍSTICO ===\n");
            ctx.append("Total clientes: ").append(clientes.size()).append("\n");
            ctx.append("Coches disponibles ahora: ").append(coches.size()).append("\n");
            ctx.append("Alquileres activos: ").append(alquileres.size()).append("\n");
            ctx.append("Ventas registradas: ").append(ventas.size()).append("\n");
            ctx.append("Transacciones de alquiler registradas: ").append(transAlquileres.size()).append("\n");
            ctx.append("Agentes activos: ")
               .append(agentes.stream().filter(com.javafx.Model.Agente::isActivo).count())
               .append(" de ").append(agentes.size()).append(" totales\n");

        } catch (Exception e) {
            ctx.append("[Error al cargar datos: ").append(e.getMessage()).append("]");
        }

        return ctx.toString();
    }

    @FXML
    void guardarAgente(ActionEvent event) {
        String username  = txtAgenteUsername.getText().trim();
        String password  = pfAgentePassword.getText();
        String nombre    = txtAgenteNombre.getText().trim();
        String apellidos = txtAgenteApellidos.getText().trim();
        String telefono  = txtAgenteTelefono.getText().trim();
        String email     = txtAgenteEmail.getText().trim();

        boolean hayErrores = false;
        if (username.isEmpty()) {
            aplicarEfectoShake(txtAgenteUsername);
            aplicarEstiloError(txtAgenteUsername);
            hayErrores = true;
        }
        if (nombre.isEmpty()) {
            aplicarEfectoShake(txtAgenteNombre);
            aplicarEstiloError(txtAgenteNombre);
            hayErrores = true;
        }
        if (apellidos.isEmpty()) {
            aplicarEfectoShake(txtAgenteApellidos);
            aplicarEstiloError(txtAgenteApellidos);
            hayErrores = true;
        }
        if (hayErrores) {
            AlertaEstilizada.mostrarAdvertencia("Campos incompletos",
                    "Datos requeridos", "Usuario, nombre y apellidos son obligatorios");
            return;
        }

        if (agenteEnEdicion == null && password.isEmpty()) {
            aplicarEfectoShake(pfAgentePassword);
            aplicarEstiloError(pfAgentePassword);
            AlertaEstilizada.mostrarAdvertencia("Contraseña requerida",
                    "Dato requerido", "La contraseña es obligatoria al crear un agente");
            return;
        }

        if (!password.isEmpty() && !password.matches(".{6,}")) {
            aplicarEfectoShake(pfAgentePassword);
            aplicarEstiloError(pfAgentePassword);
            AlertaEstilizada.mostrarAdvertencia("Contraseña débil",
                    "Longitud mínima", "La contraseña debe tener al menos 6 caracteres");
            return;
        }

        if (!txtAgenteTelefono.getText().trim().isEmpty() &&
                !txtAgenteTelefono.getText().trim().matches("\\d{9,15}")) {
            aplicarEfectoShake(txtAgenteTelefono);
            aplicarEstiloError(txtAgenteTelefono);
            AlertaEstilizada.mostrarAdvertencia("Teléfono inválido",
                    "Formato incorrecto", "El teléfono debe tener entre 9 y 15 dígitos");
            return;
        }

        if (!txtAgenteEmail.getText().trim().isEmpty() &&
                !txtAgenteEmail.getText().trim().matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
            aplicarEfectoShake(txtAgenteEmail);
            aplicarEstiloError(txtAgenteEmail);
            AlertaEstilizada.mostrarAdvertencia("Email inválido",
                    "Formato incorrecto", "Introduce un email válido (ejemplo: nombre@dominio.com)");
            return;
        }

        if (agenteEnEdicion != null) {
            agenteEnEdicion.setUsername(username);
            agenteEnEdicion.setNombre(nombre);
            agenteEnEdicion.setApellidos(apellidos);
            agenteEnEdicion.setTelefono(telefono);
            agenteEnEdicion.setEmail(email);
            try {
                Conexion.editarAgente(agenteEnEdicion, password.isEmpty() ? null : password);
                AlertaEstilizada.mostrarInformacion("Agente actualizado", "Éxito",
                        "Los datos del agente han sido actualizados");
                agenteEnEdicion = null;
                limpiarFormularioAgente();
                refrescarTablaAgentes();
            } catch (Exception e) {
                AlertaEstilizada.mostrarError("Error al actualizar agente",
                        "No se pudo actualizar", e.getMessage());
            }
        } else {
            try {
                Conexion.crearAgente(username, password, nombre, apellidos, telefono, email);
                AlertaEstilizada.mostrarInformacion("Agente creado", "Éxito",
                        "El agente " + username + " ha sido creado correctamente");
                limpiarFormularioAgente();
                refrescarTablaAgentes();
            } catch (Exception e) {
                AlertaEstilizada.mostrarError("Error al crear agente",
                        "No se pudo crear", e.getMessage());
            }
        }
    }

    @FXML
    void cancelarNuevoAgente(ActionEvent event) {
        agenteEnEdicion = null;
        limpiarFormularioAgente();
    }

    @FXML
    void editarAgente(ActionEvent event) {
        Agente seleccionado = tablaAgentes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertaEstilizada.mostrarAdvertencia("Sin selección",
                    "Ningún agente seleccionado", "Selecciona un agente de la tabla para editar");
            return;
        }

        agenteEnEdicion = seleccionado;
        txtAgenteUsername.setText(seleccionado.getUsername());
        txtAgenteNombre.setText(seleccionado.getNombre());
        txtAgenteApellidos.setText(seleccionado.getApellidos());
        txtAgenteTelefono.setText(seleccionado.getTelefono());
        txtAgenteEmail.setText(seleccionado.getEmail());
        pfAgentePassword.clear();
        pfAgentePassword.setPromptText("Dejar vacío para no cambiar");

        AlertaEstilizada.mostrarInformacion("Editar Agente",
                "Datos cargados", "Modifica los campos y pulsa Guardar para guardar los cambios");
    }

    @FXML
    void eliminarAgente(ActionEvent event) {
        Agente seleccionado = tablaAgentes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertaEstilizada.mostrarAdvertencia("Sin selección",
                    "Ningún agente seleccionado", "Selecciona un agente de la tabla para eliminar");
            return;
        }

        boolean confirmado = AlertaEstilizada.confirmar(
                "Eliminar Agente",
                "¿Estás seguro?",
                "Se eliminará permanentemente el agente " + seleccionado.getUsername() +
                ".\n\nEsta acción no se puede deshacer."
        );

        if (!confirmado) return;

        Conexion.eliminarAgente(seleccionado.getId());
        AlertaEstilizada.mostrarInformacion("Agente eliminado", "Éxito",
                "El agente " + seleccionado.getUsername() + " ha sido eliminado");
        limpiarFormularioAgente();
        refrescarTablaAgentes();
    }

    @FXML
    void refrescarAgentes(ActionEvent event) {
        refrescarTablaAgentes();
        AlertaEstilizada.mostrarInformacion("Lista actualizada", "Éxito",
                "La lista de agentes se ha actualizado correctamente");
    }

    @FXML
    void vistaPreviaAgente(ActionEvent event) {
        Agente seleccionado = tablaAgentes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertaEstilizada.mostrarAdvertencia("Sin selección",
                    "Ningún agente seleccionado", "Selecciona un agente de la tabla para previsualizar su informe");
            return;
        }

        try {
            String htmlContent = GeneradorInformes.generarHtmlInformeAgente(seleccionado);
            Stage reporteStage = new Stage();
            reporteStage.setTitle("Informe de Agente - " + seleccionado.getUsername());
            reporteStage.initModality(Modality.WINDOW_MODAL);
            if (btnVistaPreviaAgente.getScene() != null && btnVistaPreviaAgente.getScene().getWindow() != null) {
                reporteStage.initOwner(btnVistaPreviaAgente.getScene().getWindow());
            }

            WebView webView = new WebView();
            webView.getEngine().loadContent(htmlContent);

            Scene scene = new Scene(webView, 700, 500);
            reporteStage.setScene(scene);
            reporteStage.show();

        } catch (Exception e) {
            AlertaEstilizada.mostrarError("Error al generar informe",
                    "No se pudo generar la vista previa", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void descargarPDFAgente(ActionEvent event) {
        Agente seleccionado = tablaAgentes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            AlertaEstilizada.mostrarAdvertencia("Sin selección",
                    "Ningún agente seleccionado", "Selecciona un agente de la tabla para descargar su informe");
            return;
        }

        boolean confirmado = AlertaEstilizada.confirmar(
                "Descargar Informe",
                "¿Descargar PDF?",
                "Se generará un informe PDF del agente " + seleccionado.getUsername() +
                ".\n\nSe guardará en Documents/AutoReach/Informer/"
        );

        if (!confirmado) return;

        try {
            File archivoDestino = GeneradorInformes.exportarInformeAgentePDF(seleccionado);
            if (archivoDestino != null) {
                AlertaEstilizada.mostrarInformacion("Informe exportado", "Éxito",
                        "El PDF se ha guardado en:\n" + archivoDestino.getAbsolutePath());
            } else {
                AlertaEstilizada.mostrarError("Error al exportar PDF",
                        "No se pudo generar el PDF", "Ocurrió un error al generar el informe");
            }
        } catch (Exception e) {
            AlertaEstilizada.mostrarError("Error al exportar PDF",
                    "No se pudo generar el PDF", e.getMessage());
            e.printStackTrace();
        }
    }

    private void refrescarTablaAgentes() {
        tablaAgentes.setItems(Conexion.mostrarAgentes());
    }

    private void limpiarFormularioAgente() {
        txtAgenteUsername.clear();
        pfAgentePassword.clear();
        pfAgentePassword.setPromptText("Contraseña");
        txtAgenteNombre.clear();
        txtAgenteApellidos.clear();
        txtAgenteTelefono.clear();
        txtAgenteEmail.clear();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////// -
    /// ------
    /// - ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////////////
    ///
    /// FUNCIONES AUXILIARES

    private void llenarMenuMarcas() {
        List<String> lista = Conexion.obtenerMarcas();

        for (int i = 0; i < lista.size(); i++) {
            String nombreMarca = lista.get(i);

            // Verificar si la marca ya existe en el menú
            boolean marcaExiste = mnElegirMarca.getItems().stream()
                    .anyMatch(item -> item.getText().equals(nombreMarca));

            if (!marcaExiste) {
                MenuItem m = new MenuItem(nombreMarca);

                // Cuando se pulse, actualizar el texto del MenuButton y cargar modelos
                m.setOnAction(e -> {
                    mnElegirMarca.setText(nombreMarca);
                    System.out.println("Marca seleccionada: " + nombreMarca);
                    // Habilitar y cargar modelos para esta marca
                    cargarModelosPorMarca(nombreMarca);
                });

                mnElegirMarca.getItems().add(m);
            }
        }
    }

    /**
     * Llena el MenuButton de marcas para la pantalla de compra
     */
    private void llenarMenuMarcasCompra() {
        List<String> lista = Conexion.obtenerMarcas();

        for (int i = 0; i < lista.size(); i++) {
            String nombreMarca = lista.get(i);

            // Verificar si la marca ya existe en el menú
            boolean marcaExiste = mnMarcaCompra.getItems().stream()
                    .anyMatch(item -> item.getText().equals(nombreMarca));

            if (!marcaExiste) {
                MenuItem m = new MenuItem(nombreMarca);

                // Cuando se pulse, actualizar el texto del MenuButton y cargar modelos
                m.setOnAction(e -> {
                    mnMarcaCompra.setText(nombreMarca);
                    // Habilitar y cargar modelos para esta marca
                    cargarModelosPorMarcaCompra(nombreMarca);
                    // Actualizar tabla automáticamente al seleccionar marca
                    actualizarTablaCochesCompra();
                });

                mnMarcaCompra.getItems().add(m);
            }
        }
    }

    /**
     * Carga los modelos disponibles para una marca específica en el ComboBox
     */
    private boolean modelosAlquilerListenersInit = false;
    private final boolean[] actualizandoModelo = {false};

    private void cargarModelosPorMarca(String marca) {
        cmbElegirModelo.getSelectionModel().clearSelection();
        cmbElegirModelo.getEditor().clear();

        // Actualizar datos — los listeners ya capturan esta misma instancia
        modelosAlquilerData.setAll(Conexion.obtenerModelosPorMarca(marca));
        filteredModelosAlquiler.setPredicate(p -> true);

        cmbElegirModelo.setItems(filteredModelosAlquiler);
        cmbElegirModelo.setEditable(true);
        cmbElegirModelo.setDisable(false);

        if (modelosAlquilerListenersInit) return;
        modelosAlquilerListenersInit = true;

        TextField txtComboModelo = cmbElegirModelo.getEditor();

        txtComboModelo.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (event.getCharacter().equals(" ")) event.consume();
        });

        txtComboModelo.textProperty().addListener((obs, oldValue, newValue) -> {
            if (actualizandoModelo[0]) return;
            final String selected = cmbElegirModelo.getSelectionModel().getSelectedItem();
            if (newValue == null || newValue.trim().isEmpty()) {
                actualizandoModelo[0] = true;
                try { filteredModelosAlquiler.setPredicate(p -> true); } finally { actualizandoModelo[0] = false; }
                cmbElegirModelo.hide();
                return;
            }
            if (selected == null || !selected.equals(newValue)) {
                actualizandoModelo[0] = true;
                try {
                    filteredModelosAlquiler.setPredicate(p ->
                        p.toLowerCase().startsWith(newValue.toLowerCase().trim()) ||
                        p.toLowerCase().contains(newValue.toLowerCase().trim()));
                } finally { actualizandoModelo[0] = false; }
                cmbElegirModelo.setVisibleRowCount(5);
                cmbElegirModelo.show();
            } else {
                actualizandoModelo[0] = true;
                try { filteredModelosAlquiler.setPredicate(p -> true); } finally { actualizandoModelo[0] = false; }
            }
        });

        cmbElegirModelo.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                txtComboModelo.positionCaret(txtComboModelo.getText().length());
                buscarCoincidenciasAutomatico();
            }
        });
    }

    /**
     * Carga los modelos disponibles para una marca específica en el ComboBox de compra
     */
    private boolean modelosCompraListenersInit = false;
    private final boolean[] actualizandoCompra = {false};

    private void cargarModelosPorMarcaCompra(String marca) {
        cmbModeloCompra.getSelectionModel().clearSelection();
        cmbModeloCompra.getEditor().clear();

        modelosCompraData.setAll(Conexion.obtenerModelosPorMarca(marca));
        filteredModelosCompra.setPredicate(p -> true);

        cmbModeloCompra.setItems(filteredModelosCompra);
        cmbModeloCompra.setEditable(true);
        cmbModeloCompra.setDisable(false);

        if (modelosCompraListenersInit) return;
        modelosCompraListenersInit = true;

        TextField txtComboModeloCompra = cmbModeloCompra.getEditor();

        txtComboModeloCompra.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (event.getCharacter().equals(" ")) event.consume();
        });

        txtComboModeloCompra.textProperty().addListener((obs, oldValue, newValue) -> {
            if (actualizandoCompra[0]) return;
            final String selected = cmbModeloCompra.getSelectionModel().getSelectedItem();
            if (newValue == null || newValue.trim().isEmpty()) {
                actualizandoCompra[0] = true;
                try { filteredModelosCompra.setPredicate(p -> true); } finally { actualizandoCompra[0] = false; }
                cmbModeloCompra.hide();
                return;
            }
            if (selected == null || !selected.equals(newValue)) {
                actualizandoCompra[0] = true;
                try {
                    filteredModelosCompra.setPredicate(p ->
                        p.toLowerCase().startsWith(newValue.toLowerCase().trim()) ||
                        p.toLowerCase().contains(newValue.toLowerCase().trim()));
                } finally { actualizandoCompra[0] = false; }
                cmbModeloCompra.setVisibleRowCount(5);
                cmbModeloCompra.show();
            } else {
                actualizandoCompra[0] = true;
                try { filteredModelosCompra.setPredicate(p -> true); } finally { actualizandoCompra[0] = false; }
            }
        });

        cmbModeloCompra.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                txtComboModeloCompra.positionCaret(txtComboModeloCompra.getText().length());
                actualizarTablaCochesCompra();
            }
        });
    }

    /**
     * Configura el ComboBox de DNI con filtrado y autocompletado
     * Los DNIs se obtienen de la tabla de clientes
     */
    private void configurarComboDni() {
        // Crear lista filtrable con los DNIs de los clientes
        FilteredList<String> filteredDnis = new FilteredList<>(
            tablaRegistro.getItems().stream()
                .map(Cliente::getDni)
                .collect(javafx.collections.FXCollections::observableArrayList,
                         java.util.List::add,
                         java.util.List::addAll),
            p -> true
        );

        // Configurar el ComboBox con la lista filtrada
        comboDni.setItems(filteredDnis);
        comboDni.setEditable(true);

        // Obtener el editor del ComboBox (TextField interno)
        TextField txtComboDni = comboDni.getEditor();

        // EventFilter: bloquear el espacio para evitar problemas
        txtComboDni.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (event.getCharacter().equals(" ")) {
                event.consume(); // Bloquea que se escriba el espacio
            }
        });

        final boolean[] actualizandoDni = {false};

        // Listener para el texto: filtrar DNIs mientras se escribe
        txtComboDni.textProperty().addListener((obs, oldValue, newValue) -> {
            if (actualizandoDni[0]) return;
            final String selected = comboDni.getSelectionModel().getSelectedItem();

            if (newValue == null || newValue.trim().isEmpty()) {
                actualizandoDni[0] = true;
                try { filteredDnis.setPredicate(p -> true); } finally { actualizandoDni[0] = false; }
                comboDni.hide();
                return;
            }

            if (selected == null || !selected.equals(newValue)) {
                actualizandoDni[0] = true;
                try {
                    filteredDnis.setPredicate(p -> p.toLowerCase().startsWith(newValue.toLowerCase().trim()));
                } finally { actualizandoDni[0] = false; }
                comboDni.setVisibleRowCount(5);
                comboDni.show();
            } else {
                actualizandoDni[0] = true;
                try { filteredDnis.setPredicate(p -> true); } finally { actualizandoDni[0] = false; }
            }
        });

        // Listener para la selección: posicionar cursor al final y buscar cliente
        comboDni.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                txtComboDni.positionCaret(txtComboDni.getText().length());
                // Buscar y seleccionar el cliente con este DNI
                buscarYSeleccionarClientePorDni(newValue);
            }
        });
    }

    /**
     * Busca un cliente por DNI en la tabla y lo selecciona automáticamente
     */
    private void buscarYSeleccionarClientePorDni(String dni) {
        // Buscar el cliente en la tabla
        for (Cliente c : tablaRegistro.getItems()) {
            if (c.getDni().equals(dni)) {
                // Seleccionar el cliente en la tabla
                tablaRegistro.getSelectionModel().select(c);
                // Scroll para hacerlo visible
                tablaRegistro.scrollTo(c);

                // Asignar cliente seleccionado a la variable privada
                cliente = c;

                // Rellenar los labels con la información del cliente
                lblNombre.setText("Nombre: " + cliente.getNombre() + " " + cliente.getApellidos());
                lblNumero.setText("Teléfono: " + cliente.getTelefono());
                lblEmail.setText("Email: " + cliente.getEmail());
                lblSexo.setText("DNI: " + cliente.getDni());
                lblEdad.setText("Dirección: " + cliente.getDireccion());

                // Activar sesión
                sesionActiva = true;

                // Cargar matrículas de coches del cliente
                cargarMatriculasCliente();
                iniciarRefrescoMatriculas();

                // Mostrar el panel de sesión
                mostrarPanel(pantallaSesion);

                return;
            }
        }

        // Si no se encuentra, mostrar advertencia
        AlertaEstilizada.mostrarAdvertencia(
            "Cliente no encontrado",
            "DNI no válido",
            "No se encontró ningún cliente con el DNI: " + dni
        );
    }

    /**
     * Configura todas las tablas para que se deseleccionen al hacer clic fuera de ellas
     */
    private void configurarDeseleccionTablas() {
        // Ejecutar después de que se cargue la escena
        Platform.runLater(() -> {
            // Esperar a que las tablas estén completamente cargadas
            Platform.runLater(() -> {
                // Agregar listener global a la escena
                if (btnIrAInicio.getScene() != null) {
                    btnIrAInicio.getScene().setOnMousePressed(event -> {
                        // Verificar si el clic NO fue en ninguna tabla
                        if (!esClicEnAlgunaTabla(event.getTarget())) {
                            // Deseleccionar todas las tablas
                            deseleccionarTodasLasTablas();
                        }
                    });
                }
            });
        });
    }

    /**
     * Verifica si el clic fue dentro de alguna tabla
     */
    private boolean esClicEnAlgunaTabla(Object target) {
        return esHijoDeTabla(target, tablaRegistro) ||
               esHijoDeTabla(target, TablaCoches) ||
               esHijoDeTabla(target, tblAlquiler) ||
               esHijoDeTabla(target, TblCoincidencias);
    }

    /**
     * Deselecciona todas las tablas de la aplicación
     */
    private void deseleccionarTodasLasTablas() {
        if (tablaRegistro != null) {
            tablaRegistro.getSelectionModel().clearSelection();
        }
        if (TablaCoches != null) {
            TablaCoches.getSelectionModel().clearSelection();
        }
        if (tblAlquiler != null) {
            tblAlquiler.getSelectionModel().clearSelection();
        }
        if (TblCoincidencias != null) {
            TblCoincidencias.getSelectionModel().clearSelection();
        }
    }

    /**
     * Verifica si un nodo es hijo de una tabla
     */
    private boolean esHijoDeTabla(Object target, TableView<?> tabla) {
        if (!(target instanceof javafx.scene.Node)) {
            return false;
        }

        javafx.scene.Node nodo = (javafx.scene.Node) target;
        while (nodo != null) {
            if (nodo == tabla) {
                return true;
            }
            nodo = nodo.getParent();
        }
        return false;
    }

    private void deshabilitarBotonesCliente() {

        comboDni.setDisable(true);
        //btnSeleccionCLiente.setDisable(true);
        //btnEditarCliente.setDisable(true);
        //btnEliminarCliente.setDisable(true);
        //btnCancelarSeleccion.setDisable(true);
        txtNombre.setDisable(true);
        txtApellidos.setDisable(true);
        txtDireccion.setDisable(true);
        txtTelefono.setDisable(true);
        dpFechaNac.setDisable(true);
        txtDni.setDisable(true);
        txtEmail.setDisable(true);
        btnInvitarCliente.setDisable(true);
        btnAñadirNuevoCliente.setDisable(true);
        btnCancelarNuevoCliente.setDisable(true);
        rbMasculino.setDisable(true);
        rbFemenino.setDisable(true);

    }

    private void ocultarTodos() {
        panelVentas.setVisible(false);
        pantallaAlquiler.setVisible(false);
        pantallaSesion.setVisible(false);
        panelRegistro.setVisible(false);
        panelIngreso.setVisible(false);
        panelAgentes.setVisible(false);
        panelChat.setVisible(false);
    }

    private void mostrarPanel(Pane panel) {
        if (panelActual != null) {
            panelActual.setVisible(false);
        }
        panel.setVisible(true);
        panel.toFront();
        panelActual = panel;
    }

    private void desactivarBotones() {
        btnAlquiler.setDisable(true);
        btnIrAInicio.setDisable(true);
        btnCompras.setDisable(true);
        btnCerrarSesion.setDisable(true);
        btnIrAIngreso.setDisable(true);
        btnAgentes.setDisable(true);
        btnChat.setDisable(true);

    }

    private void iniciarTablas() {

        // Verificar que la conexión esté disponible antes de cargar datos
        if (!Conexion.isConexionActiva()) {
            System.err.println("✗ No se pueden cargar las tablas: conexión a base de datos no disponible");
            return;
        }

        try {
            tablaRegistro.setItems(Conexion.mostrarClientes());
            clnNombreRegistro.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            clnDniRegistro.setCellValueFactory(new PropertyValueFactory<>("dni"));

            tblAlquiler.setItems(Conexion.mostrarAlquiler());
            clnFechaAlq.setCellValueFactory(new PropertyValueFactory<>("fechaRecogidaStr"));
            clnHoraAlq.setCellValueFactory(new PropertyValueFactory<>("horaDevolucion"));
            clnDNIAlq.setCellValueFactory(new PropertyValueFactory<>("dni"));
            clnMatriculaAlq.setCellValueFactory(new PropertyValueFactory<>("matricula"));

            TablaCoches.setItems(Conexion.mostrarCoches());
            clnMarca.setCellValueFactory(new PropertyValueFactory<>("Marca"));
            clnModelo.setCellValueFactory(new PropertyValueFactory<>("Modelo"));
            clnAño.setCellValueFactory(new PropertyValueFactory<>("Año"));
            clnBastidor.setCellValueFactory(new PropertyValueFactory<>("Bastidor"));
            clnColor.setCellValueFactory(new PropertyValueFactory<>("Color"));

            // Actualizar gráfica de coches por año
            actualizarGraficaCochesPorAño();

            colAgenteUsuario.setCellValueFactory(new PropertyValueFactory<>("username"));
            colAgenteNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colAgenteEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        } catch (Exception e) {
            System.err.println("✗ Error al inicializar las tablas: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private void actualizarGraficaCochesPorAño() {
        if (chartCochesPorAño == null) return;

        // Limpiar datos anteriores
        chartCochesPorAño.getData().clear();

        // Obtener coches de la tabla
        ObservableList<Coche> coches = TablaCoches.getItems();
        if (coches == null || coches.isEmpty()) return;

        // Contar coches por año
        Map<String, Integer> cochesPorAño = new HashMap<>();
        for (Coche coche : coches) {
            String año = coche.getAño();
            cochesPorAño.put(año, cochesPorAño.getOrDefault(año, 0) + 1);
        }

        // Crear serie de datos
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Vehículos");

        // Ordenar años y agregar datos
        cochesPorAño.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> serie.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));

        chartCochesPorAño.getData().add(serie);
    }

    private void limpiarCliente() {
        txtNombre.clear();
        txtApellidos.clear();
        txtDireccion.clear();
        txtTelefono.clear();
        dpFechaNac.setValue(null);
        txtDni.clear();
        txtEmail.clear();
        if (toggleSexo != null) {
            toggleSexo.selectToggle(null);
        }
    }

    private void limpiarIngreso() {
        ingresoMarca.clear();
        ingresoModelo.clear();
        ingresoAño.clear();
        ingresoKms.clear();
        ingresoBastidor.clear();
        ingresoPrecio.clear();
        ingresoDllantas.clear();
        ingresoDiseñoLlantas.clear();
        ingresoColor.clear();
        ingresoMatricula.clear();
    }

    @FXML
    void setAzul(ActionEvent event) {
        sclColorCompra.setText(azul.getText());
    }

    @FXML
    void setBlanco(ActionEvent event) {
        sclColorCompra.setText(blanco.getText());
    }

    @FXML
    void setNegro(ActionEvent event) {
        sclColorCompra.setText(negro.getText());
    }

    @FXML
    void setGris(ActionEvent event) {
        sclColorCompra.setText(gris.getText());
    }

    /**
     * Aplica un efecto de shake (movimiento horizontal) a un control TextField.
     * Útil para indicar visualmente que un campo tiene un error o contenido incorrecto.
     *
     * @param textField TextField al que aplicar el efecto
     */
    private void aplicarEfectoShake(TextField textField) {
        if (textField == null) return;

        TranslateTransition shake = new TranslateTransition(Duration.millis(100), textField);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }

    /**
     * Aplica un estilo visual de error a un TextField.
     * Muestra un borde rojo y fondo ligeramente rojizo para indicar un error.
     *
     * @param textField TextField al que aplicar el estilo de error
     */
    private void aplicarEstiloError(TextField textField) {
        if (textField == null) return;

        textField.setStyle(
            "-fx-border-color: #ff0000; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 3px; " +
            "-fx-background-color: #ffcccc;"
        );
    }

    /**
     * Carga las matrículas de los coches asociados al cliente actual en el MenuButton
     */
    private void cargarMatriculasCliente() {
        if (cliente == null) return;

        // Limpiar el menú
        slcMatriculaLogin.getItems().clear();
        slcMatriculaLogin.setText("Matricula");

        // Obtener matrículas del cliente
        List<String> matriculas = Conexion.obtenerMatriculasPorCliente(cliente.getDni());

        // Agregar cada matrícula como MenuItem
        for (String matricula : matriculas) {
            MenuItem item = new MenuItem(matricula);
            item.setOnAction(e -> mostrarInfoCoche(matricula));
            slcMatriculaLogin.getItems().add(item);
        }

        // Si no hay matrículas, mostrar mensaje
        if (matriculas.isEmpty()) {
            lblMarca.setText("Sin vehículos asociados");
            lblModelo.setText("");
            lblAño.setText("");
            lblKms.setText("");
            lblNBastidor.setText("");
        }
    }

    private void iniciarRefrescoMatriculas() {
        detenerRefrescoMatriculas();
        refreshTask = refreshScheduler.scheduleAtFixedRate(() ->
            javafx.application.Platform.runLater(() -> {
                if (matriculaSeleccionada != null) mostrarInfoCoche(matriculaSeleccionada);
                else cargarMatriculasCliente();
            }),
        5, 5, TimeUnit.SECONDS);
    }

    private void detenerRefrescoMatriculas() {
        if (refreshTask != null && !refreshTask.isDone()) {
            refreshTask.cancel(false);
            refreshTask = null;
        }
    }

    /**
     * Muestra la información del coche en los labels correspondientes
     * @param matricula La matrícula del coche a mostrar
     */
    private void mostrarInfoCoche(String matricula) {
        matriculaSeleccionada = matricula;
        slcMatriculaLogin.setText(matricula);

        CocheCompleto coche = Conexion.buscarCochePorMatricula(matricula);

        if (coche != null) {
            lblMarca.setText("Marca: " + coche.getMarca());
            lblModelo.setText("Modelo: " + coche.getModelo());
            lblAño.setText("Año: " + coche.getAño());
            lblKms.setText("Kilometraje: " + coche.getKilometraje() + " km");
            lblNBastidor.setText("Bastidor: " + coche.getNumBastidor());

            // Cargar información adicional en la lista
            tablaDeInformacion.getItems().clear();
            tablaDeInformacion.getItems().add("Color: " + coche.getColor());
            tablaDeInformacion.getItems().add("Precio: " + coche.getPrecioBase() + "€");
            tablaDeInformacion.getItems().add("Diseño Llantas: " + coche.getDisenoLlantas());
            tablaDeInformacion.getItems().add("Diámetro Llantas: " + coche.getDiametroLlantas() + "\"");
            tablaDeInformacion.getItems().add("Disponible Venta: " + (coche.getDisponibleVentas() ? "Sí" : "No"));
            tablaDeInformacion.getItems().add("Disponible Alquiler: " + (coche.getDisponibleAlquiler() ? "Sí" : "No"));
        }
    }

    /**
     * Configura los menús contextuales (clic derecho) para todas las TableViews
     */
    private void configurarMenusContextuales() {
        // Menú contextual para tabla de Clientes
        configurarMenuContextualClientes();

        // Menú contextual para tabla de Alquileres
        configurarMenuContextualAlquileres();

        // Menú contextual para tabla de Coches
        configurarMenuContextualCoches();

        // Menú contextual para tabla de Coincidencias
        configurarMenuContextualCoincidencias();
    }

    /**
     * Configura el menú contextual para la tabla de Clientes
     */
    private void configurarMenuContextualClientes() {
        javafx.scene.control.ContextMenu contextMenu = new javafx.scene.control.ContextMenu();

        javafx.scene.control.MenuItem modificarItem = new javafx.scene.control.MenuItem("Modificar");
        modificarItem.setOnAction(e -> editarCliente(new ActionEvent()));

        javafx.scene.control.MenuItem eliminarItem = new javafx.scene.control.MenuItem("Eliminar");
        eliminarItem.setOnAction(e -> eliminarCliente(new ActionEvent()));

        contextMenu.getItems().addAll(modificarItem, eliminarItem);
        tablaRegistro.setContextMenu(contextMenu);
    }

    /**
     * Configura el menú contextual para la tabla de Alquileres
     */
    private void configurarMenuContextualAlquileres() {
        javafx.scene.control.ContextMenu contextMenu = new javafx.scene.control.ContextMenu();

        javafx.scene.control.MenuItem modificarItem = new javafx.scene.control.MenuItem("Modificar");
        modificarItem.setOnAction(e -> editarAlq(new ActionEvent()));

        javafx.scene.control.MenuItem eliminarItem = new javafx.scene.control.MenuItem("Eliminar");
        eliminarItem.setOnAction(e -> {
            Alquiler alquilerSeleccionado = tblAlquiler.getSelectionModel().getSelectedItem();
            if (alquilerSeleccionado != null) {
                boolean confirmado = AlertaEstilizada.confirmar(
                    "Eliminar Alquiler",
                    "¿Estás seguro?",
                    "¿Deseas eliminar este alquiler del vehículo " + alquilerSeleccionado.getMatricula() + "?"
                );
                if (confirmado) {
                    Conexion.eliminarAlquiler(alquilerSeleccionado.getMatricula());
                    tblAlquiler.getItems().remove(alquilerSeleccionado);
                    Conexion.liberarCoche(alquilerSeleccionado.getMatricula());
                    iniciarTablas();
                }
            }
        });

        contextMenu.getItems().addAll(modificarItem, eliminarItem);
        tblAlquiler.setContextMenu(contextMenu);
    }

    /**
     * Configura el menú contextual para la tabla de Coches
     */
    private void configurarMenuContextualCoches() {
        javafx.scene.control.ContextMenu contextMenu = new javafx.scene.control.ContextMenu();

        javafx.scene.control.MenuItem modificarItem = new javafx.scene.control.MenuItem("Modificar");
        modificarItem.setOnAction(e -> editarIngreso(new ActionEvent()));

        javafx.scene.control.MenuItem eliminarItem = new javafx.scene.control.MenuItem("Eliminar");
        eliminarItem.setOnAction(e -> {
            Coche cocheSeleccionado = TablaCoches.getSelectionModel().getSelectedItem();
            if (cocheSeleccionado != null) {
                boolean confirmado = AlertaEstilizada.confirmar(
                    "Eliminar Coche",
                    "¿Estás seguro?",
                    "¿Deseas eliminar el coche " + cocheSeleccionado.getMarca() + " " + cocheSeleccionado.getModelo() + " (" + cocheSeleccionado.getMatricula() + ")?"
                );
                if (confirmado) {
                    try {
                        Conexion.borrarCoche(cocheSeleccionado.getBastidor());
                        TablaCoches.getItems().remove(cocheSeleccionado);
                        iniciarTablas();
                        AlertaEstilizada.mostrarInformacion("Coche eliminado", "Éxito", "El coche ha sido eliminado correctamente");
                    } catch (Exception ex) {
                        AlertaEstilizada.mostrarError("Error al eliminar", "Error", "No se pudo eliminar el coche: " + ex.getMessage());
                    }
                }
            }
        });

        contextMenu.getItems().addAll(modificarItem, eliminarItem);
        TablaCoches.setContextMenu(contextMenu);
    }

    /**
     * Configura el menú contextual para la tabla de Coincidencias
     */
    private void configurarMenuContextualCoincidencias() {
        javafx.scene.control.ContextMenu contextMenu = new javafx.scene.control.ContextMenu();

        javafx.scene.control.MenuItem eliminarItem = new javafx.scene.control.MenuItem("Eliminar");
        eliminarItem.setOnAction(e -> {
            Disponible disponibleSeleccionado = TblCoincidencias.getSelectionModel().getSelectedItem();
            if (disponibleSeleccionado != null) {
                boolean confirmado = AlertaEstilizada.confirmar(
                    "Eliminar Coche",
                    "¿Estás seguro?",
                    "¿Deseas eliminar el coche con matrícula " + disponibleSeleccionado.getMatricula() + "?"
                );
                if (confirmado) {
                    try {
                        // Buscar el coche completo para obtener el bastidor
                        CocheCompleto coche = Conexion.buscarCochePorMatricula(disponibleSeleccionado.getMatricula());
                        if (coche != null) {
                            Conexion.borrarCoche(coche.getNumBastidor());
                            TblCoincidencias.getItems().remove(disponibleSeleccionado);
                            iniciarTablas();
                            AlertaEstilizada.mostrarInformacion("Coche eliminado", "Éxito", "El coche ha sido eliminado correctamente");
                        } else {
                            AlertaEstilizada.mostrarError("Error", "Coche no encontrado", "No se encontró el coche con esa matrícula");
                        }
                    } catch (Exception ex) {
                        AlertaEstilizada.mostrarError("Error al eliminar", "Error", "No se pudo eliminar el coche: " + ex.getMessage());
                    }
                }
            }
        });

        contextMenu.getItems().add(eliminarItem);
        TblCoincidencias.setContextMenu(contextMenu);
    }

}
