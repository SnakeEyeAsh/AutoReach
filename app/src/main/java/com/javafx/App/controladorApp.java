package com.javafx.App;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;


import com.javafx.Data.Datos;
import com.javafx.JDBC.Conexion;
import com.javafx.Model.Alquiler;
import com.javafx.Model.Cliente;
import com.javafx.Model.Coche;
import com.javafx.Model.CocheCompleto;
import com.javafx.Model.CocheCompra;
import com.javafx.Model.Disponible;
import com.javafx.Model.ValidadorAlquiler;
import com.javafx.Model.ValidadorCliente;
import com.javafx.Model.ValidadorCoche;


import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.util.Duration;

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
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class controladorApp implements Initializable {

    private Pane panelActual;

    private Cliente cliente;

    private Boolean editarCliente = false;

    private boolean editandoAlquiler = false;

    private boolean sesionActiva = false;

    @FXML
    private TextField ingresoAño;

    @FXML
    private TextField ingresoBastidor;

    @FXML
    private TextField ingresoColor;

    @FXML
    private TextField ingresoDiseñoInterior;

    @FXML
    private TextField ingresoDiseñoLlantas;

    @FXML
    private TextField ingresoDllantas;

    @FXML
    private TextField ingresoKms;

    @FXML
    private TextField ingresoMarca;

    @FXML
    private TextField ingresoMaterial;

    @FXML
    private TextField ingresoMatricula;

    @FXML
    private TextField ingresoModelo;

    @FXML
    private TextField ingresoPrecio;

    @FXML
    private MenuItem lujo;

    @FXML
    private MenuItem confort;

    @FXML
    private MenuItem deportivo;

    @FXML
    private MenuItem alcantara;

    @FXML
    private MenuItem cuero;

    @FXML
    private MenuItem tela;

    @FXML
    private MenuItem blanco;

    @FXML
    private MenuItem negro;

    @FXML
    private MenuItem azul;

    @FXML
    private MenuItem gris;

    @FXML
    private MenuButton slcPatronCompra;

    @FXML
    private MenuButton slcMaterialCompra;

    @FXML
    private MenuButton mnMarcaCompra;

    @FXML
    private ComboBox<String> cmbModeloCompra;

    @FXML
    private TableView<CocheCompra> tblOpcionesCompra;

    @FXML
    private TableColumn<Coche, String> clnMarcaCompra;

    @FXML
    private TableColumn<Coche, String> clnModeloCompra;

    @FXML
    private TableColumn<Coche, String> clnPrecioCompra;

    @FXML
    private TableColumn<Coche, String> clnAñoCompra;

    @FXML
    private TableColumn<Coche, String> clnColorCompra;

    @FXML
    private TableColumn<Coche, String> clnDiametroCompra;

    @FXML
    private TableColumn<Coche, String> clnInteriorCompra;

    @FXML
    private TableColumn<Coche, String> clnMatIntCompra;

    @FXML
    private TableColumn<Coche, String> clnDiseñoRCompra;

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
    private Button btnEditarCoche;

    @FXML
    private MenuButton mnElegirMarca;

    @FXML
    private ComboBox<String> cmbElegirModelo;

    @FXML
    private ListView<Coche> listaCoincidencias;

    @FXML
    private TableView<Coche> TablaCoches;

    @FXML
    private MenuItem opFemenino;

    @FXML
    private MenuItem opMasculino;

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
    private Button btnBuscarCliente;

    @FXML
    private Button btnBuscarCompra;

    @FXML
    private Button btnCancelarBCompra;

    @FXML
    private Button btnCancelarCompra;

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

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    @FXML
    private MenuButton sclColorCompra;

    @FXML
    private MenuButton slcMatriculaLogin;

    @FXML
    private SplitMenuButton splitMenuSexo;

    @FXML
    private ListView<String> tablaDeInformacion;

    @FXML
    private TableView<Cliente> tablaRegistro;

    @FXML
    private TableView<Alquiler> tblAlquiler;

    @FXML
    private TextField txtApellidos;

    @FXML
    private TextField txtContraseña;

    @FXML
    private TextField txtDireccion;

    @FXML
    private TextField txtDni;

    @FXML
    private ComboBox<String> comboDni;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtFechaNac;

    @FXML
    private TextField txtHoraEntrega;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtUser;

    @FXML
    private VBox ventanaLogin;

    private ValidadorCliente validador;
    private ValidadorCoche validadorC;
    private ValidadorAlquiler validadorAlquiler;
    

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
                    s.getIcons().add(new Image("Assets/autoIcon.png"));

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
                            s.getIcons().add(new Image("Assets/autoIcon.png"));

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

        validadorAlquiler.inicializarValidadores(cldRecoger, cldEntrega, txtHoraEntrega);
        validadorC.inicializarValidadores(ingresoMarca, ingresoModelo, ingresoAño, ingresoKms, ingresoBastidor,
                ingresoPrecio, ingresoDllantas, ingresoDiseñoLlantas, ingresoColor, ingresoMaterial,
                ingresoDiseñoInterior, ingresoMatricula);
        validador.inicializarValidadores(txtNombre, txtApellidos, txtDireccion, txtTelefono, txtFechaNac, txtDni,
                txtEmail, splitMenuSexo);

        // Configurar tooltip para el campo de hora de entrega
        Tooltip tooltipHora = new Tooltip("Formato: HH:mm (ejemplo: 14:30, 09:15)");
        txtHoraEntrega.setTooltip(tooltipHora);

        // Configurar tooltips para campos de cliente
        txtFechaNac.setTooltip(new Tooltip("Formato: AAAA-MM-DD (ejemplo: 1990-05-15)"));
        txtEmail.setTooltip(new Tooltip("Formato: usuario@dominio.com (ejemplo: juan.perez@gmail.com)"));
        txtDireccion.setTooltip(new Tooltip("Dirección completa (ejemplo: Calle Mayor 123, Madrid)"));

        // Configurar tooltips para campos de ingreso de coches
        ingresoAño.setTooltip(new Tooltip("Año del vehículo (ejemplo: 2023)"));
        ingresoBastidor.setTooltip(new Tooltip("Número de bastidor VIN de 17 caracteres (ejemplo: 1HGBH41JXMN109186)"));
        ingresoColor.setTooltip(new Tooltip("Color del vehículo (ejemplo: Rojo, Azul Metalizado, Negro)"));
        ingresoDiseñoInterior.setTooltip(new Tooltip("Diseño interior (ejemplo: Deportivo, Clásico, Moderno)"));
        ingresoDiseñoLlantas.setTooltip(new Tooltip("Diseño de llantas (ejemplo: Aleación, Radial, Deportiva)"));
        ingresoDllantas.setTooltip(new Tooltip("Diámetro de llantas en pulgadas (ejemplo: 16, 17, 18)"));
        ingresoKms.setTooltip(new Tooltip("Kilometraje del vehículo (ejemplo: 15000.5)"));
        ingresoMarca.setTooltip(new Tooltip("Marca del vehículo (ejemplo: Toyota, BMW, Ford)"));
        ingresoMaterial.setTooltip(new Tooltip("Material del interior (ejemplo: Cuero, Tela, Alcantara)"));
        ingresoMatricula.setTooltip(new Tooltip("Matrícula del vehículo: 4 números + 3 letras (ejemplo: 1234ABC)"));
        ingresoModelo.setTooltip(new Tooltip("Modelo del vehículo (ejemplo: Corolla, Serie 3, Focus)"));
        ingresoPrecio.setTooltip(new Tooltip("Precio base del vehículo (ejemplo: 25000.00)"));

        btnIrAInicio.setStyle("-fx-background-color:darkgrey");
        iniciarTablas();
        ocultarTodos();
        desactivarBotones();
        mostrarPanel(paneLogin);
        deshabilitarBotonesCliente();
        llenarMenuMarcas();
        llenarMenuMarcasCompra(); // Llenar marcas para pantalla de compra
        configurarComboDni(); // Configurar ComboBox de DNI con filtrado
        btnEditarCoche.setDisable(true); // Deshabilitar btnEditarCoche inicialmente

        // Deshabilitar ComboBox de modelo hasta que se seleccione una marca
        cmbElegirModelo.setDisable(true);
        cmbElegirModelo.setEditable(true);

        // Deshabilitar ComboBox de modelo de compra hasta que se seleccione una marca
        cmbModeloCompra.setDisable(true);
        cmbModeloCompra.setEditable(true);

        cliente = new Cliente("Ana", "Rodriguez", "12345678B", 623162788,
                "ana.rodriguez@email.com", "Avenida de la Constitución 42, Barcelona");

        // Configurar deselección al hacer clic fuera de las tablas
        configurarDeseleccionTablas();

        // Configurar menús contextuales para las tablas
        configurarMenusContextuales();
    }

    //////////////////////////////////////////////////////////////// -----LOGIN-----
    /// - //////////////////////////////////////////////////////////////

    @FXML
    void IniciarSesion(ActionEvent event) {
        if (txtUser.getText().equals(Datos.ADMIN.toString())
                && txtContraseña.getText().equals(Datos.PASSWORD.toString())) {
            mostrarPanel(panelRegistro);
            btnAlquiler.setDisable(false);
            btnIrAInicio.setDisable(false);
            btnCerrarSesion.setDisable(false);
            btnIrAIngreso.setDisable(false);
            btnCompras.setDisable(false);

            // Limpiar estilos de error si el login fue exitoso
            txtUser.setStyle("");
            txtContraseña.setStyle("");
        } else {
            // Aplicar efecto shake y borde rojo a los campos con contenido incorrecto
            if (!txtUser.getText().equals(Datos.ADMIN.toString())) {
                aplicarEfectoShake(txtUser);
                aplicarEstiloError(txtUser);
            } else {
                // Limpiar estilo si el usuario es correcto
                txtUser.setStyle("");
            }

            if (!txtContraseña.getText().equals(Datos.PASSWORD.toString())) {
                aplicarEfectoShake(txtContraseña);
                aplicarEstiloError(txtContraseña);
            } else {
                // Limpiar estilo si la contraseña es correcta
                txtContraseña.setStyle("");
            }

            AlertaFallo.mostrar();
        }
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

        txtNombre.setDisable(false);
        txtApellidos.setDisable(false);
        txtDireccion.setDisable(false);
        txtTelefono.setDisable(false);
        txtFechaNac.setDisable(false);
        txtDni.setDisable(false);
        txtEmail.setDisable(false);
        btnAñadirNuevoCliente.setDisable(false);
        btnCancelarNuevoCliente.setDisable(false);
        splitMenuSexo.setDisable(false);
    }

    @FXML
    void añadirCliente(ActionEvent event) {

        // Validar el formulario antes de guardar
        if (validador.validarYMostrarErrores()) {

            String nombre = txtNombre.getText();
            String apellidos = txtApellidos.getText();
            String direccion = txtDireccion.getText();
            String telefono = txtTelefono.getText();
            String fechaNac = txtFechaNac.getText();
            String dni = txtDni.getText();
            String email = txtEmail.getText();
            String sexo = splitMenuSexo.getText();

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
            } catch (SQLException error) {
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

        txtNombre.setDisable(true);
        txtApellidos.setDisable(true);
        txtDireccion.setDisable(true);
        txtTelefono.setDisable(true);
        txtFechaNac.setDisable(true);
        txtDni.setDisable(true);
        txtEmail.setDisable(true);
        btnAñadirNuevoCliente.setDisable(true);
        btnCancelarNuevoCliente.setDisable(true);
        splitMenuSexo.setDisable(true);

    }

    @FXML
    void seleccionarCliente(ActionEvent event) {
        if (editarCliente) {
            // Validar formulario antes de guardar cambios
            if (validador.validarYMostrarErrores()) {
                try {
                    Cliente c = tablaRegistro.getSelectionModel().getSelectedItem();

                    // Actualizar datos del cliente con los valores del formulario
                    c.setNombre(txtNombre.getText());
                    c.setApellidos(txtApellidos.getText());
                    c.setDireccion(txtDireccion.getText());
                    c.setTelefono(Double.parseDouble(txtTelefono.getText().replaceAll("[\\s-+]", "")));
                    c.setEmail(txtEmail.getText());

                    Conexion.editarCliente(c);

                    AlertaEstilizada.mostrarInformacion(
                        "Cliente actualizado",
                        "Éxito",
                        "El cliente ha sido actualizado correctamente"
                    );

                    editarCliente = false;
                    limpiarCliente();
                    iniciarTablas();
                    deshabilitarBotonesCliente();

                } catch (NumberFormatException e) {
                    AlertaEstilizada.mostrarError(
                        "Error de formato",
                        "Teléfono inválido",
                        "El teléfono debe contener solo números"
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
                lblNumero.setText("Teléfono: " + (int)cliente.getTelefono());
                lblEmail.setText("Email: " + cliente.getEmail());
                lblSexo.setText("DNI: " + cliente.getDni());
                lblEdad.setText("Dirección: " + cliente.getDireccion());

                // Activar sesión
                sesionActiva = true;

                // Cargar matrículas de coches del cliente
                cargarMatriculasCliente();

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
                "Ningún cliente seleccionado",
                "Selecciona un cliente",
                "Por favor, selecciona un cliente de la tabla para editarlo"
            );
            return;
        }

        // Activar modo edición
        editarCliente = true;

        // Cargar datos del cliente en los campos
        txtNombre.setText(c.getNombre());
        txtNombre.setDisable(false);

        txtApellidos.setText(c.getApellidos());
        txtApellidos.setDisable(false);

        txtDireccion.setText(c.getDireccion());
        txtDireccion.setDisable(false);

        // Convertir teléfono a String sin decimales
        txtTelefono.setText(String.valueOf((int) c.getTelefono()));
        txtTelefono.setDisable(false);

        txtDni.setText(c.getDni());
        txtDni.setDisable(true); // DNI no se debe editar

        txtEmail.setText(c.getEmail());
        txtEmail.setDisable(false);

        // Deshabilitar otros botones durante la edición
        btnEliminarCliente.setDisable(true);
        btnCancelarSeleccion.setDisable(false);
        btnSeleccionCLiente.setDisable(false); // Este botón guardará los cambios
        btnEditarCliente.setDisable(true);
        btnNuevoCliente.setDisable(true);
        btnBuscarCliente.setDisable(true);
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
    }

    ////////////////////////// -----ELEGIR
    /// SEXO------- /////////////////////////////////

    @FXML
    void elegirFemenino(ActionEvent event) {
        splitMenuSexo.setText(opFemenino.getText());
    }

    @FXML
    void elegirMasculino(ActionEvent event) {
        splitMenuSexo.setText(opMasculino.getText());
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

                LocalDate fecha = cldRecoger.getValue();
                LocalDate fechaFin = cldEntrega.getValue();

                long dias = ChronoUnit.DAYS.between(fecha, fechaFin);
                double precioTotal = dias * Double.parseDouble(d.getPrecio());
                String estado = d.getDisponible().equals("si") ? "en_proceso" : "pendiente";
                if(d.getDisponible().toLowerCase().equals("si")){
                    try {

                    Conexion.crearAlquiler(
                            fecha.toString(),
                            fechaFin.toString(),
                            precioTotal + "",
                            d.getPrecio(),
                            estado,
                            1 + "",
                            txtHoraEntrega.getText(),
                            d.getMatricula(),
                            cliente.getDni());
                    } catch (SQLException e) {
                        System.out.println("Error al incertar cita desde controlador " + e.getMessage());
                    }finally{
                        Conexion.actualizarAlquilado(d.getMatricula());
                    }
                }else{
                    /////////ALERTA UNICA
                    AlertaEstilizada.mostrarError(
                        "Coche no disponible",
                        "Cuidado",
                        "No puedes alquilar un coche que no se encuentra disponible"
                    );
                }

            } else {
                try {
                    Alquiler a = tblAlquiler.getSelectionModel().getSelectedItem();
                    Timestamp t1 = Timestamp.valueOf(cldRecoger.getValue().atStartOfDay());
                    Timestamp t2 = Timestamp.valueOf(cldEntrega.getValue().atStartOfDay());

                    Conexion.editarAlquiler(t1, txtHoraEntrega.getText(), t2, a.getMatricula(), a.getDni());

                } catch (Exception e) {
                    System.out.println("Error al actualizar alquiler" + e.getMessage());
                }
                mnElegirMarca.setDisable(false);
                cmbElegirModelo.setDisable(false);
                cldRecoger.getEditor().clear();
                txtHoraEntrega.clear();
                cldEntrega.getEditor().clear();
                editandoAlquiler = false;

            }
        }

    }

    @FXML
    void cancelarAlquiler(ActionEvent event) {
        mnElegirMarca.setText("Marca");
        cmbElegirModelo.getSelectionModel().clearSelection();
        TblCoincidencias.getItems().clear();
        cldRecoger.getEditor().clear();
        txtHoraEntrega.clear();
        cldEntrega.getEditor().clear();
    }

    @FXML
    void editarAlq(ActionEvent event) {
        Alquiler a = tblAlquiler.getSelectionModel().getSelectedItem();

        if (a != null) {
            editandoAlquiler = true;

            mnElegirMarca.setDisable(true);
            cmbElegirModelo.setDisable(true);

            Timestamp fechaR = a.getFechaRecogida();
            Timestamp fechaE = a.getFechaDevolucion();
            String hora = a.getHoraDevolucion();

            cldRecoger.setValue(fechaR.toLocalDateTime().toLocalDate());
            cldEntrega.setValue(fechaE.toLocalDateTime().toLocalDate());
            txtHoraEntrega.setText(hora);

        } else {
            AlertaEstilizada.mostrarError(
                "Error",
                "Sin selección",
                "Debes seleccionar un alquiler para editarlo"
            );
        }

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

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ventanaInfo.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Compra");
            stage.initModality(Modality.WINDOW_MODAL); // hace modal
            stage.initOwner(btnCompra.getScene().getWindow()); // define dueño
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            System.out.println(e.getMessage() + "Error al efectuar Compra");
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
    void BuscarCompra(ActionEvent event) {
        // Simplemente ejecutar la búsqueda automática
        actualizarBusquedaCompraAutomatica();
    }

    @FXML
    void CancelarBusquedaCompra(ActionEvent event) {
        // Limpiar criterios de búsqueda
        mnMarcaCompra.setText("Marca");
        cmbModeloCompra.getSelectionModel().clearSelection();
        cmbModeloCompra.getEditor().clear();
        cmbModeloCompra.setDisable(true);
        slcPatronCompra.setText("Diseño Interior");
        slcMaterialCompra.setText("Material Interior");
        sclColorCompra.setText("Color");

        // Limpiar tabla
        tblOpcionesCompra.getItems().clear();
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
            String material = ingresoMaterial.getText();
            String diseñoInterior = ingresoDiseñoInterior.getText();
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
                        diseñoInterior,
                        material,
                        matricula);

                AlertaEstilizada.mostrarInformacion(
                    "Coche añadido",
                    "Éxito",
                    "El coche ha sido registrado correctamente"
                );
                limpiarIngreso();
                iniciarTablas();

            } catch (SQLException e) {
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
                "Ningún coche seleccionado",
                "Selecciona un coche",
                "Por favor, selecciona un coche de la tabla para editarlo"
            );
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
        ingresoMaterial.setText(cc.getMaterialInterior());
        ingresoDiseñoInterior.setText(cc.getDisenoInterior());
        ingresoMatricula.setText(cc.getMatricula());

    }

    @FXML
    void editarCoche(ActionEvent event) {
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
                    ingresoDiseñoInterior.getText(),
                    ingresoMaterial.getText(),
                    ingresoMatricula.getText());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        btnAceptarIngreso.setDisable(false);
        btnCancelarIngresos.setDisable(false);
        btnBorrarIngreso.setDisable(false);
        btnEditarCoche.setDisable(true); // Deshabilitar btnEditarCoche después de guardar

        limpiarIngreso();
        iniciarTablas();

    }

    @FXML
    void borrarIngreso(ActionEvent event) {
        Coche c = TablaCoches.getSelectionModel().getSelectedItem();

        // Validar que haya un coche seleccionado
        if (c == null) {
            AlertaEstilizada.mostrarAdvertencia(
                "Selección requerida",
                "No hay ningún coche seleccionado",
                "Por favor, seleccione un coche de la tabla antes de intentar eliminarlo."
            );
            return;
        }

        try {
            Conexion.borrarCoche(c.getBastidor());

        } catch (SQLException e) {
            System.out.println("Error al eliminar coche" + e.getMessage());
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
            Stage escena = (Stage) btnIrAInicio.getScene().getWindow();
            escena.close();
        }
    }

    /////////////////////////////////////////////////////////// CAMBIAR ENTRE
    /// PANELES /////////////////////////////////////////////
    @FXML
    void irAInicio(ActionEvent event) {
        mostrarPanel(panelRegistro);
        cambiarColores.cambiar(btnIrAInicio, btnAlquiler, btnCompras, btnIrAIngreso);
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
        cambiarColores.cambiar(btnAlquiler, btnIrAInicio, btnCompras, btnIrAIngreso);
    }

    @FXML
    void irAcompras(ActionEvent event) {
        mostrarPanel(panelVentas);
        cambiarColores.cambiar(btnCompras, btnIrAInicio, btnAlquiler, btnIrAIngreso);
    }

    @FXML
    void irAIngresos(ActionEvent event) {
        mostrarPanel(panelIngreso);
        cambiarColores.cambiar(btnIrAIngreso, btnCompras, btnIrAInicio, btnAlquiler);
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
                    System.out.println("Marca seleccionada para compra: " + nombreMarca);
                    // Habilitar y cargar modelos para esta marca
                    cargarModelosPorMarcaCompra(nombreMarca);
                    // Actualizar búsqueda automáticamente al seleccionar marca
                    actualizarBusquedaCompraAutomatica();
                });

                mnMarcaCompra.getItems().add(m);
            }
        }
    }

    /**
     * Carga los modelos disponibles para una marca específica en el ComboBox
     */
    private void cargarModelosPorMarca(String marca) {
        // Limpiar selección anterior
        cmbElegirModelo.getSelectionModel().clearSelection();
        cmbElegirModelo.getEditor().clear();

        // Obtener modelos de la marca seleccionada
        List<String> modelos = Conexion.obtenerModelosPorMarca(marca);

        // Crear lista filtrable con los modelos
        FilteredList<String> filteredModelos = new FilteredList<>(
            javafx.collections.FXCollections.observableArrayList(modelos),
            p -> true
        );

        // Configurar el ComboBox con la lista filtrada
        cmbElegirModelo.setItems(filteredModelos);
        cmbElegirModelo.setEditable(true);
        cmbElegirModelo.setDisable(false); // Habilitar el ComboBox

        // Obtener el editor del ComboBox
        TextField txtComboModelo = cmbElegirModelo.getEditor();

        // EventFilter: bloquear el espacio
        txtComboModelo.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (event.getCharacter().equals(" ")) {
                event.consume();
            }
        });

        // Listener para filtrar mientras se escribe
        txtComboModelo.textProperty().addListener((obs, oldValue, newValue) -> {
            final String selected = cmbElegirModelo.getSelectionModel().getSelectedItem();

            // Si está vacío, restaurar filtro y ocultar
            if (newValue == null || newValue.trim().isEmpty()) {
                filteredModelos.setPredicate(p -> true);
                cmbElegirModelo.hide();
                return;
            }

            // Si no hay nada seleccionado o se está escribiendo algo diferente
            if (selected == null || !selected.equals(newValue)) {
                // Filtrar modelos que empiecen o contengan el texto
                filteredModelos.setPredicate(p ->
                    p.toLowerCase().startsWith(newValue.toLowerCase().trim()) ||
                    p.toLowerCase().contains(newValue.toLowerCase().trim())
                );
                cmbElegirModelo.setVisibleRowCount(5);
                cmbElegirModelo.show();
            } else {
                filteredModelos.setPredicate(p -> true);
            }
        });

        // Listener para la selección - Ejecuta búsqueda automática
        cmbElegirModelo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                txtComboModelo.positionCaret(txtComboModelo.getText().length());
                // Ejecutar búsqueda automática
                buscarCoincidenciasAutomatico();
            }
        });
    }

    /**
     * Carga los modelos disponibles para una marca específica en el ComboBox de compra
     */
    private void cargarModelosPorMarcaCompra(String marca) {
        // Limpiar selección anterior
        cmbModeloCompra.getSelectionModel().clearSelection();
        cmbModeloCompra.getEditor().clear();

        // Obtener modelos de la marca seleccionada
        List<String> modelos = Conexion.obtenerModelosPorMarca(marca);

        // Crear lista filtrable con los modelos
        FilteredList<String> filteredModelos = new FilteredList<>(
            javafx.collections.FXCollections.observableArrayList(modelos),
            p -> true
        );

        // Configurar el ComboBox con la lista filtrada
        cmbModeloCompra.setItems(filteredModelos);
        cmbModeloCompra.setEditable(true);
        cmbModeloCompra.setDisable(false); // Habilitar el ComboBox

        // Obtener el editor del ComboBox
        TextField txtComboModeloCompra = cmbModeloCompra.getEditor();

        // EventFilter: bloquear el espacio
        txtComboModeloCompra.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (event.getCharacter().equals(" ")) {
                event.consume();
            }
        });

        // Listener para filtrar mientras se escribe
        txtComboModeloCompra.textProperty().addListener((obs, oldValue, newValue) -> {
            final String selected = cmbModeloCompra.getSelectionModel().getSelectedItem();

            // Si está vacío, restaurar filtro y ocultar
            if (newValue == null || newValue.trim().isEmpty()) {
                filteredModelos.setPredicate(p -> true);
                cmbModeloCompra.hide();
                return;
            }

            // Si no hay nada seleccionado o se está escribiendo algo diferente
            if (selected == null || !selected.equals(newValue)) {
                // Filtrar modelos que empiecen o contengan el texto
                filteredModelos.setPredicate(p ->
                    p.toLowerCase().startsWith(newValue.toLowerCase().trim()) ||
                    p.toLowerCase().contains(newValue.toLowerCase().trim())
                );
                cmbModeloCompra.setVisibleRowCount(5);
                cmbModeloCompra.show();
            } else {
                filteredModelos.setPredicate(p -> true);
            }
        });

        // Listener para la selección
        cmbModeloCompra.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                txtComboModeloCompra.positionCaret(txtComboModeloCompra.getText().length());
                // Actualizar búsqueda automáticamente al seleccionar modelo
                actualizarBusquedaCompraAutomatica();
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

        // Listener para el texto: filtrar DNIs mientras se escribe
        txtComboDni.textProperty().addListener((obs, oldValue, newValue) -> {
            final String selected = comboDni.getSelectionModel().getSelectedItem();

            // Si el campo está vacío, restaurar filtro y ocultar dropdown
            if (newValue == null || newValue.trim().isEmpty()) {
                filteredDnis.setPredicate(p -> true);
                comboDni.hide();
                return;
            }

            // Si no hay nada seleccionado o se está escribiendo algo diferente
            if (selected == null || !selected.equals(newValue)) {
                // Filtrar DNIs que empiecen con el texto escrito
                filteredDnis.setPredicate(p -> p.toLowerCase().startsWith(newValue.toLowerCase().trim()));
                comboDni.setVisibleRowCount(5);
                comboDni.show();
            } else {
                // Si se seleccionó algo, quitar el filtro
                filteredDnis.setPredicate(p -> true);
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
                lblNumero.setText("Teléfono: " + (int)cliente.getTelefono());
                lblEmail.setText("Email: " + cliente.getEmail());
                lblSexo.setText("DNI: " + cliente.getDni());
                lblEdad.setText("Dirección: " + cliente.getDireccion());

                // Activar sesión
                sesionActiva = true;

                // Cargar matrículas de coches del cliente
                cargarMatriculasCliente();

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
               esHijoDeTabla(target, TblCoincidencias) ||
               esHijoDeTabla(target, tblOpcionesCompra);
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
        if (tblOpcionesCompra != null) {
            tblOpcionesCompra.getSelectionModel().clearSelection();
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
        txtFechaNac.setDisable(true);
        txtDni.setDisable(true);
        txtEmail.setDisable(true);
        btnAñadirNuevoCliente.setDisable(true);
        btnCancelarNuevoCliente.setDisable(true);
        splitMenuSexo.setDisable(true);

    }

    private void ocultarTodos() {
        panelVentas.setVisible(false);
        pantallaAlquiler.setVisible(false);
        pantallaSesion.setVisible(false);
        panelRegistro.setVisible(false);
        panelIngreso.setVisible(false);
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

    }

    private void iniciarTablas() {

        // Verificar que la conexión esté disponible antes de cargar datos
        if (Conexion.getConnection() == null) {
            System.err.println("✗ No se pueden cargar las tablas: conexión a base de datos no disponible");
            return;
        }

        try {
            tablaRegistro.setItems(Conexion.mostrarClientes());
            clnNombreRegistro.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            clnDniRegistro.setCellValueFactory(new PropertyValueFactory<>("dni"));

            tblAlquiler.setItems(Conexion.mostrarAlquiler());
            clnFechaAlq.setCellValueFactory(new PropertyValueFactory<>("fechaDevolucion"));
            clnHoraAlq.setCellValueFactory(new PropertyValueFactory<>("horaDevolucion"));
            clnDNIAlq.setCellValueFactory(new PropertyValueFactory<>("dni"));
            clnMatriculaAlq.setCellValueFactory(new PropertyValueFactory<>("matricula"));

            TablaCoches.setItems(Conexion.mostrarCoches());
            clnMarca.setCellValueFactory(new PropertyValueFactory<>("Marca"));
            clnModelo.setCellValueFactory(new PropertyValueFactory<>("Modelo"));
            clnAño.setCellValueFactory(new PropertyValueFactory<>("Año"));
            clnBastidor.setCellValueFactory(new PropertyValueFactory<>("Bastidor"));
            clnColor.setCellValueFactory(new PropertyValueFactory<>("Color"));
        } catch (Exception e) {
            System.err.println("✗ Error al inicializar las tablas: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private void limpiarCliente() {
        txtNombre.clear();
        txtApellidos.clear();
        txtDireccion.clear();
        txtTelefono.clear();
        txtFechaNac.clear();
        txtDni.clear();
        txtEmail.clear();
        splitMenuSexo.setText("Sexo");
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
        ingresoMaterial.clear();
        ingresoDiseñoInterior.clear();
        ingresoMatricula.clear();
    }

    @FXML
    void setAlcantara(ActionEvent event) {
        slcMaterialCompra.setText(alcantara.getText());
        actualizarBusquedaCompraAutomatica();
    }

    @FXML
    void setCuero(ActionEvent event) {
        slcMaterialCompra.setText(cuero.getText());
        actualizarBusquedaCompraAutomatica();
    }

    @FXML
    void setTela(ActionEvent event) {
        slcMaterialCompra.setText(tela.getText());
        actualizarBusquedaCompraAutomatica();
    }

    @FXML
    void setAzul(ActionEvent event) {
        sclColorCompra.setText(azul.getText());
        actualizarBusquedaCompraAutomatica();
    }

    @FXML
    void setBlanco(ActionEvent event) {
        sclColorCompra.setText(blanco.getText());
        actualizarBusquedaCompraAutomatica();
    }

    @FXML
    void setNegro(ActionEvent event) {
        sclColorCompra.setText(negro.getText());
        actualizarBusquedaCompraAutomatica();
    }

    @FXML
    void setGris(ActionEvent event) {
        sclColorCompra.setText(gris.getText());
        actualizarBusquedaCompraAutomatica();
    }

    @FXML
    void setConfort(ActionEvent event) {
        slcPatronCompra.setText(confort.getText());
        actualizarBusquedaCompraAutomatica();
    }

    @FXML
    void setDeportivo(ActionEvent event) {
        slcPatronCompra.setText(deportivo.getText());
        actualizarBusquedaCompraAutomatica();
    }

    @FXML
    void setLujo(ActionEvent event) {
        slcPatronCompra.setText(lujo.getText());
        actualizarBusquedaCompraAutomatica();
    }

    /**
     * Actualiza automáticamente la tabla de búsqueda de compra
     * según los criterios seleccionados en los MenuButton
     */
    private void actualizarBusquedaCompraAutomatica() {
        String marca = mnMarcaCompra.getText();
        String modelo = cmbModeloCompra.getSelectionModel().getSelectedItem();
        String patron = slcPatronCompra.getText();
        String material = slcMaterialCompra.getText();
        String color = sclColorCompra.getText();

        // Realizar búsqueda flexible con los criterios actuales
        tblOpcionesCompra.getItems().clear();
        tblOpcionesCompra.setItems(Conexion.buscarCocheFlexible(marca, modelo, color, patron, material));

        // Configurar las columnas de la tabla
        clnMarcaCompra.setCellValueFactory(new PropertyValueFactory<>("Marca"));
        clnModeloCompra.setCellValueFactory(new PropertyValueFactory<>("Modelo"));
        clnAñoCompra.setCellValueFactory(new PropertyValueFactory<>("Año"));
        clnColorCompra.setCellValueFactory(new PropertyValueFactory<>("Color"));
        clnPrecioCompra.setCellValueFactory(new PropertyValueFactory<>("Precio"));
        clnDiseñoRCompra.setCellValueFactory(new PropertyValueFactory<>("DiseñoRuedas"));
        clnDiametroCompra.setCellValueFactory(new PropertyValueFactory<>("DiametroLlantas"));
        clnInteriorCompra.setCellValueFactory(new PropertyValueFactory<>("Interior"));
        clnMatIntCompra.setCellValueFactory(new PropertyValueFactory<>("Material"));
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

    /**
     * Muestra la información del coche en los labels correspondientes
     * @param matricula La matrícula del coche a mostrar
     */
    private void mostrarInfoCoche(String matricula) {
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
            tablaDeInformacion.getItems().add("Diseño Interior: " + coche.getDisenoInterior());
            tablaDeInformacion.getItems().add("Material Interior: " + coche.getMaterialInterior());
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

        // Menú contextual para tabla de Opciones de Compra
        configurarMenuContextualOpcionesCompra();
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

    /**
     * Configura el menú contextual para la tabla de Opciones de Compra
     */
    private void configurarMenuContextualOpcionesCompra() {
        javafx.scene.control.ContextMenu contextMenu = new javafx.scene.control.ContextMenu();

        javafx.scene.control.MenuItem modificarItem = new javafx.scene.control.MenuItem("Modificar");
        modificarItem.setOnAction(e -> editarIngreso(new ActionEvent()));

        javafx.scene.control.MenuItem eliminarItem = new javafx.scene.control.MenuItem("Eliminar");
        eliminarItem.setOnAction(e -> {
            CocheCompra cocheCompraSeleccionado = tblOpcionesCompra.getSelectionModel().getSelectedItem();
            if (cocheCompraSeleccionado != null) {
                boolean confirmado = AlertaEstilizada.confirmar(
                    "Eliminar Coche",
                    "¿Estás seguro?",
                    "¿Deseas eliminar el coche " + cocheCompraSeleccionado.getMarca() + " " + cocheCompraSeleccionado.getModelo() + " (" + cocheCompraSeleccionado.getMatricula() + ")?"
                );
                if (confirmado) {
                    try {
                        // Buscar el coche completo para obtener el bastidor
                        CocheCompleto coche = Conexion.buscarCochePorMatricula(cocheCompraSeleccionado.getMatricula());
                        if (coche != null) {
                            Conexion.borrarCoche(coche.getNumBastidor());
                            tblOpcionesCompra.getItems().remove(cocheCompraSeleccionado);
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

        contextMenu.getItems().addAll(modificarItem, eliminarItem);
        tblOpcionesCompra.setContextMenu(contextMenu);
    }

}
