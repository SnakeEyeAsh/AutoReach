package com.javafx.App;

import java.io.IOException;
import java.net.URL;

import java.util.Optional;
import java.util.ResourceBundle;

import com.javafx.Data.Datos;
import com.javafx.JDBC.Conexion;
import com.javafx.JDBC.seleccion;
import com.javafx.Model.Alquiler;
import com.javafx.Model.Cita;
import com.javafx.Model.Cliente;
import com.javafx.Model.Coche;

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
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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

    ///////////////////////////////// CRUD COCHES ////////////

    @FXML
    private TableView<Coche> TablaCoches;
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

    ///////////////////////////////////////////

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
    private TableColumn<?, ?> clnDNIAlq;

    @FXML
    private TableColumn<Cita, String> clnFecha;

    @FXML
    private TableColumn<?, ?> clnFechaAlq;

    @FXML
    private TableColumn<?, ?> clnHora;

    @FXML
    private TableColumn<Alquiler, String> clnHoraAlq;

    @FXML
    private TableColumn<Alquiler, String> clnMatricula;

    @FXML
    private TableColumn<?, ?> clnNombre;

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
    private MenuButton selectAñoAlq;

    @FXML
    private MenuButton selectMarcaAlq;

    @FXML
    private MenuButton selectModeloAlq;

    @FXML
    private MenuButton slcAñoCompra;

    @FXML
    private MenuButton slcConsumoCompra;

    @FXML
    private SplitMenuButton slcHoraCita;

    @FXML
    private MenuButton slcLLantasCompra;

    @FXML
    private MenuButton slcMarcaCompra;

    @FXML
    private SplitMenuButton slcMatCita;

    @FXML
    private MenuButton slcMaterialCompra;

    @FXML
    private MenuButton slcMatriculaLogin;

    @FXML
    private MenuButton slcModeloCompra;

    @FXML
    private MenuButton slcPatronCompra;

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
    private TextField txtHoraRecogida;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtNombreVer;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtUser;

    @FXML
    private VBox ventanaLogin;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        Conexion.realizarConexion();

        Platform.runLater(()->{
            Stage s = (Stage)btnIrAInicio.getScene().getWindow();
            s.getIcons().add(new Image("Assets/autoIcon.png"));
        });
    
        btnIrAInicio.setStyle("-fx-background-color:darkgrey");

        iniciarTablas();
        ocultarTodos();
        desactivarBotones();

        mostrarPanel(paneLogin);
        

    }

    @FXML
    void IniciarSesion(ActionEvent event) {
        if (txtUser.getText().equals(Datos.ADMIN.toString())
                && txtContraseña.getText().equals(Datos.PASSWORD.toString())) {
            mostrarPanel(panelRegistro);
            btnAlquiler.setDisable(false);
            btnIrAInicio.setDisable(false);
            btnCitas.setDisable(false);
            btnCompras.setDisable(false);
            btnCerrarSesion.setDisable(false);
            btnIrAIngreso.setDisable(false);
        }
    }

    @FXML
    void aceptarIngreso(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void cancelarIngreso(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void editarIngreso(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void borrarIngreso(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void borrarAlq(ActionEvent event) {
        alertaDesarrollo.mostrar();

    }

    @FXML
    void editarAlq(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void añadirCliente(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void borrarCita(ActionEvent event) {
        alertaDesarrollo.mostrar();

    }

    @FXML
    void buscarLogin(ActionEvent event) {
        alertaDesarrollo.mostrar();

    }

    @FXML
    void cancelarCita(ActionEvent event) {
        alertaDesarrollo.mostrar();

    }

    @FXML
    void cancelarCompr(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void cancelarNuevoCliente(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void cancelarSeleccion(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

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

    @FXML
    void crearCliente(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void editarCita(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void editarCliente(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void efectuarCompra(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pantallaCompra.fxml"));
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
    void elegirSexo(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void guardarCita(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void cancelarAlquiler(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void confirmarAlquiler(ActionEvent event) {
        alertaDesarrollo.mostrar();
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @FXML
    void seleccionarAño(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void seleccionarCliente(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void seleccionarFechaEntrega(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void seleccionarFechaRecogida(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void seleccionarMarca(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void seleccionarModelo(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void seleccionarfechaCita(ActionEvent event) {
        alertaDesarrollo.mostrar();
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

}
