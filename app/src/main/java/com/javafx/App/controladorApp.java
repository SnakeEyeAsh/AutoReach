package com.javafx.App;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.javafx.Data.Datos;
import com.javafx.JDBC.Conexion;
import com.javafx.Model.Alquiler;
import com.javafx.Model.Cita;
import com.javafx.Model.Cliente;
import com.javafx.Model.Coche;
import com.javafx.Model.CocheCompleto;
import com.javafx.Model.CocheCompra;
import com.javafx.Model.Disponible;
import com.javafx.Model.ValidadorAlquiler;
import com.javafx.Model.ValidadorCliente;
import com.javafx.Model.ValidadorCoche;

import javafx.application.Platform;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
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
    private TextField txtAñoCompra;

    @FXML
    private TextField txtMarcaCompra;

    @FXML
    private TextField txtModeloCompra;

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
    private Button btnBuscarMatricula;

    @FXML
    private Button btnEliminarCliente;

    @FXML
    private Button btnEditarCoche;

    @FXML
    private Button btnCancelarMatricula;
    @FXML
    private MenuButton mnElegirMarca;

    @FXML
    private TextField txtElegirModelo;

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
    private Button btnBorrarCita;

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
    private Button btnBuscarCliente;

    @FXML
    private Button btnBuscarCompra;

    @FXML
    private Button btnCancelarBCompra;

    @FXML
    private Button btnCancelarCita;

    @FXML
    private Button btnCancelarCompra;

    @FXML
    private Button btnCancelarNuevoCliente;

    @FXML
    private Button btnCancelarSeleccion;

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private Button btnCitas;

    @FXML
    private Button btnCompra;

    @FXML
    private Button btnCompras;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnEditarCliente;

    @FXML
    private Button btnGuardarCita;

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
    private DatePicker cldCita;

    @FXML
    private DatePicker cldEntrega;

    @FXML
    private DatePicker cldRecoger;

    @FXML
    private TableColumn<Alquiler, String> clnDNIAlq;

    @FXML
    private TableColumn<Cita, String> clnFecha;

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
    private Pane panelCitas;

    @FXML
    private AnchorPane panelLoginCliente;

    @FXML
    private AnchorPane panelNuevoCliente;
    /////////////////////////////////////////// PANELES
    /////////////////////////////////////////// //////////////////////////////////////

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
    private SplitMenuButton slcMatCita;

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
    private TableView<Cita> tblCitas;

    @FXML
    private TextField txtApellidos;

    @FXML
    private TextField txtContraseña;

    @FXML
    private TextField txtDireccion;

    @FXML
    private TextField txtDni;

    @FXML
    private TextField txtDniVer;

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

    @FXML
    private TextArea txtAreaCitas;

    private ValidadorCliente validador;
    private ValidadorCoche validadorC;
    private ValidadorAlquiler validadorAlquiler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Conexion.realizarConexion();
        Platform.runLater(() -> {
            Stage s = (Stage) btnIrAInicio.getScene().getWindow();
            s.getIcons().add(new Image("Assets/autoIcon.png"));
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
        btnIrAInicio.setStyle("-fx-background-color:darkgrey");
        iniciarTablas();
        ocultarTodos();
        desactivarBotones();
        mostrarPanel(paneLogin);
        deshabilitarBotonesCliente();
        llenarMenuMarcas();

        txtElegirModelo.setDisable(true);//////// Paralizacion temporal

        cliente = new Cliente("Ana", "Rodriguez", "12345678B", 623162788,
                "ana.rodriguez@email.com", "Avenida de la Constitución 42, Barcelona");

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
            // btnCitas.setDisable(false);
            // btnCompras.setDisable(false);
            btnCerrarSesion.setDisable(false);
            btnIrAIngreso.setDisable(false);
        } else {
            AlertaFallo.mostrar();
        }
    }

    //////////////////////////// ---LOGIN
    /// CLIENTE--- /////////////////////////////////////////

    @FXML
    void crearCliente(ActionEvent event) {

        txtDniVer.setDisable(true);
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

            try {
                Conexion.crearCliente(nombre, apellidos, dni, telefono, email, direccion, fechaNac, sexo);
                Alert alerta = new Alert(AlertType.CONFIRMATION);
                alerta.setTitle("Cliente añadido");
                alerta.setHeaderText("Éxito");
                alerta.setContentText("El cliente ha sido creado correctamente");
                alerta.showAndWait();
                limpiarCliente();
                iniciarTablas();
            } catch (SQLException error) {
                Alert alerta = new Alert(AlertType.ERROR);
                alerta.setTitle("Error de base de datos");
                alerta.setHeaderText("No se pudo crear el cliente");
                alerta.setContentText("Error: " + error.getMessage());
                alerta.showAndWait();
            }
        }
    }

    @FXML
    void cancelarNuevoCliente(ActionEvent event) {

    }
    /*
     * ==================================BUSCAR=====================================
     * ======
     */

    @FXML
    void buscarLogin(ActionEvent event) {

        txtDniVer.setDisable(false);
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

                    Alert alerta = new Alert(AlertType.INFORMATION);
                    alerta.setTitle("Cliente actualizado");
                    alerta.setHeaderText("Éxito");
                    alerta.setContentText("El cliente ha sido actualizado correctamente");
                    alerta.showAndWait();

                    editarCliente = false;
                    limpiarCliente();
                    iniciarTablas();
                    deshabilitarBotonesCliente();

                } catch (NumberFormatException e) {
                    Alert alerta = new Alert(AlertType.ERROR);
                    alerta.setTitle("Error de formato");
                    alerta.setHeaderText("Teléfono inválido");
                    alerta.setContentText("El teléfono debe contener solo números");
                    alerta.showAndWait();
                } catch (Exception e) {
                    Alert alerta = new Alert(AlertType.ERROR);
                    alerta.setTitle("Error al actualizar");
                    alerta.setHeaderText("No se pudo actualizar el cliente");
                    alerta.setContentText("Error: " + e.getMessage());
                    alerta.showAndWait();
                }
            }
        } else {
            // Modo selección normal (no edición)
            Cliente c = tablaRegistro.getSelectionModel().getSelectedItem();
            if (c != null) {
                // Aquí puedes agregar lógica para seleccionar un cliente
                // Por ejemplo, mostrarlo en los campos de solo lectura

                txtDniVer.setText(c.getDni());
            }
        }
    }

    @FXML
    void editarCliente(ActionEvent event) {
        Cliente c = tablaRegistro.getSelectionModel().getSelectedItem();

        if (c == null) {
            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Ningún cliente seleccionado");
            alerta.setHeaderText("Selecciona un cliente");
            alerta.setContentText("Por favor, selecciona un cliente de la tabla para editarlo");
            alerta.showAndWait();
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
            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Ningún cliente seleccionado");
            alerta.setHeaderText("Selecciona un cliente");
            alerta.setContentText("Por favor, selecciona un cliente de la tabla para eliminarlo");
            alerta.showAndWait();
            return;
        }

        // Confirmar eliminación
        Alert confirmacion = new Alert(AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar cliente?");
        confirmacion.setContentText(
                "¿Estás seguro de que deseas eliminar a " + c.getNombre() + " " + c.getApellidos() + "?");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            try {
                Conexion.eliminarCliente(c);
                Alert alerta = new Alert(AlertType.INFORMATION);
                alerta.setTitle("Cliente eliminado");
                alerta.setHeaderText("Éxito");
                alerta.setContentText("El cliente ha sido eliminado correctamente");
                alerta.showAndWait();
                iniciarTablas();
                limpiarCliente();
            } catch (Exception e) {
                Alert alerta = new Alert(AlertType.ERROR);
                alerta.setTitle("Error al eliminar");
                alerta.setHeaderText("No se pudo eliminar el cliente");
                alerta.setContentText("Error: " + e.getMessage());
                alerta.showAndWait();
            }
        }
    }

    @FXML
    void cancelarSeleccion(ActionEvent event) {
        // Cancelar modo edición y limpiar campos
        editarCliente = false;
        limpiarCliente();
        deshabilitarBotonesCliente();
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

    ////////////////////////////////////////////////////////////////////////////////// -
    /// ----------CITAS-----------------//////////////////////////
    /// /
    ////////////////////////////////////////////////////////////////////////////////// //////////////////////////////////////////////////////////////////////////////////

    @FXML
    void guardarCita(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void editarCita(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void borrarCita(ActionEvent event) {
        alertaDesarrollo.mostrar();

    }

    @FXML
    void cancelarCita(ActionEvent event) {
        alertaDesarrollo.mostrar();

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

        if(validadorAlquiler.esFormularioValido()){
            if (!editandoAlquiler) {
                Disponible d = TblCoincidencias.getSelectionModel().getSelectedItem();

                LocalDate fecha = cldRecoger.getValue();
                LocalDate fechaFin = cldEntrega.getValue();

                long dias = ChronoUnit.DAYS.between(fecha, fechaFin);
                double precioTotal = dias * Double.parseDouble(d.getPrecio());
                String estado = d.getDisponible().equals("si") ? "en_proceso" : "pendiente";
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
                }
                Conexion.actualizarAlquilado(d.getMatricula());

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
                txtElegirModelo.setDisable(false);
                btnBuscarMatricula.setDisable(false);
                btnCancelarMatricula.setDisable(false);
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
        txtElegirModelo.clear();
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
            txtElegirModelo.setDisable(true);
            btnBuscarMatricula.setDisable(true);
            btnCancelarMatricula.setDisable(true);

            Timestamp fechaR = a.getFechaRecogida();
            Timestamp fechaE = a.getFechaDevolucion();
            String hora = a.getHoraDevolucion();

            cldRecoger.setValue(fechaR.toLocalDateTime().toLocalDate());
            cldEntrega.setValue(fechaE.toLocalDateTime().toLocalDate());
            txtHoraEntrega.setText(hora);

        } else {
            Alert alerta = new Alert(AlertType.ERROR);
            alerta.setHeaderText("Error");
            alerta.setContentText("Debes seleccionar un alquiler para editarlo");
            alerta.showAndWait();

        }

    }

    @FXML
    void borrarAlq(ActionEvent event) {
        alertaDesarrollo.mostrar();

    }

    @FXML
    void buscarMatricula(ActionEvent event) {

        // TblCoincidencias.getItems().clear();
        String marca = mnElegirMarca.getText();
        // String modelo = txtElegirModelo.getText();
        TblCoincidencias.setItems(Conexion.mostrarCoincidencias(marca));
        clnAñoCoche.setCellValueFactory(new PropertyValueFactory<>("año"));
        clnDisponibleCoche.setCellValueFactory(new PropertyValueFactory<>("Disponible"));
        clnColorCoche.setCellValueFactory(new PropertyValueFactory<>("Color"));
        clnPrecioCoche.setCellValueFactory(new PropertyValueFactory<>("Precio"));
        clnMatriculaCoche.setCellValueFactory(new PropertyValueFactory<>("matricula"));

    }

    @FXML
    void cancelarBusquedaMatricula(ActionEvent event) {
        mnElegirMarca.setText("Marca");
        ;
        txtElegirModelo.clear();
        TblCoincidencias.getItems().clear();
    }

    ///
    /////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////// -----------COMPRA------------//////////////////////////////////////////////
    /// /
    /// ///////////////////////////////////////////////////////////////////////////////////

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
            System.out.println(e.getMessage() + "Hola");
        }

    }

    @FXML
    void cancelarCompra(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void BuscarCompra(ActionEvent event) {

        String marca = txtMarcaCompra.getText();
        String modelo = txtModeloCompra.getText();
        String año = txtAñoCompra.getText();
        String patron = slcPatronCompra.getText();
        String material = slcMaterialCompra.getText();
        String color = sclColorCompra.getText();

        if (año.isEmpty() || !año.matches("\\d+")) {
            // Mostrar alerta al usuario
            System.err.println("Por favor ingrese un año válido");
            return;
        }

        CocheCompra c = new CocheCompra(marca, modelo, año, "0", color, "", "", "", patron, material);

        tblOpcionesCompra.getItems().clear();
        tblOpcionesCompra.setItems(Conexion.buscarCoche(c));

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

    @FXML
    void CancelarBusquedaCompra(ActionEvent event) {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////// -
    /// -------INGRESOS----------------- ///////////////////////////////////
    /// /
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @FXML
    void aceptarIngreso(ActionEvent event) {

        if (validadorC.esFormularioValido()) {
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

            } catch (SQLException e) {
                System.out.println("error al incertar Coche" + e.getMessage());
            }
            limpiarIngreso();
        }
    }

    @FXML
    void cancelarIngreso(ActionEvent event) {
        limpiarIngreso();
    }

    @FXML
    void editarIngreso(ActionEvent event) {
        btnAceptarIngreso.setDisable(true);
        btnCancelarIngresos.setDisable(true);
        btnBorrarIngreso.setDisable(true);

        Coche c = TablaCoches.getSelectionModel().getSelectedItem();

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

        limpiarIngreso();
        iniciarTablas();

    }

    @FXML
    void borrarIngreso(ActionEvent event) {
        Coche c = TablaCoches.getSelectionModel().getSelectedItem();
        try {
            Conexion.borrarCoche(c.getBastidor());

        } catch (SQLException e) {
            System.out.println("Error al eliminar coche" + e.getMessage());
        }
        limpiarIngreso();
        iniciarTablas();

    }

    ///////////////////////////////////////////////////////////////////////////////////////////// /
    /// ////////////////////------SALIR----------------//////////////////////////////
    /// //////////
    /// /
    ///////////////////////////////////////////////////////////////////////////////////////////// /////////////////////////////////////////////////////////////////////////////////////
    @FXML
    void cerrarSesion(ActionEvent event) {
        Alert alerta = new Alert(AlertType.WARNING);
        alerta.setHeaderText("Saliendo del Programa");
        alerta.setContentText("Seguro que quieres salir del programa");
        alerta.getButtonTypes().addAll(ButtonType.CANCEL);

        Optional<ButtonType> opcion = alerta.showAndWait();

        if (opcion.isPresent() && opcion.get().equals(ButtonType.OK)) {
            Stage escena = (Stage) btnIrAInicio.getScene().getWindow();
            escena.close();
        } else {
            alerta.close();
        }
    }

    /////////////////////////////////////////////////////////// CAMBIAR ENTRE
    /// PANELES /////////////////////////////////////////////
    @FXML
    void irAInicio(ActionEvent event) {
        mostrarPanel(panelRegistro);
        cambiarColores.cambiar(btnIrAInicio, btnAlquiler, btnCompras, btnCitas, btnIrAIngreso);
    }

    @FXML
    void irAalquiler(ActionEvent event) {
        mostrarPanel(pantallaAlquiler);
        cambiarColores.cambiar(btnAlquiler, btnIrAInicio, btnCompras, btnCitas, btnIrAIngreso);
    }

    @FXML
    void irAcitas(ActionEvent event) {
        mostrarPanel(panelCitas);
        cambiarColores.cambiar(btnCitas, btnIrAInicio, btnAlquiler, btnCompras, btnIrAIngreso);
    }

    @FXML
    void irAcompras(ActionEvent event) {
        mostrarPanel(panelVentas);
        cambiarColores.cambiar(btnCompras, btnIrAInicio, btnAlquiler, btnCitas, btnIrAIngreso);
    }

    @FXML
    void irAIngresos(ActionEvent event) {
        mostrarPanel(panelIngreso);
        cambiarColores.cambiar(btnIrAIngreso, btnCompras, btnIrAInicio, btnAlquiler, btnCitas);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////// -
    /// ------
    /// -
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////// ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////////////
    ///
    /// FUNCIONES AUXILIARES

    private void llenarMenuMarcas() {
        List<String> lista = Conexion.obtenerMarcas();

        for (int i = 0; i < lista.size(); i++) {
            String nombreMarca = lista.get(i);
            MenuItem m = new MenuItem(nombreMarca);

            // Cuando se pulse, actualizar el texto del MenuButton
            m.setOnAction(e -> {
                mnElegirMarca.setText(nombreMarca);
                System.out.println("Marca seleccionada: " + nombreMarca);
            });

            mnElegirMarca.getItems().add(m);
        }
    }

    private void deshabilitarBotonesCliente() {

        txtDniVer.setDisable(true);
        btnSeleccionCLiente.setDisable(true);
        btnEditarCliente.setDisable(true);
        btnCancelarSeleccion.setDisable(true);
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
        panelCitas.setVisible(false);
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
        btnCitas.setDisable(true);
        btnCompras.setDisable(true);
        btnCerrarSesion.setDisable(true);
        btnIrAIngreso.setDisable(true);
    }

    private void iniciarTablas() {

        tablaRegistro.setItems(Conexion.mostrarClientes());
        clnNombreRegistro.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        clnDniRegistro.setCellValueFactory(new PropertyValueFactory<>("dni"));

        tblCitas.setItems(Conexion.mostrarCitas());
        clnMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        clnFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        clnHora.setCellValueFactory(new PropertyValueFactory<>("hora"));

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
    }

    @FXML
    void setCuero(ActionEvent event) {
        slcMaterialCompra.setText(cuero.getText());
    }

    @FXML
    void setTela(ActionEvent event) {
        slcMaterialCompra.setText(tela.getText());
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

    @FXML
    void setConfort(ActionEvent event) {
        slcPatronCompra.setText(confort.getText());
    }

    @FXML
    void setDeportivo(ActionEvent event) {
        slcPatronCompra.setText(deportivo.getText());
    }

    @FXML
    void setLujo(ActionEvent event) {
        slcPatronCompra.setText(lujo.getText());
    }

}
